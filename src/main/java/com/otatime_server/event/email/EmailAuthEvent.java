package com.otatime_server.event.email;

public record EmailAuthEvent(
        Long userId,
        String email
) {
}
