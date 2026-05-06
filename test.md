# Hướng dẫn kiểm thử thủ công

## Tài khoản có sẵn (mật khẩu đều là: `123456`)

| Username | Role | Tên |
|----------|------|-----|
| `landlord01` | Chủ trọ | Nguyễn Văn An |
| `tenant01` | Người thuê | Trần Thị Bình — Phòng 101 |
| `tenant02` | Người thuê | Lê Văn Cường — Phòng 201 & 501 |
| `tenant03` | Người thuê | Phạm Thị Dung — Phòng 202 & 503 |
| `tenant04` | Người thuê | Hoàng Văn Em — Phòng 103 |
| `tenant05` | Người thuê | Ngô Thị Phương — Phòng 303 |
| `tenant06` | Người thuê | Vũ Đình Quang — Phòng 401 |
| `admin` | Quản trị viên | Admin Hệ Thống |

---

## PHẦN 1 — Kiểm thử Admin Dashboard

### Bước 1: Đăng nhập Admin
1. Mở app
2. Nhập username: `admin` / password: `123456` → nhấn **Đăng nhập**
3. ✅ App phải chuyển thẳng đến màn hình **"Quản Trị Hệ Thống"** (không vào màn hình Chủ trọ hay Người thuê)

### Bước 2: Kiểm tra màn hình Admin
4. Kiểm tra dòng chào: phải hiện **"Xin chào, Admin Hệ Thống 👋"**
5. Kiểm tra 8 ô thống kê có hiển thị số liệu (không trống, không lỗi):
   - Tổng phòng, Đang thuê, Phòng trống, Tổng người thuê
   - HĐ hiệu lực, HĐ chưa TT, Doanh thu, Công nợ
6. ✅ Nhấn icon **chuông** (góc phải trên) → mở màn hình Thông báo
7. Nhấn **Back** → quay lại Admin Dashboard
8. ✅ Nhấn icon **người** (góc phải trên) → mở màn hình Hồ sơ cá nhân

### Bước 3: Kiểm tra Hồ sơ từ Admin
9. Màn hình Hồ sơ phải hiện: Họ tên = **Admin Hệ Thống**, username = **admin**
10. Nhấn **Back** → quay lại Admin Dashboard
11. Nhấn **Đăng xuất** → quay về màn hình Đăng nhập

---

## PHẦN 2 — Kiểm thử Chủ trọ (Landlord)

### Bước 4: Đăng nhập Landlord
1. Nhập username: `landlord01` / password: `123456` → nhấn **Đăng nhập**
2. ✅ App phải vào màn hình **Dashboard Chủ trọ** (tiêu đề "Quản Lý Phòng Trọ")
3. Kiểm tra dòng chào: **"Xin chào, Nguyễn Văn An 👋"**

### Bước 5: Kiểm tra Dashboard — Hoạt động gần đây
4. Cuộn xuống phần dưới của Dashboard
5. ✅ Phải thấy section **"Hoạt động gần đây"** với các thông báo thực tế (VD: "Hóa đơn tháng 3/2026 đã tạo", "Thanh toán xác nhận"...)
6. Mỗi item phải có: tiêu đề, nội dung (tối đa 2 dòng), thời gian (VD: "X ngày trước")

### Bước 6: Kiểm tra Hồ sơ Landlord
7. Nhấn icon **người** (góc phải trên Dashboard)
8. ✅ Màn hình Hồ sơ hiện: Họ tên = **Nguyễn Văn An**, Email = **landlord01@demo.com**, SĐT = **0901234567**
9. Sửa **Họ tên** thành "Nguyễn Văn An (Test)" → nhấn **Lưu thay đổi**
10. ✅ Hiện thông báo "Cập nhật thành công", tên mới xuất hiện
11. Sửa lại về "Nguyễn Văn An" → nhấn **Lưu thay đổi** (khôi phục)
12. Nhấn **Đổi mật khẩu** → dialog hiện ra
13. Nhập mật khẩu hiện tại: `123456` / mật khẩu mới: `1234` (4 ký tự)
14. ✅ Nút **Xác nhận** phải bị disable (không nhấn được)
15. Sửa mật khẩu mới thành `123456` (6 ký tự) → nhấn **Xác nhận**
16. ✅ Dialog đóng, không có lỗi
17. Nhấn **Back** → quay lại Dashboard

