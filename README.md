# Bookstore API

Bookstore API for managing a bookstore catalog

---

## Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 26 LTS                       |
| Framework   | Spring Boot 4.0.6                 |
| ORM         | Spring Data JPA / Hibernate 6     |
| Database    | PostgreSQL (Docker)               |
| Security    | Spring Security 6 (HTTP Basic)    |
| Docs        | SpringDoc OpenAPI / Swagger UI    |
| Build       | Maven (latest)                    |
| Extras      | Lombok, Bean Validation           |

---

## Roles & Credentials

| Username | Password  | Role  | Allowed Operations           |
|----------|-----------|-------|------------------------------|
| user     | password  | USER  | GET, POST, PUT               |
| admin    | admin123  | ADMIN | GET, POST, PUT, **DELETE**   |

> In production, replace `InMemoryUserDetailsManager` in `SecurityConfig.java` with a database-backed `UserDetailsService`.

---

## Quick Start (Docker)

```bash
# 1. Build and start everything (PostgreSQL + API)
docker compose up --build

# 2. API is available at
http://localhost:8080

# 3. Swagger UI
http://localhost:8080/swagger-ui.html
```

---

## Quick Start (Local — PostgreSQL running separately)

```bash
# Start just PostgreSQL
docker compose up postgres -d

# Run the app in 'dev' profile (seeds 2 sample books on startup)
./mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## API Endpoints

### Base URL: `http://localhost:8080/api/v1`

| Method | Path              | Role Required | Description                              |
|--------|-------------------|---------------|------------------------------------------|
| POST   | /books            | USER, ADMIN   | Add a new book                           |
| PUT    | /books/{isbn}     | USER, ADMIN   | Update an existing book                  |
| GET    | /books            | USER, ADMIN   | Find books (by title, author, or both)   |
| DELETE | /books/{isbn}     | **ADMIN only**| Delete a book                            |

---

## Sample curl Commands

### Add a book (USER)
```bash
curl -u user:password -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "978-0-13-468599-1",
    "title": "Effective Java",
    "year": 2018,
    "price": 45.99,
    "genre": "Programming",
    "authors": [
      { "name": "Joshua Bloch", "birthday": "1961-08-28" }
    ]
  }'
```

### Update a book (USER)
```bash
curl -u user:password -X PUT http://localhost:8080/api/v1/books/978-0-13-468599-1 \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "978-0-13-468599-1",
    "title": "Effective Java (3rd Edition)",
    "year": 2018,
    "price": 49.99,
    "genre": "Programming",
    "authors": [
      { "name": "Joshua Bloch", "birthday": "1961-08-28" }
    ]
  }'
```

### Find all books (USER)
```bash
curl -u user:password http://localhost:8080/api/v1/books
```

### Find by title (exact match)
```bash
curl -u user:password "http://localhost:8080/api/v1/books?title=Effective+Java"
```

### Find by author name (exact match)
```bash
curl -u user:password "http://localhost:8080/api/v1/books?authorName=Joshua+Bloch"
```

### Find by both title AND author
```bash
curl -u user:password "http://localhost:8080/api/v1/books?title=Effective+Java&authorName=Joshua+Bloch"
```

### Delete a book (ADMIN only)
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/books/978-0-13-468599-1
```

### Delete attempt as USER → 403 Forbidden
```bash
curl -u user:password -X DELETE http://localhost:8080/api/v1/books/978-0-13-468599-1
```

### Unauthenticated request → 401 Unauthorized
```bash
curl http://localhost:8080/api/v1/books
```

---

## Expected Outputs

### POST /books — 201 Created
```json
{
  "isbn": "978-0-13-468599-1",
  "title": "Effective Java",
  "year": 2018,
  "price": 45.99,
  "genre": "Programming",
  "authors": [
    { "name": "Joshua Bloch", "birthday": "1961-08-28" }
  ]
}
```

### GET /books — 200 OK
```json
[
  {
    "isbn": "978-0-13-468599-1",
    "title": "Effective Java",
    "year": 2018,
    "price": 45.99,
    "genre": "Programming",
    "authors": [
      { "name": "Joshua Bloch", "birthday": "1961-08-28" }
    ]
  }
]
```

### DELETE /books/{isbn} as USER — 403 Forbidden
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You do not have permission to perform this action",
  "timestamp": "2026-06-07T10:30:00"
}
```

### POST /books with missing field — 400 Bad Request
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields are invalid",
  "timestamp": "2026-06-07T10:30:00",
  "fieldErrors": {
    "title": "Title is required",
    "price": "Price must be greater than 0"
  }
}
```

### DELETE /books/{isbn} with unknown ISBN — 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Book not found with ISBN: 999-FAKE",
  "timestamp": "2026-06-07T10:30:00"
}
```

---

## Running Tests

```bash
./mvnw test
```

Tests cover: add book, update, find all, delete as ADMIN (204), delete as USER (403), unauthenticated access (401).

---
