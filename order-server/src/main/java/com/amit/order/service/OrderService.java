package com.amit.order.service;


import com.amit.order.entity.Order;
import com.amit.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to palce orders
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    /**
     * Interface to interact with mongo
     */
    private final OrderRepository orderRepository;

    /**
     * Method to palce and order
     */
    public Order placeOrder(Order order) {

        Order savedOrder = orderRepository.save(order);

        log.info("Order Placed : {}", savedOrder.getId());
        return savedOrder;
    }
}
