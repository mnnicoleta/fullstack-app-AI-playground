# Online Shop API - Spring Boot Backend Documentation

## 1. Project Overview

**Online Shop API** is a Spring Boot 4.0.6 REST API backend for an e-commerce application. It provides comprehensive product management, user authentication with JWT tokens, shopping cart functionality via orders, and sophisticated multi-warehouse inventory management.

**Key Features**:
- JWT-based stateless authentication with role-based access control (ADMIN, CUSTOMER)
- RESTful API with Swagger/OpenAPI interactive documentation
- Multi-warehouse inventory with configurable order fulfillment strategies
- PostgreSQL database with Flyway version-controlled migrations
- Comprehensive testing with TestContainers and JUnit 5

**Technology Stack**:
- **Framework**: Spring Boot 4.0.6
- **Language**: Java 21
- **Build Tool**: Maven 3
- **Database**: PostgreSQL 18
- **Security**: Spring Security with JWT (JJWT 0.13.0)
- **ORM**: Spring Data JPA (Hibernate)
- **Migrations**: Flyway 2.x
- **API Documentation**: Springdoc OpenAPI 3.0.3
- **Testing**: JUnit 5, Spring Boot Test, TestContainers
- **Utilities**: Lombok for boilerplate reduction

---

## 2. Architecture

### 2.1 Layered Architecture

```
┌─────────────────────────────────────┐
│   REST Controllers (@RestController) │  ← HTTP Layer
│   - Endpoint definitions             │
│   - Request validation               │
│   - Swagger annotations              │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Service Layer (@Service)           │  ← Business Logic
│   - Transaction management           │
│   - Business rules                   │
│   - Strategy patterns                │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Repository Layer (JPA)             │  ← Data Access
│   - Spring Data repositories         │
│   - Custom query methods             │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Database (PostgreSQL)              │  ← Persistence
│   - Flyway-managed schema            │
└─────────────────────────────────────┘
```

### 2.2 JWT Security Flow

```
1. User → POST /api/auth/login {email, password}
2. AuthService validates credentials
3. JwtService generates signed token (24hr expiration)
4. Token returned: {"access_token": "eyJ..."}
5. Frontend stores token (localStorage)
6. Subsequent requests → Authorization: Bearer eyJ...
7. JwtAuthFilter intercepts request
8. JwtService validates token signature + expiration
9. SecurityContext populated with authenticated user
10. Controller checks @PreAuthorize("hasRole('ADMIN')")
11. Response returned
```

### 2.3 Order Fulfillment Strategy Pattern

The application uses the **Strategy Pattern** to determine how orders are fulfilled from multiple warehouse locations.

**OrderStrategy Interface**:
```java
public interface OrderStrategy {
    List<Stock> findStocks(Set<OrderDetail> orderDetails);
}
```

**Implementations**:

1. **SingleLocationStrategy** (default):
   - Fulfills entire order from a single warehouse
   - Queries for locations having all required products
   - Selects first location with sufficient quantities
   - Throws `OrderNotProcessableException` if no single location has all items

2. **MostAbundantStrategy**:
   - Fulfills each product from the location with maximum stock
   - Allows split shipments from multiple warehouses
   - Queries for max stock per product
   - Throws `OrderNotProcessableException` if any product has insufficient stock

**Configuration**: Set strategy in `application.yml`:
```yaml
app:
  order:
    strategy: SINGLE_LOCATION  # or MOST_ABUNDANT
```

---

## 3. Directory Structure

### 3.1 Project Root

```
onlineshopapi/
├── src/
│   ├── main/
│   │   ├── java/msg/onlineshopapi/    # Java source code
│   │   └── resources/                  # Configuration and migrations
│   └── test/
│       └── java/msg/onlineshopapi/    # Test code
├── pom.xml                             # Maven configuration
└── README.md                           # Project documentation
```

### 3.2 Source Code Structure (`/src/main/java/msg/onlineshopapi/`)

