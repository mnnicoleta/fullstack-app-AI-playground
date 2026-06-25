package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.ProductCategory;
import msg.onlineshopapi.repository.ProductCategoryRepository;
import msg.onlineshopapi.service.ProductCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductCategoryService.
 */
@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductCategoryService productCategoryService;

    private ProductCategory category1;
    private ProductCategory category2;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category1 = ProductCategory.builder()
                .id(categoryId)
                .name("Electronics")
                .description("Electronic devices and accessories")
                .build();

        category2 = ProductCategory.builder()
                .id(UUID.randomUUID())
                .name("Clothing")
                .description("Apparel and fashion items")
                .build();
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        // Arrange
        when(productCategoryRepository.findAll()).thenReturn(List.of(category1, category2));

        // Act
        List<ProductCategory> result = productCategoryService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(category1, category2);
        verify(productCategoryRepository).findAll();
    }

    @Test
    void findAll_withNoCategories_shouldReturnEmptyList() {
        // Arrange
        when(productCategoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductCategory> result = productCategoryService.findAll();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_withExistingId_shouldReturnCategory() {
        // Arrange
        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));

        // Act
        Optional<ProductCategory> result = productCategoryService.findById(categoryId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(category1);
        verify(productCategoryRepository).findById(categoryId);
    }

    @Test
    void findById_withNonExistingId_shouldReturnEmpty() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(productCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act
        Optional<ProductCategory> result = productCategoryService.findById(nonExistingId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldSaveCategory() {
        // Arrange
        when(productCategoryRepository.save(category1)).thenReturn(category1);

        // Act
        ProductCategory result = productCategoryService.save(category1);

        // Assert
        assertThat(result).isEqualTo(category1);
        verify(productCategoryRepository).save(category1);
    }

    @Test
    void update_withExistingCategory_shouldUpdateFields() {
        // Arrange
        ProductCategory updatedCategory = ProductCategory.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(category1);

        // Act
        ProductCategory result = productCategoryService.update(categoryId, updatedCategory);

        // Assert
        ArgumentCaptor<ProductCategory> categoryCaptor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(productCategoryRepository).save(categoryCaptor.capture());

        ProductCategory savedCategory = categoryCaptor.getValue();
        assertThat(savedCategory.getName()).isEqualTo("Updated Electronics");
        assertThat(savedCategory.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void update_withNonExistingCategory_shouldThrowException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(productCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productCategoryService.update(nonExistingId, category1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product category not found with id: " + nonExistingId);

        verify(productCategoryRepository, never()).save(any());
    }

    @Test
    void update_shouldPreserveCategoryId() {
        // Arrange
        ProductCategory updateData = ProductCategory.builder()
                .id(UUID.randomUUID())  // Different ID
                .name("New Name")
                .build();

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));
        when(productCategoryRepository.save(any())).thenReturn(category1);

        // Act
        productCategoryService.update(categoryId, updateData);

        // Assert
        ArgumentCaptor<ProductCategory> categoryCaptor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(productCategoryRepository).save(categoryCaptor.capture());
        // Original ID should be preserved
        assertThat(categoryCaptor.getValue().getId()).isEqualTo(categoryId);
    }

    @Test
    void deleteById_shouldCallRepository() {
        // Arrange
        doNothing().when(productCategoryRepository).deleteById(categoryId);

        // Act
        productCategoryService.deleteById(categoryId);

        // Assert
        verify(productCategoryRepository).deleteById(categoryId);
    }

    @Test
    void update_withNullDescription_shouldUpdateToNull() {
        // Arrange
        ProductCategory updateData = ProductCategory.builder()
                .name("Updated Name")
                .description(null)
                .build();

        when(productCategoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));
        when(productCategoryRepository.save(any())).thenReturn(category1);

        // Act
        productCategoryService.update(categoryId, updateData);

        // Assert
        ArgumentCaptor<ProductCategory> categoryCaptor = ArgumentCaptor.forClass(ProductCategory.class);
        verify(productCategoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getDescription()).isNull();
    }
}
