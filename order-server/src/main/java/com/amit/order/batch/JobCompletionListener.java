package com.amit.order.batch;

import com.amit.order.export.Status;
import com.amit.order.util.JobParamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Lister to intercept the completion message for the {@link Job}
 */
@Component
@RequiredArgsConstructor
public class JobCompletionListener extends JobExecutionListenerSupport {

    /**
     * Interface to inform about completion of a {@link Job}
     */
    private final StatusResponder statusResponder;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

        Status status = jobExecution.getStatus() == BatchStatus.COMPLETED ? Status.COMPLETED : Status.FAILED;
        statusResponder.sendStatus(JobParamUtil.convertToMap(jobExecution.getJobParameters()), status);
    }
}
