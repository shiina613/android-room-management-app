package com.kma.lamphoun.roomapp.data.remote.dto

data class TenantResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String?,
    val phone: String?
)

data class CreateTenantRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val email: String,
    val phone: String
)

data class MeterReadingResponse(
    val id: Long,
    val roomId: Long,
    val billingMonth: String,
    val electricPrevious: Double,
    val electricCurrent: Double,
    val waterPrevious: Double,
    val waterCurrent: Double,
    val electricUsage: Double,
    val waterUsage: Double,
    val electricAmount: Double,
    val waterAmount: Double
)

data class MeterReadingRequest(
    val roomId: Long,
    val billingMonth: String,
    val electricPrevious: Double,
    val electricCurrent: Double,
    val waterPrevious: Double,
    val waterCurrent: Double
)

data class DashboardResponse(
    val totalRooms: Int,
    val occupiedRooms: Int,
    val availableRooms: Int,
    val totalTenants: Int,
    val revenueThisMonth: Double,
    val unpaidInvoices: Int,
    val unpaidAmount: Double
)

data class NotificationResponse(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,
    val read: Boolean,
    val referenceId: Long?,
    val createdAt: String
)
