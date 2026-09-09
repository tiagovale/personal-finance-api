# Personal Finance API

REST API for managing personal finance accounts, developed with Java and Spring Boot.

This project is being built as a hands-on study of REST API design, HTTP semantics, Java, Spring Boot, persistence, and related backend practices.

The implementation is intentionally simple, keeping the focus on API design rather than business-domain complexity.

## Technologies

* Java 21
* Spring Boot 4
* Spring Data JPA
* PostgreSQL 16
* Docker
* Maven

## Project Structure

The project uses a **package-by-feature** structure.

```text
src/main/java/com/rest/personalfinance/personal_finance_api
│
├── account
│   ├── Account.java
│   ├── AccountController.java
│   ├── AccountRepository.java
│   ├── AccountService.java
│   ├── AccountType.java
│   └── dto
│       └── CreateAccountRequest.java
│
└── PersonalFinanceApiApplication.java
```

## Getting Started

### Prerequisites

Make sure you have installed:

* Java 21
* Docker
* Git

### 1. Clone the repository

```bash
git clone git@github.com:tiagovale/personal-finance-api.git
cd personal-finance-api
```

### 2. Start PostgreSQL

The project uses PostgreSQL running in Docker.

```bash
docker compose up -d
```

PostgreSQL will be available on:

```text
localhost:5433
```

Database:

```text
personal_finance
```

### 3. Run the application

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

## API

Base URL:

```text
/api/v1
```

### Accounts

| Method | Endpoint         | Description           |
| ------ | ---------------- | --------------------- |
| POST   | `/accounts`      | Create an account     |
| GET    | `/accounts`      | Find all accounts     |
| GET    | `/accounts/{id}` | Find an account by ID |

### Create Account

```http
POST /api/v1/accounts
Content-Type: application/json
```

Request:

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 1500.00
}
```

Response:

```http
201 Created
Location: /api/v1/accounts/1
```

```json
{
  "id": 1,
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 1500.00
}
```

### Find All Accounts

```http
GET /api/v1/accounts
```

Response:

```http
200 OK
```

```json
[
  {
    "id": 1,
    "name": "Conta Principal",
    "type": "CHECKING",
    "balance": 1500.00
  }
]
```

### Find Account by ID

```http
GET /api/v1/accounts/1
```

If the account exists:

```http
200 OK
```

If the account does not exist:

```http
404 Not Found
```

## Account Types

The following account types are currently supported:

```text
CHECKING
SAVINGS
CREDIT_CARD
```

## Development

This project is continuously evolving as new REST concepts and backend practices are explored.

The implementation and decisions made during development are documented through a series of technical articles.

## Roadmap

* [x] Create Account resource
* [x] POST `/api/v1/accounts`
* [x] GET `/api/v1/accounts`
* [x] GET `/api/v1/accounts/{id}`
* [ ] PUT `/api/v1/accounts/{id}`
* [ ] DELETE `/api/v1/accounts/{id}`
* [ ] Validation and error handling
* [ ] Automated tests
* [ ] Authentication
* [ ] Further REST API improvements

## Author

Tiago Vale

This project is part of a hands-on learning journey focused on backend development with Java and Spring Boot.
