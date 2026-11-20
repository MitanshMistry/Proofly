package com.ledgerly.reminder;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyReminderRepository extends JpaRepository<WarrantyReminder, UUID> {
    List<WarrantyReminder> findByUserIdOrderByReminderAtAsc(UUID userId);
}
