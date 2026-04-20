# Dealer & Vehicle Inventory Module

A **multi-tenant Inventory module** built as a **Modular Monolith** using **Spring Boot 3.2**, **Java 21**, and **PostgreSQL**.  
The design follows **Clean Architecture**, **SOLID principles**, and multi-tenancy via an `X-Tenant-Id` HTTP header.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Data Model](#data-model)
4. [API Reference](#api-reference)
5. [Security & Multi-Tenancy](#security--multi-tenancy)
6. [SOLID Principles Applied](#solid-principles-applied)
7. [Getting Started](#getting-started)
8. [Running Tests](#running-tests)
9. [Admin Endpoint — Subscription Count](#admin-endpoint--subscription-count)
10. [Acceptance Check Matrix](#acceptance-check-matrix)
11. [Tech Stack](#tech-stack)

---

## Architecture Overview

```
┌───────────────────────────────────────────────────────────┐
│                     HTTP Layer                            │
│   DealerController   VehicleController   AdminController  │
├───────────────────────────────────────────────────────────┤
│                    Service Layer (Port)                    │
│        DealerService (interface + impl)                   │
│        VehicleService (interface + impl)                  │
├───────────────────────────────────────────────────────────┤
│                  Repository / Persistence                  │
│    DealerRepository   VehicleRepository (JPA + Spec)      │
├───────────────────────────────────────────────────────────┤
│                     Domain / Entities                      │
│            Dealer   Vehicle   Enums                       │
├───────────────────────────────────────────────────────────┤
│                   Shared Infrastructure                    │
│    TenantFilter  TenantContext  SecurityConfig  ExHandler  │
└───────────────────────────────────────────────────────────┘
```

The application is a **Modular Monolith**: the `dealer`, `vehicle`, and `admin` packages are logically isolated modules sharing no internal state, communicating only through well-defined interfaces. They can be extracted into independent microservices without touching business logic.

---

## Project Structure

```
src/
└── main/java/com/dealersautocenter/inventory/
    ├── InventoryApplication.java
    │
    ├── dealer/                         # Dealer module
    │   ├── controller/DealerController.java
    │   ├── domain/Dealer.java
    │   ├── dto/                        # Request & response records
    │   ├── mapper/DealerMapper.java    # MapStruct
    │   ├── repository/DealerRepository.java
    │   └── service/
    │       ├── DealerService.java      # Port (interface)
    │       └── DealerServiceImpl.java  # Adapter (implementation)
    │
    ├── vehicle/                        # Vehicle module
    │   ├── controller/VehicleController.java
    │   ├── domain/Vehicle.java
    │   ├── dto/
    │   ├── mapper/VehicleMapper.java
    │   ├── repository/
    │   │   ├── VehicleRepository.java
    │   │   └── VehicleSpecification.java  # Dynamic JPA queries
    │   └── service/
    │       ├── VehicleService.java
    │       └── VehicleServiceImpl.java
    │
    ├── admin/
    │   └── controller/AdminController.java
    │
    └── shared/                         # Cross-cutting concerns
        ├── domain/                     # Enums: SubscriptionType, VehicleStatus
        ├── exception/                  # Custom exceptions + GlobalExceptionHandler
        ├── security/                   # SecurityConfig, Roles constants
        └── tenant/                     # TenantContext (ThreadLocal) + TenantFilter

src/main/resources/
    ├── application.yml
    └── db/migration/
        ├── V1__create_dealers_table.sql
        └── V2__create_vehicles_table.sql
```

---

## Data Model

### Dealer

| Column            | Type         | Constraints                        |
|-------------------|--------------|------------------------------------|
| id                | UUID (PK)    | Auto-generated                     |
| tenant_id         | VARCHAR(100) | NOT NULL, immutable                |
| name              | VARCHAR(255) | NOT NULL                           |
| email             | VARCHAR(255) | NOT NULL, UNIQUE                   |
| subscription_type | ENUM         | BASIC \| PREMIUM, NOT NULL         |

### Vehicle

| Column    | Type           | Constraints                              |
|-----------|----------------|------------------------------------------|
| id        | UUID (PK)      | Auto-generated                           |
| tenant_id | VARCHAR(100)   | NOT NULL, immutable                      |
| dealer_id | UUID (FK)      | References dealers(id) ON DELETE CASCADE |
| model     | VARCHAR(255)   | NOT NULL                                 |
| price     | NUMERIC(12,2)  | NOT NULL, must be > 0                    |
| status    | ENUM           | AVAILABLE \| SOLD, NOT NULL              |

---

## API Reference

> All endpoints (except Swagger/health) require:
> - HTTP Basic authentication
> - `X-Tenant-Id: <tenant>` header

### Dealers

| Method | Path              | Role Required          | Description                          |
|--------|-------------------|------------------------|--------------------------------------|
| POST   | `/dealers`        | DEALER_ADMIN / GLOBAL_ADMIN | Create a dealer                 |
| GET    | `/dealers/{id}`   | Any authenticated      | Get dealer by ID                     |
| GET    | `/dealers`        | Any authenticated      | List dealers (paginated/sorted)      |
| PATCH  | `/dealers/{id}`   | DEALER_ADMIN / GLOBAL_ADMIN | Partial update                  |
| DELETE | `/dealers/{id}`   | DEALER_ADMIN / GLOBAL_ADMIN | Delete                          |

**Pagination params:** `?page=0&size=20&sort=name,asc`

---

### Vehicles

| Method | Path              | Role Required          | Description                          |
|--------|-------------------|------------------------|--------------------------------------|
| POST   | `/vehicles`       | DEALER_ADMIN / GLOBAL_ADMIN | Add a vehicle                   |
| GET    | `/vehicles/{id}`  | Any authenticated      | Get vehicle by ID                    |
| GET    | `/vehicles`       | Any authenticated      | List vehicles (filtered/paginated)   |
| PATCH  | `/vehicles/{id}`  | DEALER_ADMIN / GLOBAL_ADMIN | Partial update                  |
| DELETE | `/vehicles/{id}`  | DEALER_ADMIN / GLOBAL_ADMIN | Delete                          |

**Filter params for `GET /vehicles`:**

| Param        | Type           | Description                                           |
|--------------|----------------|-------------------------------------------------------|
| `model`      | string         | Partial case-insensitive match                        |
| `status`     | AVAILABLE/SOLD | Exact match                                           |
| `priceMin`   | decimal        | Price ≥ value                                         |
| `priceMax`   | decimal        | Price ≤ value                                         |
| `subscription` | BASIC/PREMIUM | Only vehicles whose dealer has this subscription type |

**Example — PREMIUM subscription filter:**
```
GET /vehicles?subscription=PREMIUM&page=0&size=10
X-Tenant-Id: tenant-a
Authorization: Basic dmlld2VyOnZpZXdlcjEyMw==
```
Returns vehicles that belong to PREMIUM dealers **within tenant-a only**.

---

### Admin

| Method | Path                                | Role Required | Description                            |
|--------|-------------------------------------|---------------|----------------------------------------|
| GET    | `/admin/dealers/countBySubscription` | GLOBAL_ADMIN | Count dealers by subscription type    |

**Response:**
```json
{
  "BASIC": 12,
  "PREMIUM": 5
}
```

> ⚠️ **This count is GLOBAL (cross-tenant).** A GLOBAL_ADMIN sees aggregate numbers across the entire system. If per-tenant counts are needed, add an optional `?tenantId=` query parameter with an additional `GLOBAL_ADMIN` ownership check.

---

## Security & Multi-Tenancy

### How Multi-Tenancy Works

1. **`TenantFilter`** (runs before Spring Security) reads the `X-Tenant-Id` header from every incoming request.
2. The tenant ID is stored in **`TenantContext`** (a `ThreadLocal` wrapper) for the duration of the request.
3. After the request completes, `TenantContext.clear()` is called in a `finally` block to prevent thread pool leaks.
4. Every service method explicitly reads from `TenantContext` and **appends the tenant ID to all queries** — no cross-tenant data is ever accessible.
5. When a resource is found but belongs to a different tenant, a **403 Forbidden** (`TenantAccessDeniedException`) is thrown.

### Roles

| Role          | Capabilities                                                  |
|---------------|---------------------------------------------------------------|
| `GLOBAL_ADMIN`| Full access to all endpoints, including `/admin/**`           |
| `DEALER_ADMIN`| Create, update, delete dealers and vehicles within own tenant |
| `VIEWER`      | Read-only access to dealers and vehicles within own tenant    |

### Demo Credentials (in-memory, for local testing only)

| Username      | Password    | Role          |
|---------------|-------------|---------------|
| `admin`       | `admin123`  | GLOBAL_ADMIN  |
| `dealer_admin`| `dealer123` | DEALER_ADMIN  |
| `viewer`      | `viewer123` | VIEWER        |

> In production, replace `InMemoryUserDetailsManager` with a proper `UserDetailsService` backed by a database, LDAP, or OAuth2/JWT provider.

---

## SOLID Principles Applied

| Principle | Where Applied |
|-----------|---------------|
| **S** — Single Responsibility | Each class has one job: Controller routes, Service orchestrates business logic, Repository handles persistence, Mapper handles DTO conversion |
| **O** — Open/Closed | `VehicleSpecification` can be extended with new filter methods without modifying existing ones. New modules can be added without touching existing ones |
| **L** — Liskov Substitution | `DealerServiceImpl` and `VehicleServiceImpl` are fully substitutable for their interfaces `DealerService` and `VehicleService` |
| **I** — Interface Segregation | `DealerService` and `VehicleService` define only the use cases their callers need; they don't force unrelated methods onto implementors |
| **D** — Dependency Inversion | Controllers depend on service interfaces, not implementations. Services depend on repository interfaces, not JPA implementations. `@Mock` in tests proves this |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### 1. Start the database

```bash
docker-compose up -d
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Explore with Swagger UI

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

### Example Requests

**Create a dealer:**
```bash
curl -X POST http://localhost:8080/dealers \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: tenant-a" \
  -u dealer_admin:dealer123 \
  -d '{"name":"AutoMax","email":"info@automax.com","subscriptionType":"PREMIUM"}'
```

**List PREMIUM vehicles (tenant-scoped):**
```bash
curl "http://localhost:8080/vehicles?subscription=PREMIUM" \
  -H "X-Tenant-Id: tenant-a" \
  -u viewer:viewer123
```

**Admin count by subscription:**
```bash
curl http://localhost:8080/admin/dealers/countBySubscription \
  -H "X-Tenant-Id: any-tenant" \
  -u admin:admin123
```

---

## Running Tests

```bash
./mvnw test
```

Tests use an **in-memory H2 database** (via `application-test.yml`) — no external dependencies required.

---

## Acceptance Check Matrix

| Requirement | Implementation |
|---|---|
| Missing `X-Tenant-Id` → 400 | `TenantFilter` returns `400` with JSON error before request reaches any controller |
| Cross-tenant access blocked → 403 | `resolveDealer()` / `resolveVehicle()` in service layer compares resource's `tenant_id` with `TenantContext`, throws `TenantAccessDeniedException` (403) |
| `subscription=PREMIUM` is tenant-scoped | `VehicleSpecification` always applies `tenantId` predicate first; `subscription` filter joins dealer and checks `subscriptionType` within the same scoped query |
| Admin count requires `GLOBAL_ADMIN` | `@PreAuthorize("hasRole('GLOBAL_ADMIN')")` on `AdminController.countBySubscription()` |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | ORM + repository abstraction |
| PostgreSQL | 16 | Primary database |
| Flyway | 9.x | Database migrations |
| MapStruct | 1.5.5 | DTO ↔ entity mapping |
| Lombok | latest | Boilerplate reduction |
| SpringDoc OpenAPI | 2.5.0 | Swagger UI / API docs |
| JUnit 5 + Mockito | latest | Unit & slice testing |
| H2 | latest | In-memory DB for tests |
| Docker Compose | — | Local PostgreSQL setup |
