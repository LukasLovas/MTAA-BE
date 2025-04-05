package com.example.mtaa.controller;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/images")
public class ImageController {

    @GetMapping("/generate-upload-url")
    public ResponseEntity<Map<String, String>> generateUploadUrl(@RequestParam String filename,
                                                                 @RequestParam String contentType) {
        Bucket bucket = StorageClient.getInstance().bucket();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) authentication.getPrincipal();
        filename = "files/" + userId + "/" + filename;

        BlobInfo blobInfo = BlobInfo.newBuilder(
                        bucket.getName(),
                        filename
                ).setContentType(contentType)
                .build();

        URL signedUrl = bucket.getStorage().signUrl(blobInfo, 15, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withContentType());

        Map<String, String> response = new HashMap<>();
        response.put("uploadUrl", signedUrl.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/generateDownloadUrl")
    public ResponseEntity<Map<String, String>> generateDownloadUrl(@RequestParam String filename) {
        Bucket bucket = StorageClient.getInstance().bucket();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), filename).build();
        URL signedUrl = bucket.getStorage().signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET)
        );

        Map<String, String> response = new HashMap<>();
        response.put("downloadUrl", signedUrl.toString());
        return ResponseEntity.ok(response);
    }
}
