package com.otatime_server.post.repository;

import static com.otatime_server.like.domain.QPostLike.*;
import static com.otatime_server.post.domain.QPost.*;

import com.otatime_server.post.domain.Category;
import com.otatime_server.post.domain.EventType;
import com.otatime_server.post.domain.Post;
import com.otatime_server.post.domain.Region;
import com.otatime_server.post.dto.PostDetail;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<PostDetail> getMainPosts(
            Pageable pageable,
            LocalDate startDate,
            LocalDate endDate,
            Region region,
            Long userId,
            List<Category> categories,
            List<EventType> eventTypes
    ) {
        // 좋아요 ID 목록
        List<Long> likeIds = (userId != null) ?
                queryFactory.select(postLike.postId)
                        .from(postLike)
                        .where(postLike.userId.eq(userId))
                        .fetch()
                : Collections.emptyList();

        // 동적 조건
        BooleanBuilder builder = new BooleanBuilder();

        if (startDate != null) {
            builder.and(post.startDate.goe(startDate));
        }
        if (endDate != null) {
            builder.and(post.startDate.loe(endDate));
        }
        if (region != null) {
            builder.and(post.region.eq(region));
        }
        if (categories != null && !categories.isEmpty()) {
            builder.and(post.category.in(categories));
        }
        if (eventTypes != null && !eventTypes.isEmpty()) {
            builder.and(post.eventType.in(eventTypes));
        }

        // 전체 개수
        Long total = queryFactory.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        if (total == null || total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 실제 데이터 조회
        List<Post> posts = queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.startDate.asc(), post.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // DTO 매핑
        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds))
                .toList();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<PostDetail> getDailyPosts(Pageable pageable, LocalDate date, Long userId) {

        // 좋아요 ID 목록 (userId가 있을 때만)
        List<Long> likeIds = (userId != null) ?
                queryFactory.select(postLike.postId)
                        .from(postLike)
                        .where(postLike.userId.eq(userId))
                        .fetch()
                : Collections.emptyList();

        // 조건: 해당 날짜의 post
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.startDate.eq(date));

        // 전체 개수 조회
        Long total = queryFactory.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        if (total == null || total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 실제 데이터 조회 (내림차순)
        List<Post> posts = queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.startDate.desc(), post.id.desc()) // 내림차순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // DTO 매핑
        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds))
                .toList();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<PostDetail> getMonthlyPosts(Pageable pageable, LocalDate firstDay, LocalDate lastDay, Long userId) {

        // 좋아요 ID 목록 (userId가 있을 때만)
        List<Long> likeIds = (userId != null) ?
                queryFactory.select(postLike.postId)
                        .from(postLike)
                        .where(postLike.userId.eq(userId))
                        .fetch()
                : Collections.emptyList();

        // 조건: firstDay ~ lastDay 범위
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.startDate.goe(firstDay))
                .and(post.startDate.loe(lastDay));

        // 전체 개수 조회
        Long total = queryFactory.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        if (total == null || total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 실제 데이터 조회 (날짜 오름차순)
        List<Post> posts = queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.startDate.asc(), post.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // DTO 매핑
        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds))
                .toList();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<PostDetail> searchPosts(Pageable pageable, String query, Long userId) {

        // 사용자가 좋아요한 게시글 id 조회
        List<Long> likeIds = queryFactory.select(postLike.postId)
                .from(postLike)
                .where(postLike.userId.eq(userId))
                .fetch();

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 검색 + 오늘 이후 + 제목/내용 포함
        List<Post> posts = queryFactory.selectFrom(post)
                .where(
                        post.startDate.goe(today),
                        post.title.containsIgnoreCase(query)
                                .or(post.details.containsIgnoreCase(query))
                )
                .orderBy(post.startDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 (페이징 total count)
        Long total = queryFactory.select(post.count())
                .from(post)
                .where(
                        post.startDate.goe(today),
                        post.title.containsIgnoreCase(query)
                                .or(post.details.containsIgnoreCase(query))
                )
                .fetchOne();

        if (posts.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds))
                .toList();

        return new PageImpl<>(result, pageable, total != null ? total : 0);
    }



}
