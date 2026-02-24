package com.academy.aws.controller;

import com.academy.aws.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * File Attachment Controller — S3 upload/download API.
 *
 * TODO Task 3: Implement the endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachmentController {

    private final S3Service s3Service;

    // TODO Task 3a: POST /api/files/upload
    // multipart/form-data with a "file" field
    // Return: { "key": "...", "message": "Uploaded successfully" }
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        try {
            String key = s3Service.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                "key",     key,
                "message", "Uploaded successfully",
                "size",    String.valueOf(file.getSize())
            ));
        } catch (IOException e) {
            log.error("Upload failed", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // TODO Task 3b: GET /api/files/{key}/url?expiryMinutes=15
    // Return a pre-signed download URL
    @GetMapping("/{key}/url")
    public ResponseEntity<Map<String, String>> getDownloadUrl(
            @PathVariable String key,
            @RequestParam(defaultValue = "15") int expiryMinutes) {
        // TODO: check file exists, generate URL
        String url = s3Service.generatePresignedUrl(key, expiryMinutes);
        return ResponseEntity.ok(Map.of("url", url, "expiresInMinutes", String.valueOf(expiryMinutes)));
    }

    // TODO Task 3c: GET /api/files — list all uploaded files
    @GetMapping
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(s3Service.listFiles());
    }

    // TODO Task 3d: DELETE /api/files/{key}
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable String key) {
        s3Service.deleteFile(key);
        return ResponseEntity.noContent().build();
    }
}
