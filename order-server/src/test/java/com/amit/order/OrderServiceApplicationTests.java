package com.amit.order;

import com.amit.order.entity.Order;
import com.amit.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.System.getProperty;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository repository;

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

        Assertions.assertEquals(1, repository.findAll().size());
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

        generateData();

        long endTime = System.currentTimeMillis() + 600000;
        long startTime = System.currentTimeMillis() - 600000;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/order/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"key\":\"test\", \"format\":\"csv\",\"filter\":{\"product\":\"Test Product\",\"date\":{\"from\":" + startTime + ",\"to\":" + endTime + "}}}"))
                .andExpect(status().isOk());

        String filePath = getProperty("user.dir") + "/output/test.csv";

        Thread.sleep(2000);
        Assertions.assertTrue(new File(filePath).exists());
        Path path = Paths.get(filePath);
        long lines = Files.lines(path).count();
        Assertions.assertEquals(12, lines);
    }

    private void generateData() {

        String product = "Test Product";
        String customer = "Test Customer";
        List<Order> orders = new ArrayList<>();

        IntStream.range(0, 100).forEach(counter -> {

            Order.OrderBuilder builder = Order.builder().orderDate(System.currentTimeMillis());
            if (counter % 10 != 0) {
                builder.productName(product + "-" + counter);
                builder.customerName(customer + "-" + counter);
            }
            else {
                builder.productName(product);
                builder.customerName(customer);
            }
            orders.add(builder.build());
        });

        repository.saveAll(orders);
    }
}