```
msg/onlineshopapi/
├── OnlineShopApiApplication.java      # Main application class
│
├── controller/                         # REST endpoints
│   ├── AuthController.java            # /auth/* endpoints
│   ├── OrderController.java           # /orders/* endpoints
│   ├── ProductController.java         # /products/* endpoints
│   └── ProductCategoryController.java # /products/categories/* endpoints
│
├── service/                            # Business logic
│   ├── AuthService.java               # Authentication operations
│   ├── OrderService.java              # Order processing (transactional)
│   ├── ProductService.java            # Product CRUD
│   ├── ProductCategoryService.java    # Category CRUD
│   └── strategy/                      # Order fulfillment strategies
│       ├── OrderStrategy.java         # Strategy interface
│       ├── OrderStrategyConfig.java   # Bean configuration
│       ├── SingleLocationStrategy.java
│       └── MostAbundantStrategy.java
│
├── repository/                         # Data access (Spring Data JPA)
│   ├── LocationRepository.java
│   ├── OrderDetailRepository.java
│   ├── OrderRepository.java
│   ├── ProductCategoryRepository.java
│   ├── ProductRepository.java
│   ├── StockRepository.java           # Custom queries for strategies
│   └── UserRepository.java            # findByEmail query
│
├── model/                              # JPA entities
│   ├── Address.java                   # @Embeddable
│   ├── Location.java
│   ├── Order.java
│   ├── OrderDetail.java               # Composite key
│   ├── OrderDetailId.java             # Composite key class
│   ├── Product.java
│   ├── ProductCategory.java
│   ├── Stock.java                     # Composite key
│   ├── StockId.java                   # Composite key class
│   ├── User.java
│   └── UserRole.java                  # Enum: ADMIN, CUSTOMER
│
├── dto/                                # Data Transfer Objects
│   ├── request/                       # Request DTOs
│   │   ├── CreateProductRequestDto.java
│   │   ├── LoginRequestDto.java
│   │   ├── OrderItemRequestDto.java
│   │   ├── OrderRequestDto.java
│   │   ├── RegisterRequestDto.java
│   │   └── UpdateProductRequestDto.java
│   ├── response/                      # Response DTOs
│   │   ├── AddressDto.java
│   │   ├── AuthResponseDto.java
│   │   ├── LocationResponseDto.java
│   │   ├── OrderDetailResponseDto.java
│   │   ├── OrderResponseDto.java
│   │   ├── ProductCategoryDto.java
│   │   ├── ProductResponseDto.java
│   │   └── UserDto.java
│   └── mapper/                        # Entity-DTO conversion
│       ├── AddressMapper.java
│       ├── AuthMapper.java
│       ├── LocationMapper.java
│       ├── OrderDetailMapper.java
│       ├── OrderMapper.java
│       ├── ProductCategoryMapper.java
│       └── ProductMapper.java
│
├── security/                           # Authentication & authorization
│   ├── JwtAuthFilter.java             # Token validation filter
│   ├── JwtProperties.java             # JWT configuration record
│   ├── JwtService.java                # Token generation/validation
│   ├── SecurityConfig.java            # Spring Security configuration
│   └── UserDetailsServiceImpl.java    # User loading for auth
│
├── config/                             # Spring configuration
│   └── OpenApiConfig.java             # Swagger/OpenAPI setup
│
└── exception/                          # Custom exceptions
    ├── DuplicateResourceException.java
    ├── GlobalExceptionHandler.java    # @RestControllerAdvice
    ├── OrderNotProcessableException.java
    └── ResourceNotFoundException.java
```

### 3.3 Resources Structure (`/src/main/resources/`)

```
resources/
├── application.yml                     # Production configuration
├── application-local.yml               # Local development profile
├── application-test.yml                # Test profile
└── db/migration/                       # Flyway migrations
    ├── V1__create_tables.sql          # Initial schema
    └── local/                          # Local profile only
        └── V1.1__populate_mock_data.sql  # Seed data
```

### 3.4 Test Structure (`/src/test/java/msg/onlineshopapi/`)

```
test/msg/onlineshopapi/
├── config/
│   └── TestSecurityConfig.java        # Test security configuration
├── controller/                         # Controller tests
│   ├── OrderControllerTest.java
│   ├── ProductCategoryControllerTest.java
│   └── ProductController Test.java
├── integration/                        # Integration tests
│   └── OrderServiceTest.java          # TestContainers
└── unit/service/strategy/              # Unit tests
    ├── MostAbundantStrategyTest.java
    └── SingleLocationStrategyTest.java
```

---

## 4. Development Setup

### 4.1 Prerequisites

- **Java 21** (JDK)
- **Maven 3.6+**
- **Docker and Docker Compose** (for PostgreSQL)
- **IDE**: IntelliJ IDEA recommended (has run configurations in `.run/`)

### 4.2 Database Setup

Start PostgreSQL using Docker Compose:

```bash
cd ../docker/development
docker-compose up -d
```

**Database Connection**:
- Host: `localhost`
- Port: `5433` (mapped to avoid conflicts with existing PostgreSQL)
- Database: `shopdb`
- Username: `shopuser`
- Password: `shoppassword`

**Verify Connection**:
```bash
psql -h localhost -p 5433 -U shopuser -d shopdb
# Password: shoppassword
```

### 4.3 Running the Application

**Option 1: Maven Command Line**

```bash
# From onlineshopapi directory
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Option 2: IntelliJ Run Configuration**

Use the predefined configuration:
- **api:local** - Runs with `-Dspring.profiles.active=local`

**Application URLs**:
- API Base: `http://localhost:3000/api`
- Swagger UI: `http://localhost:3000/api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:3000/api/v3/api-docs`

### 4.4 Mock Users (Local Profile)

The `V1.1__populate_mock_data.sql` migration seeds these users:

| Email | Password | Role | Usage |
|-------|----------|------|-------|
| `admin@onlineshop.com` | `password` | `ADMIN` | Product management, view all orders |
| `john.doe@email.com` | `password` | `CUSTOMER` | Place orders, view own orders |
| `jane.smith@email.com` | `password` | `CUSTOMER` | Place orders, view own orders |

