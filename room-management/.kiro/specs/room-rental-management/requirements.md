# Tài Liệu Yêu Cầu: Hệ Thống Quản Lý Phòng Trọ

## Giới Thiệu

Hệ thống quản lý phòng trọ theo mô hình client-server gồm Spring Boot backend và Android client. Hệ thống hỗ trợ 3 vai trò: Admin, Landlord (chủ trọ), Tenant (người thuê). Các module chính: xác thực, người dùng, phòng, hợp đồng, chỉ số điện nước, hóa đơn, thanh toán, thông báo, báo cáo.

---

## Bảng Chú Giải

- **System**: Hệ thống quản lý phòng trọ (backend Spring Boot)
- **Admin**: Quản trị viên hệ thống, có toàn quyền
- **Landlord**: Chủ trọ, quản lý nhà trọ và phòng của mình
- **Tenant**: Người thuê phòng
- **AuthService**: Module xử lý xác thực và phân quyền
- **UserService**: Module quản lý người dùng
- **RoomService**: Module quản lý phòng trọ
- **ContractService**: Module quản lý hợp đồng thuê
- **MeterService**: Module ghi chỉ số điện nước
- **InvoiceService**: Module tạo và quản lý hóa đơn
- **PaymentService**: Module xử lý thanh toán
- **NotificationService**: Module gửi thông báo
- **ReportService**: Module thống kê báo cáo
- **JWT_Token**: Token xác thực dạng JSON Web Token

---

## Use Case Tổng Quát

| Actor    | Use Case                                                                 |
|----------|--------------------------------------------------------------------------|
| Admin    | Quản lý tất cả người dùng, xem báo cáo toàn hệ thống, cấu hình hệ thống |
| Landlord | Quản lý phòng, tạo hợp đồng, ghi điện nước, tạo hóa đơn, xem báo cáo   |
| Tenant   | Xem thông tin phòng, xem hóa đơn, thực hiện thanh toán, xem thông báo   |

---

## Danh Sách Bảng Dữ Liệu

### Bảng `users`
| Cột            | Kiểu         | Mô tả                          |
|----------------|--------------|--------------------------------|
| id             | BIGINT PK    | Khóa chính                     |
| full_name      | VARCHAR(100) | Họ tên                         |
| email          | VARCHAR(100) | Email (unique)                 |
| phone          | VARCHAR(20)  | Số điện thoại                  |
| password_hash  | VARCHAR(255) | Mật khẩu đã mã hóa             |
| role           | ENUM         | ADMIN / LANDLORD / TENANT      |
| avatar_url     | VARCHAR(255) | Ảnh đại diện                   |
| is_active      | BOOLEAN      | Trạng thái tài khoản           |
| created_at     | TIMESTAMP    | Thời gian tạo                  |

### Bảng `houses`
| Cột         | Kiểu         | Mô tả                    |
|-------------|--------------|--------------------------|
| id          | BIGINT PK    | Khóa chính               |
| landlord_id | BIGINT FK    | Tham chiếu users(id)     |
| name        | VARCHAR(100) | Tên nhà trọ              |
| address     | TEXT         | Địa chỉ                  |
| description | TEXT         | Mô tả                    |
| created_at  | TIMESTAMP    | Thời gian tạo            |

### Bảng `rooms`
| Cột           | Kiểu          | Mô tả                        |
|---------------|---------------|------------------------------|
| id            | BIGINT PK     | Khóa chính                   |
| house_id      | BIGINT FK     | Tham chiếu houses(id)        |
| room_number   | VARCHAR(20)   | Số phòng                     |
| floor         | INT           | Tầng                         |
| area          | DECIMAL(6,2)  | Diện tích (m²)               |
| max_occupants | INT           | Số người tối đa              |
| base_price    | DECIMAL(12,2) | Giá thuê cơ bản              |
| status        | ENUM          | AVAILABLE / OCCUPIED / MAINTENANCE |
| description   | TEXT          | Mô tả                        |
| created_at    | TIMESTAMP     | Thời gian tạo                |

