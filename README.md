# Quản Lý Phòng Trọ — Hướng Dẫn Chạy Local

## Yêu Cầu

| Công cụ | Phiên bản |
|---------|-----------|
| Java | 17+ |
| Maven Wrapper | có sẵn (`mvnw`) |
| MySQL | 8.0+ |
| Android Studio | Hedgehog 2023.1+ |
| Android SDK | API 26+ |

---

## 1. Chuẩn Bị Database (MySQL)

Mở MySQL client và chạy:

```sql
CREATE DATABASE room_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Mặc định dùng user `root` / password `123456`. Nếu khác, sửa trong file:

```
android-room-management-app/room-management/room-management/src/main/resources/application.properties
```

```properties
spring.datasource.username=root
spring.datasource.password=123456
```

---

## 2. Chạy Backend (Spring Boot)

Mở terminal tại thư mục backend:

```bash
cd android-room-management-app/room-management/room-management
```

Chạy server:

```bash
./mvnw spring-boot:run
```

Trên Windows nếu dùng CMD:

```cmd
mvnw.cmd spring-boot:run
```

Server khởi động tại: `http://localhost:8080`

Kiểm tra hoạt động:

```bash
curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}"

curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d "{\"username\":\"landlord01\",\"password\":\"123456\"}"
```

> JPA sẽ tự tạo bảng (`ddl-auto=update`) khi khởi động lần đầu. Dữ liệu mẫu được seed tự động qua `DataSeeder.java`.

---

## 3. Lấy IP Máy Tính (để Android kết nối)

Android emulator/device cần biết IP thực của máy chạy backend.

**Windows:**

```cmd
ipconfig
```

Tìm dòng `IPv4 Address` trong adapter đang dùng (WiFi hoặc Ethernet), ví dụ: `192.168.1.100`

**macOS/Linux:**

```bash
ifconfig | grep "inet "
```

---

## 4. Cấu Hình Android — Cập Nhật BASE_URL

Mở file:

```
android-room-management-app/room-management/android/app/build.gradle.kts
```

Sửa dòng `BASE_URL` thành IP máy tính của bạn:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://<IP_CUA_BAN>:8080/\"")
```

Ví dụ:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.1.100:8080/\"")
```

> Nếu chạy trên **emulator Android Studio**, dùng `10.0.2.2` thay cho `localhost`:
> ```kotlin
> buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
> ```

---

## 5. Chạy Android App

1. Mở Android Studio
2. Chọn **Open** → trỏ đến thư mục:
   ```
   android-room-management-app/room-management/android
   ```
3. Đợi Gradle sync xong
4. Chọn device (emulator hoặc thiết bị thật)
5. Nhấn **Run** (▶)

---

## 6. Tài Khoản Mặc Định (Seed Data)

Sau khi backend khởi động lần đầu, `DataSeeder` tạo sẵn các tài khoản:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `123456` |
| Landlord | `landlord01` | `123456` |
| Tenant | `tenant01` | `123456` |
| Tenant | `tenant02` | `123456` |
| Tenant | `tenant03` | `123456` |
| Tenant | `tenant04` | `123456` |
| Tenant | `tenant05` | `123456` |
| Tenant | `tenant06` | `123456` |

Kiểm tra đăng nhập:

```bash
curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d "{\"username\":\"landlord01\",\"password\":\"123456\"}"
```

---

## 7. Cấu Trúc Thư Mục

```
android-room-management-app/
├── room-management/
│   ├── android/          ← Android app (mở bằng Android Studio)
│   └── room-management/  ← Spring Boot backend (chạy bằng mvnw)
├── database.sql          ← Script SQL tham khảo
├── test.md               ← Hướng dẫn kiểm thử chi tiết
└── README.md
```

---

## 8. Lỗi Thường Gặp

**Backend không start:**
- Kiểm tra MySQL đang chạy và database `room_management` đã tạo
- Kiểm tra port 8080 chưa bị chiếm: `netstat -ano | findstr :8080`

