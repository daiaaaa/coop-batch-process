package com.example.coopbatchprocess.writer;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.entity.Transacao;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
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
class TransacaoWriterIntegrationTest {

	@Container
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
			.withDatabaseName("coopbatchprocess")
			.withUsername("postgres")
			.withPassword("postgres");

	@Autowired
	private JpaItemWriter<Transacao> writer;

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
	void deveSalvarTransacaoValida() {
		Transacao transacao = transacao("1001", "Ana", "500", "DEPOSITO", false);

		writer.write(Chunk.of(transacao));
		entityManager.flush();
		entityManager.clear();

		Transacao salva = buscarPorCliente("Ana");

		assertThat(salva.getCliente()).isEqualTo("Ana");
		assertThat(salva.getValor()).isEqualByComparingTo(new BigDecimal("500"));
		assertThat(salva.getTipo()).isEqualTo("DEPOSITO");
		assertThat(salva.isSuspeita()).isFalse();
		assertThat(salva.getDataProcessamento()).isNotNull();
	}

	@Test
	void deveSalvarTransacaoSuspeita() {
		Transacao transacao = transacao("1002", "Carlos", "7000", "SAQUE", true);

		writer.write(Chunk.of(transacao));
		entityManager.flush();
		entityManager.clear();

		Transacao salva = buscarPorCliente("Carlos");

		assertThat(salva.getCliente()).isEqualTo("Carlos");
		assertThat(salva.getValor()).isEqualByComparingTo(new BigDecimal("7000"));
		assertThat(salva.getTipo()).isEqualTo("SAQUE");
		assertThat(salva.isSuspeita()).isTrue();
		assertThat(salva.getDataProcessamento()).isNotNull();
	}

	private Transacao buscarPorCliente(String cliente) {
		return entityManager.createQuery(
						"select transacao from Transacao transacao where transacao.cliente = :cliente",
						Transacao.class)
				.setParameter("cliente", cliente)
				.getSingleResult();
	}

	private Transacao transacao(String conta, String cliente, String valor, String tipo, boolean suspeita) {
		Transacao transacao = new Transacao();
		transacao.setConta(conta);
		transacao.setCliente(cliente);
		transacao.setValor(new BigDecimal(valor));
		transacao.setTipo(tipo);
		transacao.setSuspeita(suspeita);
		transacao.setDataProcessamento(LocalDateTime.now());
		return transacao;
	}
}
