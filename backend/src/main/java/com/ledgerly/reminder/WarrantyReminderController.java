package com.ledgerly.reminder;

import com.ledgerly.auth.UserAccount;
import com.ledgerly.receipt.Receipt;
import com.ledgerly.receipt.ReceiptRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reminders")
public class WarrantyReminderController {
    private final WarrantyReminderRepository reminders;
    private final ReceiptRepository receipts;

    public WarrantyReminderController(WarrantyReminderRepository reminders, ReceiptRepository receipts) {
        this.reminders = reminders;
        this.receipts = receipts;
    }

    @GetMapping
    public List<ReminderResponse> list(@AuthenticationPrincipal UserAccount account) {
        return reminders.findByUserIdOrderByReminderAtAsc(account.getId()).stream()
                .map(ReminderResponse::from).toList();
    }

    @PostMapping
    public ReminderResponse create(@AuthenticationPrincipal UserAccount account,
                                   @RequestParam UUID receiptId,
                                   @RequestParam @NotBlank String intervalLabel,
                                   @RequestParam LocalDate reminderAt) {
        Receipt receipt = receipts.findById(receiptId)
                .filter(item -> item.getUserId().equals(account.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));
        WarrantyReminder reminder = reminders.save(new WarrantyReminder(
                account.getId(), receipt.getId(), reminderAt.atStartOfDay(ZoneOffset.UTC).toInstant(), intervalLabel));
        return ReminderResponse.from(reminder);
    }

    @GetMapping("/calendar.ics")
    public void calendar(@AuthenticationPrincipal UserAccount account, HttpServletResponse response) throws IOException {
        response.setContentType("text/calendar");
        response.setHeader("Content-Disposition", "attachment; filename=\"ledgerly-warranty-reminders.ics\"");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("BEGIN:VCALENDAR");
            writer.println("VERSION:2.0");
            writer.println("PRODID:-//Ledgerly//Warranty Reminders//EN");
            for (WarrantyReminder reminder : reminders.findByUserIdOrderByReminderAtAsc(account.getId())) {
                String stamp = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                        .format(reminder.getReminderAt().atZone(ZoneOffset.UTC));
                writer.println("BEGIN:VEVENT");
                writer.println("UID:" + reminder.getId() + "@ledgerly");
                writer.println("DTSTART:" + stamp);
                writer.println("SUMMARY:Ledgerly warranty reminder");
                writer.println("DESCRIPTION:" + reminder.getIntervalLabel());
                writer.println("END:VEVENT");
            }
            writer.println("END:VCALENDAR");
        }
    }

    public record ReminderResponse(UUID id, UUID receiptId, Instant reminderAt, String intervalLabel) {
        static ReminderResponse from(WarrantyReminder reminder) {
            return new ReminderResponse(reminder.getId(), reminder.getReceiptId(),
                    reminder.getReminderAt(), reminder.getIntervalLabel());
        }
    }
}
