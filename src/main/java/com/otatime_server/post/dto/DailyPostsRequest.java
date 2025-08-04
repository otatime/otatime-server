package com.otatime_server.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DailyPostsRequest(
        @NotBlank(message = "날짜는 필수 값입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
        String date
) {}
