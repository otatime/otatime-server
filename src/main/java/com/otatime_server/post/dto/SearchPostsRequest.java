package com.otatime_server.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SearchPostsRequest(
        @NotBlank(message = "검색어는 필수 값입니다.")
        @Size(min = 2, max = 50, message = "검색어는 최소 {min}자 이상, {max}자 이하로 입력해주세요.")
        String keyword
) {}
