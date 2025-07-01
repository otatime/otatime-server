package com.otatime_server.event.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAuthEventListener {

    @Async
    @EventListener(EmailAuthEvent.class)
    public void sendEmail(EmailAuthEvent event) {
        log.info("Email event received: {}", event.email());
    }
}
