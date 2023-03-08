package com.amit.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create an order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    /**
     * Time at which order is placed
     */
    private long orderDate;

    /**
     * Product for which order is placed
     */
    private String productName;

    /**
     * Customer who placed the order
     */
    private String customerName;
}
