# Kế Hoạch Triển Khai: Hệ Thống Quản Lý Phòng Trọ

## Tổng Quan

Backend Spring Boot và Android client đã có cấu trúc cơ bản. Các task dưới đây tập trung vào hoàn thiện những phần còn thiếu và kết nối Android với API thực.

---

## Tasks

- [x] 1. Hoàn thiện Backend: Scheduled Jobs & Enums còn thiếu
  - [x] 1.1 Thêm enum ROLE_TENANT, ROLE_LANDLORD, ROLE_ADMIN vào Role.java (hiện dùng TENANT/LANDLORD/ADMIN)
  - [x] 1.2 Thêm NotificationType.GENERAL vào NotificationType.java
  - [x] 1.3 Tạo ScheduledJobService với @Scheduled cho auto-expire contracts và overdue invoices
  - [x] 1.4 Thêm @EnableScheduling vào main application class

- [x] 2. Hoàn thiện Backend: Repository queries còn thiếu
  - [x] 2.1 Kiểm tra và bổ sung ReportRepository với các JPQL queries cần thiết
  - [x] 2.2 Kiểm tra và bổ sung MeterReadingRepository với findLatestByRoomId
  - [x] 2.3 Kiểm tra và bổ sung InvoiceRepository với findByLandlordId, findByTenantId

- [x] 3. Hoàn thiện Backend: WebSocket & Notification
  - [x] 3.1 Hoàn thiện WebSocketEventService với các push methods (pushContractCreated, pushInvoiceCreated, v.v.)
  - [x] 3.2 Hoàn thiện NotificationController (WebSocket) với pushToUser method
  - [x] 3.3 Thêm NotificationRestController với các REST endpoints

- [x] 4. Hoàn thiện Backend: MeterReading & Payment Controllers
  - [x] 4.1 Tạo MeterReadingController với đầy đủ endpoints
  - [x] 4.2 Hoàn thiện PaymentController với đầy đủ endpoints

- [x] 5. Android: Kết nối RoomListScreen với API thực
  - [x] 5.1 Tạo RoomViewModel với StateFlow và Hilt injection
  - [x] 5.2 Cập nhật RoomListScreen để dùng RoomViewModel thay MockData
  - [x] 5.3 Cập nhật RoomFormScreen để gọi API create/update room

- [x] 6. Android: Kết nối ContractListScreen với API thực
  - [x] 6.1 Tạo ContractViewModel với StateFlow
  - [x] 6.2 Cập nhật ContractListScreen để dùng ContractViewModel
  - [x] 6.3 Cập nhật ContractFormScreen để gọi API create contract

- [x] 7. Android: Kết nối MeterReadingScreen với API thực
  - [x] 7.1 Tạo MeterReadingViewModel
  - [x] 7.2 Cập nhật MeterReadingScreen để gọi API record meter reading

- [x] 8. Android: Kết nối InvoiceListScreen với API thực
  - [x] 8.1 Tạo InvoiceViewModel với StateFlow
  - [x] 8.2 Cập nhật InvoiceListScreen để dùng InvoiceViewModel
  - [x] 8.3 Cập nhật InvoiceDetailScreen để gọi API và hiển thị breakdown thực

- [x] 9. Android: Kết nối PaymentHistoryScreen với API thực
  - [x] 9.1 Tạo PaymentViewModel
  - [x] 9.2 Cập nhật PaymentHistoryScreen để gọi API create payment

- [x] 10. Android: Kết nối Tenant screens với API thực
  - [x] 10.1 Tạo TenantViewModel cho TenantHomeScreen
  - [x] 10.2 Cập nhật TenantInvoiceListScreen để gọi API /invoices/tenant/me
  - [x] 10.3 Cập nhật TenantInvoiceDetailScreen để hiển thị dữ liệu thực
  - [x] 10.4 Cập nhật MyContractScreen và MyRoomScreen với API thực

- [x] 11. Android: Kết nối NotificationScreen với API thực
  - [x] 11.1 Tạo NotificationViewModel
  - [x] 11.2 Cập nhật NotificationScreen (landlord) và TenantNotificationScreen

- [x] 12. Android: Kết nối ReportScreen với API thực
  - [x] 12.1 Tạo ReportViewModel
  - [x] 12.2 Cập nhật ReportScreen để hiển thị dữ liệu dashboard và revenue thực

- [x] 13. Android: Thêm PaymentDto và cập nhật ApiService
  - [x] 13.1 Thêm PaymentResponse, CreatePaymentRequest vào PaymentDto.kt
  - [x] 13.2 Cập nhật ApiService với các endpoint còn thiếu (mark paid, notifications, reports)
