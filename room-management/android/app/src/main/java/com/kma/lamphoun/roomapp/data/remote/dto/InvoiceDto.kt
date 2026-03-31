package com.kma.lamphoun.roomapp.data.remote.dto

data class InvoiceResponse(
    val id: Long,
    val contractId: Long,
    val billingMonth: String,
    val totalAmount: Double,
    val status: String,
    val dueDate: String?,
    val paidAt: String?,
    val breakdown: Breakdown?
) {
    data class Breakdown(
        val rentAmount: Double,
        val electricAmount: Double,
        val waterAmount: Double,
        val serviceAmount: Double,
        val electricUsage: Double,
        val waterUsage: Double
    )
}

data class CreateInvoiceRequest(
    val contractId: Long,
    val meterReadingId: Long,
    val billingMonth: String,
    val dueDate: String
)

data class MarkPaidRequest(val note: String? = null)