All passwords are BCrypt-hashed in the database.

---

## 5. Security Implementation

### 5.1 JWT Configuration

**JwtProperties** (`/security/JwtProperties.java`):
```java
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
    String secret,      // Base64-encoded HMAC signing key
    long expiration     // Token lifetime in milliseconds (default: 86400000 = 24 hours)
) {}
```

**Configuration in application.yml**:
```yaml
app:
  jwt:
    secret: ${JWT_SECRET}      # Environment variable
    expiration: 86400000        # 24 hours
```

### 5.2 JWT Service

**JwtService** (`/security/JwtService.java`):

**Key Methods**:
- `generateToken(String username)` - Creates signed JWT with subject and expiration
- `extractUsername(String token)` - Parses token and extracts subject (username)
- `isTokenValid(String token, UserDetails userDetails)` - Validates signature, expiration, and username match
- `isTokenExpired(String token)` - Checks if token expiration date has passed

**Token Structure**:
```json
{
  "sub": "admin@onlineshop.com",
  "iat": 1677649200,
  "exp": 1677735600
}
```

**Signing**: Uses HMAC-SHA256 with secret key from `JwtProperties`

### 5.3 JWT Authentication Filter

**JwtAuthFilter** (`/security/JwtAuthFilter.java`):

Extends `OncePerRequestFilter` to execute once per request.

**Flow**:
1. Extract `Authorization` header
2. Check for `Bearer ` prefix
3. Extract token substring
4. Validate token via `JwtService`
5. Load `UserDetails` from database
6. Create `UsernamePasswordAuthenticationToken`
7. Set authentication in `SecurityContextHolder`
8. Continue filter chain

**Note**: Silently passes invalid tokens to allow Spring Security to handle 401 responses.

### 5.4 Security Configuration

**SecurityConfig** (`/security/SecurityConfig.java`):

**Key Configurations**:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(csrf -> csrf.disable())  // Disabled for stateless API
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/register", "/auth/login").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

**CORS Configuration**:
- Allowed origins from `app.cors.allowed-origins` (comma-separated list)
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed headers: Authorization, Content-Type
- Credentials allowed

**Public Endpoints** (no authentication required):
- `POST /api/auth/register`
- `POST /api/auth/login`
- `/api/v3/api-docs/**`
- `/api/swagger-ui/**`

**Protected Endpoints**:
- All other endpoints require valid JWT token
- Some require specific roles (via `@PreAuthorize`)

### 5.5 User Details Service

**UserDetailsServiceImpl** (`/security/UserDetailsServiceImpl.java`):

Implements Spring's `UserDetailsService` interface.

**loadUserByUsername(String email)**:
1. Queries `UserRepository.findByEmail(email)`
2. Throws `UsernameNotFoundException` if not found
3. Maps `UserRole` enum to Spring Security authorities:
   - `UserRole.ADMIN` → `ROLE_ADMIN`
   - `UserRole.CUSTOMER` → `ROLE_CUSTOMER`
4. Returns `org.springframework.security.core.userdetails.User`

### 5.6 Password Encoding

**Bean Configuration**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // Default: 10 rounds
}
```

**Usage**:
- Registration: `passwordEncoder.encode(plainPassword)`
- Login: Handled by `AuthenticationManager` with BCrypt comparison

---

## 6. Database Layer

### 6.1 JPA Configuration

**Hibernate DDL Strategy**: `validate` (schema must exist via Flyway)

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # No auto-schema generation
```

### 6.2 Entities

#### User (`/model/User.java`)
```java
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    private UUID id;
    
    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String password;  // BCrypt-hashed
    
    @Enumerated(EnumType.STRING)
    private UserRole role;    // ADMIN or CUSTOMER
}
```

#### ProductCategory (`/model/ProductCategory.java`)
```java
@Entity
@Table(name = "product_categories")
public class ProductCategory {
    @Id
    private UUID id;
    private String name;
    private String description;
}
```

#### Product (`/model/Product.java`)
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    private UUID id;
    
    private String name;
    private String description;
    private BigDecimal price;
    private Double weight;
    private String imageUrl;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private ProductCategory category;
}
```

#### Location (`/model/Location.java`)
```java
@Entity
@Table(name = "locations")
public class Location {
    @Id
    private UUID id;
    
    private String name;
    
    @Embedded
    private Address address;  // country, city, county, streetAddress
}
```

#### Address (`/model/Address.java`)
```java
@Embeddable
public class Address {
    private String country;
    private String city;
    private String county;
    private String streetAddress;
}
```

**Usage**: Embedded in `Location` and `Order` entities.

#### Stock (`/model/Stock.java`)
```java
@Entity
@Table(name = "stocks")
public class Stock {
    @EmbeddedId
    private StockId id;  // Composite: productId + locationId
    
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name = "location_id")
    private Location location;
    
    private Integer quantity;
}
```

#### StockId (`/model/StockId.java`)
```java
@Embeddable
public class StockId implements Serializable {
    private UUID productId;
    private UUID locationId;
    
