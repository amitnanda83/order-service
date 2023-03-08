package com.amit.order.rabbit;

import com.amit.order.batch.JobScheduler;
import com.amit.order.export.ExportRequest;
import com.amit.order.util.JobParamUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Interface to receive requests for exporting orders
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageReceiver {

    /**
     * Interface to schedule jobs for exporting orders
     */
    private final JobScheduler scheduler;

    /**
     * Method which binds to the queue and receive message on it.
     */
    @RabbitListener(queues = {"${queue.name}"})
    public void receive(final ExportRequest request) {

        try {
            // Convert the message into Job-Parameters
            scheduler.schedule(JobParamUtil.createParams(request));
        }
        catch (Exception ex) {
            log.error("Failed to execute export orders with error : {}", ex.getMessage());
        }
    }
}
