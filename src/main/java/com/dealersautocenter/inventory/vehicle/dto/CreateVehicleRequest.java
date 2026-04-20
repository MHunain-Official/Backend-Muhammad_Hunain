package com.dealersautocenter.inventory.vehicle.dto;

import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateVehicleRequest(
        @NotNull(message = "Dealer ID is required")
        UUID dealerId,

        @NotBlank(message = "Model is required")
        String model,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Status is required")
        VehicleStatus status
) {}
