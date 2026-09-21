# Personal Finance API

REST API for managing personal finance accounts, developed with Java and Spring Boot.

This project is being built as a hands-on study of REST API design, HTTP semantics, Java, Spring Boot, persistence, and related backend practices.

The implementation is intentionally simple, keeping the focus on API design and backend practices rather than business-domain complexity.

## Technologies

* Java 21
* Spring Boot 4
* Spring Data JPA
* PostgreSQL 16
* Docker
* Maven

## Project Structure

The project uses a **package-by-feature** structure, keeping the classes related to each domain resource together.

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
│       ├── CreateAccountRequest.java
│       ├── UpdateAccountRequest.java
│       └── UpdateAccountPatchRequest.java
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

| Method | Endpoint         | Description                 | Response                           |
| ------ | ---------------- | --------------------------- | ---------------------------------- |
| POST   | `/accounts`      | Create an account           | `201 Created`                      |
| GET    | `/accounts`      | Find all accounts           | `200 OK`                           |
| GET    | `/accounts/{id}` | Find an account by ID       | `200 OK` / `404 Not Found`         |
| PUT    | `/accounts/{id}` | Replace an account          | `200 OK` / `404 Not Found`         |
| PATCH  | `/accounts/{id}` | Partially update an account | `200 OK` / `404 Not Found`         |
| DELETE | `/accounts/{id}` | Delete an account           | `204 No Content` / `404 Not Found` |

## Create Account

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

## Find All Accounts

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

When no accounts exist, the endpoint returns an empty collection:

```http
200 OK
```

```json
[]
```

## Find Account by ID

```http
GET /api/v1/accounts/1
```

If the account exists:

```http
200 OK
```

```json
{
  "id": 1,
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 1500.00
}
```

If the account does not exist:

```http
404 Not Found
```

## Replace Account

The `PUT` endpoint replaces the account representation.

```http
PUT /api/v1/accounts/1
Content-Type: application/json
```

Request:

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 2000.00
}
```

Response:

```http
200 OK
```

```json
{
  "id": 1,
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 2000.00
}
```

If the account does not exist:

```http
404 Not Found
```

## Partially Update Account

The `PATCH` endpoint allows individual fields to be updated without replacing the entire account.

```http
PATCH /api/v1/accounts/1
Content-Type: application/json
```

For example, only the balance can be updated:

```json
{
  "balance": 2500.00
}
```

The other fields remain unchanged.

Response:

```http
200 OK
```

```json
{
  "id": 1,
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 2500.00
}
```

The PATCH request uses `JsonNullable` to distinguish between a field that was not provided and a field that was explicitly provided.

## Delete Account

```http
DELETE /api/v1/accounts/1
```

If the account exists and is successfully deleted:

```http
204 No Content
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

## REST and HTTP Semantics

The API is being developed with a focus on understanding and applying HTTP semantics in practice.

Some of the concepts explored in the current implementation include:

* Resource-oriented endpoints
* HTTP methods: `POST`, `GET`, `PUT`, `PATCH`, and `DELETE`
* HTTP status codes such as `200`, `201`, `204`, `404`, and `405`
* `Location` header after resource creation
* `Optional` for resource lookup
* Idempotent update operations
* Partial resource updates with `PATCH`
* DTOs using Java records
* `JsonNullable` for PATCH request semantics
* Package-by-feature organization

## Development

This project is continuously evolving as new REST concepts and backend practices are explored.

The implementation and design decisions made during development are documented through a series of technical articles.

## Roadmap

### REST and API Design

* [x] Create Account resource
* [x] POST `/api/v1/accounts`
* [x] GET `/api/v1/accounts`
* [x] GET `/api/v1/accounts/{id}`
* [x] PUT `/api/v1/accounts/{id}`
* [x] PATCH `/api/v1/accounts/{id}`
* [x] DELETE `/api/v1/accounts/{id}`
* [ ] REST Level 3 / HATEOAS
* [ ] Caching

### API Improvements

* [ ] Bean Validation
* [ ] Global exception handling
* [ ] Response DTOs
* [ ] OpenAPI / Swagger
* [ ] Automated tests
* [ ] Database migrations

### Domain

* [ ] Transaction resource
* [ ] User resource
* [ ] Authentication and authorization

## Author

Tiago Vale

This project is part of a hands-on learning journey focused on backend development with Java and Spring Boot.
