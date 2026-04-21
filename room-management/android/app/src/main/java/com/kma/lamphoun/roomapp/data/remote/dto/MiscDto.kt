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
    val roomTitle: String?,
    val billingMonth: String,
    val electricPrevious: Double,
    val electricCurrent: Double,
    val waterPrevious: Double,
    val waterCurrent: Double,
    val electricUsage: Double,
    val waterUsage: Double,
    val electricPrice: Double?,
    val waterPrice: Double?,
    val electricAmount: Double,
    val waterAmount: Double,
    val note: String?,
    val recordedBy: String?,
    val createdAt: String?
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
    val totalRooms: Long,
    val occupiedRooms: Long,
    val availableRooms: Long,
    val maintenanceRooms: Long,
    val occupancyRate: Double,
    val currentMonth: String?,
    val revenueThisMonth: Double,
    val collectedThisMonth: Double,
    val debtThisMonth: Double,
    val activeContracts: Long,
    val expiringIn30Days: Long,
    val totalTenants: Long,
    val unpaidInvoices: Long,
    val overdueInvoices: Long
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

data class UnreadCountResponse(val count: Long)

data class RevenueReportResponse(
    val year: Int,
    val month: Int?,
    val totalInvoiced: Double,
    val totalCollected: Double,
    val totalDebt: Double,
    val invoiceCount: Long?,
    val paidCount: Long?,
    val unpaidCount: Long?,
    val overdueCount: Long?,
    val monthly: List<MonthlyRevenue>?
) {
    data class MonthlyRevenue(
        val month: String,
        val invoiced: Double,
        val collected: Double,
        val debt: Double,
        val invoiceCount: Long
    )
}

data class DebtReportResponse(
    val totalDebt: Double,
    val debtorCount: Long,
    val details: List<DebtDetail>
) {
    data class DebtDetail(
        val invoiceId: Long,
        val billingMonth: String,
        val tenantId: Long?,
        val tenantName: String?,
        val tenantPhone: String?,
        val roomId: Long?,
        val roomTitle: String?,
        val invoiceTotal: Double?,
        val paid: Double?,
        val remaining: Double,
        val dueDate: String?,
        val status: String
    )
}

data class RoomStatusReportResponse(
    val totalRooms: Long,
    val available: Long,
    val occupied: Long,
    val maintenance: Long,
    val occupancyRate: Double
)

