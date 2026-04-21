package com.kma.lamphoun.roomapp.data.remote.dto

data class PaymentResponse(
    val id: Long,
    val invoiceId: Long,
    val billingMonth: String,
    val invoiceTotal: Double,
    val invoiceStatus: String,
    val tenantId: Long,
    val tenantName: String?,
    val roomId: Long,
    val roomTitle: String?,
    val amount: Double,
    val paymentMethod: String,
    val paidAt: String,
    val note: String?,
    val recordedBy: String,
    val totalPaid: Double,
    val remaining: Double,
    val createdAt: String?
)

data class CreatePaymentRequest(
    val invoiceId: Long,
    val amount: Double,
    val paymentMethod: String,
    val paidAt: String,
    val note: String? = null
)

