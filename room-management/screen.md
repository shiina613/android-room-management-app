# Danh Sách Màn Hình Android - Hệ Thống Quản Lý Phòng Trọ

---

## 1. SplashScreen

**Route:** `splash`
**Điều kiện vào:** Màn hình khởi động mặc định khi mở app.

**Mô tả:** Hiển thị logo, tên app và loading indicator trong khi kiểm tra token đã lưu.

**API sử dụng:** Không gọi API trực tiếp. Đọc token từ DataStore local.

**Bố cục:**
- Nền gradient teal nhạt
- Logo hình vuông bo góc (icon 🏠) ở giữa màn hình
- Tiêu đề "Quản Lý Phòng Trọ"
- Subtitle "Hệ thống quản lý thông minh"
- CircularProgressIndicator
- Badge "Hệ thống bảo mật" ở dưới cùng

**Nút / Hành động:** Không có nút. Tự động điều hướng sau 1.5 giây.

**Điều hướng đi:**
- Token hợp lệ + role = `ROLE_LANDLORD` → `LandlordDashboard`
- Token hợp lệ + role = `ROLE_TENANT` → `TenantDashboard`
- Không có token / hết hạn → `LoginScreen`

---

## 2. LoginScreen

**Route:** `login`
**Điều kiện vào:** Từ SplashScreen (không có token) hoặc sau khi logout.

**Mô tả:** Màn hình đăng nhập bằng username + password.

**API sử dụng:**
- `POST /api/auth/login`
  - Input: `{ username, password }`
  - Output: `{ accessToken, role, userId, fullName }`

**Bố cục:**
- Nền trắng với vòng tròn trang trí góc trên trái và dưới phải
- Header: logo + tên app
- Card trắng bo góc chứa form:
  - TextField "Tên đăng nhập"
  - TextField "Mật khẩu" (có toggle hiện/ẩn)
  - Text lỗi (nếu có)
  - Button "Đăng nhập"
- Link "Chưa có tài khoản? Đăng ký ngay"

**Nút:**
- "Đăng nhập" — gọi API login, disabled khi field trống hoặc đang loading
- "Đăng ký ngay" (TextButton) — điều hướng sang RegisterScreen

**Điều hướng đi:**
- Đăng nhập thành công + `ROLE_LANDLORD` → `LandlordDashboard`
- Đăng nhập thành công + `ROLE_TENANT` → `TenantDashboard`
- Nhấn "Đăng ký ngay" → `RegisterScreen`

---

## 3. RegisterScreen

**Route:** `register`
**Điều kiện vào:** Từ LoginScreen nhấn "Đăng ký ngay".

**Mô tả:** Đăng ký tài khoản mới (Chủ trọ hoặc Người thuê).

**API sử dụng:**
- `POST /api/auth/register`
  - Input: `{ username, password, email, fullName, phone, role }`
  - Output: `{ accessToken, role, userId, fullName }`

**Bố cục (scrollable):**
- TopAppBar với nút Back
- Các TextField: Họ tên, Tên đăng nhập, Email, Số điện thoại, Mật khẩu
- FilterChip chọn loại tài khoản: "Chủ trọ" / "Người thuê"
- Text lỗi (nếu có)
- Button "Đăng ký"

**Nút:**
- Back (icon) → quay lại LoginScreen
- "Đăng ký" — gọi API register, disabled khi có field trống

**Điều hướng đi:**
- Đăng ký thành công → `LoginScreen` (popUpTo RegisterScreen)

---

## 4. LandlordDashboard (DashboardScreen)

**Route:** `landlord/dashboard`
**Điều kiện vào:** Đăng nhập thành công với role `ROLE_LANDLORD`, hoặc SplashScreen phát hiện token hợp lệ của landlord.

**Mô tả:** Màn hình tổng quan dành cho chủ trọ. Hiển thị các chỉ số tổng hợp: số phòng, doanh thu, hóa đơn chưa thanh toán.

**API sử dụng:**
- `GET /api/reports/dashboard`
  - Output: `{ totalRooms, occupiedRooms, availableRooms, maintenanceRooms, occupancyRate, revenueThisMonth, collectedThisMonth, debtThisMonth, activeContracts, unpaidInvoices, overdueInvoices, totalTenants, ... }`

