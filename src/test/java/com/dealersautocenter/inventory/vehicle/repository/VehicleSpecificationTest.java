package com.dealersautocenter.inventory.vehicle.repository;

import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("VehicleSpecification Unit Tests")
class VehicleSpecificationTest {

    @Test
    @DisplayName("withFilters() — builds specification without exceptions when all params provided")
    void withFilters_allParams_noException() {
        assertThatNoException().isThrownBy(() ->
                VehicleSpecification.withFilters(
                        "tenant-1", "Toyota", VehicleStatus.AVAILABLE,
                        BigDecimal.valueOf(5000), BigDecimal.valueOf(50000),
                        SubscriptionType.PREMIUM));
    }

    @Test
    @DisplayName("withFilters() — builds specification without exceptions when optional params null")
    void withFilters_nullOptionals_noException() {
        assertThatNoException().isThrownBy(() ->
                VehicleSpecification.withFilters("tenant-1", null, null, null, null, null));
    }
}
