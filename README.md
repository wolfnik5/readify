# 📚 BookStore REST API

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)
![Build](https://github.com/WOLFnik5/book-store/actions/workflows/ci.yml/badge.svg)
![License](https://img.shields.io/badge/license-MIT-green)

> A production-ready online bookstore backend built with Spring Boot — featuring JWT authentication, role-based access control, shopping cart management, order processing, and full Swagger documentation.

---

## 🌟 Inspiration

Managing books shouldn't be complicated. This project was born out of the desire to build a clean, well-structured backend that any bookstore could plug into — handling everything from browsing and searching books by category, to adding them to a cart and placing orders. It also served as a deep-dive into building secure, scalable REST APIs with Spring Boot best practices.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Client / Postman                      │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP Requests
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Spring Security Layer                     │
│          JWT Filter → Authentication → Authorization         │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      REST Controllers                        │
│  Auth │ Books │ Categories │ Cart │ Orders                   │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                           │
│   Business Logic │ Validation │ Transaction Management       │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│               Repository Layer (Spring Data JPA)            │
│        Specifications │ Custom Queries │ EntityGraphs        │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     MySQL Database                           │
│              Liquibase migrations │ Soft Deletes             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technologies & Tools

| Category | Technology |
|---|---|
| **Framework** | Spring Boot 3.5.x |
| **Security** | Spring Security + JWT (jjwt 0.12.3) |
| **Persistence** | Spring Data JPA + Hibernate |
| **Database** | MySQL 8.0 (production), H2 (tests) |
| **Migrations** | Liquibase |
| **Mapping** | MapStruct 1.5.5 |
| **Validation** | Jakarta Bean Validation |
| **Documentation** | SpringDoc OpenAPI (Swagger UI) |
| **Build Tool** | Maven |
| **Containerization** | Docker + Docker Compose |
| **Code Quality** | Checkstyle (Google Java Style) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Java Version** | Java 21 |
| **CI** | GitHub Actions |

---

## 🔑 Features

- **JWT Authentication** — stateless login and registration with BCrypt password encoding
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` with method-level security
- **Book Management** — full CRUD with soft delete, category associations, and rich search
- **Dynamic Book Search** — filter by title, author, ISBN, min/max price using JPA Specifications
- **Category Management** — organize books into categories, browse books by category
- **Shopping Cart** — persistent cart per user, add/update/remove items
- **Order Processing** — place orders from cart, track order history, update order status
- **Soft Deletes** — data is never physically removed; Hibernate filters handle visibility
- **Pagination & Sorting** — all list endpoints support pageable results
- **Swagger UI** — interactive API documentation at `/api/swagger-ui/index.html`
- **Liquibase Migrations** — reproducible, versioned database schema

---

## 📂 Project Structure

```
src/
├── main/java/store/book/bookstore/
│   ├── config/          # Security configuration
│   ├── controller/      # REST endpoints
│   ├── dto/             # Request/Response data transfer objects
│   ├── exception/       # Custom exceptions & global handler
│   ├── mapper/          # MapStruct mappers
│   ├── model/           # JPA entities
│   ├── repository/      # Spring Data repositories & Specifications
│   ├── security/        # JWT util, filter, authentication service
│   ├── service/         # Business logic interfaces & implementations
│   └── validation/      # Custom validators (e.g. FieldMatch)
├── main/resources/
│   ├── application.properties
│   └── db/changelog/    # Liquibase changesets (14 migrations)
└── test/                # Unit & integration tests
```

---

## 🎯 API Endpoints

### 🔐 Authentication — `/api/auth`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/auth/registration` | Public | Register a new user account |
| `POST` | `/auth/login` | Public | Authenticate and receive a JWT token |

### 📖 Books — `/api/books`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/books/{id}` | USER | Get a book by ID |
| `GET` | `/books/search` | USER | Search books by title, author, ISBN, price range |
| `POST` | `/books` | ADMIN | Create a new book |
| `PUT` | `/books/{id}` | ADMIN | Update an existing book |
| `DELETE` | `/books/{id}` | ADMIN | Soft-delete a book |

### 🏷️ Categories — `/api/categories`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/categories` | USER | Get all categories (paginated) |
| `GET` | `/categories/{id}` | USER | Get a category by ID |
| `GET` | `/categories/{id}/books` | USER | Get all books in a category |
| `POST` | `/categories` | ADMIN | Create a new category |
| `PUT` | `/categories/{id}` | ADMIN | Update a category |
| `DELETE` | `/categories/{id}` | ADMIN | Soft-delete a category |

### 🛒 Shopping Cart — `/api/cart`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/cart` | USER | View current user's shopping cart |
| `POST` | `/cart` | USER | Add a book to the cart |
| `PUT` | `/cart/items/{cartItemId}` | USER | Update item quantity |
| `DELETE` | `/cart/items/{cartItemId}` | USER | Remove item from cart |

### 📦 Orders — `/api/orders`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/orders` | USER | Place an order from cart |
| `GET` | `/orders` | USER | View order history |
| `GET` | `/orders/{orderId}/items` | USER | Get all items in an order |
| `GET` | `/orders/{orderId}/items/{itemId}` | USER | Get a specific order item |
| `PATCH` | `/orders/{id}` | ADMIN | Update order status |

---

## 🗄️ Database Schema

![Database Schema](docs/db-schema.png)

> Schema generated with [dbdiagram.io](https://dbdiagram.io).

---

## 📦 Order Status Flow

```
                    ┌─────────┐
                    │ PENDING │
                    └────┬────┘
                         │
                         ▼
                  ┌─────────────┐
                  │ PROCESSING  │
                  └──────┬──────┘
                         │
                         ▼
                    ┌────────┐
                    │ SHIPPED │
                    └────┬────┘
                         │
                         ▼
                  ┌───────────┐
                  │ DELIVERED │
                  └─────┬─────┘
                        │
              ┌─────────┴──────────┐
              ▼                    ▼
        ┌──────────┐        ┌───────────┐
        │COMPLETED │        │ CANCELLED │
        └──────────┘        └───────────┘
```

Status updates are performed by **Admin only** via `PATCH /api/orders/{id}`.

---

## 🚀 Getting Started

### Prerequisites

- [Java 21](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker & Docker Compose](https://www.docker.com/)

### Option 1: Run with Docker Compose (Recommended)

**1. Clone the repository**
```bash
git clone https://github.com/WOLFnik5/book-store.git
cd book-store
```

**2. Create your `.env` file**
```bash
cp .env.sample .env
```

**3. Fill in the `.env` file**
```env
MYSQLDB_ROOT_PASSWORD=rootpassword
MYSQLDB_DATABASE=bookstore
MYSQLDB_USER=bookstore_user
MYSQLDB_PASSWORD=bookstore_pass

MYSQLDB_LOCAL_PORT=3307
MYSQLDB_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080

JWT_SECRET=your-super-secret-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
```

**4. Build the application JAR**
```bash
mvn clean package -DskipTests
```

**5. Start everything**
```bash
docker-compose up --build
```

The API will be available at: **`http://localhost:8080/api`**  
Swagger UI: **`http://localhost:8080/api/swagger-ui/index.html`**

---

### Option 2: Run Locally (with external MySQL)

**1. Start a MySQL 8 instance** and create a database

**2. Set environment variables:**
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bookstore?createDatabaseIfNotExist=true
export SPRING_DATASOURCE_USERNAME=your_user
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key-at-least-32-characters-long
export JWT_EXPIRATION=86400000
```

**3. Run the application**
```bash
mvn spring-boot:run
```

---

## 🔑 Default Users (seeded by Liquibase)

| Role | Email | Password |
|---|---|---|
| Admin | `admin@bookstore.com` | `admin1234` |
| User | `user@bookstore.com` | `user1234` |

---

## 📋 Quick Usage Guide

### Step 1 — Login and get your token
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@bookstore.com",
  "password": "admin1234"
}
```
Response:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Step 2 — Add the token to all requests
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Step 3 — Create a category (Admin)
```http
POST /api/categories
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Science Fiction",
  "description": "Books about future and space"
}
```

### Step 4 — Add a book (Admin)
```http
POST /api/books
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "title": "Dune",
  "author": "Frank Herbert",
  "isbn": "9780441013593",
  "price": 12.99,
  "description": "A science fiction masterpiece",
  "categoryIds": [1]
}
```

### Step 5 — Add to cart (User)
```http
POST /api/cart
Authorization: Bearer <user_token>
Content-Type: application/json

{ "bookId": 1, "quantity": 2 }
```

### Step 6 — Place an order (User)
```http
POST /api/orders
Authorization: Bearer <user_token>
Content-Type: application/json

{ "shippingAddress": "123 Main St, Kyiv, Ukraine" }
```

---

## 🧪 Postman Collection

Import `docs/BookStore.postman_collection.json` into Postman to get all endpoints pre-configured with example request bodies.

**Setup:**
1. Import the collection file into Postman
2. Create a Postman environment with variable `baseUrl = http://localhost:8080/api`
3. Run the **Login** request — copy the token from the response
4. Set environment variable `token = <your JWT>`
5. All other requests use `{{token}}` automatically via the `Authorization` header

> The collection covers all 20 endpoints across all 5 controllers, with example payloads for every request.

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run with full build and verification
mvn verify
```

Tests use an **H2 in-memory database** — no external services required. The suite includes:

- **Unit tests** for all service implementations (Mockito)
- **Repository tests** with `@DataJpaTest`
- **Integration/controller tests** with `@SpringBootTest` + `MockMvc`

---

## 🔒 Security Design

- Passwords hashed with **BCrypt**
- JWT tokens signed with **HMAC-SHA**, validated on every request
- **Stateless** sessions — no server-side session storage
- Method-level access control via `@PreAuthorize`
- CSRF disabled (stateless JWT API)
- Custom `@FieldMatch` annotation validates password confirmation at registration

---

## ⚠️ Challenges & Solutions

**Challenge 1: Atomic cart clearing on order placement**  
When an order is placed, the cart must be cleared in the same transaction. Solved using `@Transactional` with cascading JPA operations — cart items are removed through the relationship rather than separate delete queries.

**Challenge 2: N+1 queries with lazy-loaded associations**  
Fetching orders with their items naively triggers many queries. Solved using `@EntityGraph` on repository methods to eagerly fetch only what's needed, per query.

**Challenge 3: Flexible search without combinatorial explosion**  
Building a search with multiple optional filters required the Strategy pattern. Each criterion (`TitleSpecificationProvider`, `PriceSpecificationProvider`, etc.) is a separate component, assembled dynamically by `BookSpecificationBuilder`.

**Challenge 4: Test isolation with shared database state**  
Integration tests that write data can interfere with each other. Solved with explicit `@BeforeEach`/`@AfterEach` cleanup via `JdbcTemplate` hard deletes, bypassing soft-delete filters.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