### Bước 7: Tạo hóa đơn tháng 2026-06 (tháng chưa có chỉ số)
18. Nhấn **Hóa đơn** (shortcut hoặc bottom nav)
19. Nhấn FAB **+** → mở màn hình **Tạo hóa đơn**
20. Nhấn dropdown **Hợp đồng** → chọn **"Phòng 101 — Trần Thị Bình"**
21. ✅ Hiện InfoRow: Phòng 101, Trần Thị Bình, 3.500.000 ₫/tháng
22. Nhập **Kỳ tính tiền**: `2026-06`
23. ✅ Sau ~1 giây, hiện form nhập chỉ số (vì tháng 2026-06 chưa có chỉ số)
24. Nhập **Điện đầu kỳ**: `105` / **Điện cuối kỳ**: `100`
25. ✅ Hiện "Tiêu thụ: -5 kWh" màu **đỏ**, nút "Tạo hóa đơn" bị disable
26. Sửa **Điện cuối kỳ**: `140`
27. ✅ Hiện "Tiêu thụ: 35 kWh" màu **xanh**
28. Nhập **Nước đầu kỳ**: `20` / **Nước cuối kỳ**: `27`
29. ✅ Hiện "Tiêu thụ: 7 m³" màu xanh, nút "Tạo hóa đơn" được enable
30. Nhấn **Tạo hóa đơn**
31. ✅ Loading → quay về InvoiceListScreen, hóa đơn tháng **2026-06** xuất hiện trong danh sách

### Bước 8: Tạo hóa đơn từ màn hình Chỉ số điện nước
32. Nhấn **Phòng** (bottom nav hoặc shortcut)
33. Tìm **Phòng 303** → nhấn icon đồng hồ (⏱) để vào màn hình nhập chỉ số
34. Sửa kỳ tính thành `2026-04` (kỳ chưa có dữ liệu cho Phòng 303)
35. Nhập **Điện đầu**: để nguyên giá trị tự điền (`30`) / **Điện cuối**: `55`
36. Nhập **Nước đầu**: để nguyên giá trị tự điền (`5`) / **Nước cuối**: `10`
37. Nhấn **Lưu chỉ số**
38. ✅ Hiện dialog **"Tạo hóa đơn ngay?"**
39. Nhấn **Tạo hóa đơn** trong dialog
40. ✅ Mở InvoiceFormScreen với Phòng 303 và chỉ số đã điền sẵn (read-only)
41. Nhấn **Tạo hóa đơn**
42. ✅ Tạo thành công → về InvoiceListScreen

### Bước 9: Ghi nhận thanh toán
43. Trong InvoiceListScreen, tìm hóa đơn **Phòng 101 tháng 2026-03** (UNPAID)
44. Nhấn vào hóa đơn → xem chi tiết
45. Nhấn nút **Ghi nhận thanh toán** (hoặc tương đương)
46. Nhập số tiền = tổng hóa đơn → chọn phương thức **BANK_TRANSFER** → xác nhận
47. ✅ Hóa đơn chuyển sang trạng thái **PAID**

---

## PHẦN 3 — Kiểm thử Người thuê (Tenant)

### Bước 10: Đăng nhập Tenant
1. Đăng xuất khỏi Landlord (nếu đang đăng nhập)
2. Nhập username: `tenant01` / password: `123456` → nhấn **Đăng nhập**
3. ✅ App vào màn hình **TenantHomeScreen** (tiêu đề "Phòng Trọ")
4. Kiểm tra dòng chào: **"Xin chào, Trần Thị Bình 👋"**

