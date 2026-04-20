package com.dealersautocenter.inventory.vehicle.mapper;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.shared.domain.VehicleStatus;
import com.dealersautocenter.inventory.vehicle.domain.Vehicle;
import com.dealersautocenter.inventory.vehicle.dto.CreateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.UpdateVehicleRequest;
import com.dealersautocenter.inventory.vehicle.dto.VehicleResponse;
import java.math.BigDecimal;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T19:38:33+0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class VehicleMapperImpl implements VehicleMapper {

    @Override
    public Vehicle toEntity(CreateVehicleRequest request) {
        if ( request == null ) {
            return null;
        }

        Vehicle.VehicleBuilder vehicle = Vehicle.builder();

        vehicle.model( request.model() );
        vehicle.price( request.price() );
        vehicle.status( request.status() );

        return vehicle.build();
    }

    @Override
    public VehicleResponse toResponse(Vehicle vehicle) {
        if ( vehicle == null ) {
            return null;
        }

        UUID dealerId = null;
        String dealerName = null;
        UUID id = null;
        String tenantId = null;
        String model = null;
        BigDecimal price = null;
        VehicleStatus status = null;

        dealerId = vehicleDealerId( vehicle );
        dealerName = vehicleDealerName( vehicle );
        id = vehicle.getId();
        tenantId = vehicle.getTenantId();
        model = vehicle.getModel();
        price = vehicle.getPrice();
        status = vehicle.getStatus();

        VehicleResponse vehicleResponse = new VehicleResponse( id, tenantId, dealerId, dealerName, model, price, status );

        return vehicleResponse;
    }

    @Override
    public void updateEntityFromRequest(UpdateVehicleRequest request, Vehicle vehicle) {
        if ( request == null ) {
            return;
        }

        if ( request.model() != null ) {
            vehicle.setModel( request.model() );
        }
        if ( request.price() != null ) {
            vehicle.setPrice( request.price() );
        }
        if ( request.status() != null ) {
            vehicle.setStatus( request.status() );
        }
    }

    private UUID vehicleDealerId(Vehicle vehicle) {
        if ( vehicle == null ) {
            return null;
        }
        Dealer dealer = vehicle.getDealer();
        if ( dealer == null ) {
            return null;
        }
        UUID id = dealer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String vehicleDealerName(Vehicle vehicle) {
        if ( vehicle == null ) {
            return null;
        }
        Dealer dealer = vehicle.getDealer();
        if ( dealer == null ) {
            return null;
        }
        String name = dealer.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
