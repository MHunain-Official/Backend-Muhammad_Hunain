package com.dealersautocenter.inventory.dealer.domain;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Core Dealer aggregate root.
 * tenant_id is set from TenantContext and is immutable after creation.
 */
@Entity
@Table(
        name = "dealers",
        indexes = {
                @Index(name = "idx_dealer_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_dealer_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dealer {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;
}
