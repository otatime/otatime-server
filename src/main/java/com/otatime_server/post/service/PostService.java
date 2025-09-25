package com.otatime_server.post.service;

import com.otatime_server.like.domain.PostLike;
import com.otatime_server.like.repository.PostLikeRepository;
import com.otatime_server.post.domain.Address;
import com.otatime_server.post.domain.Category;
import com.otatime_server.post.domain.EventStatus;
import com.otatime_server.post.domain.EventType;
import com.otatime_server.post.domain.Post;
import com.otatime_server.post.domain.PostStatus;
import com.otatime_server.post.domain.Region;
import com.otatime_server.post.domain.ReportHistory;
import com.otatime_server.post.dto.PostDetail;
import com.otatime_server.post.dto.PostRequest;
import com.otatime_server.post.dto.PostResponse;
import com.otatime_server.post.dto.PostUpdateRequest;
import com.otatime_server.post.dto.ReportRequest;
import com.otatime_server.post.repository.AddressRepository;
import com.otatime_server.post.repository.PostRepository;
import com.otatime_server.post.repository.ReportHistoryRepository;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ReportHistoryRepository reportHistoryRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final AddressRepository addressRepository;

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public PostResponse report(String email, ReportRequest reportRequest) {
        User user = getUser(email);

        Region region = Region.getRegionByValue(reportRequest.region());
        Category category = Category.fromValue(reportRequest.category());
        EventType eventType = EventType.fromValue(reportRequest.eventType());

        LocalDate startDate = LocalDate.parse(reportRequest.startDate());
        LocalDate endDate = LocalDate.parse(reportRequest.endDate());

        Address address = new Address(
                reportRequest.zipCode(),
                reportRequest.street(),
                reportRequest.detailsAddress(),
                reportRequest.latitude(),
                reportRequest.longitude()
        );

        Address savedAddress = addressRepository.save(address);

        Post post = new Post(
                reportRequest.title(),
                reportRequest.details(),
                startDate,
                endDate,
                reportRequest.imageUrl(),
                region,
                EventStatus.SCHEDULED,
                category,
                eventType,
                PostStatus.PENDING,
                savedAddress
        );

        Post savedPost = postRepository.save(post);

        // 제보 히스토리 기록
        reportHistoryRepository.save(new ReportHistory(user.getId(), savedPost.getId()));
        user.adopt();

        return new PostResponse(savedPost.getId());
    }


    @Transactional
    public PostResponse likePost(String email, Long postId) {
        User user = getUser(email);
        Post post = getPost(postId);
        if (postLikeRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            postLikeRepository.deleteByUserIdAndPostId(user.getId(), post.getId());
            return new PostResponse(post.getId());
        }

        PostLike postLike = new PostLike(user.getId(), post.getId());
        postLikeRepository.save(postLike);

        return new PostResponse(post.getId());
    }

    public Page<PostDetail> getMainPage(Pageable pageable, String startDate, String endDate, String region, String email, List<Category> categories, List<EventType> eventTypes) {
        User user = getUser(email);

        Long userId = user == null ? null : user.getId();

        return postRepository.getMainPosts(
                pageable,
                toLocalDate(startDate),
                toLocalDate(endDate),
                Region.getRegionByValue(region),
                userId,
                categories,
                eventTypes
        );
    }

    public Page<PostDetail> getDatePage(Pageable pageable, String startDate, String email) {

        LocalDate date = toLocalDate(startDate);
        System.out.println("request date = " + startDate);
        System.out.println("parsed LocalDate = " + date);

        User user = getUser(email);
        Long userId = user == null ? null : user.getId();
        return postRepository.getDailyPosts(pageable, toLocalDate(startDate), userId);
    }

    public Page<PostDetail> getMonthPage(Pageable pageable, String month, String email) {
        User user = getUser(email);
        Long userId = user == null ? null : user.getId();
        return postRepository.getMonthlyPosts(pageable, getFirstDayOfMonth(month), getLastDayOfMonth(month), userId);
    }

    public List<PostDetail> getBanner() {
        return postRepository.findTop4ByStartDateClosest(LocalDate.now()).stream()
                .map(p -> PostDetail.of(p, Collections.emptyList()))
                .toList();
    }

    public Page<PostDetail> search(String query, Pageable pageable, String email) {

        User user = getUser(email);
        Long userId = user == null ? null : user.getId();
        return postRepository.searchPosts(pageable, query, userId);
    }

    public LocalDate getFirstDayOfMonth(String yearMonthStr) {
        try {
            YearMonth ym = YearMonth.parse(yearMonthStr, YEAR_MONTH_FORMATTER);
            return ym.atDay(1);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 월 형식입니다: " + yearMonthStr);
        }
    }

    public LocalDate getLastDayOfMonth(String yearMonthStr) {
        try {
            YearMonth ym = YearMonth.parse(yearMonthStr, YEAR_MONTH_FORMATTER);
            return ym.atEndOfMonth();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 월 형식입니다: " + yearMonthStr);
        }
    }

    public PostDetail getPostDetail(Long postId, String email) {
        Post post = getPost(postId);
        List<Long> postIdByUserId = getLikeList(email);
        return PostDetail.of(post, postIdByUserId);
    }

    private List<Long> getLikeList(String email) {
        User user =  getUser(email);
        if (user == null) {
            return Collections.emptyList();
        }
        return postLikeRepository.findPostIdByUserId(user.getId());
    }

    private User getUser(String userEmail) {
        if (userEmail.equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 포스트 없음"));
    }

    private LocalDate toLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다: " + dateString);
        }
    }

    public Page<Post> getPostList(PostStatus postStatus) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        return postRepository.findAllByStatus(pageRequest, postStatus);
    }

    @Transactional
    public Long deletePost(Long postId) {
        postRepository.deleteById(postId);
        return postId;
    }

    public Long updateToPost(Long postId) {
        postRepository.updatePostStatus(postId);
        return postId;
    }

    @Transactional
    public PostResponse post(String email, PostRequest postRequest) {
        User user = getUser(email);

        Region region = Region.getRegionByValue(postRequest.region());
        Category category = Category.fromValue(postRequest.category());
        EventType eventType = EventType.fromValue(postRequest.eventType());

        LocalDate startDate = LocalDate.parse(postRequest.startDate());
        LocalDate endDate = LocalDate.parse(postRequest.endDate());

        Address address = new Address(
                postRequest.zipCode(),
                postRequest.street(),
                postRequest.detailsAddress(),
                postRequest.latitude(),
                postRequest.longitude()
        );

        Address savedAddress = addressRepository.save(address);

        Post post = new Post(
                postRequest.title(),
                postRequest.details(),
                startDate,
                endDate,
                postRequest.imageUrl(),
                region,
                EventStatus.SCHEDULED,
                category,
                eventType,
                PostStatus.PUBLISHED,
                savedAddress
        );

        Post savedPost = postRepository.save(post);

        return new PostResponse(savedPost.getId());
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        Post post = getPost(postId);
        Long updatedId = post.update(postUpdateRequest);
        return new PostResponse(updatedId);
    }
}
