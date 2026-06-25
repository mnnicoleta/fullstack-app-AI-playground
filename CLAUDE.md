# Online Shop - Full-Stack E-Commerce Application

## 1. Project Overview

This is a full-stack e-commerce application built as a learning and demonstration project. It showcases modern web development patterns with a clear separation between frontend and backend services.

**Purpose**: Small-scale online shop with product browsing, cart management, and order processing with inventory tracking.

**Key Features**:
- Product catalog with categories
- User authentication and authorization (JWT-based)
- Shopping cart with local storage persistence
- Order management with inventory deduction
- Role-based access control (ADMIN, CUSTOMER)
- Mock mode for frontend development without backend

**Technology Stack Summary**:
- **Frontend**: Angular 21.2 with TypeScript, Tailwind CSS
- **Backend**: Spring Boot 4.0.6 with Java 21
- **Database**: PostgreSQL 18
- **Authentication**: JWT tokens

---

## 1.1 Development Workflow (MANDATORY)

**BLOCKING REQUIREMENTS**: These steps MUST be followed for ALL code changes. No exceptions.

### Workflow Steps (In Order)

1. **Planning Phase** (REQUIRED)
   - For any non-trivial change, you MUST enter plan mode first
   - Use `EnterPlanMode` to create a structured plan
   - Get user approval before proceeding to implementation
   - Document the plan with rationale, affected files, and test strategy

2. **Implementation Phase** (REQUIRED)
   - Make changes according to the approved plan
   - Follow existing code patterns and conventions
   - Write clear, maintainable code
   - Document complex logic

3. **Testing Phase** (REQUIRED)
   - Write tests for ALL new features and bug fixes
   - Frontend: Write Jasmine/Karma tests for new components, services, guards
   - Backend: Write JUnit tests (unit + integration) for new services, controllers
   - Tests MUST pass before proceeding
   - Minimum coverage: 80% for new code

4. **Code Quality Phase** (REQUIRED)
   - Run `/sonarqube-fix` skill to fix all code quality issues
   - Address ALL findings (blocker, critical, major)
   - Run linters: `npm run lint` (frontend), Maven validation (backend)
   - Fix all linting errors

5. **Verification Phase** (REQUIRED)
   - Run all tests: `npm test` (frontend), `mvn test` (backend)
   - Verify tests pass with new changes
   - Use `/verify` skill to validate in running application
   - Check for regressions

6. **Commit Phase** (ENFORCED via hooks)
   - Pre-commit hook automatically runs tests
   - Pre-commit hook blocks commit if tests fail
   - Use conventional commit format: `type(scope): description`
   - Types: feat, fix, docs, style, refactor, test, chore

### Automated Workflow

Use the `/dev-workflow` skill to automate all steps:
```bash
/dev-workflow "Add new feature"
```

This skill will:
- Enter plan mode and create a plan
- Get approval
- Make changes
- Generate tests
- Run SonarQube fixes
- Verify everything
- Prepare for commit

### Why This Workflow Exists

- **Planning prevents rework**: Thinking before coding saves time
- **Tests prevent regressions**: Automated tests catch bugs early
- **Quality prevents technical debt**: Clean code is maintainable code
- **Verification prevents production issues**: Test in dev, not prod

### Enforcement Mechanisms

1. **CLAUDE.md Instructions**: This section (permanent)
2. **Custom Skill**: `/dev-workflow` (automation)
3. **Git Hooks**: Pre-commit validation (blocks bad commits)
4. **Session Settings**: Reinforced in conversation context

### Skip Conditions (RARE)

You may skip workflow steps ONLY for:
- Documentation-only changes (README, comments)
- Configuration file updates with no logic changes
- Emergency hotfixes (with explicit user permission)

For everything else: **Follow the workflow. No shortcuts.**

---

## 2. Quick Start (First Time Setup)

```bash
# 1. Start PostgreSQL database
cd docker/development
docker-compose up -d

# 2. Start backend (new terminal)
cd onlineshopapi
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 3. Start frontend (new terminal)
cd onlineshopui
npm install
npm start

# 4. Access application
# Frontend: http://localhost:4200
# API: http://localhost:3000/api
# Swagger: http://localhost:3000/api/swagger-ui/index.html

# Login credentials:
# Admin: admin@onlineshop.com / password
# Customer: john.doe@email.com / password
```

**Alternative Frontend (No Backend Required)**:
```bash
cd onlineshopui
npm start:mock
# Uses mock API with test users: admin@example.com / admin123
```

---

## 3. Architecture

### 3.1 System Architecture

This project uses a **monorepo structure** with separate frontend and backend applications:

```
fullstack-app-AI-playground/
├── onlineshopui/          # Angular frontend
├── onlineshopapi/         # Spring Boot backend
└── docker/                # Docker Compose setup
```

**Communication Pattern**:
- Client-server architecture with REST API
- Frontend makes HTTP requests to backend API
- JWT tokens for stateless authentication
- CORS configured for local development

### 3.2 Technology Stack

