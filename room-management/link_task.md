# Kế Hoạch Link Backend ↔ Frontend (Android)

## Tổng Quan

Mục tiêu: Redesign toàn bộ UI Android theo design system từ Google Stitch (project "Smart Room Manager"), đồng thời fix các lỗi kết nối backend còn tồn đọng.

**Stitch Project:** `projects/15090967849740412956`
**Design System:** The Architectural Concierge — Teal gradient, Be Vietnam Pro, no-border cards, glassmorphism

---

## Phase 4 — Nền Tảng (Fix trước khi redesign UI)

- [x] 4.1 Cập nhật `Color.kt` theo design tokens Stitch (primary #006b5f, gradient, surface hierarchy)
- [x] 4.2 Cập nhật `Theme.kt` — thêm Tertiary color scheme
- [x] 4.3 Fix `ApiService.kt` — `getMe()` trả về `UserResponse` thay `TenantResponse`
- [x] 4.4 Thêm `UserResponse` data class vào `AuthDto.kt`
- [x] 4.5 Cập nhật `MiscDto.kt` — bổ sung fields còn thiếu trong `MeterReadingResponse`
- [x] 4.6 Cập nhật `MiscDto.kt` — bổ sung fields còn thiếu trong `DebtReportResponse.DebtDetail`
- [x] 4.7 Thêm `PUT /api/users/me` và `PUT /api/users/me/password` vào `ApiService.kt`
- [x] 4.8 Thêm `UpdateProfileRequest`, `ChangePasswordRequest` vào `AuthDto.kt`

---

## Phase 1 — Auth Flow

- [x] 1.1 Redesign `SplashScreen.kt` theo Stitch screen `f81eeedff6de4b229486ed3bb9f93b45`
- [x] 1.2 Redesign `LoginScreen.kt` theo Stitch screen `e4f838cf6b3c47a89a78084929878bf0`
- [x] 1.3 Redesign `RegisterScreen.kt` theo Stitch screen `e0d030cf2be34251bf597b698755922f`

---

## Phase 2 — Landlord Flow

- [x] 2.1 Redesign `DashboardScreen.kt` theo Stitch screen `fefef75ab68b446d9469d55bb63bfa99`
  - Fix RecentActivitySection dùng dữ liệu thực từ API thay mock
- [x] 2.2 Redesign `RoomListScreen.kt` theo Stitch screen `84aba406c8d04292ae7943afa15a2caf`
  - Thêm nút điều hướng đến MeterReadingScreen từ mỗi RoomCard
- [x] 2.3 Redesign `RoomFormScreen.kt` theo Stitch screen `494a2c7ea6ad4e3a896085b6502e0fd6`
- [x] 2.4 Redesign `ContractListScreen.kt` theo Stitch screen `1a035384acba4d489ef7e6037c253b9a`
- [x] 2.5 Redesign `ContractFormScreen.kt` theo Stitch screen `0cf764eca75a4d2780945cac750d837b`
- [x] 2.6 Redesign `MeterReadingScreen.kt` theo Stitch screen `9395530fd58f413190ae4c665124921b`
- [x] 2.7 Redesign `InvoiceListScreen.kt` theo Stitch screen `f36c2ead5b2a42428a5c4c11a7bbef5b`
- [x] 2.8 Redesign `InvoiceDetailScreen.kt` theo Stitch screen `7bbe3e3d9a0a44288f7db9f95daf5270`
- [x] 2.9 Redesign `PaymentHistoryScreen.kt` theo Stitch screen `f68d807c5c4e4620af24601dd33e5725`
- [x] 2.10 Redesign `NotificationScreen.kt` theo Stitch screen `eee7b6ad9e8044ada7524179683a410f`
- [x] 2.11 Redesign `ReportScreen.kt` theo Stitch screen `1f36761976b243f48561d4f678970ddf`

---

## Phase 3 — Tenant Flow

- [x] 3.1 Redesign `TenantHomeScreen.kt` theo Stitch screen `03907b26a0964a8497c41434d1c32d6f`
- [x] 3.2 Redesign `MyRoomScreen.kt` theo Stitch screen `4fd520596fbe4bf0974cabed57afaa28`
- [x] 3.3 Redesign `MyContractScreen.kt` theo Stitch screen `d1ee7c739acf4a66be9a5bcaa176a512`
  - Fix hiển thị `landlordName` thay `landlordId`
- [x] 3.4 Redesign `TenantInvoiceListScreen.kt` theo Stitch screen `94f7025abdc04a6c83925d782e938680`
- [x] 3.5 Redesign `TenantInvoiceDetailScreen.kt` theo Stitch screen `522f31c32123450b8fdfa242e3fc2e2f`
- [x] 3.6 Redesign `TenantNotificationScreen.kt` theo Stitch screen `b168a71742c3495e80ee106a7042af0b`

---

## Phase 5 — Shared Screens

- [x] 5.1 Redesign `ProfileScreen.kt` theo Stitch screen `3343af65c2eb48289f8d0d72093ccc8d`
  - Gọi `GET /api/auth/me` để hiển thị fullName, email, phone
  - Thêm form cập nhật profile (`PUT /api/users/me`)
  - Thêm form đổi mật khẩu (`PUT /api/users/me/password`)
  - Tạo `ProfileViewModel.kt` mới

---

## Mapping Stitch Screen ID

| Màn hình | Stitch Screen ID |
|----------|-----------------|
| Splash Screen | `f81eeedff6de4b229486ed3bb9f93b45` |
| Login Screen | `e4f838cf6b3c47a89a78084929878bf0` |
| Register Screen | `e0d030cf2be34251bf597b698755922f` |
| Landlord Dashboard | `fefef75ab68b446d9469d55bb63bfa99` |
| Room List Screen | `84aba406c8d04292ae7943afa15a2caf` |
| Room Form Screen | `494a2c7ea6ad4e3a896085b6502e0fd6` |
| Contract List Screen | `1a035384acba4d489ef7e6037c253b9a` |
| Contract Form Screen | `0cf764eca75a4d2780945cac750d837b` |
| Meter Reading Screen | `9395530fd58f413190ae4c665124921b` |
| Invoice List Screen | `f36c2ead5b2a42428a5c4c11a7bbef5b` |
| Invoice Detail Screen | `7bbe3e3d9a0a44288f7db9f95daf5270` |
| Payment History Screen | `f68d807c5c4e4620af24601dd33e5725` |
| Notification Screen (Landlord) | `eee7b6ad9e8044ada7524179683a410f` |
| Report Screen | `1f36761976b243f48561d4f678970ddf` |
| Profile Screen | `3343af65c2eb48289f8d0d72093ccc8d` |
| Tenant Home Screen | `03907b26a0964a8497c41434d1c32d6f` |
| My Room Screen (Tenant) | `4fd520596fbe4bf0974cabed57afaa28` |
| My Contract Screen (Tenant) | `d1ee7c739acf4a66be9a5bcaa176a512` |
| Tenant Invoice List Screen | `94f7025abdc04a6c83925d782e938680` |
| Tenant Invoice Detail Screen | `522f31c32123450b8fdfa242e3fc2e2f` |
| Tenant Notification Screen | `b168a71742c3495e80ee106a7042af0b` |

---

## Quy Tắc Thực Hiện

1. Với mỗi task redesign: lấy HTML từ Stitch → phân tích layout/colors → viết Composable mới
2. Giữ nguyên ViewModel và ApiService logic, chỉ thay UI layer
3. Dùng color tokens từ `Color.kt` đã cập nhật ở Phase 4, không hardcode màu
4. Không dùng divider lines (theo design system "no-line rule")
5. Tất cả card dùng `surface_container_lowest` (#ffffff) trên nền `surface_container_low` (#f1f4f4)
6. Primary button dùng gradient từ `primary` (#006b5f) đến `primary_container` (#38a596)
