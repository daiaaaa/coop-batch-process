package com.example.coopbatchprocess.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacao {

	private String conta;
	private String cliente;
	private BigDecimal valor;
	private String tipo;
	private boolean suspeita;
	private LocalDateTime dataProcessamento;

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isSuspeita() {
		return suspeita;
	}

	public void setSuspeita(boolean suspeita) {
		this.suspeita = suspeita;
	}

	public LocalDateTime getDataProcessamento() {
		return dataProcessamento;
	}

	public void setDataProcessamento(LocalDateTime dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}
}
