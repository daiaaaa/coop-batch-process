package com.example.coopbatchprocess.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.entity.Transacao;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AuditoriaServiceTest {

	private final AuditoriaService auditoriaService = new AuditoriaService();

	@Test
	void deveContabilizarProcessamentoSuspeitasInvalidosESalvos() {
		Transacao saqueSuspeito = transacao("1002", "Carlos", "7000", "SAQUE");
		Transacao invalida = transacao("1005", "Bia", "100", "PIX_DO_NADA");

		auditoriaService.registrarProcessamento(saqueSuspeito);
		auditoriaService.registrarSuspeita(saqueSuspeito);
		auditoriaService.registrarProcessamento(invalida);
		auditoriaService.registrarInvalida(invalida, "Tipo invalido");
		auditoriaService.registrarStep("importacaoTransacoesStep", 5, 3, 2, 0);

		assertThat(auditoriaService.getTotalProcessado()).isEqualTo(2);
		assertThat(auditoriaService.getTotalSuspeito()).isEqualTo(1);
		assertThat(auditoriaService.getTotalInvalido()).isEqualTo(1);
		assertThat(auditoriaService.getTotalSalvo()).isEqualTo(3);
	}

	@Test
	void deveResetarContadoresAoIniciarJob() {
		auditoriaService.registrarProcessamento(transacao("1001", "Ana", "500", "DEPOSITO"));
		auditoriaService.registrarStep("importacaoTransacoesStep", 1, 1, 0, 0);

		auditoriaService.iniciarJob("importacaoTransacoesJob", null);

		assertThat(auditoriaService.getTotalProcessado()).isZero();
		assertThat(auditoriaService.getTotalInvalido()).isZero();
		assertThat(auditoriaService.getTotalSuspeito()).isZero();
		assertThat(auditoriaService.getTotalSalvo()).isZero();
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