    // equals() and hashCode() required
}
```

#### Order (`/model/Order.java`)
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Embedded
    private Address address;  // Shipping address
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
```

#### OrderDetail (`/model/OrderDetail.java`)
```java
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId id;  // Composite: orderId + productId
    
    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "shipped_from_id")
    private Location shippedFrom;
    
    private Integer quantity;
}
```

#### OrderDetailId (`/model/OrderDetailId.java`)
```java
@Embeddable
public class OrderDetailId implements Serializable {
    private UUID orderId;
    private UUID productId;
    
    // equals() and hashCode() required
}
```

#### UserRole (`/model/UserRole.java`)
```java
public enum UserRole {
    ADMIN,
    CUSTOMER
}
```

### 6.3 Entity Relationships

```
ProductCategory  1 ────< N  Product
Product          N ────> N  Location  (via Stock)
User             1 ────< N  Order
Order            1 ────< N  OrderDetail
OrderDetail      N ────> 1  Product
OrderDetail      N ────> 1  Location (shipped_from)
```

### 6.4 Repositories

All repositories extend `JpaRepository<Entity, ID>`.

#### UserRepository (`/repository/UserRepository.java`)
```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
```

#### ProductRepository (`/repository/ProductRepository.java`)
```java
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Basic CRUD inherited
}
```

#### ProductCategoryRepository (`/repository/ProductCategoryRepository.java`)
```java
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    // Basic CRUD inherited
}
```

#### OrderRepository (`/repository/OrderRepository.java`)
```java
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Basic CRUD inherited
}
```

#### LocationRepository (`/repository/LocationRepository.java`)
```java
public interface LocationRepository extends JpaRepository<Location, UUID> {
    // Basic CRUD inherited
}
```

#### OrderDetailRepository (`/repository/OrderDetailRepository.java`)
```java
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    // Basic CRUD inherited
}
```

#### StockRepository (`/repository/StockRepository.java`)

**Custom Queries for Order Strategies**:

```java
public interface StockRepository extends JpaRepository<Stock, StockId> {
    
    /**
     * Find stock entries with maximum quantity per product.
     * Used by MostAbundantStrategy.
     */
    @Query("""
        SELECT s FROM Stock s
        WHERE s.product.id IN :productIds
        AND s.quantity = (
            SELECT MAX(s2.quantity) FROM Stock s2
            WHERE s2.product.id = s.product.id
        )
    """)
    List<Stock> findMaxStockLocations(@Param("productIds") List<UUID> productIds);
    
    /**
     * Find locations that have all required products.
     * Used by SingleLocationStrategy.
     */
    @Query("""
        SELECT s.location.id FROM Stock s
        WHERE s.product.id IN :productIds
        GROUP BY s.location.id
        HAVING COUNT(DISTINCT s.product.id) = :productCount
    """)
    List<UUID> findLocationIdsHavingAllProducts(
        @Param("productIds") List<UUID> productIds,
        @Param("productCount") long productCount
    );
    
    /**
     * Find stocks by location and product IDs.
     */
    List<Stock> findByLocationIdInAndProductIdIn(
        List<UUID> locationIds,
        List<UUID> productIds
    );
}
```

---

## 7. Service Layer

### 7.1 AuthService (`/service/AuthService.java`)

**Dependencies**: `UserRepository`, `PasswordEncoder`, `JwtService`, `AuthenticationManager`, `UserDetailsService`

**Methods**:

```java
public User register(User user) {
    // Check if email already exists
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new DuplicateResourceException("Email already in use");
    }
    
    // Encode password and set CUSTOMER role
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setRole(UserRole.CUSTOMER);
    
    return userRepository.save(user);
}

public String login(String email, String password) {
    // Authenticate with Spring Security
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );
    
    // Generate JWT token
    return jwtService.generateToken(email);
}

public User getProfile(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
}
```

### 7.2 ProductService (`/service/ProductService.java`)

**Dependencies**: `ProductRepository`

**Methods**:

```java
public List<Product> findAll() {
    return productRepository.findAll();
}

public Product findById(UUID id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
}

public Product save(Product product) {
    return productRepository.save(product);
}

public Product update(UUID id, Product product) {
    Product existing = findById(id);
    
    // Update fields
    existing.setName(product.getName());
    existing.setDescription(product.getDescription());
    existing.setPrice(product.getPrice());
    existing.setWeight(product.getWeight());
    existing.setCategory(product.getCategory());
    existing.setImageUrl(product.getImageUrl());
    
    return productRepository.save(existing);
}

public void deleteById(UUID id) {
    if (!productRepository.existsById(id)) {
        throw new ResourceNotFoundException("Product not found");
    }
    productRepository.deleteById(id);
}
```

### 7.3 ProductCategoryService (`/service/ProductCategoryService.java`)

**Dependencies**: `ProductCategoryRepository`

**Methods**: Similar CRUD pattern to `ProductService`

### 7.4 OrderService (`/service/OrderService.java`)

