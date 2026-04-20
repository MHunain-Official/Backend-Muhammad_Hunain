package com.dealersautocenter.inventory.dealer.service;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.dto.UpdateDealerRequest;
import com.dealersautocenter.inventory.dealer.mapper.DealerMapper;
import com.dealersautocenter.inventory.dealer.repository.DealerRepository;
import com.dealersautocenter.inventory.shared.exception.ResourceNotFoundException;
import com.dealersautocenter.inventory.shared.exception.TenantAccessDeniedException;
import com.dealersautocenter.inventory.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;
    private final DealerMapper dealerMapper;

    @Override
    @Transactional
    public DealerResponse create(CreateDealerRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.debug("Creating dealer for tenant={}", tenantId);

        Dealer dealer = dealerMapper.toEntity(request);
        dealer.setTenantId(tenantId);

        Dealer saved = dealerRepository.save(dealer);
        log.info("Dealer created id={} tenant={}", saved.getId(), tenantId);
        return dealerMapper.toResponse(saved);
    }

    @Override
    public DealerResponse findById(UUID id) {
        return dealerMapper.toResponse(resolveDealer(id));
    }

    @Override
    public Page<DealerResponse> findAll(Pageable pageable) {
        String tenantId = TenantContext.getTenantId();
        return dealerRepository.findAllByTenantId(tenantId, pageable)
                .map(dealerMapper::toResponse);
    }

    @Override
    @Transactional
    public DealerResponse update(UUID id, UpdateDealerRequest request) {
        Dealer dealer = resolveDealer(id);
        dealerMapper.updateEntityFromRequest(request, dealer);
        return dealerMapper.toResponse(dealerRepository.save(dealer));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        String tenantId = TenantContext.getTenantId();
        if (!dealerRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Dealer not found with id: " + id);
        }
        dealerRepository.deleteByIdAndTenantId(id, tenantId);
        log.info("Dealer deleted id={} tenant={}", id, tenantId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Dealer resolveDealer(UUID id) {
        String tenantId = TenantContext.getTenantId();
        return dealerRepository.findById(id)
                .map(dealer -> {
                    if (!dealer.getTenantId().equals(tenantId)) {
                        throw new TenantAccessDeniedException(
                                "Access denied: dealer does not belong to tenant " + tenantId);
                    }
                    return dealer;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));
    }
}