### Bảng `room_services`
| Cột         | Kiểu          | Mô tả                        |
|-------------|---------------|------------------------------|
| id          | BIGINT PK     | Khóa chính                   |
| house_id    | BIGINT FK     | Tham chiếu houses(id)        |
| name        | VARCHAR(100)  | Tên dịch vụ (điện, nước, wifi...) |
| unit        | VARCHAR(20)   | Đơn vị (kWh, m³, tháng)     |
| unit_price  | DECIMAL(12,2) | Đơn giá                      |

### Bảng `contracts`
| Cột            | Kiểu          | Mô tả                        |
|----------------|---------------|------------------------------|
| id             | BIGINT PK     | Khóa chính                   |
| room_id        | BIGINT FK     | Tham chiếu rooms(id)         |
| tenant_id      | BIGINT FK     | Tham chiếu users(id)         |
| landlord_id    | BIGINT FK     | Tham chiếu users(id)         |
| start_date     | DATE          | Ngày bắt đầu                 |
| end_date       | DATE          | Ngày kết thúc                |
| deposit_amount | DECIMAL(12,2) | Tiền đặt cọc                 |
| monthly_price  | DECIMAL(12,2) | Giá thuê hàng tháng          |
| status         | ENUM          | ACTIVE / EXPIRED / TERMINATED |
| note           | TEXT          | Ghi chú                      |
| created_at     | TIMESTAMP     | Thời gian tạo                |

### Bảng `meter_readings`
| Cột          | Kiểu          | Mô tả                        |
|--------------|---------------|------------------------------|
| id           | BIGINT PK     | Khóa chính                   |
| room_id      | BIGINT FK     | Tham chiếu rooms(id)         |
| service_id   | BIGINT FK     | Tham chiếu room_services(id) |
| reading_date | DATE          | Ngày ghi chỉ số              |
| previous     | DECIMAL(10,2) | Chỉ số kỳ trước              |
| current      | DECIMAL(10,2) | Chỉ số kỳ này                |
| consumption  | DECIMAL(10,2) | Lượng tiêu thụ (current - previous) |
| created_by   | BIGINT FK     | Người ghi (users.id)         |

### Bảng `invoices`
| Cột          | Kiểu          | Mô tả                        |
|--------------|---------------|------------------------------|
| id           | BIGINT PK     | Khóa chính                   |
| contract_id  | BIGINT FK     | Tham chiếu contracts(id)     |
| room_id      | BIGINT FK     | Tham chiếu rooms(id)         |
| billing_month| VARCHAR(7)    | Kỳ hóa đơn (YYYY-MM)        |
| total_amount | DECIMAL(12,2) | Tổng tiền                    |
| due_date     | DATE          | Hạn thanh toán               |
| status       | ENUM          | UNPAID / PAID / OVERDUE      |
| created_at   | TIMESTAMP     | Thời gian tạo                |

### Bảng `invoice_items`
| Cột         | Kiểu          | Mô tả                        |
|-------------|---------------|------------------------------|
| id          | BIGINT PK     | Khóa chính                   |
| invoice_id  | BIGINT FK     | Tham chiếu invoices(id)      |
| description | VARCHAR(200)  | Mô tả khoản mục              |
| quantity    | DECIMAL(10,2) | Số lượng                     |
| unit_price  | DECIMAL(12,2) | Đơn giá                      |
| amount      | DECIMAL(12,2) | Thành tiền                   |

### Bảng `payments`
| Cột            | Kiểu          | Mô tả                        |
|----------------|---------------|------------------------------|
| id             | BIGINT PK     | Khóa chính                   |
| invoice_id     | BIGINT FK     | Tham chiếu invoices(id)      |
| amount         | DECIMAL(12,2) | Số tiền thanh toán           |
| method         | ENUM          | CASH / BANK_TRANSFER / MOMO  |
| transaction_id | VARCHAR(100)  | Mã giao dịch (nếu có)        |
| paid_at        | TIMESTAMP     | Thời gian thanh toán         |
| confirmed_by   | BIGINT FK     | Người xác nhận (users.id)    |
| note           | TEXT          | Ghi chú                      |

