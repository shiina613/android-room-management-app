package com.kma.lamphoun.roomapp.data.remote.dto

data class RoomResponse(
    val id: Long,
    val title: String,
    val address: String,
    val price: Double,
    val elecPrice: Double,
    val waterPrice: Double,
    val servicePrice: Double,
    val status: String,
    val category: String,
    val description: String?,
    val ownerName: String?,
    val imageUrl: String? = null
)

data class RoomRequest(
    val title: String,
    val address: String,
    val price: Double,
    val elecPrice: Double,
    val waterPrice: Double,
    val servicePrice: Double,
    val status: String,
    val category: String,
    val description: String? = null
)

data class UpdateRoomStatusRequest(val status: String)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int
)

