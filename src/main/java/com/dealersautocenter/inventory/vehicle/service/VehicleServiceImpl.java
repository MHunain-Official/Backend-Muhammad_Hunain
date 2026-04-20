package com.dealersautocenter.inventory.vehicle.service;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.dealer.repository.DealerRepository;
import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import com.dealersautocenter.inventory.shared.exception.ResourceNotFoundException;
import com.dealersautocenter.inventory.shared.exception.TenantAccessDeniedException;
import com.dealersautocenter.inventory.shared.tenant.TenantContext;
import com.dealersautocenter.inventory.vehicle.domain.Vehicle;
import com.dealersautocenter.inventory.vehicle.dto.CreateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.UpdateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.VehicleResponse;
import com.dealersautocenter.inventory.vehicle.mapper.VehicleMapper;
import com.dealersautocenter.inventory.vehicle.repository.VehicleRepository;
import com.dealersautocenter.inventory.vehicle.repository.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        String tenantId = TenantContext.getTenantId();

        Dealer dealer = resolveDealer(request.dealerId(), tenantId);

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setTenantId(tenantId);
        vehicle.setDealer(dealer);

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created id={} tenant={}", saved.getId(), tenantId);
        return vehicleMapper.toResponse(saved);
    }

    @Override
    public VehicleResponse findById(UUID id) {
        return vehicleMapper.toResponse(resolveVehicle(id));
    }

    @Override
    public Page<VehicleResponse> findAll(String model, VehicleStatus status,
                                          BigDecimal priceMin, BigDecimal priceMax,
                                          SubscriptionType subscription, Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return vehicleRepository.findAll(
                VehicleSpecification.withFilters(tenantId, model, status, priceMin, priceMax, subscription),
                pageable
        ).map(vehicleMapper::toResponse);
    }

    @Override
    @Transactional
    public VehicleResponse update(UUID id, UpdateVehicleRequest request) {
        Vehicle vehicle = resolveVehicle(id);
        vehicleMapper.updateEntityFromRequest(request, vehicle);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        String tenantId = TenantContext.getTenantId();
        if (!vehicleRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteByIdAndTenantId(id, tenantId);
        log.info("Vehicle deleted id={} tenant={}", id, tenantId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Vehicle resolveVehicle(UUID id) {
        String tenantId = TenantContext.getTenantId();
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    if (!vehicle.getTenantId().equals(tenantId)) {
                        throw new TenantAccessDeniedException(
                                "Access denied: vehicle does not belong to tenant " + tenantId);
                    }
                    return vehicle;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    private Dealer resolveDealer(UUID dealerId, String tenantId) {
        return dealerRepository.findByIdAndTenantId(dealerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dealer not found with id: " + dealerId + " for tenant: " + tenantId));
    }
}
