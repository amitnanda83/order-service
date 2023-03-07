package com.amit.order.controller;


import com.amit.order.batch.JobScheduler;
import com.amit.order.export.ExportRequest;
import com.amit.order.model.Order;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    private final JobScheduler scheduler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody Order order) {
        order.setOrderDate(System.currentTimeMillis());
        return orderService.placeOrder(order);
    }

    @GetMapping(value = "/export")
    @ResponseStatus(HttpStatus.OK)
    public String exportOrders(@RequestBody ExportRequest request) {

        try {
            JobParameters jobParameters = JobParamUtil.createParams(request);
            scheduler.schedule(jobParameters);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return "OK";
    }
}
