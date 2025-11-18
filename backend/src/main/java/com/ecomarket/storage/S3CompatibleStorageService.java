package com.ecomarket.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
// Note: using default HTTP client provided by SDK

@Service
public class S3CompatibleStorageService implements StorageService {

    private final S3Client s3;
    private final String bucket;
    private final String prefix;

    public S3CompatibleStorageService(
            @Value("${s3.endpoint:}") String endpoint,
            @Value("${s3.region:us-east-1}") String region,
            @Value("${s3.access-key:}") String accessKey,
            @Value("${s3.secret-key:}") String secretKey,
            @Value("${s3.bucket:}") String bucket,
            @Value("${s3.prefix:}") String prefix
    ) {
        this.bucket = bucket;
        this.prefix = prefix != null ? prefix : "";

        var b = S3Client.builder()
            .region(Region.of(region));

        if (accessKey != null && !accessKey.isEmpty()) {
            b.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        }

        if (endpoint != null && !endpoint.isEmpty()) {
            b.endpointOverride(URI.create(endpoint));
        }

        this.s3 = b.build();
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String key = (prefix.isEmpty() ? "" : prefix + "/") + java.util.UUID.randomUUID().toString() + (original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : "");

        try (InputStream in = file.getInputStream()) {
            PutObjectRequest por = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3.putObject(por, RequestBody.fromInputStream(in, file.getSize()));
        }
        return key;
    }

    @Override
    public Path load(String filename) throws IOException {
        // For S3, return a pseudo-path containing bucket/key; callers should handle differently.
        return Paths.get("s3://" + bucket + "/" + filename);
    }

    @Override
    public void delete(String filename) throws IOException {
        DeleteObjectRequest dor = DeleteObjectRequest.builder().bucket(bucket).key(filename).build();
        s3.deleteObject(dor);
    }
}