**Frontend (`/onlineshopui/`):**
- **Framework**: Angular 21.2.0
- **Language**: TypeScript 5.9.2
- **Styling**: Tailwind CSS 4.1.12, PostCSS
- **State Management**: Angular signals with RxJS
- **HTTP Client**: Angular HttpClient with interceptors
- **Icons**: lucide-angular
- **Testing**: Karma with Jasmine
- **Linting**: ESLint 10.0.2 with angular-eslint
- **Formatting**: Prettier 3.8.1
- **Package Manager**: npm 11.6.1
- **Node Version**: 24+

**Backend (`/onlineshopapi/`):**
- **Framework**: Spring Boot 4.0.6
- **Language**: Java 21
- **Build Tool**: Maven 3
- **Security**: Spring Security with JWT (jjwt 0.13.0)
- **ORM**: Spring Data JPA with Hibernate
- **Database**: PostgreSQL (driver included)
- **Migrations**: Flyway
- **API Documentation**: Springdoc-OpenAPI 2.9.0 (Swagger)
- **Testing**: JUnit 5, Spring Boot Test, TestContainers
- **Utilities**: Lombok for boilerplate reduction

**Database:**
- **DBMS**: PostgreSQL 18
- **Port**: 5433 (local development)
- **Migration Tool**: Flyway with versioned SQL scripts

### 3.3 Communication Flow

1. **User authenticates**: Frontend sends credentials to `/api/auth/login`
2. **Backend validates**: Returns JWT token if valid
3. **Token storage**: Frontend stores JWT in localStorage
4. **Authenticated requests**: HTTP interceptor adds `Authorization: Bearer <token>` header
5. **Backend validation**: JWT filter validates token and extracts user info
6. **Role-based access**: Spring Security enforces permissions based on user role
7. **Response**: Backend returns JSON data (DTOs)

**API Base URL**: `http://localhost:3000/api` (local development)

---

## 4. Directory Structure

### 4.1 Repository Root

```
/
├── onlineshopui/          # Angular frontend application
├── onlineshopapi/         # Spring Boot backend application
├── docker/                # Docker Compose configuration
│   └── development/       # Local PostgreSQL setup
├── .claude/               # Claude Code configuration and documentation
├── .copilot/              # GitHub Copilot configuration
└── .run/                  # IntelliJ run configurations
```

### 4.2 Frontend Structure (`/onlineshopui/src/app/`)

```
app/
├── features/              # Feature modules (lazy-loaded)
│   ├── auth/             # Authentication (login, register)
│   │   ├── components/   # Auth UI components
│   │   ├── services/     # AuthService, token management
│   │   ├── types/        # Auth DTOs and types
│   │   ├── guards/       # Auth guard for route protection
│   │   ├── interceptors/ # Auth token interceptor
│   │   └── auth.routes.ts
│   ├── products/         # Product browsing and management
│   │   ├── pages/        # Product list, detail, admin pages
│   │   ├── components/   # Product card, form components
│   │   ├── services/     # ProductService
│   │   └── types/        # Product DTOs
│   ├── cart/             # Shopping cart
│   │   ├── pages/        # Cart page
│   │   ├── components/   # Cart item, summary components
│   │   └── services/     # CartService (with localStorage)
│   └── orders/           # Order management
│       ├── pages/        # Order list, detail pages
│       ├── components/   # Order components
│       ├── services/     # OrderService
│       └── types/        # Order DTOs
│
├── core/                 # Singleton services and app-wide utilities
│   ├── config/           # Constants (navigation, icons, validation rules)
│   ├── services/         # NotificationsService
│   ├── providers/        # DI providers (environment, validation, mocks)
│   ├── types/            # Shared TypeScript types and DTOs
│   └── mocks/            # Mock data and interceptors
│       ├── data/         # Mock JSON data
│       └── interceptors/ # Mock API interceptor
│
├── clib/                 # Component library (reusable UI)
│   ├── components/       # Shared components
│   ├── layouts/          # RootLayout component
│   └── services/         # ThemeService
│
├── app.component.ts      # Root component
├── app.config.ts         # App configuration (providers, DI)
└── app.routes.ts         # Root routing configuration
```

**Key Frontend Directories**:
- `features/`: Business logic organized by feature domain
- `core/`: App-wide singletons (services, configs, types)
- `clib/`: Reusable UI components and layouts

### 4.3 Backend Structure (`/onlineshopapi/src/main/java/msg/onlineshopapi/`)

```
onlineshopapi/
├── controller/           # REST endpoints (Spring @RestController)
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── ProductCategoryController.java
│   └── OrderController.java
│
├── service/              # Business logic (@Service)
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── ProductCategoryService.java
│   ├── OrderService.java
│   └── strategy/         # Strategy pattern for order processing
│       ├── OrderStrategy.java (interface)
│       ├── SingleLocationStrategy.java
│       └── MostAbundantStrategy.java
│
├── repository/           # Data access (Spring Data JPA)
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── ProductCategoryRepository.java
│   ├── OrderRepository.java
│   ├── StockRepository.java
│   └── LocationRepository.java
│
├── model/                # JPA entities
│   ├── User.java
│   ├── Product.java
│   ├── ProductCategory.java
│   ├── Order.java
│   ├── OrderDetail.java
│   ├── Stock.java
│   └── Location.java
│
├── dto/                  # Data Transfer Objects
│   ├── request/          # Request DTOs
│   ├── response/         # Response DTOs
│   └── mapper/           # Entity-DTO mappers (manual)
│       ├── ProductMapper.java
│       └── OrderMapper.java
│
├── security/             # Security configuration and JWT
│   ├── SecurityConfig.java
│   ├── JwtService.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
│
├── config/               # Spring configuration
│   ├── CorsConfig.java
│   └── OrderStrategyConfig.java
│
└── exception/            # Custom exceptions
    └── OrderNotProcessableException.java
```

