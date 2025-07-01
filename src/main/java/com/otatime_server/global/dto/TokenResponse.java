package com.otatime_server.global.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String username,
        String profileImageUrl
) {
}
