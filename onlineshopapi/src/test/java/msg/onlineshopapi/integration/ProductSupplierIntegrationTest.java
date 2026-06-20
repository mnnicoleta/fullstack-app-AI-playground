package msg.onlineshopapi.integration;

import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.ProductCategory;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.ProductCategoryRepository;
import msg.onlineshopapi.repository.ProductRepository;
import msg.onlineshopapi.repository.SupplierRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for Product-Supplier relationship.
 * Tests database constraints, JPA lazy loading, and relationship integrity.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class ProductSupplierIntegrationTest {

    private static final String POSTGRES_IMAGE = "postgres:18";

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(POSTGRES_IMAGE);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private msg.onlineshopapi.repository.StockRepository stockRepository;

    @Autowired
    private msg.onlineshopapi.repository.OrderDetailRepository orderDetailRepository;

    @Autowired
    private msg.onlineshopapi.repository.OrderRepository orderRepository;

    private Supplier testSupplier;
    private ProductCategory testCategory;

    @BeforeEach
    void setUp() {
        // Clean all data before each test to ensure isolation
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();
        productCategoryRepository.deleteAll();

        testSupplier = Supplier.builder()
                .name("Test Supplier")
                .description("Test Description")
                .build();
        testSupplier = supplierRepository.save(testSupplier);

        testCategory = new ProductCategory();
        testCategory.setName("Test Category");
        testCategory = productCategoryRepository.save(testCategory);
    }

    @AfterEach
    void cleanUp() {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();
        productCategoryRepository.deleteAll();
    }

    @Test
    void createProduct_withValidSupplierId_shouldPersistRelationship() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .weight(1.0)
                .category(testCategory)
                .supplier(testSupplier)
                .imageUrl("http://example.com/image.jpg")
                .build();

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getSupplier()).isNotNull();
        assertThat(savedProduct.getSupplier().getId()).isEqualTo(testSupplier.getId());
    }

    @Test
    void findProduct_shouldLazyLoadSupplier() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(49.99))
                .category(testCategory)
                .supplier(testSupplier)
                .build();
        Product savedProduct = productRepository.save(product);

        // Clear persistence context to force lazy loading
        productRepository.flush();

        // Act
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // Assert
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getSupplier()).isNotNull();
        assertThat(foundProduct.getSupplier().getName()).isEqualTo("Test Supplier");
    }

    @Test
    void updateProduct_toDifferentSupplier_shouldUpdateRelationship() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(29.99))
                .category(testCategory)
                .supplier(testSupplier)
                .build();
        Product savedProduct = productRepository.save(product);

        Supplier newSupplier = Supplier.builder()
                .name("New Supplier")
                .description("New Description")
                .build();
        newSupplier = supplierRepository.save(newSupplier);

        // Act
        savedProduct.setSupplier(newSupplier);
        Product updatedProduct = productRepository.save(savedProduct);

        // Assert
        assertThat(updatedProduct.getSupplier().getId()).isEqualTo(newSupplier.getId());
        assertThat(updatedProduct.getSupplier().getName()).isEqualTo("New Supplier");
    }

    @Test
    void findProductById_shouldIncludeSupplierInResponse() {
        // Arrange
        Product product = Product.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(1299.99))
                .weight(2.5)
                .category(testCategory)
                .supplier(testSupplier)
                .imageUrl("http://example.com/laptop.jpg")
                .build();
        Product savedProduct = productRepository.save(product);

        // Act
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        // Assert
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(foundProduct.getSupplier()).isNotNull();
        assertThat(foundProduct.getSupplier().getId()).isEqualTo(testSupplier.getId());
        assertThat(foundProduct.getSupplier().getName()).isEqualTo("Test Supplier");
        assertThat(foundProduct.getSupplier().getDescription()).isEqualTo("Test Description");
    }

    @Test
    void createProduct_withNullSupplier_shouldViolateConstraint() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(19.99))
                .category(testCategory)
                .supplier(null)  // NULL supplier
                .build();

        // Act & Assert
        assertThatThrownBy(() -> productRepository.save(product))
                .hasMessageContaining("null")
                .hasMessageContaining("supplier_id");
    }

    @Test
    void findAllProducts_shouldIncludeSuppliersForAllProducts() {
        // Arrange
        Supplier supplier2 = Supplier.builder()
                .name("Supplier 2")
                .build();
        supplier2 = supplierRepository.save(supplier2);

        Product product1 = Product.builder()
                .name("Product 1")
                .price(BigDecimal.valueOf(10.00))
                .category(testCategory)
                .supplier(testSupplier)
                .build();
        Product product2 = Product.builder()
                .name("Product 2")
                .price(BigDecimal.valueOf(20.00))
                .category(testCategory)
                .supplier(supplier2)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // Act
        var products = productRepository.findAll();

        // Assert
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getSupplier()).isNotNull();
        assertThat(products.get(1).getSupplier()).isNotNull();
        assertThat(products.get(0).getSupplier().getName()).isEqualTo("Test Supplier");
        assertThat(products.get(1).getSupplier().getName()).isEqualTo("Supplier 2");
    }

    @Test
    void deleteSupplier_withReferencingProducts_shouldRespectForeignKeyConstraint() {
        // Arrange
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(5.00))
                .category(testCategory)
                .supplier(testSupplier)
                .build();
        productRepository.save(product);

        // Act & Assert - Attempting to delete supplier with products should fail
        assertThatThrownBy(() -> {
            supplierRepository.deleteById(testSupplier.getId());
            supplierRepository.flush();  // Force the delete to execute
        }).hasMessageContaining("foreign key");
    }
}