**Bố cục:**
- TopAppBar: logo + tên app, icon Thông báo, icon Profile
- Body (scrollable):
  - Lời chào "Xin chào, [tên] 👋"
  - Stats Grid (2x2): Tổng phòng / Đang thuê / Phòng trống / Chưa thanh toán
  - Card doanh thu tháng (full width, nền Primary)
  - Truy cập nhanh: 4 shortcut (Phòng / Hợp đồng / Hóa đơn / Báo cáo)
  - Hoạt động gần đây (placeholder tĩnh)
- BottomNavigationBar: Tổng quan / Phòng / Hợp đồng / Báo cáo
- FAB (+) góc dưới phải

**Nút / Hành động:**
- Icon Thông báo → `NotificationScreen`
- Icon Profile → `ProfileScreen`
- FAB (+) → `RoomListScreen`
- Shortcut "Phòng" → `RoomListScreen`
- Shortcut "Hợp đồng" → `ContractListScreen`
- Shortcut "Hóa đơn" → `InvoiceListScreen`
- Shortcut "Báo cáo" → `ReportScreen`
- BottomNav tab "Phòng" → `RoomListScreen`
- BottomNav tab "Hợp đồng" → `ContractListScreen`
- BottomNav tab "Báo cáo" → `ReportScreen`

---

## 5. RoomListScreen

**Route:** `landlord/rooms`
**Điều kiện vào:** Từ Dashboard (shortcut, FAB, hoặc BottomNav tab "Phòng").

**Mô tả:** Danh sách tất cả phòng của chủ trọ, có tìm kiếm và lọc theo trạng thái.

**API sử dụng:**
- `GET /api/rooms?status=&keyword=&page=0&size=10`
  - Output: `PageResponse<RoomResponse>` gồm `{ id, title, address, price, status, category, elecPrice, waterPrice, servicePrice, description }`

**Bố cục:**
- TopAppBar với nút Back
- SearchBar "Tìm kiếm phòng..."
- Mini stats bar: Tổng / Trống / Đang thuê / Bảo trì (4 card nhỏ)
- FilterChips: Tất cả / Trống / Đang thuê / Bảo trì
- LazyColumn danh sách RoomCard:
  - Ảnh placeholder (TODO: AsyncImage)
  - Tên phòng + StatusChip
  - Địa chỉ
  - Giá + loại phòng
- FAB (+) thêm phòng mới

**Nút / Hành động:**
- Back → quay lại Dashboard
- FAB (+) → `RoomFormScreen` (tạo mới)
- Nhấn vào RoomCard → `RoomFormScreen` (chỉnh sửa, truyền `roomId`)

---

## 6. RoomFormScreen (Tạo / Chỉnh sửa phòng)

**Route:**
- Tạo mới: `landlord/rooms/create`
- Chỉnh sửa: `landlord/rooms/{roomId}`

**Điều kiện vào:**
- Tạo mới: FAB từ RoomListScreen
- Chỉnh sửa: Nhấn vào RoomCard trong RoomListScreen

**Mô tả:** Form tạo hoặc chỉnh sửa thông tin phòng.

**API sử dụng:**
- Tạo mới: `POST /api/rooms`
  - Input: `{ title, address, price, elecPrice, waterPrice, servicePrice, status, category, description }`
  - Output: `RoomResponse`
- Chỉnh sửa: `GET /api/rooms/{id}` (load dữ liệu) + `PUT /api/rooms/{id}`
  - Input/Output: tương tự

**Bố cục (scrollable):**
- TopAppBar: "Thêm phòng mới" hoặc "Chỉnh sửa phòng" + nút Back
- Placeholder ảnh phòng (📷)
- SectionCard "Thông tin cơ bản": Tên phòng, Địa chỉ, FilterChip loại phòng (Studio/Phòng đơn/Căn hộ/Phòng ghép), Mô tả
- SectionCard "Giá cả": Giá thuê/tháng, Giá điện (₫/kWh), Giá nước (₫/m³), Phí dịch vụ/tháng
- Info card ghi chú về giá điện nước
- Text lỗi (nếu có)
- Button "Tạo phòng" / "Lưu thay đổi"

**Nút / Hành động:**
- Back → quay lại RoomListScreen
- "Tạo phòng" / "Lưu thay đổi" → gọi API, sau đó popBackStack

---

## 7. ContractListScreen

**Route:** `landlord/contracts`
**Điều kiện vào:** Từ Dashboard (shortcut "Hợp đồng" hoặc BottomNav).

