package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.dto.request.ChangePasswordRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateProfileRequest;
import com.kma.lamphoun.room_management.dto.response.UserResponse;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMyProfile(String username) {
        return toResponse(findByUsername(username));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toPublicResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = findByUsername(username);

        // Kiểm tra email trùng nếu có thay đổi
        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null)    user.setEmail(request.getEmail());
        if (request.getPhone() != null)    user.setPhone(request.getPhone());

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // --- helpers ---

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    /** Full info — dùng cho chính chủ */
    private UserResponse toResponse(User user) {
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

    /** Public info — ẩn email/phone khi xem người khác */
    private UserResponse toPublicResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
