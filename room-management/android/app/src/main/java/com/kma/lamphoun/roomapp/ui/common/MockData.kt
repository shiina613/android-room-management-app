package com.kma.lamphoun.roomapp.ui.common

import com.kma.lamphoun.roomapp.data.remote.dto.*

object MockData {

    val dashboard = DashboardResponse(
        totalRooms = 12,
        occupiedRooms = 9,
        availableRooms = 2,
        totalTenants = 9,
        revenueThisMonth = 31_500_000.0,
        unpaidInvoices = 3,
        unpaidAmount = 10_500_000.0
    )

    val rooms = listOf(
        RoomResponse(1, "Phòng 101", "15 Nguyễn Trãi, Q1, TP.HCM", 3_500_000.0, 3500.0, 15000.0, 200_000.0, "OCCUPIED", "STUDIO", "Phòng studio đầy đủ nội thất, ban công rộng", "Nguyễn Văn A"),
        RoomResponse(2, "Phòng 102", "15 Nguyễn Trãi, Q1, TP.HCM", 3_200_000.0, 3500.0, 15000.0, 200_000.0, "AVAILABLE", "STUDIO", "Phòng mới sơn, cửa sổ hướng Đông", "Nguyễn Văn A"),
        RoomResponse(3, "Phòng 201", "15 Nguyễn Trãi, Q1, TP.HCM", 5_000_000.0, 3500.0, 15000.0, 300_000.0, "OCCUPIED", "APARTMENT", "Căn hộ 1 phòng ngủ, bếp riêng", "Nguyễn Văn A"),
        RoomResponse(4, "Phòng 202", "15 Nguyễn Trãi, Q1, TP.HCM", 4_800_000.0, 3500.0, 15000.0, 300_000.0, "OCCUPIED", "APARTMENT", "Căn hộ view đẹp, tầng cao", "Nguyễn Văn A"),
        RoomResponse(5, "Phòng 301", "15 Nguyễn Trãi, Q1, TP.HCM", 2_800_000.0, 3500.0, 15000.0, 150_000.0, "MAINTENANCE", "SINGLE", "Đang sửa chữa điện nước", "Nguyễn Văn A"),
        RoomResponse(6, "Phòng 302", "15 Nguyễn Trãi, Q1, TP.HCM", 2_800_000.0, 3500.0, 15000.0, 150_000.0, "AVAILABLE", "SINGLE", "Phòng đơn tiện nghi", "Nguyễn Văn A"),
    )

    val tenants = listOf(
        TenantResponse(1, "tenant01", "Trần Thị Bình", "binh@email.com", "0912345678"),
        TenantResponse(2, "tenant02", "Lê Văn Cường", "cuong@email.com", "0923456789"),
        TenantResponse(3, "tenant03", "Phạm Thị Dung", "dung@email.com", "0934567890"),
        TenantResponse(4, "tenant04", "Hoàng Văn Em", "em@email.com", "0945678901"),
        TenantResponse(5, "tenant05", "Ngô Thị Phương", "phuong@email.com", "0956789012"),
    )

    val contracts = listOf(
        ContractResponse(1, 1, "Phòng 101", 1, "Trần Thị Bình", 1, "2026-01-01", "2027-01-01", 7_000_000.0, 3_500_000.0, "ACTIVE"),
        ContractResponse(2, 3, "Phòng 201", 2, "Lê Văn Cường", 1, "2025-06-01", "2026-06-01", 10_000_000.0, 5_000_000.0, "ACTIVE"),
        ContractResponse(3, 4, "Phòng 202", 3, "Phạm Thị Dung", 1, "2025-09-01", "2026-09-01", 9_600_000.0, 4_800_000.0, "ACTIVE"),
        ContractResponse(4, 2, "Phòng 102", 4, "Hoàng Văn Em", 1, "2024-01-01", "2025-01-01", 6_400_000.0, 3_200_000.0, "EXPIRED"),
    )

