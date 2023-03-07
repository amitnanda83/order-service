package com.amit.order.service;


import com.amit.order.model.Order;
import com.amit.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order placeOrder(Order order) {

        Order savedOrder = orderRepository.save(order);

        log.info("Order Placed : {}", savedOrder.getId());
        return savedOrder;
    }
}
