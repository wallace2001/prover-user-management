# Prover Test Application

Este é um projeto Spring Boot que demonstra autenticação com JWT, CRUD completo de clientes, importação via CSV e estrutura de testes automatizados com cobertura Jacoco.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Liquibase
- Maven
- JUnit 5 + Mockito
- Jacoco
- Swagger (OpenAPI 3)

## Funcionalidades

- Autenticação com JWT (Login)
- CRUD de Clientes
- Importação de clientes via arquivo CSV
- Validações e tratativas de exceções personalizadas
- Testes unitários com cobertura
- Documentação Swagger

## Executando o Projeto

Certifique-se de ter o PostgreSQL rodando com a base configurada no `application.properties`.

```bash
mvn spring-boot:run
```

## Executando os Testes

```bash
mvn clean test
```

Para gerar o relatório de cobertura:

```bash
mvn jacoco:report
```

Relatório será gerado em: `target/site/jacoco/index.html`

## Endpoints

Documentação dos endpoints disponível via Swagger:

```
http://localhost:8080/swagger-ui/index.html
```

## Estrutura do Projeto

```
src
├── main
│   └── java
│       └── com.prover.prover_test
│           ├── application
│           │   ├── controller
│           │   └── service
│           ├── domain
│           │   ├── model
│           │   └── repository
│           └── infraestructure
│               ├── config
│               ├── exception
│               └── security
└── test
    └── java
        └── com.prover.prover_test
```

## Autor

Desenvolvido por Wallace Silva.