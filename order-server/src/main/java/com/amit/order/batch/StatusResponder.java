package com.amit.order.batch;

import com.amit.order.export.ExportResponse;
import com.amit.order.export.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helper class to send different {@link Status} for export order jobs.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusResponder {

    /**
     * Queue on which message needs to be send
     */
    private final Queue queue;

    /**
     * Template to connect to Rabbit
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * Method to send the status message
     */
    public void sendStatus(Map<String, String> params, Status status) {

        rabbitTemplate.convertAndSend(queue.getName(),
                ExportResponse.builder()
                        .status(status)
                        .id(params.get("key"))
                        .filePath(params.get("path"))
                        .build());

        log.info("Sent {} status message for {}", status, params.get("key"));
    }
}
