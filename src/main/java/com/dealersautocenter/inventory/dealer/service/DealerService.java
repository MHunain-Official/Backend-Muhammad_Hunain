package com.dealersautocenter.inventory.dealer.service;

import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.dto.UpdateDealerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Port (use-case boundary) for Dealer operations.
 * Keeps the controller layer decoupled from the implementation.
 */
public interface DealerService {
    DealerResponse create(CreateDealerRequest request);
    DealerResponse findById(UUID id);
    Page<DealerResponse> findAll(Pageable pageable);
    DealerResponse update(UUID id, UpdateDealerRequest request);
    void delete(UUID id);
}
