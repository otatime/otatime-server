package com.otatime_server.post.service;

import com.otatime_server.like.domain.PostLike;
import com.otatime_server.like.repository.PostLikeRepository;
import com.otatime_server.post.domain.Category;
import com.otatime_server.post.domain.EventStatus;
import com.otatime_server.post.domain.EventType;
import com.otatime_server.post.domain.Post;
import com.otatime_server.post.domain.PostStatus;
import com.otatime_server.post.domain.Region;
import com.otatime_server.post.domain.ReportHistory;
import com.otatime_server.post.dto.PostDetail;
import com.otatime_server.post.dto.PostResponse;
import com.otatime_server.post.dto.ReportRequest;
import com.otatime_server.post.repository.PostRepository;
import com.otatime_server.post.repository.ReportHistoryRepository;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Transactional
    public PostResponse report(String email, ReportRequest reportRequest) {
        User user = getUser(email);
        Post post = new Post(
                reportRequest.title(),
                reportRequest.summary(),
                reportRequest.details(),
                LocalDate.now(),
                LocalDate.now(),
                reportRequest.location(),
                reportRequest.imageUrl(),
                Region.SEOUL,
                EventStatus.SCHEDULED,
                Category.ANIMATION,
                EventType.COLLABO_CAFE,
                PostStatus.PENDING
        );

        Post savedPost = postRepository.save(post);

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

        return postRepository.getMainPosts(
                pageable,
                toLocalDate(startDate),
                toLocalDate(endDate),
                Region.getRegionByValue(region),
                user.getId(),
                categories,
                eventTypes
        );
    }

    public Page<PostDetail> getDatePage(Pageable pageable, String startDate, String email) {
        User user = getUser(email);
        return postRepository.getDailyPosts(pageable, toLocalDate(startDate), user.getId());
    }

    public Page<PostDetail> getMonthPage(Pageable pageable, String month, String email) {
        User user = getUser(email);
        return postRepository.getMonthlyPosts(pageable, getFirstDayOfMonth(month), getLastDayOfMonth(month), user.getId());
    }

    public List<PostDetail> getBanner() {
        return postRepository.findTop4ByStartDateClosest(LocalDate.now()).stream()
                .map(p -> PostDetail.of(p, Collections.emptyList()))
                .toList();
    }

    public Page<PostDetail> search(String query, Pageable pageable, String email) {

        User user = getUser(email);

        return postRepository.searchPosts(pageable, query, user.getId());
    }

    public LocalDate getFirstDayOfMonth(String yearMonthStr) {
        YearMonth ym = YearMonth.parse(yearMonthStr, YEAR_MONTH_FORMATTER);
        return ym.atDay(1);
    }

    public LocalDate getLastDayOfMonth(String yearMonthStr) {
        YearMonth ym = YearMonth.parse(yearMonthStr, YEAR_MONTH_FORMATTER);
        return ym.atEndOfMonth();
    }

    public PostDetail getPostDetail(Long postId, String email) {
        Post post = getPost(postId);
        User user = getUser(email);
        List<Long> postIdByUserId = postLikeRepository.findPostIdByUserId(user.getId());

        return PostDetail.of(post, postIdByUserId);
    }

    private User getUser(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 포스트 없음"));
    }

    private LocalDate toLocalDate(String dateString) {
        return LocalDate.parse(dateString);
    }
}