### Bảng `notifications`
| Cột        | Kiểu         | Mô tả                        |
|------------|--------------|------------------------------|
| id         | BIGINT PK    | Khóa chính                   |
| user_id    | BIGINT FK    | Người nhận (users.id)        |
| title      | VARCHAR(200) | Tiêu đề                      |
| body       | TEXT         | Nội dung                     |
| type       | ENUM         | INVOICE / PAYMENT / CONTRACT / SYSTEM |
| ref_id     | BIGINT       | ID đối tượng liên quan       |
| is_read    | BOOLEAN      | Đã đọc chưa                  |
| created_at | TIMESTAMP    | Thời gian tạo                |

---

## Quan Hệ Giữa Các Bảng

```
users (1) ──────────── (N) houses           [landlord_id]
houses (1) ─────────── (N) rooms            [house_id]
houses (1) ─────────── (N) room_services    [house_id]
rooms (1) ──────────── (N) contracts        [room_id]
users (1) ──────────── (N) contracts        [tenant_id, landlord_id]
contracts (1) ──────── (N) invoices         [contract_id]
rooms (1) ──────────── (N) meter_readings   [room_id]
room_services (1) ───── (N) meter_readings  [service_id]
invoices (1) ───────── (N) invoice_items    [invoice_id]
invoices (1) ───────── (1) payments         [invoice_id]
users (1) ──────────── (N) notifications    [user_id]
```

---

## Danh Sách API Theo Module

### Auth API
| Method | Endpoint              | Mô tả                    | Role       |
|--------|-----------------------|--------------------------|------------|
| POST   | /api/auth/register    | Đăng ký tài khoản        | Public     |
| POST   | /api/auth/login       | Đăng nhập, trả JWT       | Public     |
| POST   | /api/auth/logout      | Đăng xuất                | All        |
| POST   | /api/auth/refresh     | Làm mới token            | All        |
| PUT    | /api/auth/password    | Đổi mật khẩu             | All        |

### User API
| Method | Endpoint              | Mô tả                    | Role       |
|--------|-----------------------|--------------------------|------------|
| GET    | /api/users            | Danh sách người dùng     | ADMIN      |
| GET    | /api/users/{id}       | Chi tiết người dùng      | ADMIN/Self |
| PUT    | /api/users/{id}       | Cập nhật thông tin       | ADMIN/Self |
| DELETE | /api/users/{id}       | Xóa tài khoản            | ADMIN      |
| GET    | /api/users/me         | Thông tin bản thân       | All        |

### House API
| Method | Endpoint              | Mô tả                    | Role       |
|--------|-----------------------|--------------------------|------------|
| GET    | /api/houses           | Danh sách nhà trọ        | LANDLORD   |
| POST   | /api/houses           | Tạo nhà trọ              | LANDLORD   |
| GET    | /api/houses/{id}      | Chi tiết nhà trọ         | LANDLORD   |
| PUT    | /api/houses/{id}      | Cập nhật nhà trọ         | LANDLORD   |
| DELETE | /api/houses/{id}      | Xóa nhà trọ              | LANDLORD   |

### Room API
| Method | Endpoint                        | Mô tả                    | Role            |
|--------|---------------------------------|--------------------------|-----------------|
| GET    | /api/houses/{houseId}/rooms     | Danh sách phòng          | LANDLORD        |
| POST   | /api/houses/{houseId}/rooms     | Tạo phòng                | LANDLORD        |
| GET    | /api/rooms/{id}                 | Chi tiết phòng           | LANDLORD/TENANT |
| PUT    | /api/rooms/{id}                 | Cập nhật phòng           | LANDLORD        |
| DELETE | /api/rooms/{id}                 | Xóa phòng                | LANDLORD        |
| GET    | /api/rooms/{id}/services        | Dịch vụ của phòng        | LANDLORD/TENANT |

