package com.nexus.service;

import com.nexus.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${nexus.upload.dir}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(uploadRoot);
        log.info("Upload directory ready at {}", uploadRoot);
    }

    public String store(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG, PNG, GIF, and WebP images are allowed");
        }

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";
        String extension = extractExtension(original);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Unsupported image file extension");
        }

        String filename = UUID.randomUUID() + extension;
        Path directory = uploadRoot.resolve(subfolder).normalize();
        try {
            Files.createDirectories(directory);
            Path destination = directory.resolve(filename).normalize();
            if (!destination.startsWith(uploadRoot)) {
                throw new BadRequestException("Invalid upload path");
            }
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + subfolder + "/" + filename;
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store uploaded file");
        }
    }

    public void deleteIfExists(String publicPath) {
        if (publicPath == null || publicPath.isBlank() || !publicPath.startsWith("/uploads/")) {
            return;
        }
        Path file = uploadRoot.resolve(publicPath.substring("/uploads/".length())).normalize();
        if (!file.startsWith(uploadRoot)) {
            return;
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            log.warn("Could not delete file {}", file, ex);
        }
    }

    private String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0) {
            return "";
        }
        return filename.substring(idx).toLowerCase();
    }
}
