# Quản Lý Phòng Trọ

Ứng dụng Android quản lý phòng trọ, gồm backend Spring Boot và app Android.

---

## Chạy Ứng Dụng

### 1. Khởi động Backend

Mở terminal tại thư mục `room-management/room-management/`, chạy:

```bash
./mvnw spring-boot:run
```

> **Windows (CMD/PowerShell):** `mvnw.cmd spring-boot:run`

Backend sẵn sàng tại `http://localhost:8080`

---

### 2. Chạy Android App

Mở Android Studio → mở thư mục `room-management/android/` → nhấn **Run ▶**

> Dùng emulator: `BASE_URL` đã được cấu hình sẵn `http://10.0.2.2:8080/`

---

## Tài Khoản Thử Nghiệm

Mật khẩu đồng nhất: **`123456`**

| Username | Vai trò | Tên |
|----------|---------|-----|
| `admin` | Quản trị viên | Admin Hệ Thống |
| `landlord01` | Chủ trọ | Nguyễn Văn An |
| `tenant01` | Người thuê | Trần Thị Bình — Phòng 101 |
| `tenant02` | Người thuê | Lê Văn Cường — Phòng 201, 501 |
| `tenant03` | Người thuê | Phạm Thị Dung — Phòng 202, 503 |
| `tenant04` | Người thuê | Hoàng Văn Em — Phòng 103 |
| `tenant05` | Người thuê | Ngô Thị Phương — Phòng 303 |
| `tenant06` | Người thuê | Vũ Đình Quang — Phòng 401 |

---

## Luồng Khám Phá Hệ Thống

### Luồng 1 — Quản trị viên (Admin)

1. Mở app → nhập username `admin`, password `123456` → nhấn **Đăng nhập**
2. App chuyển thẳng đến màn hình **"Quản Trị Hệ Thống"** — dòng chào hiện **"Xin chào, Admin Hệ Thống 👋"**
3. Kiểm tra 8 ô thống kê đều có số liệu:
   - Tổng phòng · Đang thuê · Phòng trống · Tổng người thuê
   - HĐ hiệu lực · HĐ chưa TT · Doanh thu · Công nợ
4. Nhấn icon **🔔** (góc phải trên) → xem danh sách thông báo toàn hệ thống → nhấn **Back**
5. Nhấn icon **👤** → màn hình Hồ sơ hiện: Họ tên **Admin Hệ Thống**, username **admin** → nhấn **Back**
6. Nhấn **Đăng xuất** → quay về màn hình Đăng nhập

---

### Luồng 2 — Chủ trọ (Landlord)

#### 2a. Đăng nhập và xem Dashboard

1. Nhập username `landlord01`, password `123456` → nhấn **Đăng nhập**
2. App vào **Dashboard Chủ trọ** — dòng chào hiện **"Xin chào, Nguyễn Văn An 👋"**
3. Cuộn xuống phần **"Hoạt động gần đây"** — mỗi item có tiêu đề, nội dung, thời gian (VD: "2 ngày trước")

#### 2b. Quản lý hồ sơ cá nhân

4. Nhấn icon **👤** → Hồ sơ hiện: Email `landlord01@demo.com`, SĐT `0901234567`
5. Sửa **Họ tên** thành `Nguyễn Văn An (Test)` → nhấn **Lưu thay đổi** → hiện thông báo "Cập nhật thành công"
6. Sửa lại về `Nguyễn Văn An` → nhấn **Lưu thay đổi** để khôi phục
7. Nhấn **Đổi mật khẩu** → dialog hiện ra:
   - Nhập mật khẩu mới chỉ 4 ký tự → nút **Xác nhận** bị vô hiệu hoá (validation hoạt động)
   - Nhập mật khẩu mới đủ 6 ký tự trở lên → nút **Xác nhận** được kích hoạt → nhấn xác nhận
8. Nhấn **Back** → quay lại Dashboard

#### 2c. Tạo hóa đơn mới

9. Nhấn **Hóa đơn** (bottom nav hoặc shortcut) → nhấn **FAB +**
10. Dropdown **Hợp đồng** → chọn **"Phòng 101 — Trần Thị Bình"**
    - Hiện InfoRow: Phòng 101 · Trần Thị Bình · 3.500.000 ₫/tháng
11. Nhập **Kỳ tính tiền**: `2026-06` → sau ~1 giây, form nhập chỉ số xuất hiện
12. Thử nhập chỉ số điện sai (đầu kỳ lớn hơn cuối kỳ):
    - **Điện đầu kỳ**: `105` · **Điện cuối kỳ**: `100`
    - Hiện "Tiêu thụ: **-5 kWh**" màu đỏ → nút **Tạo hóa đơn** bị vô hiệu hoá
13. Sửa lại đúng:
    - **Điện cuối kỳ**: `140` → hiện "Tiêu thụ: **35 kWh**" màu xanh
    - **Nước đầu kỳ**: `20` · **Nước cuối kỳ**: `27` → hiện "Tiêu thụ: **7 m³**" màu xanh
14. Nhấn **Tạo hóa đơn** → quay về danh sách, hóa đơn tháng **2026-06** xuất hiện

#### 2d. Ghi nhận thanh toán

