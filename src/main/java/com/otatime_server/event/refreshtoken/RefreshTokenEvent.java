package com.otatime_server.event.refreshtoken;

public record RefreshTokenEvent(
        String email,
        String refreshToken
) {
}
