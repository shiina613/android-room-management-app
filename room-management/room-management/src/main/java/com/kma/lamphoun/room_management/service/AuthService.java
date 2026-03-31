package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.request.LoginRequest;
import com.kma.lamphoun.room_management.dto.request.RegisterRequest;
import com.kma.lamphoun.room_management.dto.response.AuthResponse;
import com.kma.lamphoun.room_management.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    UserResponse getMe(String username);
}
