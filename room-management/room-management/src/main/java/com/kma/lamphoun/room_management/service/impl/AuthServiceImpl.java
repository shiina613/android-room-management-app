package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.dto.request.LoginRequest;
import com.kma.lamphoun.room_management.dto.request.RegisterRequest;
import com.kma.lamphoun.room_management.dto.response.AuthResponse;
import com.kma.lamphoun.room_management.dto.response.UserResponse;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.security.JwtUtil;
import com.kma.lamphoun.room_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException nếu sai → GlobalExceptionHandler bắt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user);
        return buildAuthResponse(token, user);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return buildAuthResponse(token, user);
    }

    @Override
    public UserResponse getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponse(user);
    }

    // --- helpers ---

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .accessToken(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
