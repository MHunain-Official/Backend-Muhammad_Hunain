package com.dealersautocenter.inventory.vehicle.repository;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import com.dealersautocenter.inventory.vehicle.domain.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for dynamic Vehicle queries.
 * Each method returns a composable Specification following the
 * Open/Closed principle — new filters can be added without modifying existing ones.
 */
public final class VehicleSpecification {

    private VehicleSpecification() {}

    public static Specification<Vehicle> withFilters(
            String tenantId,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscription) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always enforce tenant scope
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (model != null && !model.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("model")),
                        "%" + model.toLowerCase() + "%"));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }

            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            if (subscription != null) {
                Join<Object, Object> dealer = root.join("dealer", JoinType.INNER);
                predicates.add(cb.equal(dealer.get("subscriptionType"), subscription));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
