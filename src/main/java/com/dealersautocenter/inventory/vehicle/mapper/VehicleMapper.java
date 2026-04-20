package com.dealersautocenter.inventory.vehicle.mapper;

import com.dealersautocenter.inventory.vehicle.domain.Vehicle;
import com.dealersautocenter.inventory.vehicle.dto.CreateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.VehicleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "dealer", ignore = true)
    Vehicle toEntity(CreateVehicleRequest request);

    @Mapping(source = "dealer.id", target = "dealerId")
    @Mapping(source = "dealer.name", target = "dealerName")
    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "dealer", ignore = true)
    void updateEntityFromRequest(
            com.dealersautocenter.inventory.vehicle.dto.UpdateVehicleRequest request,
            @MappingTarget Vehicle vehicle);
}
