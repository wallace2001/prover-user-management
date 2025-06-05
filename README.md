
# Prover Test Application

Sistema completo com autenticação JWT e CRUD de clientes, incluindo importação via CSV.

## 🧰 Tecnologias

- Java 17
- Spring Boot 3.5
- Spring Security
- PostgreSQL
- Liquibase
- Docker / Docker Compose
- Jacoco (para cobertura de testes)
- JUnit 5
- Mockito
- OpenAPI (Swagger)

---

## 🚀 Como rodar o projeto

### Com Docker (recomendado)

1. Certifique-se de ter o Docker e Docker Compose instalados.

2. Execute:

```bash
docker compose up --build
```

> Isso irá subir o banco de dados PostgreSQL e a aplicação Spring Boot automaticamente.

---

## ✅ Testes

```bash
   mvn clean test
```

Relatório de cobertura será gerado em: `target/site/jacoco/index.html`

---

## 📦 Importação via CSV

Faça upload de um arquivo `.csv` com os campos: `nome,email,telefone`.

Endpoint disponível na documentação Swagger.

---

## 📄 Documentação

Swagger UI disponível em:

```
http://localhost:8080/swagger-ui.html
```

---

## 🐳 Docker Compose

Estrutura de containers:

- `app`: aplicação Spring Boot
- `db`: PostgreSQL

---

## 👨‍💻 Autor

Wallace Silva — [GitHub](https://github.com/wallace-silva)

---

## 📝 Licença

MIT
