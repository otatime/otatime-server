package com.otatime_server.post.dto;

import com.otatime_server.post.domain.Post;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record PostDetail(
        Long postId,
        String title,
        String imageUrl,
        String startDate,
        String endDate,
        String category,
        String type,
        String region,
        String location,
        boolean isLiked,
        long DDay
) {

    public static PostDetail of(Post post, List<Long> likeIds) {
        return new PostDetail(
                post.getId(),
                post.getTitle(),
                post.getImageUrl(),
                post.getStartDate().toString(),
                post.getEndDate().toString(),
                post.getCategory().toString(),
                post.getEventType().getValue(),
                post.getRegion().toString(),
                post.getAddress().getStreet(),
                likeIds.contains(post.getId()),
                dday(post.getStartDate())
        );
    }

    private static long dday(LocalDate startDate) {
        return ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }

}
