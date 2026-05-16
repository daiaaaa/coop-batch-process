package com.example.coopbatchprocess.listener;

import com.example.coopbatchprocess.entity.Transacao;
import com.example.coopbatchprocess.service.AuditoriaService;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class ChunkExecutionListener implements org.springframework.batch.core.listener.ChunkListener<Transacao, Transacao> {

	private final AuditoriaService auditoriaService;

	public ChunkExecutionListener(AuditoriaService auditoriaService) {
		this.auditoriaService = auditoriaService;
	}

	@Override
	public void beforeChunk(Chunk<Transacao> chunk) {
		auditoriaService.registrarChunkInicio();
	}

	@Override
	public void afterChunk(Chunk<Transacao> chunk) {
		auditoriaService.registrarChunkFim(chunk.size());
	}

	@Override
	public void onChunkError(Exception exception, Chunk<Transacao> chunk) {
		auditoriaService.registrarFalhaChunk(exception);
	}
}
