package msg.onlineshopapi.integration;

import msg.onlineshopapi.exception.OrderNotProcessableException;
import msg.onlineshopapi.model.*;
import msg.onlineshopapi.repository.*;
import msg.onlineshopapi.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * Integration tests for OrderService.
 * Extends BaseIntegrationTest to use shared PostgreSQL container.
 */
class OrderServiceTest extends BaseIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Product laptop;
    private Location location;
    private User user;
    private Supplier supplier;
    private StockId stockId;

    @BeforeEach
    void setUp() {
        ProductCategory category = new ProductCategory();
        category.setName("Electronics");
        category = productCategoryRepository.save(category);

        // Create test supplier (required for products)
        supplier = Supplier.builder()
                .name("Test Supplier")
                .description("Supplier for test products")
                .contactEmail("test@supplier.com")
                .build();
        supplier = supplierRepository.save(supplier);

        laptop = Product.builder()
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .category(category)
                .supplier(supplier)  // Add required supplier
                .build();
        laptop = productRepository.save(laptop);

        location = new Location();
        location.setName("Warehouse A");
        location = locationRepository.save(location);

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .password("encodedPassword")
                .role(UserRole.CUSTOMER)
                .build();
        user = userRepository.save(user);

        stockId = new StockId();
        stockId.setProductId(laptop.getId());
        stockId.setLocationId(location.getId());
    }

    @AfterEach
    void cleanUp() {
        orderDetailRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        locationRepository.deleteAllInBatch();
        productCategoryRepository.deleteAllInBatch();
    }

    @Test
    void createOrder_succeeds_whenStockIsSufficient() {
        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(10)
                .build());

        OrderDetail detail = OrderDetail.builder()
                .product(laptop)
                .quantity(3)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(detail)))
                .build();

        Order result = orderService.createOrder(order, user.getEmail());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getOrderDetails()).hasSize(1);

        Stock updatedStock = stockRepository.findById(stockId).orElseThrow();
        assertThat(updatedStock.getQuantity()).isEqualTo(7);
    }

    @Test
    void createOrder_throwsOrderNotProcessableException_whenStockIsInsufficient() {
        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(2)
                .build());

        OrderDetail detail = OrderDetail.builder()
                .product(laptop)
                .quantity(5)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(detail)))
                .build();

        assertThrowsExactly(OrderNotProcessableException.class, () -> orderService.createOrder(order, user.getEmail()));
    }

    @Test
    void createOrder_succeeds_withMultipleProducts() {
        ProductCategory category = productCategoryRepository.findAll().getFirst();

        Product mouse = Product.builder()
                .name("Mouse")
                .price(BigDecimal.valueOf(29.99))
                .category(category)
                .supplier(supplier)  // Add required supplier
                .build();
        mouse = productRepository.save(mouse);

        StockId mouseStockId = new StockId();
        mouseStockId.setProductId(mouse.getId());
        mouseStockId.setLocationId(location.getId());

        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(10)
                .build());
        stockRepository.save(Stock.builder()
                .id(mouseStockId)
                .product(mouse)
                .location(location)
                .quantity(5)
                .build());

        OrderDetail laptopDetail = OrderDetail.builder()
                .product(laptop)
                .quantity(2)
                .build();
        OrderDetail mouseDetail = OrderDetail.builder()
                .product(mouse)
                .quantity(3)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(laptopDetail, mouseDetail)))
                .build();

        Order result = orderService.createOrder(order, user.getEmail());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getOrderDetails()).hasSize(2);

        Stock updatedLaptopStock = stockRepository.findById(stockId).orElseThrow();
        Stock updatedMouseStock = stockRepository.findById(mouseStockId).orElseThrow();
        assertThat(updatedLaptopStock.getQuantity()).isEqualTo(8);
        assertThat(updatedMouseStock.getQuantity()).isEqualTo(2);
    }

    @Test
    void createOrder_throwsAndStockUnchanged_whenOneProductUnavailable() {
        ProductCategory category = productCategoryRepository.findAll().getFirst();

        Product keyboard = Product.builder()
                .name("Keyboard")
                .price(BigDecimal.valueOf(49.99))
                .category(category)
                .supplier(supplier)  // Add required supplier
                .build();
        keyboard = productRepository.save(keyboard);

        StockId keyboardStockId = new StockId();
        keyboardStockId.setProductId(keyboard.getId());
        keyboardStockId.setLocationId(location.getId());

        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(10)
                .build());
        stockRepository.save(Stock.builder()
                .id(keyboardStockId)
                .product(keyboard)
                .location(location)
                .quantity(1)
                .build());

        OrderDetail laptopDetail = OrderDetail.builder()
                .product(laptop)
                .quantity(2)
                .build();
        OrderDetail keyboardDetail = OrderDetail.builder()
                .product(keyboard)
                .quantity(5)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(laptopDetail, keyboardDetail)))
                .build();

        assertThrowsExactly(OrderNotProcessableException.class, () -> orderService.createOrder(order, user.getEmail()));

        Stock unchangedLaptopStock = stockRepository.findById(stockId).orElseThrow();
        Stock unchangedKeyboardStock = stockRepository.findById(keyboardStockId).orElseThrow();
        assertThat(unchangedLaptopStock.getQuantity()).isEqualTo(10);
        assertThat(unchangedKeyboardStock.getQuantity()).isEqualTo(1);
    }

    @Test
    void createOrder_succeeds_withCompleteAddress() {
        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(10)
                .build());

        Address address = Address.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street, Apt 4B")
                .build();

        OrderDetail detail = OrderDetail.builder()
                .product(laptop)
                .quantity(2)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(detail)))
                .address(address)
                .build();

        Order result = orderService.createOrder(order, user.getEmail());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getCountry()).isEqualTo("Romania");
        assertThat(result.getAddress().getCity()).isEqualTo("Cluj-Napoca");
        assertThat(result.getAddress().getCounty()).isEqualTo("Cluj");
        assertThat(result.getAddress().getStreetAddress()).isEqualTo("123 Main Street, Apt 4B");

        // Verify address persisted to database
        Order savedOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertThat(savedOrder.getAddress()).isNotNull();
        assertThat(savedOrder.getAddress().getCountry()).isEqualTo("Romania");
        assertThat(savedOrder.getAddress().getCity()).isEqualTo("Cluj-Napoca");
        assertThat(savedOrder.getAddress().getCounty()).isEqualTo("Cluj");
        assertThat(savedOrder.getAddress().getStreetAddress()).isEqualTo("123 Main Street, Apt 4B");
    }

    @Test
    void createOrder_succeeds_withNullAddress_forBackwardCompatibility() {
        stockRepository.save(Stock.builder()
                .id(stockId)
                .product(laptop)
                .location(location)
                .quantity(10)
                .build());

        OrderDetail detail = OrderDetail.builder()
                .product(laptop)
                .quantity(1)
                .build();
        Order order = Order.builder()
                .orderDetails(new HashSet<>(Set.of(detail)))
                .address(null)  // No address provided
                .build();

        Order result = orderService.createOrder(order, user.getEmail());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getAddress()).isNull();

        // Verify NULL address persisted to database
        Order savedOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertThat(savedOrder.getAddress()).isNull();
    }
}
