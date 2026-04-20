package com.dealersautocenter.inventory.vehicle.dto;

import com.dealersautocenter.inventory.shared.domain.VehicleStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String tenantId,
        UUID dealerId,
        String dealerName,
        String model,
        BigDecimal price,
        VehicleStatus status
) {}