**Dependencies**: `OrderRepository`, `StockRepository`, `UserRepository`, `OrderStrategy`

**Main Method**:

```java
@Transactional
public Order createOrder(Order order, String email) {
    // 1. Resolve user
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    order.setUser(user);
    
    // 2. Merge duplicate products (sum quantities)
    Set<OrderDetail> mergedDetails = mergeDuplicateProducts(order.getOrderDetails());
    
    // 3. Use strategy to find optimal stock locations
    List<Stock> stocks = orderStrategy.findStocks(mergedDetails);
    
    if (stocks.isEmpty()) {
        throw new OrderNotProcessableException("Insufficient stock for order");
    }
    
    // 4. Save initial order
    Order savedOrder = orderRepository.save(order);
    
    // 5. Build order details with shipped-from locations
    Set<OrderDetail> detailsWithLocations = buildOrderDetails(savedOrder, mergedDetails, stocks);
    savedOrder.setOrderDetails(detailsWithLocations);
    
    // 6. Deduct stock quantities
    deductStock(stocks, detailsWithLocations);
    
    // 7. Save final order with details
    return orderRepository.save(savedOrder);
}
```

**Helper Methods**:

```java
private Set<OrderDetail> mergeDuplicateProducts(Set<OrderDetail> details) {
    // Group by product ID and sum quantities
    Map<UUID, Integer> productQuantities = details.stream()
        .collect(Collectors.groupingBy(
            d -> d.getProduct().getId(),
            Collectors.summingInt(OrderDetail::getQuantity)
        ));
    
    // Create merged details
    return productQuantities.entrySet().stream()
        .map(entry -> {
            OrderDetail detail = new OrderDetail();
            detail.setProduct(new Product(entry.getKey()));
            detail.setQuantity(entry.getValue());
            return detail;
        })
        .collect(Collectors.toSet());
}

private Set<OrderDetail> buildOrderDetails(Order order, Set<OrderDetail> details, List<Stock> stocks) {
    // Map product ID to stock location
    Map<UUID, Location> productToLocation = stocks.stream()
        .collect(Collectors.toMap(
            s -> s.getProduct().getId(),
            Stock::getLocation
        ));
    
    // Set shipped-from for each detail
    return details.stream()
        .map(detail -> {
            detail.setOrder(order);
            detail.setShippedFrom(productToLocation.get(detail.getProduct().getId()));
            return detail;
        })
        .collect(Collectors.toSet());
}

private void deductStock(List<Stock> stocks, Set<OrderDetail> details) {
    // Map product ID to ordered quantity
    Map<UUID, Integer> orderedQuantities = details.stream()
        .collect(Collectors.toMap(
            d -> d.getProduct().getId(),
            OrderDetail::getQuantity
        ));
    
    // Update stock quantities
    stocks.forEach(stock -> {
        UUID productId = stock.getProduct().getId();
        int orderedQty = orderedQuantities.getOrDefault(productId, 0);
        stock.setQuantity(stock.getQuantity() - orderedQty);
        stockRepository.save(stock);
    });
}
```

---

## 8. REST API Endpoints

### 8.1 AuthController (`/controller/AuthController.java`)

**Base Path**: `/api/auth`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/register` | Public | Register new user account |
| POST | `/login` | Public | Authenticate and receive JWT token |
| GET | `/profile` | Authenticated | Get current user profile |

**Example Requests**:

```bash
# Register
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
  }'

# Login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Response: {"token": "eyJhbGc..."}

# Get Profile
curl -X GET http://localhost:3000/api/auth/profile \
  -H "Authorization: Bearer eyJhbGc..."
```

### 8.2 ProductController (`/controller/ProductController.java`)

**Base Path**: `/api/products`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/` | Public | List all products |
| GET | `/{id}` | Public | Get product by ID |
| POST | `/` | ADMIN only | Create new product |
| PUT | `/{id}` | ADMIN only | Update product |
| DELETE | `/{id}` | ADMIN only | Delete product |

**Role Protection**:
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ProductResponseDto createProduct(@RequestBody CreateProductRequestDto dto) {
    // ...
}
```

### 8.3 ProductCategoryController (`/controller/ProductCategoryController.java`)

**Base Path**: `/api/products/categories`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/` | Public | List all categories |
| GET | `/{id}` | Public | Get category by ID |
| POST | `/` | ADMIN only | Create new category |
| PUT | `/{id}` | ADMIN only | Update category |
| DELETE | `/{id}` | ADMIN only | Delete category |

### 8.4 OrderController (`/controller/OrderController.java`)

**Base Path**: `/api/orders`

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/` | Authenticated | List orders (user's own, or all if ADMIN) |
| GET | `/{id}` | Authenticated | Get order by ID |
| POST | `/` | Authenticated | Create new order from cart |

**Example Create Order**:
```bash
curl -X POST http://localhost:3000/api/orders \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": "uuid-1", "quantity": 2},
      {"productId": "uuid-2", "quantity": 1}
    ]
  }'
```

### 8.5 Swagger/OpenAPI Documentation

**Interactive UI**: `http://localhost:3000/api/swagger-ui/index.html`

