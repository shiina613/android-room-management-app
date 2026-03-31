package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.Role;
import com.kma.lamphoun.room_management.dto.request.CreateTenantRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateTenantRequest;
import com.kma.lamphoun.room_management.dto.response.TenantResponse;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone '" + request.getPhone() + "' already registered");
        }

        User tenant = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.ROLE_TENANT)
                .build();

        return toResponse(userRepository.save(tenant));
    }

    @Override
    public Page<TenantResponse> getAll(Pageable pageable) {
        return userRepository.findByRole(Role.ROLE_TENANT, pageable)
                .map(this::toResponse);
    }

    @Override
    public TenantResponse getById(Long id) {
        User tenant = userRepository.findByIdAndRole(id, Role.ROLE_TENANT)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return toResponse(tenant);
    }

    @Override
    @Transactional
    public TenantResponse update(Long id, UpdateTenantRequest request) {
        User tenant = userRepository.findByIdAndRole(id, Role.ROLE_TENANT)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(tenant.getEmail())
                && userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BadRequestException("Email '" + request.getEmail() + "' already in use");
        }
        if (request.getPhone() != null
                && !request.getPhone().equals(tenant.getPhone())
                && userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new BadRequestException("Phone '" + request.getPhone() + "' already in use");
        }

        if (request.getFullName() != null) tenant.setFullName(request.getFullName());
        if (request.getEmail() != null)    tenant.setEmail(request.getEmail());
        if (request.getPhone() != null)    tenant.setPhone(request.getPhone());
        if (request.getEnabled() != null)  tenant.setEnabled(request.getEnabled());

        return toResponse(userRepository.save(tenant));
    }

    // --- helper ---

    private TenantResponse toResponse(User user) {
        return TenantResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
