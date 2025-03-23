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

| Método | Endpoint                  | Descrição                                |
|--------|---------------------------|------------------------------------------|
| POST   | `/v1/notifications`       | Cria uma nova notificação                |
| GET    | `/v1/notifications/{id}`  | (Futuro) Buscar notificação por ID       |

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