**Features**:
- Try-it-out functionality for all endpoints
- JWT authentication support (click "Authorize" button)
- Request/response schema documentation
- Example values for all DTOs

**OpenAPI JSON**: `http://localhost:3000/api/v3/api-docs`

---

## 9. DTO Patterns

### 9.1 Request DTOs (`/dto/request/`)

```java
public record LoginRequestDto(String email, String password) {}

public record RegisterRequestDto(
    String firstName,
    String lastName,
    String email,
    String password
) {}

public record CreateProductRequestDto(
    String name,
    String description,
    BigDecimal price,
    Double weight,
    UUID categoryId,
    String imageUrl
) {}

public record UpdateProductRequestDto(
    String name,
    String description,
    BigDecimal price,
    Double weight,
    UUID categoryId,
    String imageUrl
) {}

public record OrderItemRequestDto(
    UUID productId,
    Integer quantity
) {}

public record OrderRequestDto(
    List<OrderItemRequestDto> items
) {}
```

### 9.2 Response DTOs (`/dto/response/`)

```java
public record AuthResponseDto(String token) {}

public record UserDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    UserRole role
) {}

public record ProductCategoryDto(
    UUID id,
    String name,
    String description
) {}

public record ProductResponseDto(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    Double weight,
    ProductCategoryDto category,
    String imageUrl
) {}

public record AddressDto(
    String country,
    String city,
    String county,
    String streetAddress
) {}

public record LocationResponseDto(
    UUID id,
    String name,
    AddressDto address
) {}

public record OrderDetailResponseDto(
    UUID productId,
    Integer quantity,
    UUID shippedFromLocationId
) {}

public record OrderResponseDto(
    UUID id,
    UUID userId,
    LocalDateTime createdAt,
    AddressDto address,
    List<OrderDetailResponseDto> details
) {}
```

### 9.3 Mappers (`/dto/mapper/`)

All mappers are annotated with `@Component` for Spring dependency injection.

**Example: ProductMapper**

```java
@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductCategoryMapper categoryMapper;
    
    public ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getWeight(),
            categoryMapper.toDto(product.getCategory()),
            product.getImageUrl()
        );
    }
    
    public Product toEntity(CreateProductRequestDto dto) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setWeight(dto.weight());
        product.setImageUrl(dto.imageUrl());
        // Category set by service layer
        return product;
    }
}
```

**Mapper Components**:
- `AuthMapper` - User ↔ RegisterRequestDto / UserDto
- `ProductMapper` - Product ↔ CreateProductRequestDto / ProductResponseDto
- `ProductCategoryMapper` - ProductCategory ↔ ProductCategoryDto
- `OrderMapper` - Order ↔ OrderRequestDto / OrderResponseDto
- `OrderDetailMapper` - OrderDetail ↔ OrderDetailResponseDto
- `AddressMapper` - Address ↔ AddressDto
- `LocationMapper` - Location ↔ LocationResponseDto

**Note**: Manual mapping (no MapStruct) for full control and transparency.

---

## 10. Database Migrations

### 10.1 Flyway Configuration

**Location**: `/src/main/resources/db/migration/`

**Naming Convention**: `V{version}__{description}.sql`

**Profiles**:
- **Default**: `classpath:db/migration` (production migrations only)
- **Local**: `classpath:db/migration,classpath:db/migration/local` (includes seed data)

**application.yml**:
```yaml
spring:
  flyway:
    schemas: public
    # locations defaults to classpath:db/migration
```

**application-local.yml**:
```yaml
spring:
  flyway:
    locations: classpath:db/migration,classpath:db/migration/local
```

### 10.2 V1__create_tables.sql

Creates all tables with proper constraints:

```sql
-- Product Categories
CREATE TABLE product_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Products
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    weight DOUBLE PRECISION,
    category_id UUID,
    image_url VARCHAR(500),
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

-- Locations (Warehouses)
CREATE TABLE locations (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100),
    city VARCHAR(100),
    county VARCHAR(100),
    street_address VARCHAR(255)
);

-- Stock (Inventory)
CREATE TABLE stocks (
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id, location_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Orders
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    country VARCHAR(100),
    city VARCHAR(100),
    county VARCHAR(100),
    street_address VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Details (Line Items)
CREATE TABLE order_details (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    shipped_from_id UUID,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (shipped_from_id) REFERENCES locations(id)
);
```

### 10.3 V1.1__populate_mock_data.sql (Local Profile)

Seeds test data for local development:

**4 Product Categories**:
- Electronics
- Clothing
- Home & Garden
- Sports

**10 Products** (excerpt):
```sql
INSERT INTO products (id, name, description, price, weight, category_id, image_url) VALUES
('uuid-1', 'Wireless Headphones', 'Noise-cancelling Bluetooth headphones', 79.99, 0.25, 'electronics-uuid', '/images/headphones.jpg'),
('uuid-2', 'Smart Watch', 'Fitness tracker with heart rate monitor', 199.99, 0.05, 'electronics-uuid', '/images/smartwatch.jpg'),
-- ... 8 more products
```