**Android không kết nối được backend:**
- Đảm bảo điện thoại/emulator và máy tính cùng mạng WiFi
- Kiểm tra `BASE_URL` đúng IP
- Tắt firewall hoặc cho phép port 8080

**Emulator báo lỗi network:**
- Dùng `10.0.2.2` thay `localhost` hoặc `127.0.0.1`

---

## 9. Hướng Dẫn Chạy Thử Nghiệm Thủ Công

### Tài Khoản Thử Nghiệm (mật khẩu đều là `123456`)

| Username | Role | Tên hiển thị |
|----------|------|--------------|
| `admin` | Quản trị viên | Admin Hệ Thống |
| `landlord01` | Chủ trọ | Nguyễn Văn An |
| `tenant01` | Người thuê | Trần Thị Bình — Phòng 101 |
| `tenant02` | Người thuê | Lê Văn Cường — Phòng 201 & 501 |
| `tenant03` | Người thuê | Phạm Thị Dung — Phòng 202 & 503 |
| `tenant04` | Người thuê | Hoàng Văn Em — Phòng 103 |
| `tenant05` | Người thuê | Ngô Thị Phương — Phòng 303 |
| `tenant06` | Người thuê | Vũ Đình Quang — Phòng 401 |

> Xem chi tiết đầy đủ các bước trong file [`test.md`](./test.md).

---

## 10. Luồng Chạy Thử Nghiệm

Thực hiện theo thứ tự 5 phần dưới đây để bao phủ toàn bộ chức năng của hệ thống.

### Phần 1 — Admin Dashboard

1. Mở app → đăng nhập `admin` / `123456`
2. ✅ App chuyển đến màn hình **"Quản Trị Hệ Thống"** (không vào Landlord hay Tenant)
3. Kiểm tra dòng chào: **"Xin chào, Admin Hệ Thống 👋"**
4. Kiểm tra 8 ô thống kê có số liệu thực (Tổng phòng, Đang thuê, Phòng trống, Tổng người thuê, HĐ hiệu lực, HĐ chưa TT, Doanh thu, Công nợ)
5. Nhấn icon **chuông** (góc phải trên) → mở màn hình Thông báo → **Back**
6. Nhấn icon **người** → xem Hồ sơ: Họ tên `Admin Hệ Thống`, username `admin` → **Back**
7. Nhấn **Đăng xuất** → về màn hình Đăng nhập

### Phần 2 — Chủ Trọ (Landlord)

1. Đăng nhập `landlord01` / `123456`
2. ✅ Vào **Dashboard Chủ trọ** (tiêu đề "Quản Lý Phòng Trọ"), dòng chào: **"Xin chào, Nguyễn Văn An 👋"**
3. Cuộn xuống → kiểm tra section **"Hoạt động gần đây"** có dữ liệu thực tế (tiêu đề, nội dung, thời gian)
4. **Hồ sơ Landlord:**
   - Nhấn icon người → Hồ sơ → kiểm tra Email `landlord01@demo.com`, SĐT `0901234567`
   - Sửa họ tên thành `Nguyễn Văn An (Test)` → **Lưu thay đổi** → ✅ hiện "Cập nhật thành công"
   - Khôi phục lại tên gốc → **Lưu thay đổi**
   - Nhấn **Đổi mật khẩu** → nhập mật khẩu mới 4 ký tự → ✅ nút Xác nhận bị disable
   - Nhấn **Back**
5. **Tạo hóa đơn mới:**
   - Nhấn **Hóa đơn** → FAB **+** → chọn **"Phòng 101 — Trần Thị Bình"**
   - ✅ Hiện InfoRow: Phòng 101, Trần Thị Bình, 3.500.000 ₫/tháng
   - Nhập kỳ `2026-06` → ✅ form nhập chỉ số xuất hiện
   - Nhập điện đầu `105` / cuối `100` → ✅ hiện "Tiêu thụ: -5 kWh" màu đỏ, nút Tạo bị disable
   - Sửa điện cuối `140` → ✅ "Tiêu thụ: 35 kWh" màu xanh
   - Nhập nước đầu `20` / cuối `27` → ✅ nút Tạo được enable
   - Nhấn **Tạo hóa đơn** → ✅ hóa đơn tháng 2026-06 xuất hiện trong danh sách
