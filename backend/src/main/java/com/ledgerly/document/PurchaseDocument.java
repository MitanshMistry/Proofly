package com.ledgerly.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_documents")
public class PurchaseDocument {
    @Id private UUID id;
    @Column(name = "receipt_id", nullable = false) private UUID receiptId;
    @Column(name = "object_key", nullable = false) private String objectKey;
    @Column(name = "mime_type", nullable = false) private String mimeType;
    @Column(name = "document_type", nullable = false) private String documentType;
    @Column(name = "size_bytes", nullable = false) private Long sizeBytes;
    @Column(nullable = false) private String sha256;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected PurchaseDocument() {}
    public PurchaseDocument(UUID receiptId, String objectKey, String mimeType,
                            String documentType, Long sizeBytes, String sha256) {
        this.id = UUID.randomUUID();
        this.receiptId = receiptId;
        this.objectKey = objectKey;
        this.mimeType = mimeType;
        this.documentType = documentType;
        this.sizeBytes = sizeBytes;
        this.sha256 = sha256;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getReceiptId() { return receiptId; }
    public String getObjectKey() { return objectKey; }
    public String getMimeType() { return mimeType; }
    public String getDocumentType() { return documentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public String getSha256() { return sha256; }
}