**Key Backend Directories**:
- `controller/`: REST API endpoints with Swagger annotations
- `service/`: Business logic and transaction management
- `repository/`: Database access via Spring Data JPA
- `model/`: JPA entities (database tables)
- `dto/`: Request/response objects for API contracts
- `security/`: JWT authentication and authorization

### 4.4 Database Migrations (`/onlineshopapi/src/main/resources/db/migration/`)

```
db/migration/
├── V1__create_tables.sql           # Initial schema
└── local/                           # Local profile only
    └── V1.1__populate_mock_data.sql # Seed data for development
```

**Migration Locations**:
- Base migrations: `classpath:db/migration`
- Local profile: `classpath:db/migration,classpath:db/migration/local`

---

## 5. Development Setup

### 5.1 Prerequisites

- **Java 21** (for backend)
- **Node.js 24+** (for frontend)
- **Maven 3.6+** (for backend builds)
- **npm 11.6.1** (for frontend)
- **Docker and Docker Compose** (for local database)

### 5.2 Database Setup

Start the PostgreSQL database using Docker Compose:

```bash
cd docker/development
docker-compose up -d
```

**Database Connection Details**:
- Host: `localhost`
- Port: `5433`
- Database: `shopdb`
- Username: `shopuser`
- Password: `shoppassword`

**Verify Connection**:
```bash
psql -h localhost -p 5433 -U shopuser -d shopdb
```

### 5.3 Backend Setup

Navigate to the backend directory and start the application:

```bash
cd onlineshopapi

# Start with local profile (includes seed data)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Alternative (IntelliJ)**:
Use the predefined run configuration: **api:local**

**Backend URLs**:
- API Base: `http://localhost:3000/api`
- Swagger UI: `http://localhost:3000/api/swagger-ui/index.html`

### 5.4 Frontend Setup

Navigate to the frontend directory and start the development server:

```bash
cd onlineshopui

# Install dependencies (first time only)
npm install

# Start development server (requires backend running)
npm start

# OR start in mock mode (no backend needed)
npm start:mock
```

**Frontend URL**: `http://localhost:4200`

### 5.5 Mock Users (Local Profile)

The `V1.1` migration seeds the following users for testing:

| Email | Password | Role |
|-------|----------|------|
| `admin@onlineshop.com` | `password` | `ADMIN` |
| `john.doe@email.com` | `password` | `CUSTOMER` |
| `jane.smith@email.com` | `password` | `CUSTOMER` |

**Usage**: Login with any of these credentials to test the application.

---

## 6. Key Patterns and Conventions

### 6.1 Frontend Patterns

#### State Management
- **Angular Signals**: Reactive state with `signal()` and `computed()`
- **Example**: `CartService` uses signals for cart items
  ```typescript
  private cartItems = signal<CartItem[]>([]);
  public items = this.cartItems.asReadonly();
  public total = computed(() => this.items().reduce(...));
  ```
- **Persistence**: Cart and auth tokens stored in `localStorage`

#### Feature Organization
- **Lazy Loading**: Each feature is a separate module loaded on-demand
- **Structure**: Each feature has its own routes, services, components, types
- **Example**: `/features/auth/` contains `auth.routes.ts`, `auth.service.ts`, `auth.guard.ts`

#### HTTP Interceptors
- **Auth Token Interceptor** (`auth-token.interceptor.ts`): Automatically adds JWT Bearer token to all API requests
- **Mock API Interceptor** (`mock-api.interceptor.ts`): Intercepts API calls in mock mode for offline development

#### Environment Configuration
- **Three environments**: `development`, `production`, `mock`
- **File replacements**: Configured in `angular.json`
- **Injection**: Environment config provided via `EnvironmentConfig` DI token

#### Validation
- **Centralized messages**: `ValidationMessages` provider in `core/providers/`
- **Consistent error display**: Used across all forms

### 6.2 Backend Patterns

#### Layered Architecture
```
Controller (REST API)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Database
```

- **DTOs**: Used for API contracts (request/response)
- **Entities**: Used for database persistence
- **Mappers**: Manual conversion between entities and DTOs (no MapStruct)

#### Strategy Pattern for Order Processing

The order service uses the **Strategy pattern** to determine which warehouse location ships each product.

**Interface**: `OrderStrategy`
```java
public interface OrderStrategy {
    List<Stock> findStocks(UUID productId, int quantity);
}
```

**Implementations**:
1. **SingleLocationStrategy**: Ships all items from one location (default)
2. **MostAbundantStrategy**: Ships from location with highest stock