**Mô tả:** Danh sách hợp đồng thuê của chủ trọ, lọc theo trạng thái.

**API sử dụng:**
- `GET /api/contracts?status=&page=0&size=10`
  - Output: `PageResponse<ContractResponse>` gồm `{ id, roomId, roomTitle, tenantId, tenantName, landlordId, startDate, endDate, monthlyRent, deposit, status }`

**Bố cục:**
- TopAppBar với nút Back
- FilterChips: Tất cả / Hiệu lực / Hết hạn / Đã chấm dứt
- LazyColumn danh sách ContractCard:
  - Tên phòng + StatusChip
  - Tên người thuê
  - Ngày bắt đầu / Tiền thuê / Ngày kết thúc
- FAB (+) tạo hợp đồng mới

**Nút / Hành động:**
- Back → quay lại Dashboard
- FAB (+) → `ContractFormScreen`
- Nhấn ContractCard → `ContractFormScreen` (hiện tại reuse form, chưa có màn hình detail riêng)

---

## 8. ContractFormScreen (Tạo hợp đồng)

**Route:** `landlord/contracts/create`
**Điều kiện vào:** FAB từ ContractListScreen.

**Mô tả:** Form tạo hợp đồng mới, chọn phòng trống và người thuê.

**API sử dụng:**
- `GET /api/rooms?status=AVAILABLE` — load danh sách phòng trống
- `GET /api/tenants` — load danh sách người thuê
- `POST /api/contracts`
  - Input: `{ roomId, tenantId, startDate, endDate, deposit, monthlyRent }`
  - Output: `ContractResponse`

**Bố cục (scrollable):**
- TopAppBar "Tạo hợp đồng" + nút Back
- SectionCard "Thông tin hợp đồng":
  - Dropdown chọn phòng (chỉ hiện phòng AVAILABLE)
  - Dropdown chọn người thuê
- SectionCard "Thời hạn & Tài chính":
  - TextField Ngày bắt đầu / Ngày kết thúc (2 cột)
  - TextField Tiền thuê/tháng (tự điền từ giá phòng)
  - TextField Tiền cọc
- Preview card hợp đồng (hiện khi đã chọn đủ phòng + người thuê)
- Text lỗi (nếu có)
- Button "Tạo hợp đồng"

**Nút / Hành động:**
- Back → quay lại ContractListScreen
- "Tạo hợp đồng" → gọi API, sau đó popBackStack

---

## 9. MeterReadingScreen (Nhập chỉ số điện nước)

**Route:** `landlord/meter-readings/{roomId}`
**Điều kiện vào:** Chưa có shortcut trực tiếp trên UI hiện tại — cần điều hướng thủ công qua `NavRoutes.meterReading(roomId)`. (Thường được gọi từ màn hình chi tiết phòng hoặc tạo hóa đơn.)

**Mô tả:** Nhập chỉ số điện và nước cho một phòng cụ thể theo kỳ tháng.

**API sử dụng:**
- `GET /api/meter-readings/rooms/{roomId}` — load lịch sử chỉ số (hiển thị 3 kỳ gần nhất)
  - Output: `PageResponse<MeterReadingResponse>`
- `POST /api/meter-readings`
  - Input: `{ roomId, billingMonth, electricPrevious, electricCurrent, waterPrevious, waterCurrent }`
  - Output: `MeterReadingResponse`

**Bố cục (scrollable):**
- TopAppBar "Nhập chỉ số điện nước" + nút Back
- SectionCard kỳ tính: TextField "Kỳ tính (YYYY-MM)"
- SectionCard "⚡ Điện": Chỉ số đầu / Chỉ số cuối (2 cột), hiển thị tiêu thụ tính toán realtime
- SectionCard "💧 Nước": tương tự
- SectionCard "Lịch sử gần đây": 3 kỳ gần nhất (billingMonth + electricUsage + waterUsage)
- Text lỗi (nếu có)
- Button "Lưu chỉ số"

**Logic client:**
- Chỉ số đầu tự điền từ `latestReading.electricCurrent` / `waterCurrent`
- Tiêu thụ = cuối - đầu, hiển thị màu đỏ nếu âm
- Button disabled nếu tiêu thụ âm

**Nút / Hành động:**
- Back → quay lại màn hình trước
- "Lưu chỉ số" → gọi API, sau đó popBackStack

---

## 10. InvoiceListScreen

**Route:** `landlord/invoices`
**Điều kiện vào:** Từ Dashboard shortcut "Hóa đơn".

