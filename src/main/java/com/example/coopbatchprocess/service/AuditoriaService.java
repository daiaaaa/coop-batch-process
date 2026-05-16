package com.example.coopbatchprocess.service;

import com.example.coopbatchprocess.entity.Transacao;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

	private static final Logger LOG = LoggerFactory.getLogger(AuditoriaService.class);

	private final AtomicLong totalProcessado = new AtomicLong();
	private final AtomicLong totalInvalido = new AtomicLong();
	private final AtomicLong totalSuspeito = new AtomicLong();
	private final AtomicLong totalSalvo = new AtomicLong();

	public void iniciarJob(String jobName, LocalDateTime inicio) {
		resetarContadores();
		LOG.info("[JOB STARTED] Job={} Inicio={}", jobName, inicio);
		LOG.info("[READER] Lendo arquivo transacoes.csv");
	}

	public void finalizarJob(String jobName, String status, LocalDateTime inicio, LocalDateTime fim) {
		long duracaoMillis = inicio != null && fim != null ? Duration.between(inicio, fim).toMillis() : 0;
		LOG.info("[JOB FINISHED] Job={} Status={} DuracaoMs={}", jobName, status, duracaoMillis);
		LOG.info(
				"[METRICS] TotalProcessado={} TotalInvalido={} TotalSuspeito={} TotalSalvo={}",
				getTotalProcessado(),
				getTotalInvalido(),
				getTotalSuspeito(),
				getTotalSalvo());
	}

	public void registrarStep(String stepName, long readCount, long writeCount, long filterCount, long skipCount) {
		totalSalvo.set(writeCount);
		LOG.info(
				"[STEP] Step={} ReadCount={} WriteCount={} FilterCount={} SkipCount={}",
				stepName,
				readCount,
				writeCount,
				filterCount,
				skipCount);
	}

	public void registrarChunkInicio() {
		LOG.debug("[CHUNK] Inicio");
	}

	public void registrarChunkFim(int quantidadeItens) {
		LOG.info("[WRITER] RegistrosPersistidosNoChunk={}", quantidadeItens);
	}

	public void registrarFalhaChunk(Exception exception) {
		registrarFalha("CHUNK", exception);
	}

	public void registrarProcessamento(Transacao transacao) {
		totalProcessado.incrementAndGet();
		LOG.debug(
				"[PROCESSOR] Conta={} Cliente={} Tipo={} Valor={}",
				transacao.getConta(),
				transacao.getCliente(),
				transacao.getTipo(),
				transacao.getValor());
	}

	public void registrarSuspeita(Transacao transacao) {
		totalSuspeito.incrementAndGet();
		LOG.warn(
				"[PROCESSOR] Conta={} Cliente={} Tipo={} Valor={} Suspeita=true",
				transacao.getConta(),
				transacao.getCliente(),
				transacao.getTipo(),
				transacao.getValor());
	}

	public void registrarInvalida(Transacao transacao, String motivo) {
		totalInvalido.incrementAndGet();
		LOG.warn(
				"[ERROR] Conta={} Cliente={} Tipo={} Valor={} Motivo={}",
				transacao.getConta(),
				transacao.getCliente(),
				transacao.getTipo(),
				formatarValor(transacao.getValor()),
				motivo);
	}

	public void registrarFalha(String camada, Throwable exception) {
		LOG.error("[ERROR] Camada={} Motivo={}", camada, exception.getMessage(), exception);
	}

	public void resetarContadores() {
		totalProcessado.set(0);
		totalInvalido.set(0);
		totalSuspeito.set(0);
		totalSalvo.set(0);
	}

	public long getTotalProcessado() {
		return totalProcessado.get();
	}

	public long getTotalInvalido() {
		return totalInvalido.get();
	}

	public long getTotalSuspeito() {
		return totalSuspeito.get();
	}

	public long getTotalSalvo() {
		return totalSalvo.get();
	}

	private String formatarValor(BigDecimal valor) {
		return valor == null ? "null" : valor.toPlainString();
	}
}
