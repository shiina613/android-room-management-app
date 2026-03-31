package com.kma.lamphoun.room_management.dto.request;

import com.kma.lamphoun.room_management.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    @Email
    private String email;

    private String fullName;
    private String phone;
    private Role role = Role.ROLE_TENANT;
}