15. Trong danh sách hóa đơn, tìm hóa đơn **Phòng 101 tháng 2026-03** (trạng thái **UNPAID**) → nhấn vào
16. Nhấn **Ghi nhận thanh toán** → nhập số tiền bằng tổng hóa đơn → chọn phương thức **BANK_TRANSFER** → xác nhận
17. Hóa đơn chuyển sang trạng thái **PAID**

#### 2e. Tạo hóa đơn từ màn hình Chỉ số điện nước

18. Nhấn **Phòng** (bottom nav) → tìm **Phòng 303** → nhấn icon **⏱** (đồng hồ)
19. Sửa kỳ tính thành `2026-04` (kỳ chưa có dữ liệu)
20. Nhập **Điện đầu**: `30` · **Điện cuối**: `55` · **Nước đầu**: `5` · **Nước cuối**: `10`
21. Nhấn **Lưu chỉ số** → dialog **"Tạo hóa đơn ngay?"** hiện ra
22. Nhấn **Tạo hóa đơn** trong dialog → InvoiceFormScreen mở sẵn Phòng 303, chỉ số đã điền
23. Nhấn **Tạo hóa đơn** → tạo thành công, về danh sách hóa đơn

---

### Luồng 3 — Người thuê (Tenant)

#### 3a. Đăng nhập và xem trang chủ

1. Đăng xuất khỏi tài khoản hiện tại (nếu có)
2. Nhập username `tenant01`, password `123456` → nhấn **Đăng nhập**
3. App vào **TenantHomeScreen** — dòng chào hiện **"Xin chào, Trần Thị Bình 👋"**

#### 3b. Xem hợp đồng

4. Nhấn tab **Hợp đồng** (bottom nav)
5. Màn hình hiện thông tin hợp đồng Phòng 101
6. Kiểm tra dòng **"Chủ trọ"** → phải hiện tên **"Nguyễn Văn An"** (không phải số ID)

#### 3c. Quản lý hồ sơ

7. Nhấn icon **👤** → Hồ sơ hiện: Họ tên **Trần Thị Bình**, Email `tenant01@demo.com`, SĐT `0912345678`
8. Sửa **Số điện thoại** thành `0999999999` → nhấn **Lưu thay đổi** → hiện "Cập nhật thành công"
9. Sửa lại về `0912345678` → **Lưu thay đổi** để khôi phục → nhấn **Back**

#### 3d. Kiểm tra thông báo

10. Nhìn icon **🔔** ở góc phải trên — có **badge đỏ** với số (tenant01 có 2 thông báo chưa đọc)
11. Nhấn icon 🔔 → xem danh sách thông báo → nhấn **Back**

---

### Luồng 4 — Thông báo Real-time (WebSocket)

> **Yêu cầu:** 2 emulator đang chạy cùng lúc (hoặc 1 emulator + 1 thiết bị thật cùng mạng)

1. **Emulator A:** Đăng nhập `tenant01` / `123456` → để ở TenantHomeScreen
2. **Emulator B:** Đăng nhập `landlord01` / `123456`

**Test tạo hóa đơn:**

3. Trên **Emulator B (Landlord):** tạo hóa đơn tháng `2026-07` cho Phòng 101
   - Hóa đơn → **+** → chọn Phòng 101 → nhập kỳ `2026-07` → nhập chỉ số → **Tạo hóa đơn**
4. Trên **Emulator A (tenant01):** banner thông báo xuất hiện ở đầu màn hình (~4 giây) và badge số tăng lên
5. Nhấn vào banner → banner biến mất ngay lập tức

**Test ghi nhận thanh toán:**

6. Trên **Emulator B (Landlord):** ghi nhận thanh toán hóa đơn vừa tạo (Phòng 101 tháng 2026-07)
7. Trên **Emulator A (tenant01):** nhận thêm 1 banner thông báo xác nhận thanh toán

---

### Luồng 5 — Kiểm tra Xử lý Lỗi

| # | Thao tác | Kết quả mong đợi |
|---|----------|-----------------|
| 1 | Tạo hóa đơn Phòng 101 tháng `2026-03` (đã tồn tại) | Hiện lỗi từ server: "Invoice for contract … already exists" |
| 2 | Nhập chỉ số điện cuối kỳ < đầu kỳ | Hiện "Tiêu thụ: -X kWh" màu đỏ, nút Tạo bị vô hiệu hoá |
| 3 | Tắt WiFi/mạng emulator → pull-to-refresh | Hiện thông báo lỗi kết nối, app không crash |
| 4 | Bật mạng lại → pull-to-refresh | Dữ liệu tải lại bình thường |
| 5 | Đổi mật khẩu mới chỉ 4 ký tự | Nút **Xác nhận** bị vô hiệu hoá |
| 6 | Profile → Đăng xuất → đăng nhập lại `landlord01` | Vào đúng màn hình Landlord Dashboard |

---

## Cấu Trúc Dự Án

```
android-room-management-app/
├── room-management/
│   ├── android/          ← Android app (mở bằng Android Studio)
│   └── room-management/  ← Spring Boot backend
├── test.md               ← Kịch bản kiểm thử chi tiết
└── README.md
```
