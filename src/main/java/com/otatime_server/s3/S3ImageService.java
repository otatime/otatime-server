package com.otatime_server.s3;

import com.otatime_server.s3.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public UploadResponse upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("올바른 파일 이름 형식이 아닙니다.");
        }

        validateExtension(fileName);

        String createFileName = createFileName(fileName);

        // 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(createFileName)
                .contentType(file.getContentType())
                .build();

        // 실제 업로드
        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        // URL 생성
        String fileUrl = s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(createFileName)
                        .build()
        ).toString();

        return new UploadResponse(fileUrl);
    }

    public void delete(String fileUrl) {
        // fileUrl에서 objectKey 추출
        String objectKey = fileUrl.split(bucket + ".s3." + region + ".amazonaws.com/")[1];

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String createFileName(String fileName) {
        return "image/" + UUID.randomUUID() + "_" + fileName;
    }

    private void validateExtension(String fileName) {
        if (!FileExtension.isValidExtension(fileName)) {
            throw new IllegalArgumentException("올바른 파일 확장자 형식이 아닙니다. (jpg, jpeg, png)");
        }
    }
}
