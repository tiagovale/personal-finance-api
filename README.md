# Personal Finance API

REST API for managing personal finance accounts, developed with Java and Spring Boot.

This project is a hands-on study of REST API design, HTTP semantics, Java, Spring Boot, persistence, and backend engineering practices.

The implementation is intentionally simple, keeping the focus on API design and backend practices rather than business-domain complexity.

**Repository:** https://github.com/tiagovale/personal-finance-api
## Technologies

- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring HATEOAS
- PostgreSQL 16
- Docker
- Maven

## Project Structure

The project follows a **package-by-feature** structure, keeping classes related to each domain resource together.

```text
src/main/java/com/rest/personalfinance/personal_finance_api
│
├── account
│   ├── dto
│   │   ├── AccountResponse.java
│   │   ├── CreateAccountRequest.java
│   │   ├── UpdateAccountRequest.java
│   │   └── UpdateAccountPatchRequest.java
│   ├── Account.java
│   ├── AccountController.java
│   ├── AccountNotFoundException.java
│   ├── AccountModelAssembler.java
│   ├── AccountRepository.java
│   ├── AccountService.java
│   ├── AccountType.java
│   └── ETagGenerator.java
│
├── exception
│   └── GlobalExceptionHandler.java
│
└── PersonalFinanceApiApplication.java
```

## Getting Started

### Prerequisites

Make sure you have installed:

- Java 21
- Docker
- Git

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

PostgreSQL will be available at:

```text
localhost:5433
```

Database: `personal_finance`

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

Base path:

```text
/api/v1
```

### Accounts

| Method | Endpoint | Description | Response |
|---|---|---|---|
| `POST` | `/accounts` | Create an account | `201 Created` |
| `GET` | `/accounts` | List accounts with pagination | `200 OK` |
| `GET` | `/accounts/{id}` | Find an account by ID | `200 OK`, `304 Not Modified`, or `404 Not Found` |
| `PUT` | `/accounts/{id}` | Replace an account | `200 OK` or `404 Not Found` |
| `PATCH` | `/accounts/{id}` | Partially update an account | `200 OK` or `404 Not Found` |
| `DELETE` | `/accounts/{id}` | Delete an account | `204 No Content` or `404 Not Found` |

## Create Account

**Request**

