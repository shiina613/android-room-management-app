package com.kma.lamphoun.roomapp.data.remote.dto

data class InvoiceResponse(
    val id: Long,
    val contractId: Long,
    val roomId: Long?,
    val roomTitle: String?,
    val roomAddress: String?,
    val tenantId: Long?,
    val tenantName: String?,
    val tenantPhone: String?,
    val landlordId: Long?,
    val landlordName: String?,
    val billingMonth: String,
    val totalAmount: Double,
    val status: String,
    val dueDate: String?,
    val paidAt: String?,
    val breakdown: Breakdown?,
    val note: String?,
    val createdAt: String?
) {
    data class Breakdown(
        val rentAmount: Double,
        val electricPrevious: Double?,
        val electricCurrent: Double?,
        val electricUsage: Double,
        val electricPrice: Double?,
        val electricAmount: Double,
        val waterPrevious: Double?,
        val waterCurrent: Double?,
        val waterUsage: Double,
        val waterPrice: Double?,
        val waterAmount: Double,
        val serviceAmount: Double
    )
}

data class CreateInvoiceRequest(
    val contractId: Long,
    val meterReadingId: Long,
    val billingMonth: String,
    val dueDate: String? = null,
    val note: String? = null
)

data class MarkPaidRequest(
    val paidAt: String = java.time.LocalDate.now().toString(),
    val note: String? = null
)

