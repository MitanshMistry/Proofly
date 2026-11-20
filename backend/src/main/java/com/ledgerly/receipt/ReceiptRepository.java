package com.ledgerly.receipt;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    List<Receipt> findByUserIdOrderByPurchasedAtDesc(UUID userId);
}
