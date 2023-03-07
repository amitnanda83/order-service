package com.amit.order.batch;

import com.amit.order.export.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobCompletionListener extends JobExecutionListenerSupport {

    private final StatusResponder statusResponder;

    @Override
    public void afterJob(JobExecution jobExecution) {

        Status status = jobExecution.getStatus() == BatchStatus.COMPLETED ? Status.COMPLETED : Status.FAILED;
        statusResponder.sendStatus(jobExecution.getJobParameters(), status);
    }
}
