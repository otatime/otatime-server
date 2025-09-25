package com.otatime_server.mail.service;

import static com.otatime_server.mail.MailConst.MAIL_SUCCESS;

import com.otatime_server.mail.dto.CertRequest;
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

    public void sendCertMail(String email) {
        mailUtil.sendMail(email).thenAccept(certCord -> {
            mailRedisRepository.saveEmailCertCode(email, certCord);
            log.info("{} {}", MAIL_SUCCESS, email);
        });
    }

    public Boolean isValidCode(CertRequest certRequest) {
        String savedCertCode = mailRedisRepository.findCertCodeByEmail(certRequest.email());
        compareCertCode(certRequest.certCode(), savedCertCode);
        mailRedisRepository.saveVerifiedEmail(certRequest.email());
        return true;
    }

    private void compareCertCode(String certCode, String savedCertCode) {
        if (!savedCertCode.equals(certCode)) {
            throw new IllegalArgumentException("INVALID_CERT_CODE");
        }
    }
}
