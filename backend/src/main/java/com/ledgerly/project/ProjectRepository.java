package com.ledgerly.project;

import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
