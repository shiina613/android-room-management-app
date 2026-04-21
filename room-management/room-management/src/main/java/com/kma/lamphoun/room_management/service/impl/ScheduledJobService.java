package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.common.enums.NotificationType;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.entity.Contract;
import com.kma.lamphoun.room_management.entity.Invoice;
import com.kma.lamphoun.room_management.repository.ContractRepository;
import com.kma.lamphoun.room_management.repository.InvoiceRepository;
import com.kma.lamphoun.room_management.repository.RoomRepository;
import com.kma.lamphoun.room_management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledJobService {

    private final ContractRepository contractRepository;
    private final InvoiceRepository invoiceRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;

    /**
     * Chạy lúc 1:00 AM hàng ngày.
     * Cập nhật hợp đồng ACTIVE đã quá end_date → EXPIRED, phòng → AVAILABLE.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expireContracts() {
        LocalDate today = LocalDate.now();
        List<Contract> expired = contractRepository.findByStatusAndEndDateBefore(ContractStatus.ACTIVE, today);
        if (expired.isEmpty()) return;

        log.info("[ScheduledJob] Expiring {} contracts", expired.size());
        for (Contract contract : expired) {
            contract.setStatus(ContractStatus.EXPIRED);
            contractRepository.save(contract);

            // Chuyển phòng về AVAILABLE nếu không còn hợp đồng ACTIVE khác
            if (!contractRepository.existsByRoomIdAndStatus(contract.getRoom().getId(), ContractStatus.ACTIVE)) {
                contract.getRoom().setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(contract.getRoom());
            }

            // Thông báo cho tenant
            try {
                notificationService.send(
                        contract.getTenant().getId(),
                        "Hợp đồng đã hết hạn",
                        "Hợp đồng thuê phòng " + contract.getRoom().getTitle() + " đã hết hạn vào " + contract.getEndDate(),
                        NotificationType.CONTRACT_EXPIRING,
                        contract.getId()
                );
            } catch (Exception e) {
                log.warn("Failed to send expiry notification for contract {}: {}", contract.getId(), e.getMessage());
            }
        }
        log.info("[ScheduledJob] Expired {} contracts", expired.size());
    }

    /**
     * Chạy lúc 2:00 AM hàng ngày.
     * Cập nhật hóa đơn UNPAID đã quá due_date → OVERDUE.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void markOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<Invoice> overdue = invoiceRepository.findByStatusAndDueDateBefore(InvoiceStatus.UNPAID, today);
        if (overdue.isEmpty()) return;

        log.info("[ScheduledJob] Marking {} invoices as OVERDUE", overdue.size());
        for (Invoice invoice : overdue) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            try {
                notificationService.send(
                        invoice.getContract().getTenant().getId(),
                        "Hóa đơn quá hạn",
                        "Hóa đơn tháng " + invoice.getBillingMonth() + " đã quá hạn thanh toán.",
                        NotificationType.INVOICE_OVERDUE,
                        invoice.getId()
                );
            } catch (Exception e) {
                log.warn("Failed to send overdue notification for invoice {}: {}", invoice.getId(), e.getMessage());
            }
        }
        log.info("[ScheduledJob] Marked {} invoices as OVERDUE", overdue.size());
    }

    /**
     * Chạy lúc 8:00 AM hàng ngày.
     * Gửi nhắc nhở cho hóa đơn UNPAID sắp đến hạn trong 3 ngày.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public void sendDueDateReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(3);
        List<Invoice> upcoming = invoiceRepository.findByStatusAndDueDate(InvoiceStatus.UNPAID, reminderDate);
        if (upcoming.isEmpty()) return;

        log.info("[ScheduledJob] Sending due-date reminders for {} invoices", upcoming.size());
        for (Invoice invoice : upcoming) {
            try {
                notificationService.send(
                        invoice.getContract().getTenant().getId(),
                        "Nhắc nhở thanh toán",
                        "Hóa đơn tháng " + invoice.getBillingMonth() + " sẽ đến hạn vào " + invoice.getDueDate() + ". Vui lòng thanh toán đúng hạn.",
                        NotificationType.INVOICE_CREATED,
                        invoice.getId()
                );
            } catch (Exception e) {
                log.warn("Failed to send reminder for invoice {}: {}", invoice.getId(), e.getMessage());
            }
        }
    }
}
