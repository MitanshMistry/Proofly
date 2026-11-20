package com.ledgerly.storage;

public record StoredFile(String objectKey, String mimeType, long sizeBytes, String sha256) {}
