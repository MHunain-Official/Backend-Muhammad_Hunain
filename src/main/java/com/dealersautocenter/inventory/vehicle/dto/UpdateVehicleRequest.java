package com.dealersautocenter.inventory.vehicle.dto;

import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateVehicleRequest(
        String model,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        VehicleStatus status
) {}
