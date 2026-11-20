package com.ledgerly.history;

import com.ledgerly.auth.UserAccount;
import com.ledgerly.receipt.Receipt;
import com.ledgerly.receipt.ReceiptRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final ReceiptRepository receipts;

    public HistoryController(ReceiptRepository receipts) {
        this.receipts = receipts;
    }

    @GetMapping
    public List<HistoryResponse> list(@AuthenticationPrincipal UserAccount account) {
        return receipts.findByUserIdOrderByPurchasedAtDesc(account.getId()).stream()
                .map(receipt -> new HistoryResponse(
                        receipt.getId(),
                        "RECEIPT_SAVED",
                        "Receipt saved for " + (receipt.getNotes() == null ? "Needs review" : receipt.getNotes()),
                        receipt.getCreatedAt()))
                .sorted((left, right) -> right.createdAt().compareTo(left.createdAt()))
                .toList();
    }

    public record HistoryResponse(UUID receiptId, String eventType, String description, Instant createdAt) {}
}