### Bước 11: Kiểm tra hợp đồng — Tên chủ trọ
5. Nhấn tab **Hợp đồng** (bottom nav)
6. ✅ Màn hình hiện thông tin hợp đồng Phòng 101
7. Tìm dòng **"Chủ trọ"**
8. ✅ Phải hiện **"Nguyễn Văn An"** (tên thật), KHÔNG phải số ID như "1" hay "Chủ trọ #1"

### Bước 12: Kiểm tra Hồ sơ Tenant
9. Nhấn icon **người** (góc phải trên)
10. ✅ Hiện: Họ tên = **Trần Thị Bình**, Email = **tenant01@demo.com**, SĐT = **0912345678**
11. Sửa **Số điện thoại** thành `0999999999` → nhấn **Lưu thay đổi**
12. ✅ Hiện "Cập nhật thành công"
13. Sửa lại về `0912345678` → **Lưu thay đổi** (khôi phục)
14. Nhấn **Back**

### Bước 13: Kiểm tra thông báo badge
15. Nhìn icon **chuông** ở góc phải trên TenantHomeScreen
16. ✅ Phải có badge đỏ với số (tenant01 có 2 thông báo chưa đọc: id 8 và 9)
17. Nhấn icon chuông → xem danh sách thông báo
18. Nhấn **Back**

---

## PHẦN 4 — Kiểm thử WebSocket (Real-time)

> **Yêu cầu:** Cần 2 thiết bị/emulator hoặc mở app trên 1 thiết bị và dùng Postman trên máy tính

### Bước 14: Chuẩn bị
1. Đăng nhập `tenant01` trên emulator, để app ở màn hình chính (TenantHomeScreen)
2. Trên máy tính, đăng nhập `landlord01` (hoặc dùng Postman)

### Bước 15: Test nhận thông báo khi tạo hóa đơn
3. Landlord tạo hóa đơn tháng `2026-07` cho Phòng 101 (tenant01)
   - Qua app Landlord: InvoiceListScreen → FAB → chọn Phòng 101 → tháng 2026-07 → nhập chỉ số → Tạo hóa đơn
4. ✅ Trên emulator của tenant01: xuất hiện **banner thông báo** ở đầu màn hình trong ~4 giây
5. ✅ Badge số trên icon chuông **tăng lên**
6. Nhấn vào banner → banner biến mất ngay

### Bước 16: Test nhận thông báo khi ghi nhận thanh toán
7. Landlord ghi nhận thanh toán cho hóa đơn vừa tạo (Phòng 101 tháng 2026-07)
8. ✅ tenant01 nhận banner thông báo thanh toán

---

## PHẦN 5 — Kiểm thử lỗi và edge case

### Bước 17: Tạo hóa đơn trùng tháng
1. Đăng nhập `landlord01`
2. InvoiceListScreen → FAB → chọn **Phòng 101** → nhập tháng `2026-03` (đã có hóa đơn)
3. ✅ Hiện chỉ số điện nước read-only (tháng 2026-03 đã có chỉ số)
4. Nhấn **Tạo hóa đơn**
5. ✅ Hiện thông báo lỗi từ server: "Invoice for contract ... already exists"

### Bước 18: Tắt mạng
6. Tắt WiFi/mạng của emulator
7. Vào Dashboard → pull-to-refresh
8. ✅ Hiện thông báo lỗi kết nối, app không crash
9. Bật mạng lại → pull-to-refresh
10. ✅ Dữ liệu load lại bình thường

### Bước 19: Đăng xuất và đăng nhập lại
11. Nhấn Profile → **Đăng xuất**
12. ✅ Quay về màn hình Đăng nhập
13. Đăng nhập lại `landlord01`
14. ✅ Vào đúng màn hình Landlord Dashboard

---

## Tóm tắt kết quả

| Phần | Nội dung | Pass | Fail | Ghi chú |
|------|----------|------|------|---------|
| 1 | Admin Dashboard | | | |
| 2 | Landlord — Dashboard, Hồ sơ, Tạo hóa đơn | | | |
| 3 | Tenant — Hợp đồng, Hồ sơ, Badge | | | |
| 4 | WebSocket real-time | | | |
| 5 | Edge case (lỗi, trùng tháng) | | | |
