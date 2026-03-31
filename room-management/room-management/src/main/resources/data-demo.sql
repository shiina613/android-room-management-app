-- ============================================================
-- DEMO SEED DATA - Quản lý phòng trọ
-- Chạy sau khi Spring Boot đã tạo bảng (ddl-auto=update)
-- Password cho tất cả account: Password@123
-- BCrypt hash của "Password@123"
-- ============================================================

-- ── USERS ────────────────────────────────────────────────────
INSERT INTO users (username, password, email, full_name, phone, role, enabled, created_at)
VALUES
-- Chủ trọ
('landlord01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'landlord01@demo.com', 'Nguyễn Văn An', '0901234567', 'ROLE_LANDLORD', true, NOW()),
-- Người thuê
('tenant01',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'tenant01@demo.com',   'Trần Thị Bình',  '0912345678', 'ROLE_TENANT',   true, NOW()),
('tenant02',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'tenant02@demo.com',   'Lê Văn Cường',   '0923456789', 'ROLE_TENANT',   true, NOW()),
('tenant03',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'tenant03@demo.com',   'Phạm Thị Dung',  '0934567890', 'ROLE_TENANT',   true, NOW()),
-- Admin
('admin',      '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@demo.com',      'Admin Hệ Thống', '0900000000', 'ROLE_ADMIN',    true, NOW())
ON DUPLICATE KEY UPDATE username = username;

-- ── ROOMS ────────────────────────────────────────────────────
INSERT INTO rooms (title, address, price, elec_price, water_price, service_price, status, category, description, owner_id, created_at)
SELECT
    r.title, r.address, r.price, r.elec_price, r.water_price, r.service_price,
    r.status, r.category, r.description, u.id, NOW()
FROM (
    SELECT 'Phòng 101' title, '15 Nguyễn Trãi, Q1, TP.HCM' address, 3500000 price, 3500 elec_price, 15000 water_price, 200000 service_price, 'OCCUPIED'    status, 'STUDIO'    category, 'Phòng studio đầy đủ nội thất, ban công rộng' description UNION ALL
    SELECT 'Phòng 102', '15 Nguyễn Trãi, Q1, TP.HCM', 3200000, 3500, 15000, 200000, 'AVAILABLE',   'STUDIO',    'Phòng mới sơn, cửa sổ hướng Đông' UNION ALL
    SELECT 'Phòng 201', '15 Nguyễn Trãi, Q1, TP.HCM', 5000000, 3500, 15000, 300000, 'OCCUPIED',    'APARTMENT', 'Căn hộ 1 phòng ngủ, bếp riêng' UNION ALL
    SELECT 'Phòng 202', '15 Nguyễn Trãi, Q1, TP.HCM', 4800000, 3500, 15000, 300000, 'OCCUPIED',    'APARTMENT', 'Căn hộ view đẹp, tầng cao' UNION ALL
    SELECT 'Phòng 301', '15 Nguyễn Trãi, Q1, TP.HCM', 2800000, 3500, 15000, 150000, 'MAINTENANCE', 'SINGLE',    'Đang sửa chữa điện nước' UNION ALL
    SELECT 'Phòng 302', '15 Nguyễn Trãi, Q1, TP.HCM', 2800000, 3500, 15000, 150000, 'AVAILABLE',   'SINGLE',    'Phòng đơn tiện nghi, yên tĩnh'
) r
JOIN users u ON u.username = 'landlord01'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE title = r.title AND owner_id = u.id);