**2 Warehouse Locations**:
- Cluj (Romania)
- Bucharest (Romania)

**Stock Entries**:
- 50-300 units per product per location

**3 Users**:
```sql
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES
('admin-uuid', 'Admin', 'User', 'admin@onlineshop.com', '$2a$10$hashedPassword', 'ADMIN'),
('john-uuid', 'John', 'Doe', 'john.doe@email.com', '$2a$10$hashedPassword', 'CUSTOMER'),
('jane-uuid', 'Jane', 'Smith', 'jane.smith@email.com', '$2a$10$hashedPassword', 'CUSTOMER');
```

**2 Sample Orders**:
- John's order: 2 Wireless Headphones + 1 Cotton T-Shirt
- Jane's order: 1 Yoga Mat

### 10.4 Flyway Version Table

Flyway automatically creates `flyway_schema_history` table:

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Columns**:
- `installed_rank` - Sequence number
- `version` - Migration version (1, 1.1, etc.)
- `description` - From filename
- `type` - SQL or Java
- `script` - Filename
- `checksum` - MD5 hash (detects manual changes)
- `installed_on` - Timestamp
- `execution_time` - Duration in milliseconds
- `success` - Boolean

---

## 11. Testing

### 11.1 TestContainers Setup

**Dependency** (pom.xml):
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>
```

**Example: OrderServiceTest** (`/test/integration/OrderServiceTest.java`):

```java
@SpringBootTest
@Testcontainers
class OrderServiceTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
    }
    
    @Test
    void createOrder_withSufficientStock_shouldSucceed() {
        // Arrange
        Order order = createTestOrder();
        
        // Act
        Order result = orderService.createOrder(order, "john.doe@email.com");
        
        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrderDetails()).hasSize(2);
        
        // Verify stock was deducted
        Stock stock = stockRepository.findById(new StockId(productId, locationId)).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(originalQuantity - orderedQuantity);
    }
    
    @Test
    void createOrder_withInsufficientStock_shouldThrowException() {
        // Arrange
        Order order = createOrderWithHighQuantity();
        
        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(order, "john.doe@email.com"))
            .isInstanceOf(OrderNotProcessableException.class)
            .hasMessageContaining("Insufficient stock");
    }
    
    @Test
    void createOrder_withTransactionRollback_shouldNotModifyStock() {
        // Test that partial order failures don't leave inconsistent stock
    }
}
```

### 11.2 Unit Tests (Strategy Pattern)

**Example: SingleLocationStrategyTest**

```java
@ExtendWith(MockitoExtension.class)
class SingleLocationStrategyTest {
    
    @Mock
    private StockRepository stockRepository;
    
    @InjectMocks
    private SingleLocationStrategy strategy;
    
    @Test
    void findStocks_whenSingleLocationHasAllProducts_shouldReturnStocks() {
        // Arrange
        Set<OrderDetail> orderDetails = createTestOrderDetails();
        when(stockRepository.findLocationIdsHavingAllProducts(anyList(), anyLong()))
            .thenReturn(List.of(locationId));
        when(stockRepository.findByLocationIdInAndProductIdIn(anyList(), anyList()))
            .thenReturn(createMockStocks());
        
        // Act
        List<Stock> result = strategy.findStocks(orderDetails);
        
        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(stock -> stock.getLocation().getId().equals(locationId));
    }
    
    @Test
    void findStocks_whenNoLocationHasAllProducts_shouldThrowException() {
        // Arrange
        when(stockRepository.findLocationIdsHavingAllProducts(anyList(), anyLong()))
            .thenReturn(Collections.emptyList());
        
        // Act & Assert
        assertThatThrownBy(() -> strategy.findStocks(orderDetails))
            .isInstanceOf(OrderNotProcessableException.class);
    }
}
```

### 11.3 Controller Tests

Use `@WebMvcTest` for controller-only tests with mocked services.

**Example: ProductControllerTest**

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @MockBean
    private ProductMapper productMapper;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_asAdmin_shouldReturn201() throws Exception {
        // Arrange
        CreateProductRequestDto dto = new CreateProductRequestDto(...);
        Product product = new Product(...);
        when(productService.save(any())).thenReturn(product);
        
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createProduct_asCustomer_shouldReturn403() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isForbidden());
    }
}
```

### 11.4 Test Utilities

**TestSecurityConfig** (`/test/config/TestSecurityConfig.java`):
```java
@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {
    // Enables @PreAuthorize in tests
}
```

**Running Tests**:
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=OrderServiceTest

# Specific test method
mvn test -Dtest=OrderServiceTest#createOrder_withSufficientStock_shouldSucceed

# Skip tests during build
mvn package -DskipTests
```

---

## 12. Configuration

### 12.1 Profiles

**Local Profile** (`application-local.yml`):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/shopdb
    username: shopuser
    password: shoppassword
  
  flyway:
    locations: classpath:db/migration,classpath:db/migration/local
  
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

app:
  cors:
    allowed-origins: http://localhost:4200
  jwt:
    secret: dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbg==
    expiration: 86400000
```

