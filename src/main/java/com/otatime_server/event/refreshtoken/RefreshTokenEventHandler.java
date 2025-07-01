package com.otatime_server.event.refreshtoken;

import com.otatime_server.refreshtoken.RefreshToken;
import com.otatime_server.refreshtoken.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenEventHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Async
    @EventListener(RefreshTokenEvent.class)
    public void saveRefreshToken(RefreshTokenEvent event) {
        refreshTokenRepository.save(new RefreshToken(event.email(), event.refreshToken()));
    }
}
