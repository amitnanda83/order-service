package com.amit.order.service;


import com.amit.order.entity.Order;
import com.amit.order.export.ExportFilter;
import com.amit.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Order> getRecords(ExportFilter filter, int pageSize, int page) {

        List<Order> orders = orderRepository.findData(filter.getProduct(), filter.getDate().getFrom(),
                filter.getDate().getTo(), PageRequest.of(page, pageSize));

        return orders;
    }
}
