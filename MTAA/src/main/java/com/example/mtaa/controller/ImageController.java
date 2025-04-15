package com.example.mtaa.controller;

import com.example.mtaa.model.CommonException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "Get upload URL", description = "Get URL to upload image to firebase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "URL generated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Filename/Content Type is rather empty or invalid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/generate-upload-url")
    public ResponseEntity<Map<String, String>> generateUploadUrl(@RequestParam String filename,
                                                                 @RequestParam String contentType) {
        if (filename == null || filename.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Filename cannot be empty");
        }
        if (contentType == null || contentType.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Content type cannot be empty");
        }
        if (!contentType.startsWith("image/")) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Content type must be an image");
        }

        Bucket bucket = StorageClient.getInstance().bucket();

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

    @Operation(summary = "Get download URL", description = "Get URL to download image from firebase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL generated successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Filename is rather empty or invalid", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/generateDownloadUrl")
    public ResponseEntity<Map<String, String>> generateDownloadUrl(@RequestParam String filename) {

        if (filename == null || filename.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Filename cannot be null or empty");
        }

        Bucket bucket = StorageClient.getInstance().bucket();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucket.getName(), filename).build();
        URL signedUrl = bucket.getStorage().signUrl(
                blobInfo,
                15,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET)
        );

        if (signedUrl == null) {
            throw new CommonException(HttpStatus.NOT_FOUND, "File not found");
        }

        Map<String, String> response = new HashMap<>();
        response.put("downloadUrl", signedUrl.toString());
        return ResponseEntity.ok(response);
    }
}
