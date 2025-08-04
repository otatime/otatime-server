package com.otatime_server.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MonthlyPostsRequest(
        @NotBlank(message = "월은 필수 값입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}", message = "월은 yyyy-MM 형식이어야 합니다.")
        String month
) {}
