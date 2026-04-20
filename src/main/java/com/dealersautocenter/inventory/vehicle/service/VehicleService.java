package com.dealersautocenter.inventory.vehicle.service;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import com.dealersautocenter.inventory.vehicle.dto.CreateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.UpdateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port for Vehicle use cases.
 */
public interface VehicleService {
    VehicleResponse create(CreateVehicleRequest request);
    VehicleResponse findById(UUID id);
    Page<VehicleResponse> findAll(String model, VehicleStatus status,
                                   BigDecimal priceMin, BigDecimal priceMax,
                                   SubscriptionType subscription, Pageable pageable);
    VehicleResponse update(UUID id, UpdateVehicleRequest request);
    void delete(UUID id);
}
