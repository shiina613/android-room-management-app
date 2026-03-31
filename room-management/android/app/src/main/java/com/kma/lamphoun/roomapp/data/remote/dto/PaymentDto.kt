package com.kma.lamphoun.roomapp.data.remote.dto

data class PaymentResponse(
    val id: Long,
    val invoiceId: Long,
    val amount: Double,
    val paymentMethod: String,
    val paidAt: String?,
    val note: String?,
    val totalPaid: Double,
    val remaining: Double,
    val invoiceStatus: String?
)

data class CreatePaymentRequest(
    val invoiceId: Long,
    val amount: Double,
    val paymentMethod: String,
    val note: String? = null
)