**Mô tả:** Danh sách hóa đơn của chủ trọ, lọc theo trạng thái thanh toán.

**API sử dụng:**
- `GET /api/invoices/my?status=&page=0`
  - Output: `PageResponse<InvoiceResponse>` gồm `{ id, contractId, billingMonth, totalAmount, dueDate, status, roomTitle, tenantName, landlordName, breakdown }`

**Bố cục:**
- TopAppBar "Hóa đơn" + nút Back
- 2 summary card: Chưa thanh toán (tổng tiền + số lượng) / Đã thanh toán
- FilterChips: Tất cả / Chưa TT / Đã TT / Quá hạn
- LazyColumn danh sách InvoiceCard:
  - Icon trạng thái (màu theo status)
  - "Hóa đơn tháng [billingMonth]" + "Hợp đồng #[id]"
  - Hạn thanh toán
  - Tổng tiền + StatusChip
- FAB (+) tạo hóa đơn mới

**Nút / Hành động:**
- Back → quay lại Dashboard
- FAB (+) → hiện tại route `INVOICE_CREATE` reuse InvoiceListScreen (chưa có form tạo hóa đơn riêng)
- Nhấn InvoiceCard → `InvoiceDetailScreen`

---

## 11. InvoiceDetailScreen

**Route:** `landlord/invoices/{invoiceId}`
**Điều kiện vào:** Nhấn vào InvoiceCard trong InvoiceListScreen.

**Mô tả:** Chi tiết hóa đơn: breakdown các khoản, thông tin hợp đồng, nút ghi nhận thanh toán.

**API sử dụng:**
- `GET /api/invoices/{id}`
  - Output: `InvoiceResponse` gồm `{ id, billingMonth, totalAmount, dueDate, status, roomTitle, tenantName, landlordName, breakdown: { rentAmount, electricUsage, electricAmount, waterUsage, waterAmount, serviceAmount } }`

**Bố cục (scrollable):**
- TopAppBar "Chi tiết hóa đơn" + nút Back
- Header card (nền Primary): tháng hóa đơn, tổng tiền, StatusChip, hạn thanh toán
- SectionCard "Thông tin": Phòng / Người thuê / Chủ trọ
- SectionCard "Chi tiết các khoản": Tiền thuê / Tiền điện (kWh) / Tiền nước (m³) / Phí dịch vụ / Tổng cộng
- Button "Ghi nhận thanh toán" (chỉ hiện khi status != PAID)

**Nút / Hành động:**
- Back → quay lại InvoiceListScreen
- "Ghi nhận thanh toán" → `PaymentHistoryScreen` (truyền `invoiceId`)

---

## 12. PaymentHistoryScreen (Thanh toán hóa đơn)

**Route:** `landlord/payments/create/{invoiceId}`
**Điều kiện vào:** Nhấn "Ghi nhận thanh toán" từ InvoiceDetailScreen.

**Mô tả:** Ghi nhận thanh toán cho một hóa đơn cụ thể và xem lịch sử các lần thanh toán.

**API sử dụng:**
- `GET /api/invoices/{id}` — load thông tin hóa đơn
- `GET /api/payments/invoices/{invoiceId}` — load lịch sử thanh toán
  - Output: `PageResponse<PaymentResponse>` gồm `{ id, amount, paymentMethod, paidAt, note }`
- `POST /api/payments`
  - Input: `{ invoiceId, amount, method, note }`
  - Output: `PaymentResponse`

**Bố cục:**
- TopAppBar "Thanh toán hóa đơn" + nút Back
- Card tóm tắt hóa đơn (nền Primary): tháng, tổng tiền, đã TT, còn lại
- SectionCard "Ghi nhận thanh toán" (chỉ hiện khi status != PAID):
  - TextField số tiền
  - FilterChips phương thức: Tiền mặt / Chuyển khoản / MoMo
  - TextField ghi chú
  - Button "Xác nhận thanh toán"
- Tiêu đề "Lịch sử"
- LazyColumn danh sách payment: phương thức, ngày giờ, ghi chú, số tiền

**Nút / Hành động:**
- Back → quay lại InvoiceDetailScreen
- "Xác nhận thanh toán" → gọi API, sau đó popBackStack

