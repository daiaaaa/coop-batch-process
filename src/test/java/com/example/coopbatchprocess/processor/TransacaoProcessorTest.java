package com.example.coopbatchprocess.processor;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.entity.Transacao;
import com.example.coopbatchprocess.service.AuditoriaService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransacaoProcessorTest {

	private AuditoriaService auditoriaService;
	private TransacaoProcessor processor;

	@BeforeEach
	void setUp() {
		auditoriaService = new AuditoriaService();
		processor = new TransacaoProcessor(auditoriaService);
	}

	@Test
	void deveMarcarSaqueSuspeito() {
		Transacao resultado = processor.process(transacao("1002", "Carlos", "7000", "SAQUE"));

		assertThat(resultado).isNotNull();
		assertThat(resultado.isSuspeita()).isTrue();
		assertThat(resultado.getDataProcessamento()).isNotNull();
		assertThat(auditoriaService.getTotalProcessado()).isEqualTo(1);
		assertThat(auditoriaService.getTotalSuspeito()).isEqualTo(1);
	}

	@Test
	void deveAceitarDepositoValido() {
		Transacao resultado = processor.process(transacao("1001", "Ana", "500", "DEPOSITO"));

		assertThat(resultado).isNotNull();
		assertThat(resultado.isSuspeita()).isFalse();
		assertThat(resultado.getCliente()).isEqualTo("Ana");
		assertThat(resultado.getValor()).isEqualByComparingTo(new BigDecimal("500"));
		assertThat(resultado.getTipo()).isEqualTo("DEPOSITO");
		assertThat(resultado.getDataProcessamento()).isNotNull();
		assertThat(auditoriaService.getTotalProcessado()).isEqualTo(1);
		assertThat(auditoriaService.getTotalSuspeito()).isZero();
	}

	@Test
	void deveDescartarValorInvalido() {
		Transacao resultado = processor.process(transacao("1004", "Joao", "0", "DEPOSITO"));

		assertThat(resultado).isNull();
		assertThat(auditoriaService.getTotalProcessado()).isEqualTo(1);
		assertThat(auditoriaService.getTotalInvalido()).isEqualTo(1);
	}

	@Test
	void deveDescartarTipoInvalido() {
		Transacao resultado = processor.process(transacao("1005", "Bia", "100", "PIX_DO_NADA"));

		assertThat(resultado).isNull();
		assertThat(auditoriaService.getTotalProcessado()).isEqualTo(1);
		assertThat(auditoriaService.getTotalInvalido()).isEqualTo(1);
	}

	private Transacao transacao(String conta, String cliente, String valor, String tipo) {
		Transacao transacao = new Transacao();
		transacao.setConta(conta);
		transacao.setCliente(cliente);
		transacao.setValor(new BigDecimal(valor));
		transacao.setTipo(tipo);
		return transacao;
	}
}
