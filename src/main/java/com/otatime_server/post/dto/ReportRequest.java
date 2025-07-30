package com.otatime_server.post.dto;

public record ReportRequest(
        String title,
        String summary,
        String details,
        String imageUrl,
        String startDate,
        String endDate,
        String category,
        String EventType,
        String region,
        String location
) {
}
