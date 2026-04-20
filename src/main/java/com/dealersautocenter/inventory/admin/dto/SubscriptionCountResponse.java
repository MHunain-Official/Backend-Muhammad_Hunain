package com.dealersautocenter.inventory.admin.dto;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;

import java.util.Map;

/**
 * Response for the admin count-by-subscription endpoint.
 *
 * NOTE: This count is GLOBAL (across ALL tenants). A GLOBAL_ADMIN
 * sees aggregate numbers from the entire system, not per-tenant.
 * This is intentional and documented here to make the contract explicit.
 * If per-tenant counts are needed, the endpoint can be extended with
 * an optional ?tenantId= query parameter (requires GLOBAL_ADMIN check).
 */
public record SubscriptionCountResponse(
        Map<SubscriptionType, Long> counts
) {}
