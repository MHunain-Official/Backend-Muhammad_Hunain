package com.dealersautocenter.inventory.dealer.service;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.mapper.DealerMapper;
import com.dealersautocenter.inventory.dealer.repository.DealerRepository;
import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.exception.ResourceNotFoundException;
import com.dealersautocenter.inventory.shared.exception.TenantAccessDeniedException;
import com.dealersautocenter.inventory.shared.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DealerServiceImpl Unit Tests")
class DealerServiceImplTest {

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private DealerMapper dealerMapper;

    @InjectMocks
    private DealerServiceImpl dealerService;

    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";

    @BeforeEach
    void setTenant() {
        TenantContext.setTenantId(TENANT_A);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("create() — sets tenantId from context and saves dealer")
    void create_setsCorrectTenantId() {
        CreateDealerRequest request = new CreateDealerRequest("AutoMax", "auto@max.com", SubscriptionType.PREMIUM);
        Dealer entity = Dealer.builder().build();
        Dealer saved  = Dealer.builder().id(UUID.randomUUID()).tenantId(TENANT_A).name("AutoMax").build();
        DealerResponse expected = new DealerResponse(saved.getId(), TENANT_A, "AutoMax", "auto@max.com", SubscriptionType.PREMIUM);

        when(dealerMapper.toEntity(request)).thenReturn(entity);
        when(dealerRepository.save(any())).thenReturn(saved);
        when(dealerMapper.toResponse(saved)).thenReturn(expected);

        DealerResponse result = dealerService.create(request);

        assertThat(entity.getTenantId()).isEqualTo(TENANT_A);
        assertThat(result).isEqualTo(expected);
        verify(dealerRepository).save(entity);
    }

    @Test
    @DisplayName("findById() — throws 403 when dealer belongs to different tenant")
    void findById_forbiddenCrossTenant() {
        UUID id = UUID.randomUUID();
        Dealer otherTenantDealer = Dealer.builder().id(id).tenantId(TENANT_B).build();

        when(dealerRepository.findById(id)).thenReturn(Optional.of(otherTenantDealer));

        assertThatThrownBy(() -> dealerService.findById(id))
                .isInstanceOf(TenantAccessDeniedException.class);
    }

    @Test
    @DisplayName("findById() — throws 404 when dealer does not exist")
    void findById_notFound() {
        UUID id = UUID.randomUUID();
        when(dealerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dealerService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete() — throws 404 when dealer not found in tenant")
    void delete_notFound() {
        UUID id = UUID.randomUUID();
        when(dealerRepository.existsByIdAndTenantId(id, TENANT_A)).thenReturn(false);

        assertThatThrownBy(() -> dealerService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
