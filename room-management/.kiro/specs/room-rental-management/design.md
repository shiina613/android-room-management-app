# Tài Liệu Thiết Kế: Hệ Thống Quản Lý Phòng Trọ

## 1. Tổng Quan Kiến Trúc

Hệ thống theo mô hình **Client-Server** hai tầng:

```
┌─────────────────────────┐        HTTPS/REST        ┌──────────────────────────┐
│   Android Client        │ ◄──────────────────────► │   Spring Boot Backend    │
│   (Jetpack Compose)     │                           │   (REST API + WebSocket) │
│   Hilt DI + Retrofit    │        WebSocket          │   Spring Security + JWT  │
│   DataStore (token)     │ ◄──────────────────────► │   JPA + MySQL            │
└─────────────────────────┘                           └──────────────────────────┘
```

- Backend: Spring Boot 3.2.5, Java 17, MySQL
- Android: Kotlin, Jetpack Compose, Hilt, Retrofit2, Navigation Compose
- Xác thực: JWT (jjwt 0.11.5), BCrypt
- Realtime: STOMP over WebSocket

---

## 2. Kiến Trúc Backend (Spring Boot)

### 2.1 Cấu Trúc Package

```
com.kma.lamphoun.room_management/
├── common/
│   ├── ApiResponse.java          # Wrapper response chung
│   └── enums/                    # ContractStatus, InvoiceStatus, RoomStatus, Role, ...
├── config/
│   ├── SecurityConfig.java       # Spring Security, CORS, filter chain
│   ├── WebSocketConfig.java      # STOMP endpoint cấu hình
│   └── DataSeeder.java           # Seed dữ liệu ban đầu
├── controller/                   # REST Controllers (1 controller / module)
├── dto/
│   ├── request/                  # Request body DTOs
│   └── response/                 # Response DTOs
├── entity/                       # JPA Entities
├── exception/                    # GlobalExceptionHandler + custom exceptions
├── mapper/                       # Entity ↔ DTO mappers
├── repository/                   # Spring Data JPA Repositories
├── security/                     # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
├── service/                      # Service interfaces
│   └── impl/                     # Service implementations
└── websocket/                    # STOMP controllers, event service
```

### 2.2 Luồng Xử Lý Request

```
HTTP Request
    │
    ▼
JwtAuthFilter (kiểm tra Bearer token)
    │
    ▼
SecurityConfig (kiểm tra role/permission)
    │
    ▼
Controller (validate input, gọi Service)
    │
    ▼
ServiceImpl (business logic, gọi Repository)
    │
    ▼
Repository (JPA query → MySQL)
    │
    ▼
ApiResponse<T> → JSON response
```

### 2.3 Xử Lý Lỗi Tập Trung

`GlobalExceptionHandler` bắt các exception và trả về HTTP status tương ứng:

| Exception                  | HTTP Status         |
|----------------------------|---------------------|
| `ResourceNotFoundException`| 404 Not Found       |
| `BadRequestException`      | 400 Bad Request     |
| `ForbiddenException`       | 403 Forbidden       |
| `MethodArgumentNotValid`   | 400 (validation)    |
| `DataIntegrityViolation`   | 409 Conflict        |

---

## 3. Thiết Kế Bảo Mật

### 3.1 JWT Authentication

```
Client                          Server
  │                               │
  │── POST /api/auth/login ──────►│
  │                               │ Xác thực email/password
  │                               │ Tạo JWT (24h expiry)
  │◄── { accessToken, role } ────│
  │                               │
  │── GET /api/... ──────────────►│
  │   Authorization: Bearer <jwt> │
  │                               │ JwtAuthFilter.doFilter()
  │                               │   → JwtUtil.validateToken()
  │                               │   → SecurityContextHolder.set()
  │◄── 200 OK / 401 Unauthorized ─│
```

**JWT Payload:**
```json
{
  "sub": "user@email.com",
  "role": "LANDLORD",
  "userId": 1,
  "iat": 1700000000,
  "exp": 1700086400
}
```

### 3.2 Phân Quyền Theo Role

