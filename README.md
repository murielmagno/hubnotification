# 📬 Hub Notification

Serviço responsável por gerenciar o envio de notificações para múltiplos canais (EMAIL, SMS, PUSH, WHATSAPP).

## 🧱 Tecnologias

- Java 21
- Spring Boot 3
- Spring Security com JWT
- JPA + Hibernate
- Flyway
- H2 (dev/test)
- PostgreSQL (produção)
- Docker + Docker Compose
- JUnit + Mockito

---

## ✨ Como subir o projeto com Docker Compose

### Pré-requisitos

- [Docker](https://www.docker.com/)

### 1. Clone o repositório

```bash
https://github.com/murielmagno/hubnotification.git
cd notification
```

### 2. Suba os containers

```bash
docker-compose up --build
```

O backend estará disponível em: `http://localhost:8080`

### 3. Acessar o banco (PostgreSQL)

- Host: `localhost`
- Porta: `5432`
- Banco: `notification`
- Usuário: `admin`
- Senha: `admin`

---

## 📘 Swagger / OpenAPI

Documentação da API disponível via Swagger UI:

- Controllers organizados e agrupados por recurso (`/v1/notifications`, `/v1/auth`)
- Exemplos de valores esperados nos payloads de entrada
- Documentação acessível em:
  - Swagger UI: [`/swagger-ui.html`](http://localhost:8080/api/swagger-ui/index.html)
  - JSON: [`/v3/api-docs`](http://localhost:8080/api/v3/api-docs/public)

### Status da documentação:

- ✅ Controllers 100% anotados
- ✅ DTOs com validação + exemplos
- ⚠️ Respostas de erro globais podem ser detalhadas futuramente

---

## ✅ Executar os testes

```bash
./mvnw test
```

ou se preferir com cobertura de código:

```bash
./mvnw verify
```

---

## 📢 Endpoints principais

| Método | Endpoint                  | Descrição                  |
|--------|---------------------------|----------------------------|
| POST   | `/v1/notifications`       | Cria uma nova notificação  |
| GET    | `/v1/notifications/{id}`  | Buscar notificação por ID  |
| DELETE | `/v1/notifications/{id}`  | Deletar notificação por ID |

### Exemplo de request:

```json
{
  "message": "Mensagem de teste",
  "recipients": [
    { "channel": "EMAIL", "identifier": "user@email.com" },
    { "channel": "SMS", "identifier": "11999999999" }
  ]
}
```

---

## 🔐 Autenticação

A API utiliza **JWT Bearer Token**.

- Inclua no header:  
  `Authorization: Bearer <seu_token>`

---

## 🥪 Cobertura de Testes

Testes unitários já cobrem:

- Controller: status 200, 400 e 403
- Service: lógica de criação com persistência e fallback

---