---

    ## 13. NotificationScreen (Landlord)

    **Route:** `notifications`
    **Điều kiện vào:** Nhấn icon chuông trên TopAppBar của LandlordDashboard.

    **Mô tả:** Danh sách thông báo của chủ trọ, có thể đánh dấu đã đọc.

    **API sử dụng:**
    - `GET /api/notifications?page=0` — load danh sách
    - Output: `PageResponse<NotificationResponse>` gồm `{ id, title, content, type, read, referenceId, createdAt }`
    - `GET /api/notifications/unread-count`
    - Output: `{ count }`
    - `PATCH /api/notifications/{id}/read` — đánh dấu 1 thông báo đã đọc
    - `PATCH /api/notifications/read-all` — đọc tất cả

    **Bố cục:**
    - TopAppBar "Thông báo" + nút Back + TextButton "Đọc tất cả" (hiện khi có unread)
    - Banner "Bạn có X thông báo chưa đọc" (nền PrimaryContainer, hiện khi unreadCount > 0)
    - LazyColumn danh sách NotificationItem:
    - Icon theo type (INVOICE/PAYMENT/CONTRACT/ROOM_STATUS/OVERDUE)
    - Tiêu đề + nội dung (2 dòng) + ngày
    - Chấm xanh nếu chưa đọc
    - Nền nhạt hơn nếu chưa đọc

    **Nút / Hành động:**
    - Back → quay lại Dashboard
    - "Đọc tất cả" → gọi API markAllRead
    - Nhấn vào NotificationItem (chưa đọc) → gọi API markRead

---

## 14. ReportScreen

**Route:** `landlord/reports`
**Điều kiện vào:** Từ Dashboard shortcut "Báo cáo" hoặc BottomNav tab "Báo cáo".

**Mô tả:** Báo cáo tổng hợp: doanh thu, tỷ lệ lấp đầy, công nợ.

**API sử dụng:**
- `GET /api/reports/dashboard`
  - Output: `DashboardResponse` (revenueThisMonth, debtThisMonth, occupancyRate, totalTenants, totalRooms, occupiedRooms, availableRooms, maintenanceRooms)
- `GET /api/reports/revenue/yearly?year={year}`
  - Output: `RevenueReportResponse` gồm `{ year, totalInvoiced, totalCollected, totalDebt, monthly: [{ month, invoiced, collected, debt, invoiceCount }] }`
- `GET /api/reports/debt`
  - Output: `DebtReportResponse` gồm `{ totalDebt, debtorCount, details: [{ invoiceId, billingMonth, tenantName, roomTitle, remaining, dueDate, status }] }`

**Bố cục (scrollable):**
- TopAppBar "Báo cáo" + nút Back
- 2 summary card (hàng 1): Doanh thu tháng này / Công nợ
- 2 summary card (hàng 2): Tỷ lệ lấp đầy / Tổng người thuê
- SectionCard "Doanh thu 12 tháng": Bar chart đơn giản (12 cột theo tháng), tổng năm
- SectionCard "Tình trạng phòng": 3 LinearProgressBar (Đang thuê / Phòng trống / Bảo trì)
- SectionCard "Công nợ": danh sách tối đa 5 khoản nợ (phòng + người thuê + số tiền), hiển thị thêm nếu > 5

**Nút / Hành động:**
- Back → quay lại Dashboard

---

## 15. ProfileScreen

**Route:** `profile`
**Điều kiện vào:** Nhấn icon avatar trên TopAppBar của Dashboard (cả Landlord lẫn Tenant).

**Mô tả:** Hồ sơ cá nhân, hiển thị vai trò và nút đăng xuất.

**API sử dụng:**
- Hiện tại chỉ đọc `role` từ DataStore local (token). Chưa gọi `GET /api/auth/me` để lấy fullName/email/phone.

**Bố cục (scrollable):**
- TopAppBar "Hồ sơ cá nhân" + nút Back
- Card avatar (nền Primary): icon 👤, hiển thị vai trò (Chủ trọ / Người thuê / Quản trị viên)
- SectionCard "Thông tin tài khoản": InfoRow "Vai trò"
- OutlinedButton "Đăng xuất" (màu đỏ)
- AlertDialog xác nhận đăng xuất

**Nút / Hành động:**
- Back → quay lại màn hình trước
- "Đăng xuất" → hiện dialog xác nhận
- Xác nhận đăng xuất → xóa token, navigate về `LoginScreen` (popUpTo 0)

---

## 16. TenantHomeScreen (TenantDashboard)

