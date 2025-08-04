package com.otatime_server.post.dto;

import com.otatime_server.post.domain.Category;
import com.otatime_server.post.domain.EventType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@ValidDateRange
public record PostSearchRequest(

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "시작일은 yyyy-MM-dd 형식이어야 합니다.")
        String start,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "종료일은 yyyy-MM-dd 형식이어야 합니다.")
        String end,

        @Size(max = 50, message = "지역은 최대 {max}자까지 가능합니다.")
        String region,

        List<Category> categories,
        List<EventType> eventTypes
) {}
