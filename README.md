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

curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d "{\"username\":\"landlord01\",\"password\":\"Password@123\"}"

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
| Admin | `admin` | `Password@123` |
| Landlord | `landlord01` | `Password@123` |
| Tenant | `tenant01` | `Password@123` |
| Tenant | `tenant02` | `Password@123` |
| Tenant | `tenant03` | `Password@123` |

Kiểm tra đăng nhập:

```bash
curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d "{\"username\":\"landlord01\",\"password\":\"Password@123\"}"
```

---

## 7. Cấu Trúc Thư Mục

```
android-room-management-app/
├── room-management/
│   ├── android/          ← Android app (mở bằng Android Studio)
│   └── room-management/  ← Spring Boot backend (chạy bằng mvnw)
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
