package com.otatime_server.user.dto;

public record MyPageResponse(
        Long userId,
        String email,
        String username,
        String profileImageUrl,
        int adoptionCount
) {
}
