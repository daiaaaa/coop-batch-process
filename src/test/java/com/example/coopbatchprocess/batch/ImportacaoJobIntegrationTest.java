package com.example.coopbatchprocess.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.entity.Transacao;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "spring.batch.job.enabled=false")
@Transactional
class ImportacaoJobIntegrationTest {

	@Container
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("coopbatchprocess")
			.withUsername("postgres")
			.withPassword("postgres");

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private Job importacaoTransacoesJob;

	@Autowired
	private EntityManager entityManager;

	@DynamicPropertySource
	static void configurarPostgres(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}

	@Test
	void deveExecutarJobCompletoEPersistirTransacoesValidas() throws Exception {
		JobExecution jobExecution = jobOperator.start(
				importacaoTransacoesJob,
				new JobParametersBuilder()
						.addLong("timestamp", System.currentTimeMillis())
						.toJobParameters());

		entityManager.flush();
		entityManager.clear();

		List<Transacao> transacoes = buscarTodasTransacoes();
		Transacao ana = buscarPorCliente("Ana");
		Transacao carlos = buscarPorCliente("Carlos");
		Transacao maria = buscarPorCliente("Maria");

		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
		assertThat(transacoes).hasSize(3);
		assertThat(ana.getValor()).isEqualByComparingTo(new BigDecimal("500"));
		assertThat(ana.getTipo()).isEqualTo("DEPOSITO");
		assertThat(ana.isSuspeita()).isFalse();
		assertThat(ana.getDataProcessamento()).isNotNull();
		assertThat(carlos.getValor()).isEqualByComparingTo(new BigDecimal("7000"));
		assertThat(carlos.getTipo()).isEqualTo("SAQUE");
		assertThat(carlos.isSuspeita()).isTrue();
		assertThat(carlos.getDataProcessamento()).isNotNull();
		assertThat(maria.getValor()).isEqualByComparingTo(new BigDecimal("1200"));
		assertThat(maria.isSuspeita()).isFalse();
		assertThat(buscarClientes()).doesNotContain("Joao", "Bia");
	}

	private Transacao buscarPorCliente(String cliente) {
		return entityManager.createQuery(
						"select transacao from Transacao transacao where transacao.cliente = :cliente",
						Transacao.class)
				.setParameter("cliente", cliente)
				.getSingleResult();
	}

	private List<String> buscarClientes() {
		return entityManager.createQuery(
						"select transacao.cliente from Transacao transacao",
						String.class)
				.getResultList();
	}

	private List<Transacao> buscarTodasTransacoes() {
		return entityManager.createQuery(
						"select transacao from Transacao transacao",
						Transacao.class)
				.getResultList();
	}
}
