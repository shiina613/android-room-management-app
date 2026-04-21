package com.kma.lamphoun.roomapp.data.remote.dto

data class ContractResponse(
    val id: Long,
    val roomId: Long,
    val roomTitle: String?,
    val roomAddress: String?,
    val tenantId: Long,
    val tenantName: String?,
    val tenantPhone: String?,
    val landlordId: Long,
    val landlordName: String?,
    val startDate: String,
    val endDate: String,
    val deposit: Double,
    val monthlyRent: Double,
    val status: String,
    val terminationNote: String?,
    val terminatedAt: String?,
    val createdAt: String?
)

data class CreateContractRequest(
    val roomId: Long,
    val tenantId: Long,
    val startDate: String,
    val endDate: String,
    val deposit: Double,
    val monthlyRent: Double
)

data class TerminateContractRequest(
    val terminatedAt: String,  // YYYY-MM-DD
    val note: String? = null
)

data class ExtendContractRequest(val newEndDate: String)

