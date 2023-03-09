package com.amit.order.rabbit;

import com.amit.order.batch.JobScheduler;
import com.amit.order.batch.StatusResponder;
import com.amit.order.export.ExportRequest;
import com.amit.order.export.Mode;
import com.amit.order.pipeline.DataPipeline;
import com.amit.order.service.OrderService;
import com.amit.order.util.JobParamUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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
     * Service to interact with orders
     */
    private final OrderService orderService;

    /**
     * Interface to inform about completion of a {@link Job}
     */
    private final StatusResponder statusResponder;

    /**
     * Method which binds to the queue and receive message on it.
     */
    @RabbitListener(queues = {"${queue.name}"})
    public void receive(final ExportRequest request) {

        try {
            // Convert the message into Job-Parameters
            JobParameters jobParameters = JobParamUtil.createParams(request);

            if (request.getMode().equals(Mode.SPRING)) {

                scheduler.schedule(jobParameters);
            }
            else if (request.getMode().equals(Mode.CUSTOM)) {

                new DataPipeline(jobParameters.getString("key"), orderService, statusResponder, request).start();
            }
            else {
                log.error("Unknown type");
            }
        }
        catch (Exception ex) {
            log.error("Failed to execute export orders with error : {}", ex.getMessage());
        }
    }
}