**Configuration**: Set in `application.yml`:
```yaml
app:
  order:
    strategy: SINGLE_LOCATION  # or MOST_ABUNDANT
```

**Location**: `/onlineshopapi/src/main/java/msg/onlineshopapi/service/strategy/`

#### Security

**JWT-Based Authentication**:
1. User logs in → `AuthController.login()`
2. Backend validates credentials → Returns JWT token
3. Frontend stores token in localStorage
4. `JwtAuthFilter` intercepts requests → Validates token → Sets SecurityContext
5. Controllers use `@PreAuthorize("hasRole('ADMIN')")` for role-based access

**Key Security Classes**:
- `JwtService`: Token generation and validation
- `JwtAuthFilter`: HTTP filter for token extraction
- `SecurityConfig`: Spring Security configuration
- `CustomUserDetailsService`: Loads user from database

#### Exception Handling

- **Custom Exceptions**: `OrderNotProcessableException`, etc.
- **Global Handler**: Catches exceptions and returns consistent JSON responses
- **HTTP Status Codes**: 400 for validation errors, 404 for not found, 500 for server errors

#### Database

- **Flyway Migrations**: Version-controlled schema changes
- **Profile-Specific Migrations**: `db/migration/local/` for seed data
- **JPA Validation**: `ddl-auto: validate` (no auto-schema generation)

### 6.3 Naming Conventions

**Frontend**:
- Services: `*.service.ts` (e.g., `auth.service.ts`)
- Components: `*.component.ts` (e.g., `login.component.ts`)
- Guards: `*.guard.ts` (e.g., `auth.guard.ts`)
- Interceptors: `*.interceptor.ts`
- Types: `*.type.ts`, DTOs: `*.dto.ts`
- Routes: `*.routes.ts`

**Backend**:
- Controllers: `*Controller.java` (e.g., `ProductController`)
- Services: `*Service.java` (e.g., `ProductService`)
- Repositories: `*Repository.java` (e.g., `ProductRepository`)
- Entities: Plain nouns (e.g., `Product`, `Order`)
- DTOs: `*RequestDto.java`, `*ResponseDto.java`
- Mappers: `*Mapper.java` (e.g., `ProductMapper`)

---

## 7. Testing Approach

### 7.1 Frontend Testing

**Framework**: Karma with Jasmine (configured via Angular CLI)

**Test Structure**:
- Test files: `*.spec.ts` alongside source files
- Example: `notifications.service.spec.ts`

**Run Tests**:
```bash
cd onlineshopui
npm test
```

**Coverage**: Unit tests for services and components

### 7.2 Backend Testing

**Framework**: JUnit 5 with Spring Boot Test

**Test Structure**:
```
src/test/java/msg/onlineshopapi/
├── config/          # Test configuration (TestSecurityConfig)
├── controller/      # Controller tests (e.g., ProductControllerTest)
├── integration/     # Integration tests (e.g., OrderServiceTest)
└── unit/            # Unit tests (e.g., strategy tests)
```

**TestContainers**:
- PostgreSQL container for integration tests
- Example: `OrderServiceTest` uses `@Testcontainers` with `PostgreSQLContainer`
- Tests run against real database in Docker

**Test Profile**: `application-test.yml` with test-specific configuration

**Run Tests**:
```bash
cd onlineshopapi
mvn test                           # All tests
mvn test -Dtest=OrderServiceTest   # Specific test
```

### 7.3 Testing Best Practices

- Integration tests clean up data in `@AfterEach` hooks
- Use builders for test data (e.g., `Order.builder()`)
- Test both success and failure scenarios
- Use AssertJ for fluent assertions
- Mock external dependencies, but use real database for integration tests

---

## 8. API Documentation

### 8.1 Swagger UI

The backend provides interactive API documentation via Swagger.

**URL**: `http://localhost:3000/api/swagger-ui/index.html`

**Features**:
- Browse all endpoints
- View request/response schemas
- Try-it-out functionality for testing
- Authentication support (add JWT token)

### 8.2 API Structure

**Base URL**: `http://localhost:3000/api`

**Authentication**: Bearer token in `Authorization` header
```
Authorization: Bearer <jwt-token>
```

### 8.3 Main Endpoints

**Authentication**:
- `POST /auth/login` - Authenticate and receive JWT token
- `POST /auth/register` - Create new user account
- `GET /auth/profile` - Get current user profile (authenticated)

**Products**:
- `GET /products` - List all products
- `GET /products/{id}` - Get product by ID
- `POST /products` - Create product (ADMIN only)
- `PUT /products/{id}` - Update product (ADMIN only)
- `DELETE /products/{id}` - Delete product (ADMIN only)

**Product Categories**:
- `GET /product-categories` - List all categories
- `POST /product-categories` - Create category (ADMIN only)
- `PUT /product-categories/{id}` - Update category (ADMIN only)
- `DELETE /product-categories/{id}` - Delete category (ADMIN only)

