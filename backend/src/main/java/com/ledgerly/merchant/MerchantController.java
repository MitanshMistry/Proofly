package com.ledgerly.merchant;

import com.ledgerly.auth.UserAccount;
import com.ledgerly.receipt.Receipt;
import com.ledgerly.receipt.ReceiptRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private final ReceiptRepository receipts;

    public MerchantController(ReceiptRepository receipts) {
        this.receipts = receipts;
    }

    @GetMapping
    public List<MerchantResponse> list(@AuthenticationPrincipal UserAccount account) {
        Map<String, List<Receipt>> grouped = receipts.findByUserIdOrderByPurchasedAtDesc(account.getId()).stream()
                .collect(Collectors.groupingBy(receipt ->
                        receipt.getNotes() == null || receipt.getNotes().isBlank()
                                ? "Needs review" : receipt.getNotes().trim(),
                        LinkedHashMap::new, Collectors.toList()));
        return grouped.entrySet().stream().map(entry -> {
            List<Receipt> items = entry.getValue();
            BigDecimal total = items.stream().map(Receipt::getTotal).filter(value -> value != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LocalDate latest = items.stream().map(Receipt::getPurchasedAt).filter(value -> value != null)
                    .max(Comparator.naturalOrder()).orElse(null);
            return new MerchantResponse(entry.getKey(), items.size(), total, latest);
        }).sorted((left, right) -> {
            if (left.latest() == null) return right.latest() == null ? 0 : 1;
            if (right.latest() == null) return -1;
            return right.latest().compareTo(left.latest());
        }).toList();
    }

    public record MerchantResponse(String name, int receiptCount, BigDecimal total, LocalDate latest) {}
}
