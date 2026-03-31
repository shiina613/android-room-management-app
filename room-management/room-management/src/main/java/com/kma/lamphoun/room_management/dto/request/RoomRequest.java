package com.kma.lamphoun.room_management.dto.request;

import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Positive(message = "Area must be positive")
    private Double area;

    @NotNull(message = "Electricity price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Electricity price must be greater than 0")
    private BigDecimal elecPrice;

    @NotNull(message = "Water price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Water price must be greater than 0")
    private BigDecimal waterPrice;

    @NotNull(message = "Service price is required")
    @DecimalMin(value = "0.0", message = "Service price must be >= 0")
    private BigDecimal servicePrice;

    @NotNull(message = "Category is required")
    private RoomCategory category;
}
