package com.amit.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldPlaceOrder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getOrderRequest()))
                .andExpect(status().isCreated());
    }

    private String getOrderRequest() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .orderDate(System.currentTimeMillis())
                .productName("Test Product")
                .customerName("Test Customer")
                .build();

        return new ObjectMapper().writeValueAsString(request);
    }

    @Test
    void shouldExportOrders() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/order/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"format\":\"json\",\"filter\":{\"product\":\"Test Product\"}}"))
                .andExpect(status().isOk());
    }
}