**Orders**:
- `POST /orders` - Create new order (authenticated)
- `GET /orders` - List orders (user's own orders, or all if ADMIN)
- `GET /orders/{id}` - Get order by ID

### 8.4 OpenAPI Annotations

Controllers use Springdoc annotations for documentation:
- `@Tag`: Group endpoints by resource
- `@Operation`: Describe endpoint purpose
- `@ApiResponse`: Document response codes and types

**Example**:
```java
@Tag(name = "Products", description = "Product management")
@RestController
public class ProductController {
    @Operation(summary = "Get all products")
    @GetMapping("/products")
    public List<ProductResponseDto> getAllProducts() { ... }
}
```

---

## 9. Common Workflows

### 9.1 Adding a New Feature Module (Frontend)

1. **Create feature directory**:
   ```bash
   mkdir -p onlineshopui/src/app/features/new-feature
   cd onlineshopui/src/app/features/new-feature
   ```

2. **Create routes file** (`new-feature.routes.ts`):
   ```typescript
   import { Routes } from '@angular/router';
   
   export const NEW_FEATURE_ROUTES: Routes = [
     { path: '', component: NewFeatureComponent }
   ];
   ```

3. **Create service** (`new-feature.service.ts`):
   ```typescript
   import { Injectable } from '@angular/core';
   
   @Injectable({ providedIn: 'root' })
   export class NewFeatureService { }
   ```

4. **Add components**: Create pages and components as needed

5. **Define types**: Add DTOs in `types/` directory

6. **Add lazy route** to `app.routes.ts`:
   ```typescript
   {
     path: 'new-feature',
     loadChildren: () => import('./features/new-feature/new-feature.routes')
       .then(m => m.NEW_FEATURE_ROUTES)
   }
   ```

7. **Update navigation** (if needed) in `core/config/navigation.config.ts`

### 9.2 Adding a New REST Endpoint (Backend)

1. **Define DTOs** in `/dto/request/` and `/dto/response/`:
   ```java
   public record CreateWidgetRequestDto(String name, String description) { }
   public record WidgetResponseDto(UUID id, String name, String description) { }
   ```

2. **Create mapper** in `/dto/mapper/`:
   ```java
   public class WidgetMapper {
       public static WidgetResponseDto toDto(Widget widget) { ... }
       public static Widget toEntity(CreateWidgetRequestDto dto) { ... }
   }
   ```

3. **Add service method**:
   ```java
   @Service
   public class WidgetService {
       public WidgetResponseDto createWidget(CreateWidgetRequestDto dto) { ... }
   }
   ```

4. **Add controller method**:
   ```java
   @RestController
   @RequestMapping("/widgets")
   public class WidgetController {
       @Operation(summary = "Create widget")
       @ApiResponse(responseCode = "201", description = "Widget created")
       @PostMapping
       public ResponseEntity<WidgetResponseDto> create(@RequestBody CreateWidgetRequestDto dto) { ... }
   }
   ```

5. **Add security** (if needed):
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   @PostMapping
   public ResponseEntity<WidgetResponseDto> create(...) { ... }
   ```

6. **Write tests** in `/src/test/java/.../controller/WidgetControllerTest.java`

7. **Test via Swagger UI**: `http://localhost:3000/api/swagger-ui/index.html`

### 9.3 Database Schema Changes

1. **Create migration file**:
   ```bash
   cd onlineshopapi/src/main/resources/db/migration
   touch V2__add_widget_table.sql
   ```

2. **Write SQL** (forward-only, no rollback):
   ```sql
   CREATE TABLE widgets (
       id UUID PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       description TEXT
   );
   ```

3. **Update JPA entities** (if needed):
   ```java
   @Entity
   @Table(name = "widgets")
   public class Widget {
       @Id
       private UUID id;
       private String name;
       private String description;
   }
   ```

4. **For dev seed data** (optional), create file in `db/migration/local/`:
   ```bash
   touch V2.1__populate_widgets.sql
   ```

5. **Restart backend**: Flyway runs migrations automatically on startup

6. **Verify**:
   ```bash
   psql -h localhost -p 5433 -U shopuser -d shopdb
   \dt  -- List tables
   SELECT * FROM flyway_schema_history;  -- Check migration status
   ```

### 9.4 Adding a New Order Strategy

1. **Create strategy class** in `/service/strategy/`:
   ```java
   @Component("customStrategy")
   public class CustomStrategy implements OrderStrategy {
       @Override
       public List<Stock> findStocks(UUID productId, int quantity) {
           // Custom logic here
       }
   }
   ```

2. **Configure in `application.yml`**:
   ```yaml
   app:
     order:
       strategy: customStrategy
   ```

3. **Write unit test** in `/src/test/java/.../unit/service/strategy/CustomStrategyTest.java`

4. **Restart backend** to apply changes

### 9.5 Working with Mock Mode (Frontend)

**Start in mock mode**:
```bash
cd onlineshopui
npm start:mock
```

**How it works**:
- `MockApiInterceptor` intercepts HTTP requests
- Returns mock data from `/core/mocks/data/`
- No backend required

**Add/modify mock data**:
1. Edit files in `/core/mocks/data/` (e.g., `products.mock.ts`)
2. Update interceptor handlers in `mock-api.interceptor.ts`

**Use cases**:
- UI development without backend
- Offline development
- Faster iteration on frontend features

### 9.6 Adding Authentication to a Component

**Check if user is authenticated**:
```typescript
import { AuthService } from '@features/auth/services/auth.service';

constructor(private authService: AuthService) {}

ngOnInit() {
  if (this.authService.isAuthenticated()) {
    // User is logged in
  }
}
```

**Check user role**:
```typescript
if (this.authService.hasRole(['ADMIN'])) {
  // User is admin
}
```

**Protect route with guard**:
```typescript
{
  path: 'admin',
  canActivate: [authGuard],
  loadChildren: () => import('./features/admin/admin.routes')
}
```

### 9.7 Running Only Frontend or Backend

**Frontend only** (mock mode):
```bash
cd onlineshopui
npm start:mock
```

**Backend only** (test with Swagger UI or curl):
```bash
cd onlineshopapi
mvn spring-boot:run -Dspring-boot.run.profiles=local
# Test at http://localhost:3000/api/swagger-ui/index.html
```

---

## 10. Configuration

### 10.1 Backend Configuration

#### Profiles

**Local Profile** (`application-local.yml`):
- Hardcoded database credentials
- Includes seed data from `db/migration/local/`
- CORS allows `http://localhost:4200`
- Sample JWT secret for development

**Test Profile** (`application-test.yml`):
- Test-specific configuration
- Used by JUnit tests

**Production** (`application.yml`):
- All sensitive values from environment variables
- No seed data
- Strict CORS configuration

#### application.yml (Production)

```yaml
server:
  port: 3000
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

app:
  order:
    strategy: SINGLE_LOCATION  # or MOST_ABUNDANT
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS}
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24 hours
```

#### application-local.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/shopdb
    username: shopuser
    password: shoppassword
  flyway:
    locations: classpath:db/migration,classpath:db/migration/local

app:
  cors:
    allowed-origins: http://localhost:4200
  jwt:
    secret: <base64-encoded-dev-secret>
```

### 10.2 Frontend Configuration

#### Environments

**Development** (`environment.dev.ts`):
```typescript
export const environment = {
  apiUrl: 'http://localhost:3000/api'
};
```

**Production** (`environment.prod.ts`):
```typescript
export const environment = {
  apiUrl: 'https://your-api-domain.com/api'  // Update for production
};
```

**Mock** (`environment.mock.ts`):
```typescript
export const environment = {
  apiUrl: 'http://localhost:3000/api',  // Not used (intercepted)
  mockMode: true
};
```

#### File Replacements

Configured in `angular.json`:
```json
{
  "configurations": {
    "production": {
      "fileReplacements": [{
        "replace": "src/app/core/providers/environment.provider.ts",
        "with": "src/app/core/providers/environment.prod.provider.ts"
      }]
    },
    "mock": {
      "fileReplacements": [{
        "replace": "src/app/core/providers/environment.provider.ts",
        "with": "src/app/core/providers/environment.mock.provider.ts"
      }]
    }
  }
}
```

### 10.3 Docker Configuration

**Location**: `/docker/development/docker-compose.yml`

**PostgreSQL Setup**:
```yaml
services:
  db:
    image: postgres:18
    ports:
      - "5433:5432"
    environment:
      POSTGRES_DB: shopdb
      POSTGRES_USER: shopuser
      POSTGRES_PASSWORD: shoppassword
    volumes:
      - shop-data-volume:/var/lib/postgresql/data
```

**Port Mapping**: 5433 (host) → 5432 (container) to avoid conflicts with existing PostgreSQL installations

---

## 11. Deployment Considerations

### 11.1 Environment Variables (Backend)

All production deployments MUST set:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | Database host address | `postgres.example.com` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `shopdb_prod` |
| `DB_USERNAME` | Database username | `shopuser_prod` |
| `DB_PASSWORD` | Database password | `<strong-password>` |
| `JWT_SECRET` | Base64-encoded secret for JWT signing | `<base64-secret>` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend URLs | `https://shop.example.com` |

### 11.2 Frontend Build

```bash
cd onlineshopui
npm run build
```

**Output**: `dist/` directory with static files

**Steps**:
1. Update `environment.prod.ts` with production API URL
2. Build for production: `npm run build`
3. Serve static files via nginx, CDN, or hosting platform (Vercel, Netlify, etc.)

### 11.3 Backend Build

```bash
cd onlineshopapi
mvn clean package
```

**Output**: `target/onlineshopapi-0.0.1-SNAPSHOT.jar`

**Run**:
```bash
java -jar target/onlineshopapi-0.0.1-SNAPSHOT.jar
```

**Prerequisites**:
- PostgreSQL database must be accessible
- Set all required environment variables before running

**Docker** (optional):
Consider containerizing the backend with a Dockerfile for easier deployment.

### 11.4 Database Migrations

- Flyway runs automatically on application startup
- Production should NOT include `/db/migration/local/` seed data
- Ensure `spring.flyway.locations=classpath:db/migration` (default)

**Important**: Migrations are forward-only. Test migrations in staging before production.

### 11.5 Security Checklist

Before deploying to production:

- [ ] Generate strong `JWT_SECRET` (not the local dev one)
- [ ] Use HTTPS in production (TLS/SSL certificates)
- [ ] Set `CORS_ALLOWED_ORIGINS` to actual frontend domain only
- [ ] Use strong database credentials (not default dev passwords)
- [ ] Enable rate limiting on API endpoints
- [ ] Review Swagger exposure (disable in production or restrict access)
- [ ] Enable Spring Security CSRF protection if using cookies
- [ ] Configure proper logging (avoid logging sensitive data)
- [ ] Set up monitoring and alerting
- [ ] Review and limit database user permissions

### 11.6 Monitoring

**Recommendations**:
- Add Spring Boot Actuator for health checks and metrics
- Use log aggregation (ELK stack, CloudWatch, etc.)
- Monitor database connection pool
- Set up alerts for API errors and slow queries
- Track JWT token expiration issues

---

## 12. Troubleshooting

### Backend Won't Start

**Check PostgreSQL is running**:
```bash
docker ps
# Should see postgres container
```

**Verify database connection**:
```bash
psql -h localhost -p 5433 -U shopuser -d shopdb
```

**Check profile**:
Ensure `local` profile is active for development:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Check logs**:
Look for database connection errors, Flyway migration failures, or port conflicts.

### Frontend Can't Connect to Backend

**Verify backend is running**:
```bash
curl http://localhost:3000/api/products
```

**Check CORS settings**:
Ensure `application-local.yml` allows `http://localhost:4200`:
```yaml
app:
  cors:
    allowed-origins: http://localhost:4200
```

**Inspect browser console**:
Look for CORS errors, network failures, or 401 Unauthorized responses.

**Try mock mode**:
```bash
npm start:mock
```

### JWT Authentication Failing

**Check token format**:
Should be `Bearer <token>` in `Authorization` header.

**Verify JWT_SECRET matches**:
Token must be signed and validated with the same secret.

**Check token expiration**:
Default is 24 hours. Token may have expired.

**Inspect browser localStorage**:
```javascript
// In browser console
localStorage.getItem('auth_token')
```

### Database Migrations Failing

**Check Flyway schema version**:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Verify migration files**:
- Correct location: `src/main/resources/db/migration/`
- Correct naming: `V<version>__<description>.sql`
- No syntax errors in SQL

**Manual schema changes**:
If schema was modified manually, Flyway checksum validation may fail. Either:
1. Revert manual changes
2. Use Flyway repair command (not recommended)

### Tests Failing

**Backend (TestContainers)**:
- Ensure Docker is running
- TestContainers needs Docker to spin up PostgreSQL
- Check Docker daemon connection

**Frontend (Karma/Jasmine)**:
- Clear cache: `npm test -- --clearCache`
- Check test file syntax
- Ensure test environment is configured

**Test profile**:
Backend tests should use `application-test.yml` configuration.

### Port Already in Use

**Backend (port 3000)**:
```bash
lsof -i :3000
kill -9 <PID>
```

**Frontend (port 4200)**:
```bash
lsof -i :4200
kill -9 <PID>
```

**Database (port 5433)**:
```bash
docker ps
docker stop <container-id>
```

---

## 13. Code Quality Tools

### Frontend

**Linter**: ESLint with angular-eslint

**Configuration**: `eslint.config.js`

**Run**:
```bash
cd onlineshopui
npm run lint
```

**Formatter**: Prettier

**Configuration**: In `package.json`:
```json
{
  "prettier": {
    "semi": true,
    "singleQuote": true,
    "trailingComma": "es5"
  }
}
```

**Run**:
```bash
npm run format
```

### Backend

**Style**: Java standard conventions

**Lombok**: Used for boilerplate reduction
- `@Builder`: Builder pattern for entities
- `@RequiredArgsConstructor`: Constructor injection
- `@Getter`, `@Setter`: Getters and setters

**Maven**: Enforces compilation warnings

**Recommendations**:
- Use consistent formatting in IDE (IntelliJ auto-format)
- Follow Spring Boot conventions
- Write Javadoc for public APIs

---

## 14. Database Schema Reference

### Core Entities

**users**: User accounts with authentication
- `id` (UUID, PK)
- `first_name`, `last_name`
- `email` (unique)
- `password` (BCrypt hashed)
- `role` (ADMIN, CUSTOMER)

**product_categories**: Product categories
- `id` (UUID, PK)
- `name`, `description`

**products**: Product catalog
- `id` (UUID, PK)
- `name`, `description`
- `price` (DECIMAL)
- `weight` (DOUBLE)
- `category_id` (FK → product_categories)
- `image_url`

**locations**: Warehouse/store locations
- `id` (UUID, PK)
- `name`, `country`, `city`, `county`, `street_address`

**stocks**: Inventory levels (composite key)
- `product_id` (PK, FK → products)
- `location_id` (PK, FK → locations)
- `quantity` (INTEGER)

**orders**: Customer orders
- `id` (UUID, PK)
- `user_id` (FK → users)
- `created_at` (TIMESTAMP)
- `country`, `city`, `county`, `street_address` (shipping address)

**order_details**: Order line items (composite key)
- `order_id` (PK, FK → orders)
- `product_id` (PK, FK → products)
- `quantity` (INTEGER)
- `shipped_from_id` (FK → locations)

### Key Relationships

- `Product` → `Category` (many-to-one)
- `Stock` → `Product` + `Location` (composite key)
- `Order` → `User` (many-to-one)
- `Order` → `OrderDetails` (one-to-many)
- `OrderDetail` → `Product` (many-to-one)
- `OrderDetail` → `Location` (shipped_from, many-to-one)

### Visual Schema

See Mermaid diagram in `/onlineshopapi/README.md` for visual representation.

---

## 15. Contributing Guidelines

### Branch Strategy

(Define your team's branching strategy: Git Flow, GitHub Flow, Trunk-Based Development, etc.)

**Example (GitHub Flow)**:
- `main` branch is always deployable
- Create feature branches from `main`
- Open PR for review
- Merge to `main` after approval

### Commit Messages

Use clear, descriptive commit messages.

**Format**: `type(scope): description`

**Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

**Examples**:
- `feat(products): add product filtering by category`
- `fix(auth): resolve JWT expiration bug`
- `docs(readme): update setup instructions`
- `refactor(orders): extract order strategy to separate classes`

### Pull Request Process

1. **Create feature branch** from `main`
2. **Write tests** for new features
3. **Ensure all tests pass**: `mvn test` and `npm test`
4. **Run linters/formatters**: `npm run lint` and `npm run format`
5. **Push branch** and open PR
6. **Request review** from team member
7. **Address feedback**
8. **Merge** after approval

### Code Review Checklist

- [ ] All tests pass (frontend and backend)
- [ ] Code follows project patterns and conventions
- [ ] No hardcoded secrets or credentials
- [ ] API changes documented in Swagger annotations
- [ ] Database migrations included (if schema changed)
- [ ] DTOs created for new endpoints
- [ ] Error handling implemented
- [ ] Commit messages follow format
- [ ] No commented-out code or debug statements
- [ ] Security considerations addressed

---

## 16. Useful Commands Cheat Sheet

### Frontend (onlineshopui)

```bash
npm start              # Dev server (with backend)
npm start:mock         # Mock mode (no backend)
npm test               # Run tests
npm run lint           # Run ESLint
npm run format         # Format with Prettier
npm run build          # Production build
npm run watch          # Watch mode for development
```

### Backend (onlineshopapi)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local  # Run with local profile
mvn test                                               # Run all tests
mvn test -Dtest=OrderServiceTest                      # Run specific test
mvn clean package                                      # Build JAR
mvn clean                                              # Clean build artifacts
```

### Docker (docker/development)

```bash
docker-compose up -d           # Start database (detached)
docker-compose down            # Stop database
docker-compose logs -f db      # View database logs (follow)
docker-compose restart db      # Restart database
docker ps                      # List running containers
```

### Database (PostgreSQL)

```bash
# Connect to local database
psql -h localhost -p 5433 -U shopuser -d shopdb

# Inside psql:
\dt                     # List tables
\d table_name           # Describe table
SELECT * FROM flyway_schema_history;  # Check migration status
\q                      # Quit
```

### Git

```bash
git status                     # Check status
git add .                      # Stage all changes
git commit -m "message"        # Commit changes
git push origin feature-name   # Push branch
git pull origin main           # Pull latest main
git checkout -b feature-name   # Create and switch to new branch
```

---

## 17. Additional Resources

### Framework Documentation

- **Angular**: [https://angular.dev/](https://angular.dev/)
- **Spring Boot**: [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
- **Spring Security**: [https://spring.io/projects/spring-security](https://spring.io/projects/spring-security)
- **Spring Data JPA**: [https://spring.io/projects/spring-data-jpa](https://spring.io/projects/spring-data-jpa)
- **Flyway**: [https://flywaydb.org/documentation/](https://flywaydb.org/documentation/)
- **TestContainers**: [https://testcontainers.com/](https://testcontainers.com/)

### API Tools

- **Swagger UI**: `http://localhost:3000/api/swagger-ui/index.html` (when backend running)
- **Postman**: Can import OpenAPI spec from Swagger for testing

### Frontend Libraries

- **Tailwind CSS**: [https://tailwindcss.com/docs](https://tailwindcss.com/docs)
- **Lucide Icons**: [https://lucide.dev/](https://lucide.dev/)
- **RxJS**: [https://rxjs.dev/](https://rxjs.dev/)
- **Jasmine**: [https://jasmine.github.io/](https://jasmine.github.io/)
- **Karma**: [https://karma-runner.github.io/](https://karma-runner.github.io/)

### Backend Libraries

- **Lombok**: [https://projectlombok.org/](https://projectlombok.org/)
- **jjwt (JWT)**: [https://github.com/jwtk/jjwt](https://github.com/jwtk/jjwt)
- **Springdoc OpenAPI**: [https://springdoc.org/](https://springdoc.org/)

### Database

- **PostgreSQL**: [https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)

---

## Project Contacts and Support

For questions, issues, or contributions, please refer to your team's communication channels or project management system.

**Happy Coding!**
