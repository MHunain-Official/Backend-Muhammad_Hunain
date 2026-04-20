package com.dealersautocenter.inventory.dealer.dto;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import jakarta.validation.constraints.Email;

public record UpdateDealerRequest(
        String name,

        @Email(message = "Email must be valid")
        String email,

        SubscriptionType subscriptionType
) {}
