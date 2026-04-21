package com.kma.lamphoun.room_management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public String storeRoomImage(MultipartFile file) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String filename = "room_" + UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir, "rooms");
        Files.createDirectories(dir);

        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/uploads/rooms/" + filename;
    }

    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path file = Paths.get(uploadDir, "rooms", filename);
            Files.deleteIfExists(file);
        } catch (IOException ignored) {}
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}
