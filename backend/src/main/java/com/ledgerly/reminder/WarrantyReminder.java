package com.ledgerly.reminder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "warranty_reminders")
public class WarrantyReminder {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(name = "receipt_id", nullable = false) private UUID receiptId;
    @Column(name = "reminder_at", nullable = false) private Instant reminderAt;
    @Column(name = "interval_label", nullable = false) private String intervalLabel;
    @Column(nullable = false) private boolean delivered = false;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected WarrantyReminder() {}
    public WarrantyReminder(UUID userId, UUID receiptId, Instant reminderAt, String intervalLabel) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.receiptId = receiptId;
        this.reminderAt = reminderAt;
        this.intervalLabel = intervalLabel;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getReceiptId() { return receiptId; }
    public Instant getReminderAt() { return reminderAt; }
    public String getIntervalLabel() { return intervalLabel; }
}
