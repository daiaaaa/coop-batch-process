package com.example.coopbatchprocess.writer;

import com.example.coopbatchprocess.entity.Transacao;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(EntityManagerFactory.class)
public class TransacaoWriterConfig {

	@Bean
	public JpaItemWriter<Transacao> transacaoWriter(EntityManagerFactory entityManagerFactory) {
		return new JpaItemWriterBuilder<Transacao>()
				.entityManagerFactory(entityManagerFactory)
				.usePersist(true)
				.build();
	}
}
