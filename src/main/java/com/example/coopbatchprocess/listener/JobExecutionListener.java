package com.example.coopbatchprocess.listener;

import com.example.coopbatchprocess.service.AuditoriaService;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionListener implements org.springframework.batch.core.listener.JobExecutionListener {

	private final AuditoriaService auditoriaService;

	public JobExecutionListener(AuditoriaService auditoriaService) {
		this.auditoriaService = auditoriaService;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		auditoriaService.iniciarJob(
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getStartTime());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		jobExecution.getAllFailureExceptions()
				.forEach(exception -> auditoriaService.registrarFalha("JOB", exception));

		auditoriaService.finalizarJob(
				jobExecution.getJobInstance().getJobName(),
				jobExecution.getStatus().name(),
				jobExecution.getStartTime(),
				jobExecution.getEndTime());
	}
}
