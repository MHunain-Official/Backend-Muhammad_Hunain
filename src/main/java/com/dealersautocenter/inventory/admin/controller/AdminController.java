package com.dealersautocenter.inventory.admin.controller;

import com.dealersautocenter.inventory.dealer.repository.DealerRepository;
import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin-only endpoints. All routes require GLOBAL_ADMIN role.
 *
 * countBySubscription — returns a GLOBAL (cross-tenant) count.
 * See SubscriptionCountResponse Javadoc for details.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Global admin endpoints (GLOBAL_ADMIN role required)")
public class AdminController {

    private final DealerRepository dealerRepository;

    @GetMapping("/dealers/countBySubscription")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Count dealers by subscription type (GLOBAL — across all tenants)",
            description = """
                    Returns the total number of dealers per subscription type across ALL tenants.
                    This is a system-wide aggregate, not scoped to any single tenant.
                    Requires GLOBAL_ADMIN role.
                    """
    )
    public ResponseEntity<Map<String, Long>> countBySubscription() {
        List<Object[]> results = dealerRepository.countBySubscriptionTypeGlobal();

        // Pre-populate all enum keys with 0 to guarantee both keys are always present
        Map<String, Long> counts = new HashMap<>();
        for (SubscriptionType t : SubscriptionType.values()) {
            counts.put(t.name(), 0L);
        }

        for (Object[] row : results) {
            SubscriptionType type = (SubscriptionType) row[0];
            Long count = (Long) row[1];
            counts.put(type.name(), count);
        }

        return ResponseEntity.ok(counts);
    }
}
