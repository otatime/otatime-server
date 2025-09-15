package com.otatime_server.mail.repository;

import static com.otatime_server.mail.MailConst.*;

import jakarta.annotation.Resource;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MailRedisRepository {
    @Value("${mail.expiration_time}")
    private Integer expirationTime;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    public void saveEmailCertCode(String email, String certCode) {
        String key = EMAIL_CERT_CODE_KEY + email;
        Duration timeoutDuration = Duration.ofMinutes(expirationTime);
        valueOperations.set(key, certCode, timeoutDuration);
    }

    public void saveVerifiedEmail(String email) {
        String key = VERIFIED_EMAIL_KEY + email;
        Duration timeoutDuration = Duration.ofMinutes(expirationTime);
        valueOperations.set(key, VERIFIED, timeoutDuration);
    }

    public String findVerifiedEmail(String email) {
        String key = VERIFIED_EMAIL_KEY + email;
        return valueOperations.get(key);
    }

    public String findCertCodeByEmail(String email) {
        String key = EMAIL_CERT_CODE_KEY + email;
        return valueOperations.get(key);
    }
}