### Contract API
| Method | Endpoint                  | Mô tả                    | Role            |
|--------|---------------------------|--------------------------|-----------------|
| GET    | /api/contracts            | Danh sách hợp đồng       | LANDLORD/ADMIN  |
| POST   | /api/contracts            | Tạo hợp đồng             | LANDLORD        |
| GET    | /api/contracts/{id}       | Chi tiết hợp đồng        | LANDLORD/TENANT |
| PUT    | /api/contracts/{id}       | Cập nhật hợp đồng        | LANDLORD        |
| PUT    | /api/contracts/{id}/terminate | Chấm dứt hợp đồng    | LANDLORD        |
| GET    | /api/contracts/my         | Hợp đồng của tôi         | TENANT          |

### Meter Reading API
| Method | Endpoint                        | Mô tả                    | Role     |
|--------|---------------------------------|--------------------------|----------|
| GET    | /api/rooms/{roomId}/meters      | Lịch sử chỉ số           | LANDLORD |
| POST   | /api/rooms/{roomId}/meters      | Ghi chỉ số mới           | LANDLORD |
| PUT    | /api/meters/{id}                | Sửa chỉ số               | LANDLORD |
| DELETE | /api/meters/{id}                | Xóa chỉ số               | LANDLORD |

### Invoice API
| Method | Endpoint                        | Mô tả                    | Role            |
|--------|---------------------------------|--------------------------|-----------------|
| GET    | /api/invoices                   | Danh sách hóa đơn        | LANDLORD/ADMIN  |
| POST   | /api/invoices                   | Tạo hóa đơn              | LANDLORD        |
| GET    | /api/invoices/{id}              | Chi tiết hóa đơn         | LANDLORD/TENANT |
| PUT    | /api/invoices/{id}              | Cập nhật hóa đơn         | LANDLORD        |
| GET    | /api/invoices/my                | Hóa đơn của tôi          | TENANT          |
| POST   | /api/invoices/generate-monthly  | Tạo hóa đơn hàng loạt   | LANDLORD        |

### Payment API
| Method | Endpoint                        | Mô tả                    | Role            |
|--------|---------------------------------|--------------------------|-----------------|
| POST   | /api/payments                   | Ghi nhận thanh toán      | LANDLORD/TENANT |
| GET    | /api/payments/{id}              | Chi tiết thanh toán      | LANDLORD/TENANT |
| PUT    | /api/payments/{id}/confirm      | Xác nhận thanh toán      | LANDLORD        |
| GET    | /api/invoices/{id}/payment      | Thanh toán của hóa đơn  | LANDLORD/TENANT |

### Notification API
| Method | Endpoint                        | Mô tả                    | Role |
|--------|---------------------------------|--------------------------|------|
| GET    | /api/notifications              | Danh sách thông báo      | All  |
| PUT    | /api/notifications/{id}/read    | Đánh dấu đã đọc          | All  |
| PUT    | /api/notifications/read-all     | Đọc tất cả               | All  |
| DELETE | /api/notifications/{id}         | Xóa thông báo            | All  |

### Report API
| Method | Endpoint                        | Mô tả                    | Role           |
|--------|---------------------------------|--------------------------|----------------|
| GET    | /api/reports/revenue            | Báo cáo doanh thu        | LANDLORD/ADMIN |
| GET    | /api/reports/occupancy          | Tỷ lệ lấp đầy phòng      | LANDLORD/ADMIN |
| GET    | /api/reports/overdue-invoices   | Hóa đơn quá hạn          | LANDLORD/ADMIN |
| GET    | /api/reports/tenant-summary     | Tổng hợp người thuê      | LANDLORD/ADMIN |

---

## Luồng Nghiệp Vụ: Từ Tạo Phòng Đến Thanh Toán

