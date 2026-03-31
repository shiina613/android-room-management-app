package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateTenantRequest {

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Phone must be 9-11 digits")
    private String phone;

    private Boolean enabled;
}
