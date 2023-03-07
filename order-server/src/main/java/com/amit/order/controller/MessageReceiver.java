package com.amit.order.controller;

import com.amit.order.batch.JobScheduler;
import com.amit.order.export.ExportRequest;
import com.amit.order.util.JobParamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageReceiver {

    private final JobScheduler scheduler;

    @RabbitListener(queues = {"${queue.name}"})
    public void receive(final ExportRequest request) {

        try {
            // Convert the message into Job-Parameters
            scheduler.schedule(JobParamUtil.createParams(request));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
