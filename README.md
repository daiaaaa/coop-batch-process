# Coop Batch Process

Sistema de processamento em lote para cooperativa financeira utilizando Java 21, Spring Boot, Spring Batch e PostgreSQL.

## Objetivo

Automatizar o processamento diario de movimentacoes financeiras recebidas por uma cooperativa financeira, garantindo validacao de regras de negocio, persistencia das transacoes validas, identificacao de movimentacoes suspeitas e registro do resultado do processamento.

## Fluxo do Sistema

```text
Arquivo CSV -> Reader -> Processor -> Writer -> PostgreSQL
```

O processamento segue o modelo classico do Spring Batch:

- **Reader**: leitura das transacoes financeiras a partir de arquivo CSV.
- **Processor**: aplicacao das regras de negocio e validacoes.
- **Writer**: persistencia das transacoes validas no PostgreSQL.

## Requisitos Funcionais

- Importar arquivos CSV.
- Processar transacoes financeiras.
- Validar regras de negocio.
- Detectar movimentacoes suspeitas.
- Persistir transacoes validas.
- Registrar logs do processamento.
- Gerar relatorio final.

## Requisitos Nao Funcionais

- Arquitetura organizada.
- Clean Code e SOLID.
- Testes automatizados.
- Desenvolvimento orientado a TDD.
- Escalabilidade.
- Resiliencia.
- Observabilidade via logs.

## Regra de Negocio Principal

Uma transacao deve ser marcada como suspeita quando atender ao seguinte criterio:

```text
tipo = SAQUE
valor > 5000
```

Resultado esperado:

```text
suspeita = true
```

## Estrutura Planejada

```text
src/main/java/com/daiana/coopbatchprocess
|-- batch
|-- config
|-- entity
|-- processor
|-- reader
|-- repository
|-- service
`-- writer
```

> Observacao: o projeto Spring Boot foi gerado inicialmente com o pacote `com.example.coopbatchprocess`. A estrutura planejada acima representa a organizacao alvo do projeto.

## Etapas do Projeto

1. Setup inicial.
2. Modelagem da entidade.
3. Reader do CSV.
4. Processor com regras de negocio.
5. Writer para PostgreSQL.
6. Job e Step.
7. Logs e observabilidade.
8. Tratamento de erros.
9. Testes.
10. Docker.
11. Scheduler.
12. Melhorias enterprise.

## Estrategia TDD

O desenvolvimento deve seguir o ciclo:

```text
Red -> Green -> Refactor
```

Aplicacao no projeto:

1. Criar um teste que descreve o comportamento esperado.
2. Implementar o minimo necessario para o teste passar.
3. Refatorar mantendo a suite de testes verde.

## Primeira Etapa Implementada

- Configuracao inicial do Spring Boot.
- Dependencias do Spring Batch.
- Dependencias do Spring Data JPA.
- Driver PostgreSQL.
- Estrutura inicial da aplicacao.
- Primeiro teste de contexto do Spring Boot.

## Tecnologias

- Java 21.
- Spring Boot.
- Spring Batch.
- Spring Data JPA.
- PostgreSQL.
- Maven.

## Como Executar

Execute os testes:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

Execute a aplicacao:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```
