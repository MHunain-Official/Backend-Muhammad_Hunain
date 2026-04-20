package com.dealersautocenter.inventory.shared.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantContext Unit Tests")
class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("setTenantId / getTenantId — stores and retrieves correctly")
    void setAndGet() {
        TenantContext.setTenantId("tenant-xyz");
        assertThat(TenantContext.getTenantId()).isEqualTo("tenant-xyz");
    }

    @Test
    @DisplayName("clear() — removes the tenant from thread local")
    void clear_removesValue() {
        TenantContext.setTenantId("tenant-xyz");
        TenantContext.clear();
        assertThat(TenantContext.getTenantId()).isNull();
    }
}