```
1. LANDLORD tạo nhà trọ (POST /api/houses)
        │
        ▼
2. LANDLORD tạo phòng trong nhà (POST /api/houses/{id}/rooms)
        │
        ▼
3. LANDLORD cấu hình dịch vụ (điện, nước, wifi...) cho nhà
        │
        ▼
4. LANDLORD tạo hợp đồng với TENANT (POST /api/contracts)
   → Room status: AVAILABLE → OCCUPIED
   → TENANT nhận thông báo hợp đồng mới
        │
        ▼
5. Hàng tháng: LANDLORD ghi chỉ số điện nước (POST /api/rooms/{id}/meters)
        │
        ▼
6. LANDLORD tạo hóa đơn tháng (POST /api/invoices)
   → Hệ thống tự tính: tiền phòng + (tiêu thụ × đơn giá) từng dịch vụ
   → TENANT nhận thông báo hóa đơn mới
        │
        ▼
7. TENANT xem hóa đơn (GET /api/invoices/my)
        │
        ▼
8. TENANT thanh toán (POST /api/payments)
   → method: CASH / BANK_TRANSFER / MOMO
        │
        ▼
9. LANDLORD xác nhận thanh toán (PUT /api/payments/{id}/confirm)
   → Invoice status: UNPAID → PAID
   → TENANT nhận thông báo xác nhận thanh toán
        │
        ▼
10. LANDLORD xem báo cáo doanh thu (GET /api/reports/revenue)
```

---

## Yêu Cầu Hệ Thống

### Requirement 1: Xác Thực và Phân Quyền

**User Story:** Là người dùng, tôi muốn đăng nhập an toàn để truy cập đúng chức năng theo vai trò.

#### Acceptance Criteria

1. WHEN người dùng gửi email và mật khẩu hợp lệ, THE AuthService SHALL trả về JWT_Token có thời hạn 24 giờ
2. WHEN người dùng gửi JWT_Token hết hạn, THE AuthService SHALL trả về lỗi 401 Unauthorized
3. IF người dùng gửi sai mật khẩu quá 5 lần liên tiếp, THEN THE AuthService SHALL khóa tài khoản trong 15 phút
4. THE AuthService SHALL mã hóa mật khẩu bằng BCrypt trước khi lưu vào cơ sở dữ liệu
5. WHEN người dùng đổi mật khẩu, THE AuthService SHALL vô hiệu hóa tất cả JWT_Token cũ

---

### Requirement 2: Quản Lý Phòng

**User Story:** Là Landlord, tôi muốn quản lý danh sách phòng để theo dõi tình trạng cho thuê.

#### Acceptance Criteria

1. THE RoomService SHALL chỉ cho phép Landlord truy cập phòng thuộc nhà trọ của mình
2. WHEN Landlord tạo phòng mới, THE RoomService SHALL đặt trạng thái mặc định là AVAILABLE
3. WHEN hợp đồng được tạo cho một phòng, THE RoomService SHALL cập nhật trạng thái phòng thành OCCUPIED
4. WHEN hợp đồng kết thúc hoặc bị chấm dứt, THE RoomService SHALL cập nhật trạng thái phòng thành AVAILABLE
5. IF Landlord xóa phòng đang có hợp đồng ACTIVE, THEN THE RoomService SHALL từ chối và trả về lỗi 409 Conflict

---

### Requirement 3: Quản Lý Hợp Đồng

**User Story:** Là Landlord, tôi muốn tạo và quản lý hợp đồng thuê để có cơ sở pháp lý rõ ràng.

#### Acceptance Criteria

1. WHEN Landlord tạo hợp đồng, THE ContractService SHALL kiểm tra phòng có trạng thái AVAILABLE trước khi tạo
2. IF phòng đã có hợp đồng ACTIVE, THEN THE ContractService SHALL từ chối tạo hợp đồng mới và trả về lỗi 409
3. WHEN hợp đồng được tạo thành công, THE NotificationService SHALL gửi thông báo đến Tenant
4. WHEN ngày hiện tại vượt quá end_date của hợp đồng, THE ContractService SHALL tự động cập nhật trạng thái thành EXPIRED
5. THE ContractService SHALL chỉ cho phép Tenant xem hợp đồng của chính mình

---

### Requirement 4: Ghi Chỉ Số Điện Nước

