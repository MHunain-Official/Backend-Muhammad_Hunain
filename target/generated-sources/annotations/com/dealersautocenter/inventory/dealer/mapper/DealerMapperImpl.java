package com.dealersautocenter.inventory.dealer.mapper;

import com.dealersautocenter.inventory.dealer.domain.Dealer;
import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.dto.UpdateDealerRequest;
import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T19:38:33+0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class DealerMapperImpl implements DealerMapper {

    @Override
    public Dealer toEntity(CreateDealerRequest request) {
        if ( request == null ) {
            return null;
        }

        Dealer.DealerBuilder dealer = Dealer.builder();

        dealer.email( request.email() );
        dealer.name( request.name() );
        dealer.subscriptionType( request.subscriptionType() );

        return dealer.build();
    }

    @Override
    public DealerResponse toResponse(Dealer dealer) {
        if ( dealer == null ) {
            return null;
        }

        UUID id = null;
        String tenantId = null;
        String name = null;
        String email = null;
        SubscriptionType subscriptionType = null;

        id = dealer.getId();
        tenantId = dealer.getTenantId();
        name = dealer.getName();
        email = dealer.getEmail();
        subscriptionType = dealer.getSubscriptionType();

        DealerResponse dealerResponse = new DealerResponse( id, tenantId, name, email, subscriptionType );

        return dealerResponse;
    }

    @Override
    public void updateEntityFromRequest(UpdateDealerRequest request, Dealer dealer) {
        if ( request == null ) {
            return;
        }

        if ( request.email() != null ) {
            dealer.setEmail( request.email() );
        }
        if ( request.name() != null ) {
            dealer.setName( request.name() );
        }
        if ( request.subscriptionType() != null ) {
            dealer.setSubscriptionType( request.subscriptionType() );
        }
    }
}
