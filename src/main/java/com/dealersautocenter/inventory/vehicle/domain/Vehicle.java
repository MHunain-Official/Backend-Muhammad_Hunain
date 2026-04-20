package com.dealersautocenter.inventory.vehicle.domain;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vehicle aggregate — owned by a Dealer within the same tenant.
 */
@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_vehicle_dealer_id", columnList = "dealer_id"),
                @Index(name = "idx_vehicle_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;
}