6. **Ghi nhận thanh toán:**
   - Tìm hóa đơn Phòng 101 tháng `2026-03` (trạng thái UNPAID) → nhấn vào
   - Nhấn **Ghi nhận thanh toán** → nhập đủ số tiền → chọn **BANK_TRANSFER** → xác nhận
   - ✅ Hóa đơn chuyển sang trạng thái **PAID**

### Phần 3 — Người Thuê (Tenant)

1. Đăng xuất → đăng nhập `tenant01` / `123456`
2. ✅ Vào **TenantHomeScreen** (tiêu đề "Phòng Trọ"), dòng chào: **"Xin chào, Trần Thị Bình 👋"**
3. Nhấn tab **Hợp đồng** → dòng "Chủ trọ" phải hiện **"Nguyễn Văn An"** (không phải ID số)
4. **Hồ sơ Tenant:**
   - Nhấn icon người → Hồ sơ: Họ tên `Trần Thị Bình`, Email `tenant01@demo.com`, SĐT `0912345678`
   - Sửa SĐT thành `0999999999` → **Lưu thay đổi** → ✅ hiện "Cập nhật thành công"
   - Khôi phục SĐT về `0912345678` → **Lưu thay đổi** → **Back**
5. ✅ Icon **chuông** trên TenantHomeScreen có **badge đỏ** (tenant01 có 2 thông báo chưa đọc)
6. Nhấn icon chuông → xem danh sách thông báo → **Back**

### Phần 4 — WebSocket Real-time

> **Yêu cầu:** 2 thiết bị/emulator, hoặc 1 emulator + Postman trên máy tính.

1. Đăng nhập `tenant01` trên emulator → để ở **TenantHomeScreen**
2. Trên máy tính, đăng nhập `landlord01` (qua app hoặc Postman)
3. **Test thông báo tạo hóa đơn:**
   - Landlord tạo hóa đơn tháng `2026-07` cho Phòng 101 (tenant01)
   - ✅ Trên emulator: xuất hiện **banner thông báo** ở đầu màn hình trong ~4 giây
   - ✅ Badge số trên icon chuông **tăng lên**
   - Nhấn vào banner → banner biến mất ngay
4. **Test thông báo thanh toán:**
   - Landlord ghi nhận thanh toán cho hóa đơn vừa tạo (Phòng 101 tháng 2026-07)
   - ✅ tenant01 nhận banner thông báo thanh toán

### Phần 5 — Edge Case & Kiểm Tra Lỗi

| Kịch bản | Thao tác | Kết quả mong đợi |
|----------|----------|-----------------|
| Hóa đơn trùng tháng | Tạo hóa đơn Phòng 101 tháng `2026-03` (đã tồn tại) | Hiện lỗi "Invoice … already exists" |
| Chỉ số điện âm | Điện đầu `105`, cuối `100` | "Tiêu thụ: -5 kWh" màu đỏ, nút Tạo bị disable |
| Mất mạng | Tắt WiFi → pull-to-refresh | Hiện thông báo lỗi, app không crash |
| Bật mạng lại | Bật WiFi → pull-to-refresh | Dữ liệu load lại bình thường |
| Đổi mật khẩu ngắn | Mật khẩu mới 4 ký tự | Nút Xác nhận bị disable |
| Đăng xuất & đăng nhập lại | Profile → Đăng xuất → đăng nhập `landlord01` | Vào đúng Landlord Dashboard |

---

## 11. Bảng Tổng Hợp Kết Quả Thử Nghiệm

| Phần | Nội dung | Pass | Fail | Ghi chú |
|------|----------|------|------|---------|
| 1 | Admin Dashboard | | | |
| 2 | Landlord — Dashboard, Hồ sơ, Tạo hóa đơn, Thanh toán | | | |
| 3 | Tenant — Hợp đồng, Hồ sơ, Badge thông báo | | | |
| 4 | WebSocket real-time | | | |
| 5 | Edge case (lỗi, trùng tháng, mất mạng) | | | |
