# 📚 BookStore REST API

> A production-ready online bookstore backend built with Spring Boot — featuring JWT authentication, role-based access control, shopping cart management, order processing, and full Swagger documentation.

---

## 🌟 Inspiration

Managing books shouldn't be complicated. This project was born out of the desire to build a clean, well-structured backend that any bookstore could plug into — handling everything from browsing and searching books by category, to adding them to a cart and placing orders. It also served as a deep-dive into building secure, scalable REST APIs with Spring Boot best practices.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Client / Postman                     │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP Requests
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Spring Security Layer                    │
│          JWT Filter → Authentication → Authorization        │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      REST Controllers                       │
│  Auth │ Books │ Categories │ Cart │ Orders                  │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│   Business Logic │ Validation │ Transaction Management      │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│               Repository Layer (Spring Data JPA)            │
│        Specifications │ Custom Queries │ EntityGraphs       │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     MySQL Database                          │
│              Liquibase migrations │ Soft Deletes            │
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
│   └── db/changelog/    # Liquibase changesets
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

**2. Create your `.env` file** (copy from the sample)
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

**2. Set environment variables** (or add to `application.properties`):
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

## 📋 How to Use the API

### Step 1 — Authenticate
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
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Step 2 — Use the token in subsequent requests
```http
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

### Step 5 — Add to cart & place order (User)
```http
POST /api/cart
Authorization: Bearer <user_token>
Content-Type: application/json

{ "bookId": 1, "quantity": 2 }

POST /api/orders
Authorization: Bearer <user_token>
Content-Type: application/json

{ "shippingAddress": "123 Main St, Kyiv, Ukraine" }
```

---

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run with coverage report
mvn verify
```

Tests use an **H2 in-memory database** — no external services required.

The test suite includes:
- **Unit tests** for all service implementations (Mockito)
- **Repository tests** with `@DataJpaTest`
- **Integration/controller tests** with `@SpringBootTest` + `MockMvc`

---

## 🔒 Security Design

- Passwords are hashed with **BCrypt**
- JWT tokens are signed with **HMAC-SHA** and validated on every request
- Session is **stateless** — no server-side session storage
- Access control uses **Spring Security method-level annotations** (`@PreAuthorize`)
- CSRF is disabled (stateless JWT API)
- Custom `@FieldMatch` annotation validates password confirmation at registration

---

## 🗄️ Database Schema

The schema is managed entirely through **Liquibase** changesets:

```
users ──────────────────────── users_roles ── roles
  │                                │
  └─── shopping_carts ──── cart_items ──── books
  │                                          │
  └─── orders ──────── order_items ──── ─────┘
                                             │
                                         books_categories ── categories
```

All primary entities support **soft delete** via an `is_deleted` flag, enforced transparently through Hibernate `@SQLRestriction`.

---

## ⚠️ Challenges & Solutions

**Challenge 1: Keeping shopping cart in sync during order placement**  
When an order is placed, the cart must be atomically cleared. This was handled using `@Transactional` and cascading operations — the cart items are cleared through the JPA relationship rather than separate delete queries, ensuring consistency.

**Challenge 2: N+1 queries with lazy-loaded associations**  
Fetching orders with their items naively triggers many queries. The solution was using `@EntityGraph` annotations on repository methods to eagerly fetch only what's needed, per query.

**Challenge 3: Specification-based search**  
Building a flexible search without hard-coding all combinations required the Strategy pattern. Each filter criterion (`TitleSpecificationProvider`, `PriceSpecificationProvider`, etc.) is a separate component, assembled dynamically by `BookSpecificationBuilder`.

**Challenge 4: Test isolation with shared database state**  
Integration tests that modify data can interfere. The solution was explicit `@BeforeEach`/`@AfterEach` cleanup using `JdbcTemplate` for hard deletes, bypassing soft-delete filters.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

<div align="center">
  Built with ❤️ using Spring Boot
</div>
