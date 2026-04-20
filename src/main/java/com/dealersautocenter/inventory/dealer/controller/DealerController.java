package com.dealersautocenter.inventory.dealer.controller;

import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.dto.UpdateDealerRequest;
import com.dealersautocenter.inventory.dealer.service.DealerService;
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

import java.util.UUID;

@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
@Tag(name = "Dealers", description = "Dealer management endpoints (tenant-scoped)")
public class DealerController {

    private final DealerService dealerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Create a new dealer")
    public ResponseEntity<DealerResponse> create(
            @Valid @RequestBody CreateDealerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dealerService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get dealer by ID")
    public ResponseEntity<DealerResponse> getById(
            @Parameter(description = "Dealer UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(dealerService.findById(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all dealers for the current tenant (paginated)")
    public ResponseEntity<Page<DealerResponse>> getAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(dealerService.findAll(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Partially update a dealer")
    public ResponseEntity<DealerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDealerRequest request) {
        return ResponseEntity.ok(dealerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DEALER_ADMIN','GLOBAL_ADMIN')")
    @Operation(summary = "Delete a dealer")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dealerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
