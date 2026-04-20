package com.dealersautocenter.inventory.vehicle.controller;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import com.dealersautocenter.inventory.vehicle.dto.CreateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.UpdateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.VehicleResponse;
import com.dealersautocenter.inventory.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicle inventory endpoints (tenant-scoped)")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Add a new vehicle")
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List vehicles with optional filters and pagination")
    public ResponseEntity<Page<VehicleResponse>> getAll(
            @Parameter(description = "Filter by model (partial match)") @RequestParam(required = false) String model,
            @Parameter(description = "Filter by status") @RequestParam(required = false) VehicleStatus status,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal priceMin,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal priceMax,
            @Parameter(description = "Filter by dealer subscription type") @RequestParam(required = false) SubscriptionType subscription,
            @PageableDefault(size = 20, sort = "model") Pageable pageable) {

        return ResponseEntity.ok(
                vehicleService.findAll(model, status, priceMin, priceMax, subscription, pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Partially update a vehicle")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Delete a vehicle")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