-- ── CONTRACTS ────────────────────────────────────────────────
INSERT INTO contracts (room_id, tenant_id, landlord_id, start_date, end_date, deposit, monthly_rent, status, created_at)
SELECT ro.id, te.id, la.id, c.start_date, c.end_date, c.deposit, c.monthly_rent, c.status, NOW()
FROM (
    SELECT 'Phòng 101' room_title, 'tenant01' tenant_uname, '2026-01-01' start_date, '2027-01-01' end_date, 7000000 deposit, 3500000 monthly_rent, 'ACTIVE' status UNION ALL
    SELECT 'Phòng 201', 'tenant02', '2025-06-01', '2026-06-01', 10000000, 5000000, 'ACTIVE' UNION ALL
    SELECT 'Phòng 202', 'tenant03', '2025-09-01', '2026-09-01',  9600000, 4800000, 'ACTIVE'
) c
JOIN rooms ro ON ro.title = c.room_title
JOIN users te ON te.username = c.tenant_uname
JOIN users la ON la.username = 'landlord01'
WHERE NOT EXISTS (SELECT 1 FROM contracts WHERE room_id = ro.id AND status = 'ACTIVE');

-- ── METER READINGS ───────────────────────────────────────────
INSERT INTO meter_readings (room_id, billing_month, electric_previous, electric_current, water_previous, water_current, recorded_by, created_at)
SELECT ro.id, mr.billing_month, mr.elec_prev, mr.elec_curr, mr.water_prev, mr.water_curr, u.id, NOW()
FROM (
    SELECT 'Phòng 101' room_title, '2026-01' billing_month,   0 elec_prev,  35 elec_curr,  0 water_prev,  6 water_curr UNION ALL
    SELECT 'Phòng 101',            '2026-02',                35,            65,             6,            13 UNION ALL
    SELECT 'Phòng 101',            '2026-03',                65,           100,            13,            20 UNION ALL
    SELECT 'Phòng 201',            '2026-01',                 0,            55,             0,             8 UNION ALL
    SELECT 'Phòng 201',            '2026-02',                55,           110,             8,            16 UNION ALL
    SELECT 'Phòng 201',            '2026-03',               110,           175,            16,            24 UNION ALL
    SELECT 'Phòng 202',            '2026-01',                 0,            45,             0,             7 UNION ALL
    SELECT 'Phòng 202',            '2026-02',                45,            95,             7,            14 UNION ALL
    SELECT 'Phòng 202',            '2026-03',                95,           155,            14,            21
) mr
JOIN rooms ro ON ro.title = mr.room_title
JOIN users u ON u.username = 'landlord01'
WHERE NOT EXISTS (SELECT 1 FROM meter_readings WHERE room_id = ro.id AND billing_month = mr.billing_month);

-- ── INVOICES ─────────────────────────────────────────────────
INSERT INTO invoices (
    contract_id, billing_month, rent_amount, electric_usage, electric_price_snapshot,
    electric_amount, water_usage, water_price_snapshot, water_amount,
    service_amount, total_amount, status, due_date, created_at
)
SELECT
    c.id,
    inv.billing_month,
    inv.rent_amount,
    inv.elec_usage,
    3500,
    inv.elec_usage * 3500,
    inv.water_usage,
    15000,
    inv.water_usage * 15000,
    inv.service_amount,
    inv.rent_amount + (inv.elec_usage * 3500) + (inv.water_usage * 15000) + inv.service_amount,
    inv.status,
    inv.due_date,
    NOW()
