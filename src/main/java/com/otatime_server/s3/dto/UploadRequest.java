package com.otatime_server.s3.dto;

import org.springframework.web.multipart.MultipartFile;

public record UploadRequest(
        MultipartFile files
) {
}
