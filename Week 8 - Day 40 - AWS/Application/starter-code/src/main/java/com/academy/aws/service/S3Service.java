package com.academy.aws.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * S3 Service — file upload, download, list, delete, presigned URLs.
 *
 * TODO Task 2: Implement all methods.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // TODO Task 2a: Upload a file to S3
    // Return the S3 object key (unique name used to retrieve the file)
    public String uploadFile(MultipartFile file) throws IOException {
        // TODO:
        // 1. Generate a unique key: UUID + original filename
        // 2. Build PutObjectRequest with bucket, key, contentType
        // 3. Call s3Client.putObject(request, RequestBody.fromInputStream(...))
        // 4. Return the key

        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        // TODO: uncomment when AWS is configured
        // s3Client.putObject(request,
        // RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        log.info("Uploaded file to S3: key={}, bucket={}", key, bucketName);
        return key;
    }

    // TODO Task 2b: Generate a pre-signed URL (valid for N minutes)
    // A pre-signed URL lets a user download a private file directly from S3
    public String generatePresignedUrl(String key, int expiryMinutes) {
        // TODO:
        // try (S3Presigner presigner = S3Presigner.create()) {
        // GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        // .signatureDuration(Duration.ofMinutes(expiryMinutes))
        // .getObjectRequest(r -> r.bucket(bucketName).key(key))
        // .build();
        // return presigner.presignGetObject(presignRequest).url().toString();
        // }
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key + "?TODO=implement";
    }

    // TODO Task 2c: List all objects in the bucket
    public List<String> listFiles() {
        // TODO:
        // ListObjectsV2Request request =
        // ListObjectsV2Request.builder().bucket(bucketName).build();
        // return s3Client.listObjectsV2(request).contents().stream()
        // .map(S3Object::key)
        // .toList();
        return List.of("TODO: implement listFiles");
    }

    // TODO Task 2d: Delete a file from S3
    public void deleteFile(String key) {
        // TODO:
        // s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
        log.info("TODO: delete file {} from bucket {}", key, bucketName);
    }

    // TODO Task 2e: Check if a file exists
    public boolean fileExists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}
