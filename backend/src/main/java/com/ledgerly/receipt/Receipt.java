package com.ledgerly.receipt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receipts")
public class Receipt {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "project_id") private UUID projectId;
    private LocalDate purchasedAt;
    private String imageObjectKey;
    private String imageMimeType;
    private Long imageSizeBytes;
    private String imageSha256;
    private String receiptNumber;
    private String productName;
    private String brand;
    private String modelNumber;
    private String serialNumber;
    private String sku;
    private Integer quantity;
    private String warrantyProvider = "UNKNOWN";
    private Integer warrantyDurationMonths;
    private LocalDate warrantyStartAt;
    private LocalDate warrantyEndAt;
    private LocalDate returnWindowEndAt;
    private Boolean warrantyConfirmed = false;
    @Column(nullable = false) private String currency = "USD";
    private BigDecimal total;
    private String category;
    private String notes;
    @Column(nullable = false) private String status = "NEEDS_REVIEW";
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;

    protected Receipt() {}
    public Receipt(UUID userId, LocalDate purchasedAt, BigDecimal total, String merchantName, String category) {
        this.id = UUID.randomUUID(); this.userId = userId; this.purchasedAt = purchasedAt;
        this.total = total; this.notes = merchantName; this.category = category;
        this.createdAt = Instant.now(); this.updatedAt = this.createdAt;
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public LocalDate getPurchasedAt() { return purchasedAt; }
    public BigDecimal getTotal() { return total; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
    public UUID getProjectId() { return projectId; }
    public Instant getCreatedAt() { return createdAt; }
    public String getImageObjectKey() { return imageObjectKey; }
    public String getImageMimeType() { return imageMimeType; }
    public String getImageSha256() { return imageSha256; }
    public String getReceiptNumber() { return receiptNumber; }
    public String getProductName() { return productName; }
    public String getBrand() { return brand; }
    public String getModelNumber() { return modelNumber; }
    public String getSerialNumber() { return serialNumber; }
    public String getSku() { return sku; }
    public Integer getQuantity() { return quantity; }
    public String getWarrantyProvider() { return warrantyProvider; }
    public Integer getWarrantyDurationMonths() { return warrantyDurationMonths; }
    public LocalDate getWarrantyStartAt() { return warrantyStartAt; }
    public LocalDate getWarrantyEndAt() { return warrantyEndAt; }
    public LocalDate getReturnWindowEndAt() { return returnWindowEndAt; }
    public Boolean getWarrantyConfirmed() { return warrantyConfirmed; }
    public String getWarrantyStatus() {
        if ("NONE".equals(warrantyProvider)) return "NO_WARRANTY";
        if (warrantyConfirmed == null || !warrantyConfirmed || warrantyEndAt == null) return "UNKNOWN";
        if (warrantyEndAt.isBefore(LocalDate.now())) return "EXPIRED";
        if (!warrantyEndAt.isAfter(LocalDate.now().plusDays(30))) return "EXPIRING_SOON";
        return "ACTIVE";
    }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public void update(LocalDate purchasedAt, BigDecimal total, String merchantName, String category, UUID projectId) {
        this.purchasedAt = purchasedAt;
        this.total = total;
        this.notes = merchantName;
        this.category = category;
        this.projectId = projectId;
        this.status = "SAVED";
        this.updatedAt = Instant.now();
    }

    public void setImageMetadata(String objectKey, String mimeType, Long sizeBytes, String sha256) {
        this.imageObjectKey = objectKey;
        this.imageMimeType = mimeType;
        this.imageSizeBytes = sizeBytes;
        this.imageSha256 = sha256;
    }

    public void confirmPurchase() {
        this.status = "SAVED";
        this.updatedAt = Instant.now();
    }

    public void setPurchaseAndWarrantyDetails(String receiptNumber, String productName, String brand,
                                               String modelNumber, String serialNumber, String sku,
                                               Integer quantity, String warrantyProvider,
                                               Integer warrantyDurationMonths, LocalDate warrantyStartAt,
                                               LocalDate warrantyEndAt, LocalDate returnWindowEndAt,
                                               boolean warrantyConfirmed) {
        this.receiptNumber = receiptNumber;
        this.productName = productName;
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.serialNumber = serialNumber;
        this.sku = sku;
        this.quantity = quantity;
        this.warrantyProvider = warrantyProvider == null ? "UNKNOWN" : warrantyProvider;
        this.warrantyDurationMonths = warrantyDurationMonths;
        this.warrantyStartAt = warrantyStartAt;
        this.warrantyEndAt = warrantyEndAt;
        this.returnWindowEndAt = returnWindowEndAt;
        this.warrantyConfirmed = warrantyConfirmed;
    }
}
