package com.amit.order.batch;

import com.amit.order.export.Status;
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

@Component
public class JobScheduler {

    @Autowired
    private Job job;

    @Autowired
    @Qualifier("myJobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    private StatusResponder statusResponder;

    public void schedule(JobParameters params) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(job, params);
        statusResponder.sendStatus(params, Status.IN_PROGRESS);
    }
}
