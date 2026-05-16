package com.example.coopbatchprocess.reader;

import com.example.coopbatchprocess.entity.Transacao;
import java.math.BigDecimal;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TransacaoReaderConfig {

	@Bean
	public FlatFileItemReader<Transacao> transacaoReader() {
		return new FlatFileItemReaderBuilder<Transacao>()
				.name("transacaoReader")
				.resource(new ClassPathResource("input/transacoes.csv"))
				.linesToSkip(1)
				.delimited()
				.names("conta", "cliente", "valor", "tipo")
				.fieldSetMapper(fieldSet -> {
					Transacao transacao = new Transacao();
					transacao.setConta(fieldSet.readString("conta"));
					transacao.setCliente(fieldSet.readString("cliente"));
					transacao.setValor(new BigDecimal(fieldSet.readString("valor")));
					transacao.setTipo(fieldSet.readString("tipo"));
					return transacao;
				})
				.build();
	}
}