**Route:** `tenant/dashboard`
**Điều kiện vào:** Đăng nhập thành công với role `ROLE_TENANT`, hoặc SplashScreen phát hiện token hợp lệ của tenant.

**Mô tả:** Trang chủ dành cho người thuê. Hiển thị thông tin phòng đang thuê, hóa đơn mới nhất, thông báo chưa đọc.

**API sử dụng:**
- `GET /api/contracts/my` — lấy hợp đồng đang ACTIVE
  - Output: `PageResponse<ContractResponse>`
- `GET /api/invoices/tenant/me?page=0` — lấy hóa đơn mới nhất
  - Output: `PageResponse<InvoiceResponse>`
- `GET /api/notifications?page=0` — lấy thông báo chưa đọc
  - Output: `PageResponse<NotificationResponse>`
- `GET /api/notifications/unread-count`

**Bố cục (scrollable):**
- TopAppBar: logo + "Phòng Trọ", icon chuông (badge số unread), icon avatar
- Lời chào "Xin chào, [tên] 👋"
- Card phòng đang thuê (nền Primary): tên phòng, badge "Đang thuê", tiền thuê/tháng (chỉ hiện khi có hợp đồng ACTIVE)
- Card hóa đơn mới nhất: tháng, tổng tiền, StatusChip, hạn thanh toán (chỉ hiện khi có)
- SectionCard thông báo chưa đọc (tối đa 3, chỉ hiện khi có)
- Card "Cần hỗ trợ?" với nút "Liên hệ" (placeholder)
- BottomNavigationBar: Trang chủ / Phòng tôi / Hợp đồng / Hóa đơn

**Nút / Hành động:**
- Icon chuông → `TenantNotificationScreen`
- Icon avatar → `ProfileScreen`
- BottomNav "Phòng tôi" → `MyRoomScreen`
- BottomNav "Hợp đồng" → `MyContractScreen`
- BottomNav "Hóa đơn" → `TenantInvoiceListScreen`

---

## 17. MyRoomScreen

**Route:** `tenant/room`
**Điều kiện vào:** BottomNav "Phòng tôi" từ TenantHomeScreen.

**Mô tả:** Thông tin chi tiết phòng đang thuê của người thuê.

**API sử dụng:**
- Dùng lại `activeContract` từ `TenantViewModel` (đã load ở TenantHomeScreen)
- `GET /api/contracts/my` (nếu chưa load)
  - Output: `ContractResponse` gồm `{ roomTitle, roomAddress, monthlyRent, deposit, startDate, endDate, landlordName, status }`

**Bố cục (scrollable):**
- TopAppBar "Phòng của tôi" + nút Back
- Nếu chưa có hợp đồng: text "Bạn chưa có phòng đang thuê"
- Nếu có hợp đồng:
  - Ảnh placeholder phòng (200dp)
  - SectionCard tên phòng: địa chỉ + StatusChip "OCCUPIED"
  - SectionCard "Thông tin hợp đồng": Tiền thuê / Tiền cọc / Bắt đầu / Kết thúc / Chủ trọ

**Nút / Hành động:**
- Back → quay lại TenantHomeScreen

---

## 18. MyContractScreen

**Route:** `tenant/contract`
**Điều kiện vào:** BottomNav "Hợp đồng" từ TenantHomeScreen.

**Mô tả:** Chi tiết hợp đồng thuê của người thuê.

**API sử dụng:**
- Dùng lại `activeContract` từ `TenantViewModel`
  - Output: `ContractResponse` gồm `{ roomId, roomTitle, landlordId, startDate, endDate, monthlyRent, deposit, status }`

**Bố cục (scrollable):**
- TopAppBar "Hợp đồng của tôi" + nút Back
- Nếu chưa có: icon 📄 + text "Bạn chưa có hợp đồng nào"
- Nếu có:
  - Header card (nền Primary): icon hợp đồng, tên phòng, StatusChip
  - SectionCard "Chi tiết hợp đồng": Phòng / Chủ trọ (hiện ID, chưa có tên) / Ngày bắt đầu / Ngày kết thúc / Tiền thuê / Tiền cọc

**Nút / Hành động:**
- Back → quay lại TenantHomeScreen

---

## 19. TenantInvoiceListScreen

**Route:** `tenant/invoices`
**Điều kiện vào:** BottomNav "Hóa đơn" từ TenantHomeScreen.

**Mô tả:** Danh sách hóa đơn của người thuê, lọc theo trạng thái.