```http
POST /api/v1/accounts
Content-Type: application/json
```

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 1500.00
}
```

**Response**

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

The `Location` header identifies the newly created resource.

## Find All Accounts

The collection endpoint uses Spring Data pagination and Spring HATEOAS to return account representations together with navigation links and page metadata.

**Request**

```http
GET /api/v1/accounts?page=0&size=5&sort=balance,desc
```

### Pagination Parameters

| Parameter | Description | Default |
|---|---|---|
| `page` | Zero-based page number | `0` |
| `size` | Number of items per page | `20` |
| `sort` | Property and sort direction | `id` ascending |

Examples of sorting:

```http
GET /api/v1/accounts?page=0&size=5&sort=name,asc
GET /api/v1/accounts?page=0&size=5&sort=balance,desc
```

The maximum page size is configured as `100`:

```properties
spring.data.web.pageable.max-page-size=100
```

Requests exceeding this limit are capped at the configured maximum.

### Response

```http
200 OK
```

The response includes account representations under `_embedded`, navigation links under `_links`, and pagination metadata under `page`.

```json
{
  "_embedded": {
    "accountResponseList": [
      {
        "id": 7,
        "name": "Poupança para Casa",
        "type": "SAVINGS",
        "balance": 12500.00,
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/v1/accounts/7"
          },
          "all-accounts": {
            "href": "http://localhost:8080/api/v1/accounts"
          }
        }
      }
    ]
  },
  "_links": {
    "first": {
      "href": "http://localhost:8080/api/v1/accounts?page=0&size=5&sort=balance,desc"
    },
    "self": {
      "href": "http://localhost:8080/api/v1/accounts?page=0&size=5&sort=balance,desc"
    },
    "next": {
      "href": "http://localhost:8080/api/v1/accounts?page=1&size=5&sort=balance,desc"
    },
    "last": {
      "href": "http://localhost:8080/api/v1/accounts?page=2&size=5&sort=balance,desc"
    }
  },
  "page": {
    "number": 0,
    "size": 5,
    "totalElements": 11,
    "totalPages": 3
  }
}
```

The example shows one account item to keep the README readable. The actual response includes all accounts belonging to the requested page.

The available navigation links depend on the current page. For example, `next` is available when another page exists.

The implementation uses:

- `Page<Account>` and `Pageable` in the service layer.
- `PagedResourcesAssembler` to generate the paginated HATEOAS representation.
- `@PageableDefault` to configure the default page size and sorting.
- `spring.data.web.pageable.max-page-size` to limit the maximum page size.

Using pagination avoids returning the entire collection in a single response. The `Page` abstraction also provides total-element and total-page metadata, which may require an additional count query.

When no accounts exist, the endpoint still returns `200 OK` with an empty collection.

## Find Account by ID

**Request**

```http
GET /api/v1/accounts/1
```

If the account exists and no matching ETag is provided, the API returns `200 OK`, the resource representation, and an `ETag` header.

**Response**

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

If the account does not exist, the API returns `404 Not Found` with a structured error response (see [Error Handling](#error-handling)).

## HTTP Caching with ETag

The API implements HTTP-level caching using `ETag`, `If-None-Match`, and `304 Not Modified`.

The goal is to avoid sending the same resource representation to the client when the resource has not changed.

### First Request

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

### Subsequent Request

The client can send the ETag it previously received:

```http
GET /api/v1/accounts/1
If-None-Match: "a1b2c3..."
```

If the representation has not changed, the API returns:

```http
304 Not Modified
ETag: "a1b2c3..."
```

No response body is returned because the client can reuse the representation it already has.

### When the Resource Changes

If the account is modified, its representation changes and a new ETag is generated.

For example, after updating the balance:

```http
PUT /api/v1/accounts/1
```

A subsequent request using the old ETag receives the updated representation and a new ETag:

```http
200 OK
ETag: "d4e5f6..."
```

### ETag Generation

The project uses a dedicated `ETagGenerator` component to generate a deterministic hash based on the current `AccountResponse`.

The generated hash is used as the ETag value.

This project explores HTTP-level caching rather than application-level caching mechanisms such as `@Cacheable`, Redis, or Caffeine.

## Replace Account

The `PUT` endpoint replaces the account's editable representation.

**Request**

```http
PUT /api/v1/accounts/1
Content-Type: application/json
```

```json
{
  "name": "Conta Principal",
  "type": "CHECKING",
  "balance": 2000.00
}
```

**Response**

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

**Request**

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

**Response**

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

If the account does not exist, the API returns `404 Not Found`.

## Delete Account

**Request**

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

## Error Handling

The API uses Spring's `ProblemDetail` to return structured HTTP error responses following **RFC 9457**. The response uses the `application/problem+json` media type.

For account lookups, an `AccountNotFoundException` is handled by a centralized `@RestControllerAdvice`, which maps the exception to HTTP `404 Not Found`.

**Example request**

```http
GET /api/v1/accounts/999
```

**Example response**

```http
HTTP/1.1 404 Not Found
Content-Type: application/problem+json
```

```json
{
  "type": "https://api.example.com/problems/account-not-found",
  "title": "Account not found",
  "status": 404,
  "detail": "Account with id 999 was not found",
  "instance": "/api/v1/accounts/999"
}
```

The `type` URI above is currently an illustrative identifier and should be replaced with a stable application-owned URI before the API is published for external consumers.

- `type`: identifies the category of problem.
- `title`: provides a short, human-readable summary.
- `status`: represents the HTTP status code.
- `detail`: describes this specific occurrence.
- `instance`: identifies the request path associated with the error.

Consumers should rely on stable identifiers such as `type` rather than parsing the human-readable `detail` text. Other endpoints may still use their existing error responses until they are migrated to this convention.

## Account Types

The following account types are currently supported:

- `CHECKING`
- `SAVINGS`
- `CREDIT_CARD`

## REST and HTTP Semantics

The API is being developed with a focus on understanding and applying HTTP semantics in practice.

Concepts explored in the current implementation include:

- Resource-oriented endpoints.
- HTTP methods: `POST`, `GET`, `PUT`, `PATCH`, and `DELETE`.
- HTTP status codes such as `200`, `201`, `204`, `304`, and `404`.
- `Location` header after resource creation.
- `Optional` for resource lookup.
- Idempotent update operations.
- Partial resource updates with `PATCH`.
- DTOs using Java records.
- `JsonNullable` for PATCH request semantics.
- Package-by-feature organization.
- Pagination and sorting with Spring Data.
- REST Level 3 and HATEOAS.
- Hypermedia links and representation models.
- `RepresentationModelAssembler` and `PagedResourcesAssembler`.
- Collection representations and pagination metadata.
- HTTP caching with ETag.
- Conditional requests with `If-None-Match`.
- `304 Not Modified`.
- Structured HTTP error responses with `ProblemDetail` (RFC 9457).

## REST Level 3 / HATEOAS

The API includes a REST Level 3 implementation using Spring HATEOAS.

Resource representations expose hypermedia links that allow clients to navigate between related resources.

For example, an account representation exposes:

- `self` — link to the current account.
- `all-accounts` — link to the account collection.

The implementation uses:

- `RepresentationModel`
- `CollectionModel`
- `RepresentationModelAssembler`
- `PagedResourcesAssembler`
- `linkTo()`
- `methodOn()`

The goal is to explore how hypermedia can be used as part of a REST API rather than simply returning resource data.

## Development

This project is continuously evolving as new REST concepts and backend practices are explored.

The implementation and design decisions made during development are documented through a series of technical articles.

## Roadmap

### REST and API Design

- [x] Create the `Account` resource.
- [x] `POST /api/v1/accounts`
- [x] `GET /api/v1/accounts`
- [x] `GET /api/v1/accounts/{id}`
- [x] `PUT /api/v1/accounts/{id}`
- [x] `PATCH /api/v1/accounts/{id}`
- [x] `DELETE /api/v1/accounts/{id}`
- [x] REST Level 3 / HATEOAS
- [x] HTTP Caching / ETag
- [x] Pagination and sorting

### API Improvements

- [ ] Bean Validation
- [x] Global exception handling for account-not-found errors
- [x] Structured error responses with `ProblemDetail`
- [ ] Additional exception handling scenarios
- [ ] DTO improvements
- [ ] OpenAPI / Swagger
- [ ] Automated tests
- [ ] Database migrations

### Domain

- [ ] Transaction resource
- [ ] User resource
- [ ] Authentication and authorization

## Author

**Tiago Vale**

This project is part of a hands-on learning journey focused on backend development with Java and Spring Boot.
