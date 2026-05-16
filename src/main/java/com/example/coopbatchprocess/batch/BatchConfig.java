package com.example.coopbatchprocess.batch;

import com.example.coopbatchprocess.entity.Transacao;
import com.example.coopbatchprocess.listener.ChunkExecutionListener;
import com.example.coopbatchprocess.listener.JobExecutionListener;
import com.example.coopbatchprocess.listener.StepExecutionListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnBean(JpaItemWriter.class)
public class BatchConfig {

	@Bean
	public Step importacaoTransacoesStep(
			JobRepository jobRepository,
			PlatformTransactionManager transactionManager,
			FlatFileItemReader<Transacao> transacaoReader,
			ItemProcessor<Transacao, Transacao> transacaoProcessor,
			JpaItemWriter<Transacao> transacaoWriter,
			StepExecutionListener stepExecutionListener,
			ChunkExecutionListener chunkExecutionListener) {
		return new StepBuilder("importacaoTransacoesStep", jobRepository)
				.<Transacao, Transacao>chunk(10)
				.transactionManager(transactionManager)
				.reader(transacaoReader)
				.processor(transacaoProcessor)
				.writer(transacaoWriter)
				.listener(stepExecutionListener)
				.listener(chunkExecutionListener)
				.build();
	}

	@Bean
	public Job importacaoTransacoesJob(
			JobRepository jobRepository,
			Step importacaoTransacoesStep,
			JobExecutionListener jobExecutionListener) {
		return new JobBuilder("importacaoTransacoesJob", jobRepository)
				.start(importacaoTransacoesStep)
				.listener(jobExecutionListener)
				.build();
	}
}
