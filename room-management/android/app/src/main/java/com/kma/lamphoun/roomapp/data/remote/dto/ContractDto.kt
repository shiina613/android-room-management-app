package com.kma.lamphoun.roomapp.data.remote.dto

data class ContractResponse(
    val id: Long,
    val roomId: Long,
    val roomTitle: String?,
    val tenantId: Long,
    val tenantName: String?,
    val landlordId: Long,
    val startDate: String,
    val endDate: String,
    val deposit: Double,
    val monthlyRent: Double,
    val status: String
)

data class CreateContractRequest(
    val roomId: Long,
    val tenantId: Long,
    val startDate: String,
    val endDate: String,
    val deposit: Double,
    val monthlyRent: Double
)

data class TerminateContractRequest(val reason: String? = null)

data class ExtendContractRequest(val newEndDate: String)
