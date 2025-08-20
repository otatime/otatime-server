package com.otatime_server.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record PostUpdateRequest(
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 1, max = 100, message = "제목은 {min}자 이상 {max}자 이하여야 합니다.")
        String title,

        @NotBlank(message = "요약은 필수 입력값입니다.")
        @Size(min = 1, max = 300, message = "요약은 {min}자 이상 {max}자 이하여야 합니다.")
        String summary,

        @NotBlank(message = "상세 내용은 필수 입력값입니다.")
        @Size(min = 1, max = 2000, message = "상세 내용은 {min}자 이상 {max}자 이하여야 합니다.")
        String details,

        @URL(message = "유효한 이미지 URL 형식이어야 합니다.")
        String imageUrl,

        @NotBlank(message = "시작일은 필수 입력값입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "시작일은 yyyy-MM-dd 형식이어야 합니다.")
        String startDate,

        @NotBlank(message = "종료일은 필수 입력값입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "종료일은 yyyy-MM-dd 형식이어야 합니다.")
        String endDate,

        @NotBlank(message = "카테고리는 필수 입력값입니다.")
        @Size(max = 50, message = "카테고리는 최대 {max}자까지 가능합니다.")
        String category,

        @NotBlank(message = "이벤트 유형은 필수 입력값입니다.")
        @Size(max = 50, message = "이벤트 유형은 최대 {max}자까지 가능합니다.")
        String eventType,

        @NotBlank(message = "지역은 필수 입력값입니다.")
        @Size(max = 50, message = "지역은 최대 {max}자까지 가능합니다.")
        String region,

        @NotBlank(message = "위치는 필수 입력값입니다.")
        @Size(max = 255, message = "위치는 최대 {max}자까지 가능합니다.")
        String location
) {
}
