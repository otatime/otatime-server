package com.otatime_server.mail.service;

import static com.otatime_server.mail.MailConst.MAIL_SUCCESS;

import com.otatime_server.mail.repository.MailRedisRepository;
import com.otatime_server.mail.util.MailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final MailUtil mailUtil;
    private final MailRedisRepository mailRedisRepository;

    public Boolean sendCertMail(String email) {
        mailUtil.sendMail(email).thenAccept(certCord -> {
            mailRedisRepository.saveEmailCertCode(email, certCord);
            log.info("{} {}", MAIL_SUCCESS, email);
        });
        return true;
    }

    public Boolean checkCertCode(String email, String certCode) {
        String savedCertCode = mailRedisRepository.findCertCodeByEmail(email);
        compareCertCode(certCode, savedCertCode);
        mailRedisRepository.saveVerifiedEmail(email);
        return true;
    }

    public void checkVerifiedEmail(String email) {
        if (mailRedisRepository.findVerifiedEmail(email) == null)
            throw new IllegalArgumentException("");
    }

    private void compareCertCode(String certCode, String savedCertCode) {
        if (!savedCertCode.equals(certCode)) {
            throw new IllegalArgumentException("INVALID_CERT_CODE");
        }
    }
}
