package com.otatime_server.mail.controller;

import com.otatime_server.auth.service.JwtService;
import com.otatime_server.global.dto.CommonResponse;
import com.otatime_server.mail.dto.CertRequest;
import com.otatime_server.mail.dto.MailCertToken;
import com.otatime_server.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final JwtService jwtService;

    @GetMapping("/cert")
    public CommonResponse<MailCertToken> checkCertCode(@RequestBody CertRequest certRequest) {
        if (mailService.isValidCode(certRequest)) {
            String certToken = jwtService.makeCertToken(certRequest.certCode());
            return new CommonResponse<>(new MailCertToken(certToken));
        }
        return new CommonResponse<>(new MailCertToken(""));
    }

}
