package com.dealersautocenter.inventory.dealer.mapper;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DealerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Dealer toEntity(CreateDealerRequest request);

    DealerResponse toResponse(Dealer dealer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateEntityFromRequest(com.dealersautocenter.inventory.dealer.dto.UpdateDealerRequest request,
                                 @MappingTarget Dealer dealer);
}
