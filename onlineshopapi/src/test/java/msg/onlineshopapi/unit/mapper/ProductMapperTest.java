package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.ProductCategoryDto;
import msg.onlineshopapi.dto.ProductResponseDto;
import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.dto.mapper.ProductCategoryMapper;
import msg.onlineshopapi.dto.mapper.ProductMapper;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.ProductCategory;
import msg.onlineshopapi.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductMapper.
 */
class ProductMapperTest {

    private ProductMapper productMapper;
    private ProductCategoryMapper categoryMapper;
    private SupplierMapper supplierMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new ProductCategoryMapper();
        supplierMapper = new SupplierMapper();
        productMapper = new ProductMapper(categoryMapper, supplierMapper);
    }

    @Test
    void toDto_shouldMapAllFields() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();

        ProductCategory category = ProductCategory.builder()
                .id(categoryId)
                .name("Electronics")
                .description("Electronic devices")
                .build();

        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .description("Supplier desc")
                .contactEmail("supplier@test.com")
                .contactPhone("+1234567890")
                .address("123 Supplier St")
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(999.99))
                .weight(2.5)
                .category(category)
                .supplier(supplier)
                .imageUrl("/images/laptop.jpg")
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getId()).isEqualTo(productId);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getDescription()).isEqualTo("High-performance laptop");
        assertThat(dto.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(999.99));
        assertThat(dto.getWeight()).isEqualTo(2.5);
        assertThat(dto.getImageUrl()).isEqualTo("/images/laptop.jpg");

        assertThat(dto.getCategory()).isNotNull();
        assertThat(dto.getCategory().getId()).isEqualTo(categoryId);
        assertThat(dto.getCategory().getName()).isEqualTo("Electronics");

        assertThat(dto.getSupplier()).isNotNull();
        assertThat(dto.getSupplier().getId()).isEqualTo(supplierId);
        assertThat(dto.getSupplier().getName()).isEqualTo("Test Supplier");
    }

    @Test
    void toDto_withNullCategory_shouldMapOtherFields() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();

        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(999.99))
                .weight(2.5)
                .category(null)
                .supplier(supplier)
                .imageUrl("/images/laptop.jpg")
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getId()).isEqualTo(productId);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getCategory()).isNull();
        assertThat(dto.getSupplier()).isNotNull();
    }

    @Test
    void toDto_withNullSupplier_shouldMapOtherFields() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = ProductCategory.builder()
                .id(categoryId)
                .name("Electronics")
                .build();

        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("High-performance laptop")
                .price(BigDecimal.valueOf(999.99))
                .weight(2.5)
                .category(category)
                .supplier(null)
                .imageUrl("/images/laptop.jpg")
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getId()).isEqualTo(productId);
        assertThat(dto.getName()).isEqualTo("Laptop");
        assertThat(dto.getCategory()).isNotNull();
        assertThat(dto.getSupplier()).isNull();
    }

    @Test
    void toDto_withNullOptionalFields_shouldHandleGracefully() {
        // Arrange
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
                .id(productId)
                .name("Basic Product")
                .description(null)
                .price(BigDecimal.valueOf(10.00))
                .weight(null)
                .category(null)
                .supplier(null)
                .imageUrl(null)
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getId()).isEqualTo(productId);
        assertThat(dto.getName()).isEqualTo("Basic Product");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getWeight()).isNull();
        assertThat(dto.getCategory()).isNull();
        assertThat(dto.getSupplier()).isNull();
        assertThat(dto.getImageUrl()).isNull();
    }

    @Test
    void toDto_withZeroPrice_shouldMapCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Free Item")
                .price(BigDecimal.ZERO)
                .weight(0.0)
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dto.getWeight()).isEqualTo(0.0);
    }

    @Test
    void toDto_withVeryLargePrecisionPrice_shouldPreservePrecision() {
        // Arrange
        BigDecimal precisePrice = new BigDecimal("999.999999");

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Precise Item")
                .price(precisePrice)
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert
        assertThat(dto.getPrice()).isEqualByComparingTo(precisePrice);
    }

    @Test
    void toDto_categoryMapping_shouldDelegateToProductCategoryMapper() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        ProductCategory category = ProductCategory.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .build();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product")
                .price(BigDecimal.TEN)
                .category(category)
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert - Verify category was properly delegated to ProductCategoryMapper
        ProductCategoryDto categoryDto = dto.getCategory();
        assertThat(categoryDto).isNotNull();
        assertThat(categoryDto.getId()).isEqualTo(categoryId);
        assertThat(categoryDto.getName()).isEqualTo("Test Category");
        assertThat(categoryDto.getDescription()).isEqualTo("Test Description");
    }

    @Test
    void toDto_supplierMapping_shouldDelegateToSupplierMapper() {
        // Arrange
        UUID supplierId = UUID.randomUUID();

        Supplier supplier = Supplier.builder()
                .id(supplierId)
                .name("Test Supplier")
                .description("Test Supplier Desc")
                .contactEmail("test@supplier.com")
                .contactPhone("+1234567890")
                .address("123 Test St")
                .build();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Product")
                .price(BigDecimal.TEN)
                .supplier(supplier)
                .build();

        // Act
        ProductResponseDto dto = productMapper.toDto(product);

        // Assert - Verify supplier was properly delegated to SupplierMapper
        SupplierDto supplierDto = dto.getSupplier();
        assertThat(supplierDto).isNotNull();
        assertThat(supplierDto.getId()).isEqualTo(supplierId);
        assertThat(supplierDto.getName()).isEqualTo("Test Supplier");
        assertThat(supplierDto.getDescription()).isEqualTo("Test Supplier Desc");
        assertThat(supplierDto.getContactEmail()).isEqualTo("test@supplier.com");
        assertThat(supplierDto.getContactPhone()).isEqualTo("+1234567890");
        assertThat(supplierDto.getAddress()).isEqualTo("123 Test St");
    }
}