    val invoices = listOf(
        InvoiceResponse(1, 1, "2026-03", 4_052_500.0, "UNPAID", "2026-04-05", null,
            InvoiceResponse.Breakdown(3_500_000.0, 157_500.0, 105_000.0, 200_000.0, 45.0, 7.0)),
        InvoiceResponse(2, 2, "2026-03", 5_637_500.0, "PAID", "2026-04-05", "2026-04-02",
            InvoiceResponse.Breakdown(5_000_000.0, 262_500.0, 105_000.0, 300_000.0, 75.0, 7.0)),
        InvoiceResponse(3, 3, "2026-03", 5_427_500.0, "UNPAID", "2026-04-05", null,
            InvoiceResponse.Breakdown(4_800_000.0, 227_500.0, 105_000.0, 300_000.0, 65.0, 7.0)),
        InvoiceResponse(4, 1, "2026-02", 4_017_500.0, "PAID", "2026-03-05", "2026-03-03",
            InvoiceResponse.Breakdown(3_500_000.0, 122_500.0, 105_000.0, 200_000.0, 35.0, 7.0)),
    )

    val payments = listOf(
        PaymentResponse(1, 2, 5_637_500.0, "BANK_TRANSFER", "2026-04-02", "Chuyển khoản tháng 3", 5_637_500.0, 0.0, "PAID"),
        PaymentResponse(2, 4, 4_017_500.0, "CASH", "2026-03-03", "Tiền mặt tháng 2", 4_017_500.0, 0.0, "PAID"),
        PaymentResponse(3, 1, 2_000_000.0, "MOMO", "2026-04-01", "Đặt cọc trước", 2_000_000.0, 2_052_500.0, "UNPAID"),
    )

    val meterReadings = listOf(
        MeterReadingResponse(1, 1, "2026-03", 100.0, 145.0, 20.0, 27.0, 45.0, 7.0, 157_500.0, 105_000.0),
        MeterReadingResponse(2, 1, "2026-02", 65.0, 100.0, 13.0, 20.0, 35.0, 7.0, 122_500.0, 105_000.0),
        MeterReadingResponse(3, 1, "2026-01", 30.0, 65.0, 6.0, 13.0, 35.0, 7.0, 122_500.0, 105_000.0),
    )

    val notifications = listOf(
        NotificationResponse(1, "Hóa đơn tháng 3/2026", "Hóa đơn phòng 101 đã được tạo, tổng 4.052.500 ₫", "INVOICE_CREATED", false, 1, "2026-03-31T08:00:00"),
        NotificationResponse(2, "Thanh toán xác nhận", "Phòng 201 đã thanh toán đủ tháng 3/2026", "PAYMENT_RECEIVED", true, 2, "2026-04-02T14:30:00"),
        NotificationResponse(3, "Hợp đồng sắp hết hạn", "Hợp đồng phòng 202 còn 30 ngày nữa hết hạn", "CONTRACT_EXPIRING", false, 3, "2026-03-30T09:00:00"),
        NotificationResponse(4, "Phòng mới trống", "Phòng 102 đã chuyển sang trạng thái trống", "ROOM_STATUS_CHANGED", true, 2, "2026-03-28T16:00:00"),
        NotificationResponse(5, "Hóa đơn quá hạn", "Hóa đơn phòng 101 tháng 3 đã quá hạn thanh toán", "INVOICE_OVERDUE", false, 1, "2026-04-06T08:00:00"),
    )

    // TODO: Replace with actual room images from server or asset bundle
    // Placeholder image URL - mark for replacement with real room photos
    const val PLACEHOLDER_ROOM_IMAGE = "https://via.placeholder.com/400x200/005344/FFFFFF?text=Room+Photo"
    // TODO: Replace with user avatar images
    const val PLACEHOLDER_AVATAR = "https://via.placeholder.com/80x80/E5E9E6/005344?text=Avatar"
}
