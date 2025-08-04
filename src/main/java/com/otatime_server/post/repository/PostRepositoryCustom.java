package com.otatime_server.post.repository;

import com.otatime_server.post.domain.Category;
import com.otatime_server.post.domain.EventType;
import com.otatime_server.post.domain.Region;
import com.otatime_server.post.dto.PostDetail;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostDetail> getMainPosts(
            Pageable pageable,
            LocalDate startDate,
            LocalDate endDate,
            Region region,
            Long userId,
            List<Category> categories,
            List<EventType> eventTypes
    );

    Page<PostDetail> getDailyPosts(Pageable pageable, LocalDate date, Long userId);

    Page<PostDetail> getMonthlyPosts(Pageable pageable, LocalDate firstDay, LocalDate lastDay, Long userId);

    Page<PostDetail> searchPosts(Pageable pageable, String query, Long userId);

}
