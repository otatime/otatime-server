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
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public PostRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        this.queryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
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
        Long total = getSize(builder);

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
                .map(p -> PostDetail.of(p, likeIds, new ArrayList<>()))
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

        // 조건: 특정 날짜가 진행 기간 안에 포함되는 포스트
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.startDate.loe(date))   // startDate <= date
                .and(post.endDate.goe(date));    // endDate >= date

        // 전체 개수 조회 (중복 제거 필요하면 distinct)
        Long total = getSize(builder);

        if (total == null || total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 실제 데이터 조회
        List<Post> posts = queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.startDate.desc(), post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // DTO 매핑
        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds, new ArrayList<>()))
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
        Long total = getSize(builder);

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
                .map(p -> PostDetail.of(p, likeIds, new ArrayList<>()))
                .toList();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<PostDetail> searchPosts(Pageable pageable, String query, Long userId) {

        // 1. Native Query - 게시글 목록 조회
        String sql = """
        SELECT * FROM post
        WHERE MATCH(title, details) AGAINST(:query IN BOOLEAN MODE)
        ORDER BY start_date, post_id
        LIMIT :limit OFFSET :offset
    """;

        List<Post> posts = entityManager.createNativeQuery(sql, Post.class)
                .setParameter("query", "+" + query + "*")
                .setParameter("limit", pageable.getPageSize())
                .setParameter("offset", pageable.getOffset())
                .getResultList();

        if (posts.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 2. Native Query - 총 개수 조회
        String countSql = """
        SELECT COUNT(*)
        FROM post
        WHERE MATCH(title, details) AGAINST(:query IN BOOLEAN MODE)
    """;

        Number totalCount = (Number) entityManager.createNativeQuery(countSql)
                .setParameter("query", "+" + query + "*")
                .getSingleResult();

        long total = totalCount.longValue();

        // 3. 좋아요된 게시글 id 목록 조회
        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .toList();

        List<Long> likeIds = queryFactory.select(postLike.postId)
                .from(postLike)
                .where(
                        postLike.userId.eq(userId),
                        postLike.postId.in(postIds)
                )
                .fetch();

        // 4. DTO 매핑
        List<PostDetail> result = posts.stream()
                .map(p -> PostDetail.of(p, likeIds, new ArrayList<>()))
                .toList();

        return new PageImpl<>(result, pageable, total);
    }

    private Long getSize(BooleanBuilder builder) {
        return queryFactory.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();
    }
}
