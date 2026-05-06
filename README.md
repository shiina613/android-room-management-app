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

### Luồng 1 — Quản trị viên

1. Đăng nhập `admin` / `123456`
2. Xem tổng quan hệ thống: tổng phòng, đang thuê, phòng trống, doanh thu, công nợ
3. Nhấn icon 🔔 xem thông báo toàn hệ thống
4. Nhấn icon 👤 xem hồ sơ cá nhân
5. Nhấn **Đăng xuất**

---

### Luồng 2 — Chủ trọ quản lý phòng và hóa đơn

1. Đăng nhập `landlord01` / `123456`
2. Xem Dashboard: thống kê phòng, hóa đơn, hoạt động gần đây
3. **Xem danh sách phòng** → nhấn vào từng phòng để xem chi tiết, hợp đồng, chỉ số điện nước
4. **Tạo hóa đơn mới:**
   - Vào **Hóa đơn** → nhấn **+**
   - Chọn hợp đồng (ví dụ: Phòng 101)
   - Nhập kỳ tính `2026-06` → nhập chỉ số điện nước → nhấn **Tạo hóa đơn**
5. **Ghi nhận thanh toán:**
   - Trong danh sách hóa đơn, chọn hóa đơn trạng thái **UNPAID**
   - Nhấn **Ghi nhận thanh toán** → nhập số tiền → chọn phương thức → xác nhận
   - Hóa đơn chuyển sang **PAID**
6. Nhấn icon 👤 → sửa thông tin hồ sơ → **Lưu thay đổi**

---

### Luồng 3 — Người thuê xem phòng và hóa đơn

1. Đăng nhập `tenant01` / `123456`
2. Xem trang chủ: thông tin phòng đang thuê, trạng thái hóa đơn mới nhất
3. Tab **Hợp đồng**: xem chi tiết hợp đồng, tên chủ trọ, ngày thuê, giá phòng
4. Tab **Hóa đơn**: xem lịch sử hóa đơn và trạng thái thanh toán
5. Nhấn icon 🔔 → xem thông báo (badge đỏ chỉ số chưa đọc)
6. Nhấn icon 👤 → xem và sửa thông tin cá nhân

---

### Luồng 4 — Real-time (cần 2 thiết bị hoặc 2 emulator)

1. Đăng nhập `tenant01` trên emulator A — để ở trang chủ
2. Đăng nhập `landlord01` trên emulator B
3. Landlord tạo hóa đơn cho Phòng 101 → **tenant01 nhận banner thông báo ngay lập tức**
4. Landlord ghi nhận thanh toán → **tenant01 nhận thêm 1 banner**

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
