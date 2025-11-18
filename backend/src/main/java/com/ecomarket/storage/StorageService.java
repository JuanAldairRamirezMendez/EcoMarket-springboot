package com.ecomarket.storage;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file) throws IOException;
    Path load(String filename) throws IOException;
    void delete(String filename) throws IOException;
}
