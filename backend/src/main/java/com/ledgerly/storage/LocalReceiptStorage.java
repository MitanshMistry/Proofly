package com.ledgerly.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LocalReceiptStorage {
    private static final Logger log = LoggerFactory.getLogger(LocalReceiptStorage.class);
    private final Path root;

    public LocalReceiptStorage(@Value("${ledgerly.storage.local-root:./ledgerly-files}") String localRoot) throws IOException {
        this.root = Path.of(localRoot).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public StoredFile store(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IOException("Receipt image is empty");
        if (file.getSize() > 10 * 1024 * 1024) throw new IOException("Receipt image exceeds 10 MB");
        String mime = file.getContentType();
        if (mime == null || !mime.matches("image/(jpeg|png|webp|heic|heif)")) {
            throw new IOException("Only JPG, PNG, WEBP, HEIC, or HEIF uploads are supported");
        }

        String objectKey = UUID.randomUUID() + "-" + sanitize(file.getOriginalFilename());
        Path target = this.root.resolve(objectKey);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return new StoredFile(objectKey, mime, file.getSize(), sha256(target));
    }

    private String sha256(Path file) throws IOException {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IOException("SHA-256 is unavailable", exception);
        }
    }

    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) return;
        try {
            Path target = this.root.resolve(objectKey).normalize();
            if (target.startsWith(this.root)) Files.deleteIfExists(target);
        } catch (IOException exception) {
            log.warn("Could not remove a local receipt file");
        }
    }

    public byte[] read(String objectKey) throws IOException {
        if (objectKey == null || objectKey.isBlank()) throw new IOException("Receipt image is unavailable");
        Path target = this.root.resolve(objectKey).normalize();
        if (!target.startsWith(this.root) || !Files.isRegularFile(target)) {
            throw new IOException("Receipt image is unavailable");
        }
        return Files.readAllBytes(target);
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) return "receipt";
        String safe = name.replaceAll("[^A-Za-z0-9._-]", "_");
        return safe.length() > 80 ? safe.substring(safe.length() - 80) : safe;
    }
}
