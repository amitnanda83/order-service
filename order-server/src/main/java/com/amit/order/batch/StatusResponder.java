package com.amit.order.batch;

import com.amit.order.export.ExportResponse;
import com.amit.order.export.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusResponder {

    private final Queue queue;

    private final RabbitTemplate rabbitTemplate;

    public void sendStatus(JobParameters params, Status status) {

        rabbitTemplate.convertAndSend(queue.getName(),
                ExportResponse.builder()
                        .status(status)
                        .id(params.getString("key"))
                        .filePath(params.getString("path"))
                        .build());
    }
}
