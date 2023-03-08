package com.amit.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Define an Order placed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "order")
public class Order {

    /**
     * Identifier for the order
     */
    @Id
    private String id;

    /**
     * Time of the order
     */
    @Indexed
    private long orderDate;

    /**
     * Name of the product
     */
    @Indexed
    private String productName;

    /**
     * Name of the customer
     */
    private String customerName;
}