FROM (
    -- Phòng 101 / tenant01
    SELECT 'Phòng 101' room_title, 'tenant01' tenant_uname, '2026-01' billing_month, 3500000 rent_amount, 35 elec_usage, 6 water_usage, 200000 service_amount, 'PAID'   status, '2026-02-05' due_date UNION ALL
    SELECT 'Phòng 101', 'tenant01', '2026-02', 3500000, 30, 7, 200000, 'PAID',   '2026-03-05' UNION ALL
    SELECT 'Phòng 101', 'tenant01', '2026-03', 3500000, 35, 7, 200000, 'UNPAID', '2026-04-05' UNION ALL
    -- Phòng 201 / tenant02
    SELECT 'Phòng 201', 'tenant02', '2026-01', 5000000, 55, 8, 300000, 'PAID',   '2026-02-05' UNION ALL
    SELECT 'Phòng 201', 'tenant02', '2026-02', 5000000, 55, 8, 300000, 'PAID',   '2026-03-05' UNION ALL
    SELECT 'Phòng 201', 'tenant02', '2026-03', 5000000, 65, 8, 300000, 'UNPAID', '2026-04-05' UNION ALL
    -- Phòng 202 / tenant03
    SELECT 'Phòng 202', 'tenant03', '2026-01', 4800000, 45, 7, 300000, 'PAID',   '2026-02-05' UNION ALL
    SELECT 'Phòng 202', 'tenant03', '2026-02', 4800000, 50, 7, 300000, 'PAID',   '2026-03-05' UNION ALL
    SELECT 'Phòng 202', 'tenant03', '2026-03', 4800000, 60, 7, 300000, 'UNPAID', '2026-04-05'
) inv
JOIN rooms ro ON ro.title = inv.room_title
JOIN users te ON te.username = inv.tenant_uname
JOIN contracts c ON c.room_id = ro.id AND c.tenant_id = te.id AND c.status = 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM invoices WHERE contract_id = c.id AND billing_month = inv.billing_month);

-- ── PAYMENTS (cho các invoice PAID) ──────────────────────────
INSERT INTO payments (invoice_id, amount, payment_method, paid_at, note, recorded_by, created_at)
SELECT i.id, i.total_amount, 'BANK_TRANSFER', DATE_ADD(i.due_date, INTERVAL -3 DAY), 'Chuyển khoản đúng hạn', u.id, NOW()
FROM invoices i
JOIN contracts c ON c.id = i.contract_id
JOIN users u ON u.username = 'landlord01'
WHERE i.status = 'PAID'
AND NOT EXISTS (SELECT 1 FROM payments WHERE invoice_id = i.id);

-- ── NOTIFICATIONS ────────────────────────────────────────────
INSERT INTO notifications (user_id, title, content, type, is_read, reference_id, created_at)
SELECT u.id, n.title, n.content, n.type, n.is_read, n.ref_id, NOW()
FROM (
    SELECT 'landlord01' uname, 'Hóa đơn tháng 3/2026 đã tạo'    title, 'Phòng 101 - Tổng 4.052.500 ₫ - Hạn 05/04/2026'  content, 'INVOICE_CREATED'      type, false is_read, 3 ref_id UNION ALL
    SELECT 'landlord01',       'Thanh toán xác nhận',             'Phòng 201 đã thanh toán đủ tháng 2/2026',               'PAYMENT_RECEIVED',              true,         2 UNION ALL
    SELECT 'landlord01',       'Hợp đồng sắp hết hạn',           'Hợp đồng Phòng 201 còn 60 ngày nữa hết hạn',            'CONTRACT_EXPIRING',             false,        2 UNION ALL
    SELECT 'tenant01',         'Hóa đơn tháng 3/2026',           'Hóa đơn phòng 101 tháng 3 đã được tạo: 4.052.500 ₫',   'INVOICE_CREATED',               false,        3 UNION ALL
    SELECT 'tenant01',         'Nhắc nhở thanh toán',             'Hóa đơn tháng 3/2026 sẽ đến hạn ngày 05/04/2026',      'INVOICE_OVERDUE',               false,        3 UNION ALL
    SELECT 'tenant02',         'Hóa đơn tháng 3/2026',           'Hóa đơn phòng 201 tháng 3 đã được tạo: 5.832.500 ₫',   'INVOICE_CREATED',               false,        6 UNION ALL
    SELECT 'tenant03',         'Hóa đơn tháng 3/2026',           'Hóa đơn phòng 202 tháng 3 đã được tạo: 5.427.500 ₫',   'INVOICE_CREATED',               false,        9
) n
JOIN users u ON u.username = n.uname
WHERE NOT EXISTS (SELECT 1 FROM notifications WHERE user_id = u.id AND title = n.title);
