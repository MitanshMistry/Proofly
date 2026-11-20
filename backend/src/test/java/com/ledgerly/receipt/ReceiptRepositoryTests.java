package com.ledgerly.receipt;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ReceiptRepositoryTests {
    @Autowired
    ReceiptRepository repository;

    @Test
    void ordersOwnedReceiptsNewestFirst() {
        UUID userId = UUID.randomUUID();
        repository.save(new Receipt(userId, LocalDate.of(2026, 11, 10), new BigDecimal("20.00"), "Target", "Shopping"));
        repository.save(new Receipt(userId, LocalDate.of(2026, 11, 18), new BigDecimal("40.00"), "Trader Joe's", "Groceries"));

        var receipts = repository.findByUserIdOrderByPurchasedAtDesc(userId);

        assertThat(receipts).hasSize(2);
        assertThat(receipts.get(0).getPurchasedAt()).isEqualTo(LocalDate.of(2026, 11, 18));
    }
}
