package com.example.coopbatchprocess.reader;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coopbatchprocess.entity.Transacao;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;

class TransacaoReaderTest {

	@Test
	void deveLerCsvIgnorandoCabecalhoEConverterLinhaEmTransacao() throws Exception {
		FlatFileItemReader<Transacao> reader = new TransacaoReaderConfig().transacaoReader();
		reader.open(new ExecutionContext());

		Transacao transacao = reader.read();

		assertThat(transacao).isNotNull();
		assertThat(transacao.getConta()).isEqualTo("1001");
		assertThat(transacao.getCliente()).isEqualTo("Ana");
		assertThat(transacao.getValor()).isEqualByComparingTo(new BigDecimal("500"));
		assertThat(transacao.getTipo()).isEqualTo("DEPOSITO");

		reader.close();
	}
}
