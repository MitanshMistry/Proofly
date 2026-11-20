package com.ledgerly.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {
    @Id private UUID id;
    @Column(name = "user_id", nullable = false) private UUID userId;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String color = "purple";
    @Column(nullable = false) private Instant createdAt;

    protected Project() {}
    public Project(UUID userId, String name, String color) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.color = color == null || color.isBlank() ? "purple" : color;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public Instant getCreatedAt() { return createdAt; }
}
