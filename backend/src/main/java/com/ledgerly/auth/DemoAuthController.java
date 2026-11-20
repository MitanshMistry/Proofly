package com.ledgerly.auth;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.ledgerly.receipt.Receipt;
import com.ledgerly.receipt.ReceiptRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@RequestMapping("/api/auth/demo")
public class DemoAuthController {
    private static final String DEMO_EMAIL = "demo@ledgerly.local";
    private static final String DEMO_PASSWORD = "Password123";

    private final UserAccountRepository users;
    private final PasswordEncoder passwords;
    private final AuthenticationManager authenticationManager;
    private final ReceiptRepository receipts;

    public DemoAuthController(UserAccountRepository users, PasswordEncoder passwords,
                              AuthenticationManager authenticationManager,
                              ReceiptRepository receipts) {
        this.users = users;
        this.passwords = passwords;
        this.authenticationManager = authenticationManager;
        this.receipts = receipts;
    }

    @PostMapping
    public Map<String, Object> login(HttpServletRequest request) {
        UserAccount account = users.findByEmail(DEMO_EMAIL).orElseGet(() ->
                users.save(new UserAccount(DEMO_EMAIL, "Demo User", passwords.encode(DEMO_PASSWORD))));
        if (receipts.findByUserIdOrderByPurchasedAtDesc(account.getId()).isEmpty()) {
            seedPurchases(account);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(DEMO_EMAIL, DEMO_PASSWORD));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return Map.of("id", account.getId().toString(), "email", account.getUsername(),
                "displayName", account.getDisplayName());
    }

    private void seedPurchases(UserAccount account) {
        addPurchase(account, "Canadian Tire", "2026-10-15", "249.99", "Tools",
                "CT-4821-20261015", "20V Cordless Drill", "DeWalt", "DCD796D2",
                "DCD796-2026-4821", "SKU-DCD796D2", "MANUFACTURER", 36,
                "2026-10-15", "2029-10-14", "2026-11-14", true);
        addPurchase(account, "Best Buy", "2025-11-10", "1299.99", "Electronics",
                "BB-73194-20251110", "65-inch Smart TV", "Samsung", "QN65Q80C",
                "SAM-Q80C-73194", "SKU-QN65Q80C", "MANUFACTURER", 12,
                "2025-11-10", "2026-11-09", "2025-11-24", true);
        addPurchase(account, "Apple Store", "2026-08-20", "1499.00", "Computer",
                "APL-M2-20260820", "MacBook Air, 13-inch", "Apple", "M2",
                "C02W7M2AMPLE", "SKU-MBA-M2-13", "MANUFACTURER", 12,
                "2026-08-20", "2027-08-19", "2026-09-19", true);
        addPurchase(account, "The Brick", "2026-02-12", "1199.00", "Appliance",
                "BRICK-WM-20260212", "Front-load washing machine", "LG", "WM4000HWA",
                "LG-WM-2026018", "SKU-WM4000HWA", "MANUFACTURER", 12,
                "2026-02-12", "2026-12-12", "2026-02-26", true);
        addPurchase(account, "Canadian Tire", "2026-09-01", "189.50", "Automotive",
                "CT-BRAKE-20260901", "Toyota brake kit", "Toyota", "PAD-TOY-4521",
                null, "SKU-PAD-TOY-4521", "UNKNOWN", null,
                null, null, "2026-09-15", false);
        addPurchase(account, "Sleep Country", "2023-11-19", "2199.00", "Furniture",
                "SC-MT-20231119", "Purple Restore Hybrid mattress", "Purple", "RESTORE-HYBRID-Q",
                "PURPLE-MT-11842", "SKU-PURPLE-HYBRID-Q", "MANUFACTURER", 120,
                "2023-11-19", "2033-11-18", "2023-12-19", true);
        addPurchase(account, "Home Depot", "2026-11-01", "579.00", "Tools",
                "HD-MWB-20261101", "Compact workbench", "Milwaukee", "MWB-4820",
                "MW-4820-7391", "SKU-MWB-4820", "MANUFACTURER", 60,
                "2026-11-01", "2031-10-31", "2026-11-16", true);
        addPurchase(account, "Leon's", "2024-11-20", "2499.00", "Appliance",
                "LEONS-FRIDGE-20241120", "French-door refrigerator", "LG", "LRFCS2503S",
                "LG-FRIDGE-55018", "SKU-LRFCS2503S", "MANUFACTURER", 12,
                "2024-11-20", "2025-11-19", "2024-12-04", true);
        addPurchase(account, "Best Buy", "2026-10-22", "749.99", "Health",
                "BB-DYSON-20261022", "V15 Detect vacuum", "Dyson", "V15",
                "DYSON-V15-64108", "SKU-DYSON-V15", "MANUFACTURER", 60,
                "2026-10-22", "2031-10-21", "2026-11-05", true);
        addPurchase(account, "Costco", "2026-06-15", "1240.00", "Automotive",
                "COSTCO-TIRE-20260615", "CrossClimate 2 tires", "Michelin", "CROSSCLIMATE-2",
                null, "SKU-CROSSCLIMATE-2", "MANUFACTURER", 72,
                "2026-06-15", "2032-06-14", "2026-06-29", true);
    }

    private void addPurchase(UserAccount account, String store, String purchaseDate, String total,
                             String category, String receiptNumber, String productName, String brand,
                             String modelNumber, String serialNumber, String sku, String warrantyProvider,
                             Integer warrantyMonths, String warrantyStart, String warrantyEnd,
                             String returnWindowEnd, boolean warrantyConfirmed) {
        Receipt purchase = new Receipt(account.getId(),
                LocalDate.parse(purchaseDate), new BigDecimal(total), store, category);
        purchase.setPurchaseAndWarrantyDetails(
                receiptNumber, productName, brand, modelNumber, serialNumber, sku, 1,
                warrantyProvider, warrantyMonths,
                warrantyStart == null ? null : LocalDate.parse(warrantyStart),
                warrantyEnd == null ? null : LocalDate.parse(warrantyEnd),
                returnWindowEnd == null ? null : LocalDate.parse(returnWindowEnd),
                warrantyConfirmed);
        if (warrantyConfirmed) purchase.confirmPurchase();
        receipts.save(purchase);
    }
}
