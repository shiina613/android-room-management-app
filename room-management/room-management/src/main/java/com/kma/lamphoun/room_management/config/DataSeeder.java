package com.kma.lamphoun.room_management.config;

import com.kma.lamphoun.room_management.common.enums.*;
import com.kma.lamphoun.room_management.entity.*;
import com.kma.lamphoun.room_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds demo data on startup if DB is empty.
 *
 * Demo accounts (password: Password@123):
 *   landlord01  — Chủ trọ
 *   tenant01    — Người thuê Phòng 101
 *   tenant02    — Người thuê Phòng 201
 *   tenant03    — Người thuê Phòng 202
 *   admin       — Admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository         userRepository;
    private final RoomRepository         roomRepository;
    private final ContractRepository     contractRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final InvoiceRepository      invoiceRepository;
    private final PaymentRepository      paymentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder        passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already has data — skipping seed.");
            return;
        }
        log.info("Seeding demo data...");
        seed();
        log.info("==============================================");
        log.info("  DEMO ACCOUNTS  (password: Password@123)");
        log.info("  Chủ trọ  : landlord01");
        log.info("  Người thuê: tenant01 / tenant02 / tenant03");
        log.info("  Admin    : admin");
        log.info("==============================================");
    }

    private void seed() {
        String pw = passwordEncoder.encode("Password@123");

        // ── Users ──────────────────────────────────────────────
        User landlord = user("landlord01", pw, "landlord01@demo.com", "Nguyễn Văn An",  "0901234567", Role.ROLE_LANDLORD);
        User t1       = user("tenant01",   pw, "tenant01@demo.com",   "Trần Thị Bình",  "0912345678", Role.ROLE_TENANT);
        User t2       = user("tenant02",   pw, "tenant02@demo.com",   "Lê Văn Cường",   "0923456789", Role.ROLE_TENANT);
        User t3       = user("tenant03",   pw, "tenant03@demo.com",   "Phạm Thị Dung",  "0934567890", Role.ROLE_TENANT);
        user("admin",      pw, "admin@demo.com",      "Admin Hệ Thống", "0900000000", Role.ROLE_ADMIN);

        // ── Rooms ──────────────────────────────────────────────
        Room r101 = room("Phòng 101", "15 Nguyễn Trãi, Q1, TP.HCM", 3_500_000, 3_500, 15_000, 200_000, RoomStatus.OCCUPIED,    RoomCategory.STUDIO,    "Phòng studio đầy đủ nội thất, ban công rộng", landlord);
        Room r102 = room("Phòng 102", "15 Nguyễn Trãi, Q1, TP.HCM", 3_200_000, 3_500, 15_000, 200_000, RoomStatus.AVAILABLE,   RoomCategory.STUDIO,    "Phòng mới sơn, cửa sổ hướng Đông", landlord);
        Room r201 = room("Phòng 201", "15 Nguyễn Trãi, Q1, TP.HCM", 5_000_000, 3_500, 15_000, 300_000, RoomStatus.OCCUPIED,    RoomCategory.APARTMENT, "Căn hộ 1 phòng ngủ, bếp riêng", landlord);
        Room r202 = room("Phòng 202", "15 Nguyễn Trãi, Q1, TP.HCM", 4_800_000, 3_500, 15_000, 300_000, RoomStatus.OCCUPIED,    RoomCategory.APARTMENT, "Căn hộ view đẹp, tầng cao", landlord);
        room("Phòng 301",  "15 Nguyễn Trãi, Q1, TP.HCM", 2_800_000, 3_500, 15_000, 150_000, RoomStatus.MAINTENANCE, RoomCategory.SINGLE,    "Đang sửa chữa điện nước", landlord);
        room("Phòng 302",  "15 Nguyễn Trãi, Q1, TP.HCM", 2_800_000, 3_500, 15_000, 150_000, RoomStatus.AVAILABLE,   RoomCategory.SINGLE,    "Phòng đơn tiện nghi, yên tĩnh", landlord);

        // ── Contracts ──────────────────────────────────────────
        Contract c101 = contract(r101, t1, landlord, LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),  7_000_000, 3_500_000);
        Contract c201 = contract(r201, t2, landlord, LocalDate.of(2025, 6, 1), LocalDate.of(2026, 6, 1), 10_000_000, 5_000_000);
        Contract c202 = contract(r202, t3, landlord, LocalDate.of(2025, 9, 1), LocalDate.of(2026, 9, 1),  9_600_000, 4_800_000);

        // ── Meter Readings ─────────────────────────────────────
        MeterReading mr101_01 = meter(r101, "2026-01",   0,  35,  0,  6, landlord);
        MeterReading mr101_02 = meter(r101, "2026-02",  35,  65,  6, 13, landlord);
        MeterReading mr101_03 = meter(r101, "2026-03",  65, 100, 13, 20, landlord);
        MeterReading mr201_01 = meter(r201, "2026-01",   0,  55,  0,  8, landlord);
        MeterReading mr201_02 = meter(r201, "2026-02",  55, 110,  8, 16, landlord);
        MeterReading mr201_03 = meter(r201, "2026-03", 110, 175, 16, 24, landlord);
        MeterReading mr202_01 = meter(r202, "2026-01",   0,  45,  0,  7, landlord);
        MeterReading mr202_02 = meter(r202, "2026-02",  45,  95,  7, 14, landlord);
        MeterReading mr202_03 = meter(r202, "2026-03",  95, 155, 14, 21, landlord);

        // ── Invoices ───────────────────────────────────────────
        Invoice inv101_01 = invoice(c101, mr101_01, "2026-01", InvoiceStatus.PAID,   LocalDate.of(2026, 2, 5));
        Invoice inv101_02 = invoice(c101, mr101_02, "2026-02", InvoiceStatus.PAID,   LocalDate.of(2026, 3, 5));
        Invoice inv101_03 = invoice(c101, mr101_03, "2026-03", InvoiceStatus.UNPAID, LocalDate.of(2026, 4, 5));
        Invoice inv201_01 = invoice(c201, mr201_01, "2026-01", InvoiceStatus.PAID,   LocalDate.of(2026, 2, 5));
        Invoice inv201_02 = invoice(c201, mr201_02, "2026-02", InvoiceStatus.PAID,   LocalDate.of(2026, 3, 5));
        Invoice inv201_03 = invoice(c201, mr201_03, "2026-03", InvoiceStatus.UNPAID, LocalDate.of(2026, 4, 5));
        Invoice inv202_01 = invoice(c202, mr202_01, "2026-01", InvoiceStatus.PAID,   LocalDate.of(2026, 2, 5));
        Invoice inv202_02 = invoice(c202, mr202_02, "2026-02", InvoiceStatus.PAID,   LocalDate.of(2026, 3, 5));
        Invoice inv202_03 = invoice(c202, mr202_03, "2026-03", InvoiceStatus.UNPAID, LocalDate.of(2026, 4, 5));

        // ── Payments (PAID invoices) ───────────────────────────
        List.of(inv101_01, inv101_02, inv201_01, inv201_02, inv202_01, inv202_02)
            .forEach(inv -> payment(inv, landlord));

        // ── Notifications ──────────────────────────────────────
        notif(landlord, "Hóa đơn tháng 3/2026 đã tạo",  "Phòng 101 - Tổng 4.052.500 ₫ - Hạn 05/04/2026",  NotificationType.INVOICE_CREATED,   false, inv101_03.getId());
        notif(landlord, "Thanh toán xác nhận",           "Phòng 201 đã thanh toán đủ tháng 2/2026",         NotificationType.PAYMENT_RECEIVED,  true,  inv201_02.getId());
        notif(landlord, "Hợp đồng sắp hết hạn",         "Hợp đồng Phòng 201 còn 60 ngày nữa hết hạn",      NotificationType.CONTRACT_EXPIRING, false, c201.getId());
        notif(t1,       "Hóa đơn tháng 3/2026",         "Hóa đơn phòng 101 tháng 3: 4.052.500 ₫",          NotificationType.INVOICE_CREATED,   false, inv101_03.getId());
        notif(t1,       "Nhắc nhở thanh toán",           "Hóa đơn tháng 3/2026 đến hạn ngày 05/04/2026",    NotificationType.INVOICE_OVERDUE,   false, inv101_03.getId());
        notif(t2,       "Hóa đơn tháng 3/2026",         "Hóa đơn phòng 201 tháng 3: 5.832.500 ₫",          NotificationType.INVOICE_CREATED,   false, inv201_03.getId());
        notif(t3,       "Hóa đơn tháng 3/2026",         "Hóa đơn phòng 202 tháng 3: 5.427.500 ₫",          NotificationType.INVOICE_CREATED,   false, inv202_03.getId());
    }

    // ── Helper builders ────────────────────────────────────────

    private User user(String username, String pw, String email, String fullName, String phone, Role role) {
        User u = new User();
        u.setUsername(username); u.setPassword(pw); u.setEmail(email);
        u.setFullName(fullName); u.setPhone(phone); u.setRole(role); u.setEnabled(true);
        return userRepository.save(u);
    }

    private Room room(String title, String address, long price, long elec, long water, long service,
                      RoomStatus status, RoomCategory category, String desc, User owner) {
        Room r = new Room();
        r.setTitle(title); r.setAddress(address);
        r.setPrice(BigDecimal.valueOf(price));
        r.setElecPrice(BigDecimal.valueOf(elec));
        r.setWaterPrice(BigDecimal.valueOf(water));
        r.setServicePrice(BigDecimal.valueOf(service));
        r.setStatus(status); r.setCategory(category);
        r.setDescription(desc); r.setOwner(owner);
        return roomRepository.save(r);
    }

    private Contract contract(Room room, User tenant, User landlord,
                               LocalDate start, LocalDate end, long deposit, long rent) {
        Contract c = new Contract();
        c.setRoom(room); c.setTenant(tenant); c.setLandlord(landlord);
        c.setStartDate(start); c.setEndDate(end);
        c.setDeposit(BigDecimal.valueOf(deposit));
        c.setMonthlyRent(BigDecimal.valueOf(rent));
        c.setStatus(ContractStatus.ACTIVE);
        return contractRepository.save(c);
    }

    private MeterReading meter(Room room, String month,
                                double ePrev, double eCurr,
                                double wPrev, double wCurr, User recordedBy) {
        MeterReading m = new MeterReading();
        m.setRoom(room); m.setBillingMonth(month);
        m.setElectricPrevious(ePrev); m.setElectricCurrent(eCurr);
        m.setWaterPrevious(wPrev);    m.setWaterCurrent(wCurr);
        m.setRecordedBy(recordedBy);
        return meterReadingRepository.save(m);
    }

    private Invoice invoice(Contract contract, MeterReading mr, String month,
                             InvoiceStatus status, LocalDate dueDate) {
        Room room = contract.getRoom();
        BigDecimal elecPrice  = room.getElecPrice();
        BigDecimal waterPrice = room.getWaterPrice();
        double elecUsage  = mr.getElectricCurrent() - mr.getElectricPrevious();
        double waterUsage = mr.getWaterCurrent()    - mr.getWaterPrevious();

        BigDecimal elecAmt    = elecPrice.multiply(BigDecimal.valueOf(elecUsage));
        BigDecimal waterAmt   = waterPrice.multiply(BigDecimal.valueOf(waterUsage));
        BigDecimal total      = contract.getMonthlyRent()
                                    .add(elecAmt)
                                    .add(waterAmt)
                                    .add(room.getServicePrice());

        Invoice inv = new Invoice();
        inv.setContract(contract);
        inv.setMeterReading(mr);
        inv.setBillingMonth(month);
        inv.setRentAmount(contract.getMonthlyRent());
        inv.setElectricUsage(elecUsage);
        inv.setElectricPrice(elecPrice);
        inv.setElectricAmount(elecAmt);
        inv.setWaterUsage(waterUsage);
        inv.setWaterPrice(waterPrice);
        inv.setWaterAmount(waterAmt);
        inv.setServiceAmount(room.getServicePrice());
        inv.setTotalAmount(total);
        inv.setStatus(status);
        inv.setDueDate(dueDate);
        return invoiceRepository.save(inv);
    }

    private void payment(Invoice inv, User landlord) {
        Payment p = new Payment();
        p.setInvoice(inv);
        p.setAmount(inv.getTotalAmount());
        p.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
        p.setPaidAt(inv.getDueDate().minusDays(3));   // LocalDate
        p.setNote("Chuyển khoản đúng hạn");
        p.setRecordedBy(landlord);
        paymentRepository.save(p);

        inv.setStatus(InvoiceStatus.PAID);
        inv.setPaidAt(inv.getDueDate().minusDays(3)); // LocalDate
        invoiceRepository.save(inv);
    }

    private void notif(User user, String title, String content,
                        NotificationType type, boolean read, Long refId) {
        Notification n = new Notification();
        n.setUser(user); n.setTitle(title); n.setContent(content);
        n.setType(type); n.setRead(read); n.setReferenceId(refId);
        notificationRepository.save(n);
    }
}