**User Story:** Là Landlord, tôi muốn ghi chỉ số điện nước hàng tháng để tính tiền chính xác.

#### Acceptance Criteria

1. WHEN Landlord ghi chỉ số mới, THE MeterService SHALL kiểm tra chỉ số hiện tại lớn hơn hoặc bằng chỉ số kỳ trước
2. IF chỉ số hiện tại nhỏ hơn chỉ số kỳ trước, THEN THE MeterService SHALL trả về lỗi 400 Bad Request kèm thông báo rõ ràng
3. THE MeterService SHALL tự động tính consumption = current - previous khi lưu bản ghi
4. WHEN chỉ số được ghi, THE MeterService SHALL lưu thông tin người ghi (created_by) để kiểm tra sau

---

### Requirement 5: Tạo Hóa Đơn

**User Story:** Là Landlord, tôi muốn tạo hóa đơn hàng tháng để thu tiền thuê và dịch vụ.

#### Acceptance Criteria

1. WHEN Landlord tạo hóa đơn, THE InvoiceService SHALL tự động tính tổng tiền từ tiền phòng và các khoản dịch vụ
2. THE InvoiceService SHALL tạo các invoice_items tương ứng cho từng khoản mục trong hóa đơn
3. IF đã tồn tại hóa đơn cho cùng contract_id và billing_month, THEN THE InvoiceService SHALL từ chối tạo và trả về lỗi 409
4. WHEN hóa đơn được tạo, THE NotificationService SHALL gửi thông báo đến Tenant của hợp đồng tương ứng
5. WHEN ngày hiện tại vượt quá due_date và hóa đơn vẫn UNPAID, THE InvoiceService SHALL cập nhật trạng thái thành OVERDUE

---

### Requirement 6: Thanh Toán

**User Story:** Là Tenant, tôi muốn thanh toán hóa đơn và nhận xác nhận để theo dõi lịch sử chi tiêu.

#### Acceptance Criteria

1. WHEN Tenant gửi yêu cầu thanh toán, THE PaymentService SHALL kiểm tra hóa đơn thuộc về Tenant đó trước khi xử lý
2. IF hóa đơn đã có trạng thái PAID, THEN THE PaymentService SHALL từ chối thanh toán và trả về lỗi 409
3. WHEN Landlord xác nhận thanh toán, THE PaymentService SHALL cập nhật trạng thái hóa đơn thành PAID
4. WHEN thanh toán được xác nhận, THE NotificationService SHALL gửi thông báo xác nhận đến Tenant
5. THE PaymentService SHALL lưu thông tin confirmed_by để kiểm tra ai đã xác nhận thanh toán

---

### Requirement 7: Thông Báo

**User Story:** Là người dùng, tôi muốn nhận thông báo kịp thời để không bỏ lỡ thông tin quan trọng.

#### Acceptance Criteria

1. THE NotificationService SHALL gửi thông báo đến đúng người nhận dựa trên user_id
2. WHEN người dùng đọc thông báo, THE NotificationService SHALL cập nhật is_read thành true
3. THE System SHALL chỉ trả về thông báo thuộc về người dùng đang đăng nhập
4. WHEN hóa đơn sắp đến hạn trong 3 ngày, THE NotificationService SHALL tự động gửi thông báo nhắc nhở đến Tenant

---

### Requirement 8: Báo Cáo

**User Story:** Là Landlord, tôi muốn xem báo cáo doanh thu và tình trạng phòng để quản lý hiệu quả.

#### Acceptance Criteria

1. THE ReportService SHALL chỉ trả về dữ liệu thuộc nhà trọ của Landlord đang đăng nhập
2. WHEN Landlord xem báo cáo doanh thu, THE ReportService SHALL tổng hợp theo tháng và năm được chỉ định
3. THE ReportService SHALL tính tỷ lệ lấp đầy = (số phòng OCCUPIED / tổng số phòng) × 100
4. WHEN Admin xem báo cáo, THE ReportService SHALL trả về dữ liệu tổng hợp toàn hệ thống
