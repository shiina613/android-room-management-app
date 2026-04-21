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

// Full user profile returned by GET /api/auth/me and GET /api/users/me
data class UserResponse(
    val id: Long,
    val username: String?,
    val email: String?,
    val fullName: String?,
    val phone: String?,
    val role: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val email: String,
    val phone: String
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

