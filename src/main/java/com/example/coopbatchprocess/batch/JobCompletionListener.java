package com.example.coopbatchprocess.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {

	private static final Logger LOG = LoggerFactory.getLogger(JobCompletionListener.class);

	@Override
	public void beforeJob(JobExecution jobExecution) {
		LOG.info("JOB STARTED");
		LOG.info("Job name: {}", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LOG.info("JOB FINISHED");
		LOG.info("Job name: {}", jobExecution.getJobInstance().getJobName());
		LOG.info("Status: {}", jobExecution.getStatus());

		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			LOG.info("Step: {}", stepExecution.getStepName());
			LOG.info("Read count: {}", stepExecution.getReadCount());
			LOG.info("Write count: {}", stepExecution.getWriteCount());
			LOG.info("Filter count: {}", stepExecution.getFilterCount());
		}

		if (jobExecution.getStatus() == BatchStatus.FAILED) {
			jobExecution.getAllFailureExceptions()
					.forEach(exception -> LOG.error("Job failure: {}", exception.getMessage(), exception));
		}
	}
}
