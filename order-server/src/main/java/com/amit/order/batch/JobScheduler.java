package com.amit.order.batch;

import com.amit.order.export.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Helper class to schedule {@link Job}s for Spring batch
 */
@Slf4j
@Component
public class JobScheduler {

    /**
     * Job to be executed
     */
    @Autowired
    private Job job;

    /**
     * Interface to execute the Job
     */
    @Autowired
    @Qualifier("myJobLauncher")
    private JobLauncher jobLauncher;

    /**
     * Interface to send status of export jobs
     */
    @Autowired
    private StatusResponder statusResponder;

    /**
     * Method to schedule a {@link Job} for Spring Batch processing
     */
    public void schedule(JobParameters params) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        log.info("Scheduled Batch Job for : {}", params.getString("key"));

        jobLauncher.run(job, params);
        statusResponder.sendStatus(params, Status.IN_PROGRESS);
    }
}
