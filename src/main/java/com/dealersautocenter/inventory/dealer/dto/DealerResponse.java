package com.dealersautocenter.inventory.dealer.dto;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;

import java.util.UUID;

public record DealerResponse(
        UUID id,
        String tenantId,
        String name,
        String email,
        SubscriptionType subscriptionType
) {}