| Endpoint Pattern          | ADMIN | LANDLORD | TENANT |
|---------------------------|-------|----------|--------|
| /api/auth/**              | ✓     | ✓        | ✓      |
| /api/users                | ✓     | ✗        | ✗      |
| /api/houses/**            | ✗     | ✓ (own)  | ✗      |
| /api/rooms/**             | ✗     | ✓ (own)  | ✓ (view)|
| /api/contracts/**         | ✓     | ✓ (own)  | ✓ (own)|
| /api/invoices/**          | ✓     | ✓ (own)  | ✓ (own)|
| /api/payments/**          | ✗     | ✓        | ✓      |
| /api/reports/**           | ✓     | ✓ (own)  | ✗      |
| /api/notifications/**     | ✓     | ✓        | ✓      |

### 3.3 Bảo Vệ Tài Khoản

- Mật khẩu mã hóa bằng `BCryptPasswordEncoder` (strength=10)
- Khóa tài khoản 15 phút sau 5 lần đăng nhập sai liên tiếp (theo dõi qua `is_active` + timestamp)
- Đổi mật khẩu → invalidate token cũ (lưu `password_changed_at`, so sánh với `iat` của JWT)

---

## 4. Thiết Kế Dữ Liệu

### 4.1 Entity Relationship Diagram

```
users ──────────────────────────────────────────────────────────────────┐
  │ (landlord_id)                                                        │
  ▼                                                                      │
houses ──────────────────────────────────────────────────────────────   │
  │ (house_id)              │ (house_id)                                 │
  ▼                         ▼                                            │
rooms               room_services                                        │
  │ (room_id)               │ (service_id)                               │
  │                         │                                            │
  ├──────────────────────── meter_readings ◄──── (created_by → users)   │
  │                                                                      │
  ▼ (room_id)                                                            │
contracts ◄──────────────────────────────────── (tenant_id, landlord_id → users)
  │ (contract_id)
  ▼
invoices
  │ (invoice_id)
  ├──► invoice_items
  └──► payments ◄──────────────────────────── (confirmed_by → users)

users ──► notifications
```

### 4.2 Các Enum Quan Trọng

```java
// RoomStatus
AVAILABLE, OCCUPIED, MAINTENANCE

// ContractStatus
ACTIVE, EXPIRED, TERMINATED

// InvoiceStatus
UNPAID, PAID, OVERDUE

// PaymentMethod
CASH, BANK_TRANSFER, MOMO

// NotificationType
INVOICE, PAYMENT, CONTRACT, SYSTEM

// Role
ADMIN, LANDLORD, TENANT
```

### 4.3 Indexes Quan Trọng

| Bảng            | Index                                      | Mục đích                        |
|-----------------|--------------------------------------------|---------------------------------|
| users           | UNIQUE(email)                              | Đăng nhập nhanh                 |
| contracts       | INDEX(room_id, status)                     | Kiểm tra phòng AVAILABLE        |
| contracts       | INDEX(tenant_id)                           | Lấy hợp đồng của tenant         |
| invoices        | UNIQUE(contract_id, billing_month)         | Tránh tạo hóa đơn trùng         |
| invoices        | INDEX(status, due_date)                    | Scheduled job cập nhật OVERDUE  |
| notifications   | INDEX(user_id, is_read)                    | Lấy thông báo chưa đọc          |
| meter_readings  | INDEX(room_id, service_id, reading_date)   | Lấy chỉ số kỳ trước             |

---

## 5. Thiết Kế Các Module Service

### 5.1 AuthService

```
AuthService
├── login(LoginRequest) → AuthResponse
│     ├── Tìm user theo email
│     ├── Kiểm tra is_active
│     ├── BCrypt.matches(password, hash)
│     ├── Kiểm tra login_attempts (≤5)
│     └── JwtUtil.generateToken(user) → JWT 24h
│
├── register(RegisterRequest) → AuthResponse
│     ├── Kiểm tra email chưa tồn tại
│     ├── BCrypt.encode(password)
│     └── Lưu user với role mặc định
│
└── changePassword(ChangePasswordRequest, userId)
      ├── Xác thực mật khẩu cũ
      ├── BCrypt.encode(newPassword)
      └── Cập nhật password_changed_at → invalidate token cũ
```

### 5.2 RoomService

```
RoomService
├── createRoom(houseId, RoomRequest, landlordId) → RoomResponse
│     ├── Kiểm tra house thuộc landlord
│     └── Tạo room với status = AVAILABLE
│
├── deleteRoom(roomId, landlordId)
│     ├── Kiểm tra quyền sở hữu
│     ├── Kiểm tra KHÔNG có contract ACTIVE
│     └── Xóa room (hoặc soft delete)
│
└── updateRoomStatus(roomId, status)
      └── Được gọi nội bộ bởi ContractService
```

### 5.3 ContractService

```
ContractService
├── createContract(CreateContractRequest, landlordId) → ContractResponse
│     ├── Kiểm tra room.status == AVAILABLE
│     ├── Kiểm tra không có contract ACTIVE cho room
│     ├── Lưu contract với status = ACTIVE
│     ├── RoomService.updateStatus(roomId, OCCUPIED)
│     └── NotificationService.send(tenantId, CONTRACT, contractId)
│
├── terminateContract(contractId, TerminateRequest, landlordId)
│     ├── Cập nhật status = TERMINATED
│     └── RoomService.updateStatus(roomId, AVAILABLE)
│
└── [Scheduled] checkExpiredContracts()  // chạy hàng ngày
      └── Cập nhật ACTIVE → EXPIRED nếu end_date < today
```

### 5.4 MeterService

```
MeterService
├── createReading(roomId, MeterReadingRequest, userId) → MeterReadingResponse
│     ├── Lấy chỉ số kỳ trước (latest reading cho room+service)
│     ├── Validate: current >= previous
│     ├── consumption = current - previous
│     └── Lưu với created_by = userId
│
└── getReadings(roomId) → List<MeterReadingResponse>
      └── Sắp xếp theo reading_date DESC
```

### 5.5 InvoiceService

```
InvoiceService
├── createInvoice(CreateInvoiceRequest, landlordId) → InvoiceResponse
│     ├── Kiểm tra contract ACTIVE
│     ├── Kiểm tra UNIQUE(contract_id, billing_month) → 409 nếu trùng
│     ├── Tính invoice_items:
│     │     ├── Item 1: Tiền phòng (monthly_price × 1 tháng)
│     │     └── Item N: Dịch vụ (consumption × unit_price)
│     ├── total_amount = SUM(invoice_items.amount)
│     ├── Lưu invoice + invoice_items
│     └── NotificationService.send(tenantId, INVOICE, invoiceId)
│
└── [Scheduled] checkOverdueInvoices()  // chạy hàng ngày
      └── Cập nhật UNPAID → OVERDUE nếu due_date < today
```

### 5.6 PaymentService

```
PaymentService
├── createPayment(CreatePaymentRequest, userId) → PaymentResponse
│     ├── Kiểm tra invoice thuộc về tenant (hoặc landlord có quyền)
│     ├── Kiểm tra invoice.status != PAID → 409 nếu đã thanh toán
│     └── Lưu payment (chưa confirmed)
│
└── confirmPayment(paymentId, landlordId)
      ├── Cập nhật invoice.status = PAID
      ├── Lưu confirmed_by = landlordId
      └── NotificationService.send(tenantId, PAYMENT, invoiceId)
```

### 5.7 NotificationService

```
NotificationService
├── send(userId, type, refId, title, body)
│     ├── Lưu notification vào DB
│     └── WebSocketEventService.push(userId, notification)
│
├── markRead(notificationId, userId)
│     └── Cập nhật is_read = true
│
└── [Scheduled] sendDueDateReminders()  // chạy hàng ngày
      └── Tìm invoice UNPAID có due_date = today + 3 ngày
            └── send(tenantId, INVOICE, invoiceId, "Nhắc nhở thanh toán")
```

### 5.8 ReportService

```
ReportService
├── getRevenue(landlordId, year, month) → RevenueReportResponse
│     └── SUM(payments.amount) GROUP BY billing_month
│           WHERE landlord_id = ? (hoặc toàn hệ thống nếu ADMIN)
│
├── getOccupancy(landlordId) → RoomStatusReportResponse
│     └── COUNT(rooms) GROUP BY status
│           tỷ lệ = OCCUPIED / total × 100
│
└── getOverdueInvoices(landlordId) → List<DebtReportResponse>
      └── invoices WHERE status = OVERDUE AND landlord_id = ?
```

---

## 6. Thiết Kế WebSocket (Realtime Notification)

### 6.1 Cấu Hình STOMP

```
WebSocket Endpoint: /ws
Message Broker:
  - /topic  (broadcast)
  - /queue  (user-specific)
App Destination Prefix: /app
```

### 6.2 Luồng Gửi Thông Báo Realtime

```
Service (vd: InvoiceService)
    │
    ▼
NotificationService.send(userId, ...)
    │
    ├── Lưu vào DB (bảng notifications)
    │
    └── WebSocketEventService.push(userId, WsPayload)
              │
              ▼
        SimpMessagingTemplate.convertAndSendToUser(
            userId, "/queue/notifications", payload
        )
              │
              ▼
        Android Client nhận qua STOMP subscription
        /user/queue/notifications
```

---

## 7. Kiến Trúc Android Client

### 7.1 Cấu Trúc Package

```
com.kma.lamphoun.roomapp/
├── data/
│   ├── local/
│   │   └── TokenDataStore.kt       # Lưu JWT token (DataStore Preferences)
│   └── remote/
│       ├── api/
│       │   └── ApiService.kt       # Retrofit interface (tất cả endpoints)
│       └── dto/                    # Data classes cho request/response
│           ├── AuthDto.kt
│           ├── RoomDto.kt
│           ├── ContractDto.kt
│           ├── InvoiceDto.kt
│           ├── PaymentDto.kt
│           └── MiscDto.kt
├── di/
│   └── NetworkModule.kt            # Hilt module: Retrofit, OkHttp, ApiService
├── ui/
│   ├── auth/                       # Login, Register, Splash screens
│   ├── landlord/                   # Dashboard, Room, Contract, Invoice, ...
│   ├── tenant/                     # Tenant-specific screens
│   └── common/                     # Shared components, MockData
├── MainActivity.kt                 # NavHost, navigation graph
└── RoomApp.kt                      # @HiltAndroidApp Application class
```

### 7.2 Luồng Dữ Liệu (MVVM)

```
UI (Composable Screen)
    │  collectAsState()
    ▼
ViewModel (StateFlow<UiState>)
    │  suspend fun / coroutine
    ▼
ApiService (Retrofit)
    │  HTTP Request + Bearer token
    ▼
Spring Boot Backend
    │  JSON Response
    ▼
DTO → ViewModel cập nhật StateFlow
    │
    ▼
UI re-compose
```

### 7.3 Quản Lý Token

```kotlin
// TokenDataStore.kt
// Lưu: DataStore<Preferences> với key TOKEN_KEY
// Đọc: Flow<String?>
// Xóa: khi logout

// NetworkModule.kt
// OkHttp Interceptor tự động đính kèm:
// Authorization: Bearer <token>
// vào mọi request (trừ /auth/login, /auth/register)
```

### 7.4 Navigation Graph

```
SplashScreen
    │
    ├── (token hợp lệ + role=LANDLORD) ──► LandlordDashboard
    │       ├── RoomListScreen
    │       ├── ContractListScreen / ContractFormScreen
    │       ├── MeterReadingScreen
    │       ├── InvoiceListScreen / InvoiceDetailScreen
    │       ├── PaymentScreen
    │       ├── NotificationScreen
    │       └── ReportScreen
    │
    ├── (token hợp lệ + role=TENANT) ───► TenantDashboard
    │       ├── RoomDetailScreen
    │       ├── InvoiceListScreen
    │       ├── PaymentScreen
    │       └── NotificationScreen
    │
    └── (không có token) ────────────────► LoginScreen
                                                └── RegisterScreen
```

### 7.5 Dependency Injection (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // OkHttpClient với AuthInterceptor + HttpLoggingInterceptor
    // Retrofit với GsonConverterFactory
    // ApiService (Singleton)
    // TokenDataStore (Singleton)
}
```

---

## 8. Thiết Kế API Chi Tiết

### 8.1 Chuẩn Response

```json
// Thành công
{
  "success": true,
  "message": "OK",
  "data": { ... }
}

// Lỗi
{
  "success": false,
  "message": "Phòng đã có hợp đồng đang hoạt động",
  "data": null
}
```

### 8.2 Các Endpoint Quan Trọng

**POST /api/auth/login**
```json
// Request
{ "email": "landlord@example.com", "password": "secret" }

// Response 200
{ "accessToken": "eyJ...", "role": "LANDLORD", "userId": 1, "fullName": "Nguyễn Văn A" }

// Response 401
{ "success": false, "message": "Sai email hoặc mật khẩu" }
```

**POST /api/contracts**
```json
// Request
{
  "roomId": 5,
  "tenantId": 12,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "depositAmount": 2000000,
  "monthlyPrice": 3000000,
  "note": "..."
}

// Response 409 nếu phòng đã có hợp đồng ACTIVE
```

**POST /api/invoices**
```json
// Request
{
  "contractId": 3,
  "billingMonth": "2024-01",
  "dueDate": "2024-01-15",
  "meterReadingIds": [10, 11]
}

// Response: invoice với danh sách invoice_items đã tính
```

**POST /api/payments**
```json
// Request
{
  "invoiceId": 7,
  "amount": 1500000,
  "method": "BANK_TRANSFER",
  "transactionId": "TXN123456",
  "note": "Chuyển khoản tháng 1"
}
```

---

## 9. Scheduled Jobs

| Job                        | Cron Expression | Mô tả                                      |
|----------------------------|-----------------|--------------------------------------------|
| checkExpiredContracts      | `0 0 1 * * *`   | Cập nhật ACTIVE → EXPIRED hàng ngày 1:00   |
| checkOverdueInvoices       | `0 0 2 * * *`   | Cập nhật UNPAID → OVERDUE hàng ngày 2:00   |
| sendDueDateReminders       | `0 0 8 * * *`   | Gửi nhắc nhở hóa đơn sắp đến hạn 8:00     |

---

## 10. Cấu Hình Môi Trường

### Backend (application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/room_management
spring.datasource.username=root
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

jwt.secret=<base64-secret-256bit>
jwt.expiration=86400000

server.port=8080
```

### Android (build.gradle.kts)
```kotlin
buildConfigField("String", "BASE_URL", "\"http://<server-ip>:8080/\"")
```

---

## 11. Các Tính Chất Đúng Đắn (Correctness Properties)

Các tính chất này có thể được kiểm tra bằng Property-Based Testing:

**P1 – Tính nhất quán trạng thái phòng:**
> Với mọi phòng, nếu tồn tại ít nhất một hợp đồng ACTIVE thì `room.status == OCCUPIED`, ngược lại `room.status == AVAILABLE` (trừ MAINTENANCE).

**P2 – Tính duy nhất hóa đơn:**
> Với mọi cặp `(contract_id, billing_month)`, chỉ tồn tại tối đa một hóa đơn trong hệ thống.

**P3 – Tính chính xác chỉ số tiêu thụ:**
> Với mọi bản ghi meter_reading, `consumption == current - previous` và `current >= previous`.

**P4 – Tính toàn vẹn tổng hóa đơn:**
> Với mọi hóa đơn, `invoice.total_amount == SUM(invoice_items.amount)`.

**P5 – Tính bảo mật dữ liệu:**
> Tenant chỉ có thể đọc hóa đơn, hợp đồng, thông báo thuộc về chính mình (tenant_id == currentUserId).

**P6 – Tính nhất quán trạng thái thanh toán:**
> Nếu `invoice.status == PAID` thì tồn tại đúng một payment với `confirmed_by != null` liên kết với invoice đó.
