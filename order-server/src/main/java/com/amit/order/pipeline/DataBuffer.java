package com.amit.order.pipeline;

import com.amit.order.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a data buffer that store the data collected.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DataBuffer {

    /**
     * Records in the buffer
     */
    private List<Order> orders;

    /**
     * Create the buffer with the required capacity
     *
     * @param capacity with which buffer needs to be created
     */
    public DataBuffer(int capacity) {

        orders = new ArrayList<>(capacity);
    }
}
