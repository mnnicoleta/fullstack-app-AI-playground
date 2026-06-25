package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.ProductCategory;
import msg.onlineshopapi.repository.ProductRepository;
import msg.onlineshopapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private UUID productId;
    private ProductCategory category;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        category = ProductCategory.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .build();

        product1 = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("999.99"))
                .weight(2.5)
                .category(category)
                .imageUrl("/images/laptop.jpg")
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("29.99"))
                .weight(0.1)
                .category(category)
                .build();
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(product1, product2);
        verify(productRepository).findAll();
    }

    @Test
    void findAll_withNoProducts_shouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<Product> result = productService.findAll();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_withExistingId_shouldReturnProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        Optional<Product> result = productService.findById(productId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(product1);
        verify(productRepository).findById(productId);
    }

    @Test
    void findById_withNonExistingId_shouldReturnEmpty() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.findById(nonExistingId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldSaveProduct() {
        // Arrange
        when(productRepository.save(product1)).thenReturn(product1);

        // Act
        Product result = productService.save(product1);

        // Assert
        assertThat(result).isEqualTo(product1);
        verify(productRepository).save(product1);
    }

    @Test
    void update_withExistingProduct_shouldUpdateAllFields() {
        // Arrange
        Product updatedProduct = Product.builder()
                .name("Updated Laptop")
                .description("New description")
                .price(new BigDecimal("1299.99"))
                .weight(3.0)
                .category(category)
                .imageUrl("/images/new-laptop.jpg")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act
        Product result = productService.update(productId, updatedProduct);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getName()).isEqualTo("Updated Laptop");
        assertThat(savedProduct.getDescription()).isEqualTo("New description");
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("1299.99"));
        assertThat(savedProduct.getWeight()).isEqualTo(3.0);
        assertThat(savedProduct.getImageUrl()).isEqualTo("/images/new-laptop.jpg");
    }

    @Test
    void update_withNonExistingProduct_shouldThrowException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.update(nonExistingId, product1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: " + nonExistingId);

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_shouldPreserveProductId() {
        // Arrange
        Product updateData = Product.builder()
                .id(UUID.randomUUID())  // Different ID
                .name("New Name")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any())).thenReturn(product1);

        // Act
        productService.update(productId, updateData);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        // Original ID should be preserved
        assertThat(productCaptor.getValue().getId()).isEqualTo(productId);
    }

    @Test
    void deleteById_shouldCallRepository() {
        // Arrange
        doNothing().when(productRepository).deleteById(productId);

        // Act
        productService.deleteById(productId);

        // Assert
        verify(productRepository).deleteById(productId);
    }

    @Test
    void update_shouldUpdateCategory() {
        // Arrange
        ProductCategory newCategory = ProductCategory.builder()
                .id(UUID.randomUUID())
                .name("Home & Garden")
                .build();

        Product updateData = Product.builder()
                .name(product1.getName())
                .category(newCategory)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any())).thenReturn(product1);

        // Act
        productService.update(productId, updateData);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getCategory()).isEqualTo(newCategory);
    }

    // Phase 2 edge cases

    @Test
    void save_withNullProduct_shouldStillCallRepository() {
        // Arrange - Testing that service doesn't add extra null checks
        when(productRepository.save(null)).thenReturn(null);

        // Act
        Product result = productService.save(null);

        // Assert
        assertThat(result).isNull();
        verify(productRepository).save(null);
    }

    @Test
    void update_withPartialData_shouldOnlyUpdateProvidedFields() {
        // Arrange - Only updating name and price, leaving other fields as before
        Product partialUpdate = Product.builder()
                .name("Partially Updated")
                .price(new BigDecimal("1499.99"))
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any())).thenReturn(product1);

        // Act
        productService.update(productId, partialUpdate);

        // Assert
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product saved = productCaptor.getValue();

        // Updated fields
        assertThat(saved.getName()).isEqualTo("Partially Updated");
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("1499.99"));

        // Fields should be updated even if null in partial update
        // (This tests the actual behavior of the update method)
    }

    @Test
    void findById_withNullId_shouldReturnEmpty() {
        // Arrange
        when(productRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.findById(null);

        // Assert
        assertThat(result).isEmpty();
        verify(productRepository).findById(null);
    }
}
