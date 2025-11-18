package com.ecomarket.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StorageConfig {

    @Value("${file.storage.type:local}")
    private String storageType;

    @Bean
    @Primary
    public StorageService storageService(LocalStorageService local, S3CompatibleStorageService s3) {
        if ("s3".equalsIgnoreCase(storageType)) {
            return s3;
        }
        return local;
    }
}
