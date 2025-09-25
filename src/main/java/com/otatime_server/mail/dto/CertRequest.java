package com.otatime_server.mail.dto;

public record CertRequest(
        String email,
        String certCode
) {
}
