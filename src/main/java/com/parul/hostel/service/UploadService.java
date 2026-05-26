package com.parul.hostel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class UploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Uploads directory initialized at: {}", Paths.get(uploadDir).toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create uploads directory", e);
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String saveFile(MultipartFile file, String prefix) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot save empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "file";
        }
        
        // Emulate Python secure_filename: keep only alphanumeric, dots, hyphens, and underscores
        String cleanName = originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String finalFilename = prefix + "_" + cleanName;
        
        Path destination = Paths.get(uploadDir).resolve(finalFilename);
        log.info("Saving file to: {}", destination.toAbsolutePath());
        
        Files.copy(file.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        return finalFilename;
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
