package com.example.coopbatchprocess.listener;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.service.AuditoriaService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.step.StepExecution;

class BatchObservabilityListenerTest {

	@Test
	void deveResetarContadoresAoIniciarJobERegistrarSalvosAoFinalizarStep() {
		AuditoriaService auditoriaService = new AuditoriaService();
		JobExecutionListener jobListener = new JobExecutionListener(auditoriaService);
		StepExecutionListener stepListener = new StepExecutionListener(auditoriaService);

		auditoriaService.registrarStep("preStep", 1, 1, 0, 0);
		JobExecution jobExecution = new JobExecution(
				1,
				new JobInstance(1, "importacaoTransacoesJob"),
				new JobParametersBuilder().toJobParameters());
		jobExecution.setStartTime(LocalDateTime.now());
		jobExecution.setEndTime(LocalDateTime.now());
		jobExecution.setStatus(BatchStatus.COMPLETED);

		StepExecution stepExecution = new StepExecution(1, "importacaoTransacoesStep", jobExecution);
		stepExecution.setReadCount(5);
		stepExecution.setWriteCount(3);
		stepExecution.setFilterCount(2);

		jobListener.beforeJob(jobExecution);
		stepListener.afterStep(stepExecution);
		jobListener.afterJob(jobExecution);

		assertThat(auditoriaService.getTotalProcessado()).isZero();
		assertThat(auditoriaService.getTotalSalvo()).isEqualTo(3);
	}
}
