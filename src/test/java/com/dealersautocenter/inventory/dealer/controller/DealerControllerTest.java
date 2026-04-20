package com.dealersautocenter.inventory.dealer.controller;

import com.dealersautocenter.inventory.dealer.dto.CreateDealerRequest;
import com.dealersautocenter.inventory.dealer.dto.DealerResponse;
import com.dealersautocenter.inventory.dealer.service.DealerService;
import com.dealersautocenter.inventory.shared.domain.SubscriptionType;
import com.dealersautocenter.inventory.shared.security.SecurityConfig;
import com.dealersautocenter.inventory.shared.tenant.TenantFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealerController.class)
@Import({SecurityConfig.class, TenantFilter.class})
@DisplayName("DealerController Integration Tests")
class DealerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DealerService dealerService;

    @Test
    @DisplayName("POST /dealers — returns 400 when X-Tenant-Id header is missing")
    void create_missingTenantHeader_returns400() throws Exception {
        CreateDealerRequest body = new CreateDealerRequest("AutoMax", "a@b.com", SubscriptionType.BASIC);

        mockMvc.perform(post("/dealers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DEALER_ADMIN")
    @DisplayName("POST /dealers — returns 201 with valid request")
    void create_validRequest_returns201() throws Exception {
        CreateDealerRequest body = new CreateDealerRequest("AutoMax", "a@b.com", SubscriptionType.BASIC);
        DealerResponse response = new DealerResponse(UUID.randomUUID(), "t1", "AutoMax", "a@b.com", SubscriptionType.BASIC);

        when(dealerService.create(any())).thenReturn(response);

        mockMvc.perform(post("/dealers")
                        .header("X-Tenant-Id", "t1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("AutoMax"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    @DisplayName("POST /dealers — returns 403 when user lacks required role")
    void create_insufficientRole_returns403() throws Exception {
        CreateDealerRequest body = new CreateDealerRequest("AutoMax", "a@b.com", SubscriptionType.BASIC);

        mockMvc.perform(post("/dealers")
                        .header("X-Tenant-Id", "t1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