**Test Profile** (`application-test.yml`):
```yaml
spring:
  datasource:
    # Configured via @DynamicPropertySource in tests
  jpa:
    hibernate:
      ddl-auto: create-drop  # Auto-create schema for tests
```

**Production** (`application.yml`):
```yaml
logging:
  level:
    msg.onlineshopapi: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG

server:
  port: 3000
  servlet:
    context-path: /api

spring:
  application:
    name: onlineshopapi
  
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  flyway:
    schemas: public
  
  jpa:
    hibernate:
      ddl-auto: validate

app:
  order:
    strategy: SINGLE_LOCATION  # or MOST_ABUNDANT
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS}
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000  # 24 hours
```

### 12.2 Environment Variables (Production)

Required environment variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_HOST` | PostgreSQL host | `postgres.example.com` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | Database name | `shopdb_prod` |
| `DB_USERNAME` | Database username | `shopuser` |
| `DB_PASSWORD` | Database password | `<strong-password>` |
| `JWT_SECRET` | Base64-encoded signing key | `<base64-secret>` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend URLs | `https://shop.example.com,https://admin.shop.example.com` |

**Generating JWT Secret**:
```bash
# Generate 256-bit key and encode as Base64
openssl rand -base64 32
```

### 12.3 Order Strategy Configuration

**Property**: `app.order.strategy`

**Values**:
- `SINGLE_LOCATION` - Fulfill from one warehouse (default)
- `MOST_ABUNDANT` - Fulfill each product from location with max stock

**Change Strategy**:
1. Update `application.yml` or set environment variable `APP_ORDER_STRATEGY`
2. Restart application

---

## 13. Troubleshooting & Commands

### 13.1 Common Issues

#### Application Won't Start

**Check Database Connection**:
```bash
psql -h localhost -p 5433 -U shopuser -d shopdb
# If connection fails, ensure Docker container is running
docker ps | grep postgres
```

**Check Logs**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
# Look for:
# - Connection refused (database not running)
# - Flyway migration errors
# - Port 3000 already in use
```

**Port Conflict**:
```bash
# Find process using port 3000
lsof -i :3000
# Kill process
kill -9 <PID>
```

#### Flyway Migration Fails

**Check Migration Status**:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Repair Flyway** (if checksum mismatch):
```bash
mvn flyway:repair
```

**Reset Database** (local only):
```bash
# Drop and recreate
psql -h localhost -p 5433 -U shopuser -d postgres
DROP DATABASE shopdb;
CREATE DATABASE shopdb;
\q

# Restart application to re-run migrations
```

#### JWT Authentication Fails

**Check Token Format**:
```bash
# Must be: Bearer <token>
# NOT: <token>
```

**Verify Secret** (local):
```bash
# Check application-local.yml has valid Base64 secret
echo "dGVzdC1zZWNyZXQta2V5..." | base64 -d
```

**Test Login**:
```bash
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@onlineshop.com","password":"password"}'
```

#### Tests Fail

**TestContainers Requires Docker**:
```bash
# Check Docker is running
docker ps
# If not, start Docker Desktop
```

**Clean Build**:
```bash
mvn clean test
```

### 13.2 Maven Commands

```bash
# Run application
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run tests
mvn test

# Run specific test
mvn test -Dtest=OrderServiceTest

# Build JAR
mvn clean package

# Build without tests
mvn package -DskipTests

# Clean build artifacts
mvn clean

# Flyway commands
mvn flyway:migrate
mvn flyway:info
mvn flyway:repair
```

### 13.3 Database Commands

```bash
# Connect to database
psql -h localhost -p 5433 -U shopuser -d shopdb

# Inside psql:
\dt                              # List tables
\d table_name                    # Describe table
\di                              # List indexes
\du                              # List users
SELECT * FROM flyway_schema_history;  # Migration status
SELECT * FROM users;             # Query table
\q                               # Quit
```

### 13.4 Curl Examples

```bash
# Register user
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@onlineshop.com","password":"password"}' \
  | jq -r '.token')

# Get products
curl -X GET http://localhost:3000/api/products \
  -H "Authorization: Bearer $TOKEN"

# Create product (admin only)
curl -X POST http://localhost:3000/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Product","description":"Test","price":29.99,"weight":0.5,"categoryId":"uuid","imageUrl":"/images/test.jpg"}'

# Create order
curl -X POST http://localhost:3000/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"items":[{"productId":"uuid-1","quantity":2}]}'
```

---

## Additional Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Spring Security**: https://spring.io/projects/spring-security
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Flyway**: https://flywaydb.org/documentation/
- **JJWT Library**: https://github.com/jwtk/jjwt
- **Springdoc OpenAPI**: https://springdoc.org/
- **TestContainers**: https://testcontainers.com/
- **Lombok**: https://projectlombok.org/

---

**Swagger UI**: http://localhost:3000/api/swagger-ui/index.html (when application is running)