**API sử dụng:**
- `GET /api/invoices/tenant/me?status=&page=0`
  - Output: `PageResponse<InvoiceResponse>`

**Bố cục:**
- TopAppBar "Hóa đơn của tôi" + nút Back
- 2 summary card: Cần thanh toán (tổng tiền UNPAID) / Tổng hóa đơn (số lượng)
- FilterChips: Tất cả / Chưa TT / Đã TT
- LazyColumn danh sách InvoiceCard (reuse component từ landlord)

**Nút / Hành động:**
- Back → quay lại TenantHomeScreen
- Nhấn InvoiceCard → `TenantInvoiceDetailScreen`

---

## 20. TenantInvoiceDetailScreen

**Route:** `tenant/invoices/{invoiceId}`
**Điều kiện vào:** Nhấn vào InvoiceCard trong TenantInvoiceListScreen.

**Mô tả:** Chi tiết hóa đơn dành cho người thuê (chỉ xem, không có nút thanh toán).

**API sử dụng:**
- `GET /api/invoices/{id}`
  - Output: `InvoiceResponse` (tương tự InvoiceDetailScreen của landlord)

**Bố cục (scrollable):**
- TopAppBar "Chi tiết hóa đơn" + nút Back
- Header card: màu xanh nếu PAID, màu Primary nếu chưa — icon, tháng, tổng tiền, StatusChip, hạn thanh toán
- SectionCard "Chi tiết các khoản": Tiền thuê / Tiền điện / Tiền nước / Phí dịch vụ / Tổng cộng
- Info card: "Vui lòng thanh toán đúng hạn. Liên hệ chủ trọ nếu có thắc mắc."

**Nút / Hành động:**
- Back → quay lại TenantInvoiceListScreen
- Không có nút thanh toán (tenant chỉ xem)

---

## 21. TenantNotificationScreen

**Route:** `tenant/notifications`
**Điều kiện vào:** Nhấn icon chuông trên TopAppBar của TenantHomeScreen.

**Mô tả:** Danh sách thông báo của người thuê. Giống NotificationScreen của landlord, dùng chung `NotificationViewModel`.

**API sử dụng:** Giống màn hình 13 (NotificationScreen landlord):
- `GET /api/notifications?page=0`
- `GET /api/notifications/unread-count`
- `PATCH /api/notifications/{id}/read`
- `PATCH /api/notifications/read-all`

**Bố cục:**
- TopAppBar "Thông báo" + nút Back + TextButton "Đọc tất cả"
- Banner unread count (nếu có)
- LazyColumn danh sách NotificationItem (reuse component từ landlord)

**Nút / Hành động:**
- Back → quay lại TenantHomeScreen
- "Đọc tất cả" → markAllRead
- Nhấn item chưa đọc → markRead

---

## Tóm Tắt Navigation Graph

```
SplashScreen
├── (LANDLORD) → LandlordDashboard
│     ├── RoomListScreen → RoomFormScreen (create/edit)
│     ├── ContractListScreen → ContractFormScreen
│     ├── InvoiceListScreen → InvoiceDetailScreen → PaymentHistoryScreen
│     ├── ReportScreen
│     ├── NotificationScreen
│     └── ProfileScreen (logout → LoginScreen)
│
├── (TENANT) → TenantDashboard
│     ├── MyRoomScreen
│     ├── MyContractScreen
│     ├── TenantInvoiceListScreen → TenantInvoiceDetailScreen
│     ├── TenantNotificationScreen
│     └── ProfileScreen (logout → LoginScreen)
│
└── (no token) → LoginScreen ↔ RegisterScreen
```

---

## Ghi Chú Thiếu Sót

| Vấn đề | Màn hình liên quan |
|--------|-------------------|
| ProfileScreen chưa gọi `GET /api/auth/me` để hiển thị fullName/email/phone | ProfileScreen |
| MyContractScreen hiển thị `landlordId` thay vì `landlordName` | MyContractScreen |
| Chưa có màn hình tạo hóa đơn riêng (InvoiceFormScreen) | InvoiceListScreen FAB |
| MeterReadingScreen không có shortcut trực tiếp từ UI | MeterReadingScreen |
| RecentActivitySection trong Dashboard dùng dữ liệu tĩnh (placeholder) | LandlordDashboard |
| Nút "Liên hệ" trong TenantHomeScreen chưa có chức năng | TenantHomeScreen |
