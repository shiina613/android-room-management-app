package com.kma.lamphoun.roomapp.data.remote.dto

data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val role: String = "ROLE_LANDLORD"
)

data class AuthResponse(
    val accessToken: String,
    val userId: Long,
    val email: String,
    val fullName: String,
    val role: String
)
