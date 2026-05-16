package com.example.coopbatchprocess.listener;

import com.example.coopbatchprocess.service.AuditoriaService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class StepExecutionListener implements org.springframework.batch.core.listener.StepExecutionListener {

	private final AuditoriaService auditoriaService;

	public StepExecutionListener(AuditoriaService auditoriaService) {
		this.auditoriaService = auditoriaService;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		stepExecution.getFailureExceptions()
				.forEach(exception -> auditoriaService.registrarFalha("STEP", exception));

		auditoriaService.registrarStep(
				stepExecution.getStepName(),
				stepExecution.getReadCount(),
				stepExecution.getWriteCount(),
				stepExecution.getFilterCount(),
				stepExecution.getSkipCount());

		return stepExecution.getExitStatus();
	}
}
