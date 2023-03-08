package com.amit.order.controller;


import com.amit.order.batch.JobScheduler;
import com.amit.order.entity.Order;
import com.amit.order.export.ExportRequest;
import com.amit.order.service.OrderService;
import com.amit.order.util.JobParamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Interface to place and export orders placed
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    /**
     * Interface to schedule jobs for exporting orders
     */
    private final JobScheduler scheduler;

    /**
     * Service to interact with orders
     */
    private final OrderService orderService;

    /**
     * API is create a new {@link Order}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody Order order) {
        order.setOrderDate(System.currentTimeMillis());
        return orderService.placeOrder(order);
    }

    /**
     * Interface to export the orders available
     */
    @GetMapping(value = "/export")
    @ResponseStatus(HttpStatus.OK)
    public String exportOrders(@RequestBody ExportRequest request) throws Exception {

        JobParameters jobParameters = JobParamUtil.createParams(request);
        scheduler.schedule(jobParameters);
        return jobParameters.getString("key");
    }
}
