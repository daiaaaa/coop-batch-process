package com.example.coopbatchprocess.processor;

import com.example.coopbatchprocess.entity.Transacao;
import com.example.coopbatchprocess.service.AuditoriaService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransacaoProcessor implements ItemProcessor<Transacao, Transacao> {

	private static final BigDecimal LIMITE_SAQUE_SUSPEITO = new BigDecimal("5000");

	private final AuditoriaService auditoriaService;

	public TransacaoProcessor(AuditoriaService auditoriaService) {
		this.auditoriaService = auditoriaService;
	}

	@Override
	public Transacao process(Transacao transacao) {
		auditoriaService.registrarProcessamento(transacao);

		if (valorInvalido(transacao)) {
			auditoriaService.registrarInvalida(transacao, "Valor invalido");
			return null;
		}

		if (tipoInvalido(transacao)) {
			auditoriaService.registrarInvalida(transacao, "Tipo invalido");
			return null;
		}

		TipoTransacao tipo = TipoTransacao.valueOf(transacao.getTipo());
		transacao.setSuspeita(ehSaqueSuspeito(transacao, tipo));
		transacao.setDataProcessamento(LocalDateTime.now());

		if (transacao.isSuspeita()) {
			auditoriaService.registrarSuspeita(transacao);
		}

		return transacao;
	}

	private boolean valorInvalido(Transacao transacao) {
		return transacao.getValor() == null || transacao.getValor().compareTo(BigDecimal.ZERO) <= 0;
	}

	private boolean tipoInvalido(Transacao transacao) {
		if (transacao.getTipo() == null) {
			return true;
		}

		try {
			TipoTransacao.valueOf(transacao.getTipo());
			return false;
		} catch (IllegalArgumentException exception) {
			return true;
		}
	}

	private boolean ehSaqueSuspeito(Transacao transacao, TipoTransacao tipo) {
		return TipoTransacao.SAQUE == tipo && transacao.getValor().compareTo(LIMITE_SAQUE_SUSPEITO) > 0;
	}
}
