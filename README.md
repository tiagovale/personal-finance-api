# Personal Finance API

REST API for managing personal finance accounts, developed with Java and Spring Boot.

This project is being built as a hands-on study of REST API design, HTTP semantics, Java, Spring Boot, persistence, and related backend practices.

The implementation is intentionally simple, keeping the focus on API design and backend practices rather than business-domain complexity.

**Repository:** https://github.com/tiagovale/personal-finance-api

## Technologies

* Java 21
* Spring Boot 4
* Spring Data JPA
* Spring HATEOAS
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
│   ├── AccountModelAssembler.java
│   ├── AccountRepository.java
│   ├── AccountResponse.java
│   ├── AccountService.java
│   ├── AccountType.java
│   ├── ETagGenerator.java
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

| **Method** | **Endpoint**     | **Description**             | **Response**                                    |
| ---------- | ---------------- | --------------------------- | ----------------------------------------------- |
| POST       | `/accounts`      | Create an account           | `201 Created`                                   |
| GET        | `/accounts`      | Find all accounts           | `200 OK`                                        |
| GET        | `/accounts/{id}` | Find an account by ID       | `200 OK` / `304 Not Modified` / `404 Not Found` |
| PUT        | `/accounts/{id}` | Replace an account          | `200 OK` / `404 Not Found`                      |
| PATCH      | `/accounts/{id}` | Partially update an account | `200 OK` / `404 Not Found`                      |
| DELETE     | `/accounts/{id}` | Delete an account           | `204 No Content` / `404 Not Found`              |

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

The collection is represented using **Spring HATEOAS** and includes navigation links:

```json
{
  "_embedded": {
    "accountResponseList": [
      {
        "id": 1,
        "name": "Conta Principal",
        "type": "CHECKING",
        "balance": 1500.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/v1/accounts/1"
          },
          "all-accounts": {
            "href": "http://localhost:8080/api/v1/accounts"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/accounts"
    }
  }
}
```

When no accounts exist, the endpoint still returns:

```http
200 OK
```

with an empty collection.

## Find Account by ID

```http
GET /api/v1/accounts/1
```

If the account exists and no matching ETag is provided:

```http
200 OK
ETag: "a1b2c3..."
```

```json
{
  "id": 1,
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 1500.00,
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/accounts/1"
    },
    "all-accounts": {
      "href": "http://localhost:8080/api/v1/accounts"
    }
  }
}
```

If the account does not exist:

```http
404 Not Found
```

## HTTP Caching with ETag

The API implements HTTP-level caching using `ETag`, `If-None-Match`, and `304 Not Modified`.

The goal is to avoid sending the same resource representation to the client when the resource has not changed.

### First request

When the client requests an account without an `If-None-Match` header:

```http
GET /api/v1/accounts/1
```

The API returns the resource together with an ETag:

```http
200 OK
ETag: "a1b2c3..."
```

The ETag identifies the current representation of the resource.

### Subsequent request

The client can send the ETag it previously received:

```http
GET /api/v1/accounts/1
If-None-Match: "a1b2c3..."
```

The API compares the received ETag with the current representation.

If the resource has not changed, the API returns:

```http
304 Not Modified
ETag: "a1b2c3..."
```

No response body is returned because the client can reuse the representation it already has.

### When the resource changes

If the account is modified, its representation changes and a new ETag is generated.

For example, after changing the balance:

```http
PUT /api/v1/accounts/1
```

A subsequent request using the old ETag will result in:

```http
200 OK
ETag: "d4e5f6..."
```

The updated representation is returned together with the new ETag.

### ETag generation

The project uses a dedicated `ETagGenerator` component to generate a deterministic hash based on the current `AccountResponse`.

The implementation uses the generated hash as the ETag value.

This project uses HTTP-level caching rather than application-level caching mechanisms such as `@Cacheable`, Redis, or Caffeine.

The goal is to explore how HTTP itself can be used to avoid unnecessary data transfers when a resource has not changed.

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

The PATCH request uses `JsonNullable` to distinguish between a field that was not provided and a field that was explicitly provided as `null`.

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
* HTTP status codes such as `200`, `201`, `204`, `304`, `404`, and `405`
* `Location` header after resource creation
* `Optional` for resource lookup
* Idempotent update operations
* Partial resource updates with `PATCH`
* DTOs using Java records
* `JsonNullable` for PATCH request semantics
* Package-by-feature organization
* REST Level 3
* HATEOAS
* Hypermedia links
* Representation models
* Collection representations
* `RepresentationModelAssembler`
* HTTP caching with `ETag`
* Conditional requests with `If-None-Match`
* `304 Not Modified`

## REST Level 3 / HATEOAS

The API currently includes a REST Level 3 implementation using **Spring HATEOAS**.

Resource representations expose hypermedia links that allow clients to navigate between related resources.

For example, an account representation exposes:

* `self` — link to the current account
* `all-accounts` — link to the account collection

The implementation uses:

* `RepresentationModel`
* `CollectionModel`
* `RepresentationModelAssembler`
* `linkTo()`
* `methodOn()`

The goal is to explore how hypermedia can be used as part of a REST API rather than simply returning resource data.

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
* [x] REST Level 3 / HATEOAS
* [x] HTTP Caching / ETag
* [ ] Pagination

### API Improvements

* [ ] Bean Validation
* [ ] Global exception handling
* [ ] DTO improvements
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
