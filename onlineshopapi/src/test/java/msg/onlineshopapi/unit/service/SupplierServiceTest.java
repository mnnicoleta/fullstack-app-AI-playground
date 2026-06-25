package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.SupplierRepository;
import msg.onlineshopapi.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SupplierService.
 * Uses Mockito to mock repository layer.
 * <p>
 * Note: These tests are excluded from execution due to Java 25 incompatibility.
 * See pom.xml surefire configuration.
 */
@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier testSupplier;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testSupplier = Supplier.builder()
                .id(testId)
                .name("Test Supplier")
                .description("Test Description")
                .contactEmail("test@supplier.com")
                .contactPhone("+1-555-0000")
                .address("123 Test Street")
                .build();
    }

    @Test
    void findAll_shouldReturnAllSuppliers() {
        // Arrange
        Supplier supplier2 = Supplier.builder()
                .id(UUID.randomUUID())
                .name("Another Supplier")
                .build();
        List<Supplier> suppliers = Arrays.asList(testSupplier, supplier2);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // Act
        List<Supplier> result = supplierService.findAll();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(testSupplier, supplier2);
        verify(supplierRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoSuppliers() {
        // Arrange
        when(supplierRepository.findAll()).thenReturn(List.of());

        // Act
        List<Supplier> result = supplierService.findAll();

        // Assert
        assertThat(result).isEmpty();
        verify(supplierRepository).findAll();
    }

    @Test
    void findById_shouldReturnSupplier_whenExists() {
        // Arrange
        when(supplierRepository.findById(testId)).thenReturn(Optional.of(testSupplier));

        // Act
        Optional<Supplier> result = supplierService.findById(testId);

        // Assert
        assertThat(result)
                .isPresent()
                .get().isEqualTo(testSupplier);
        verify(supplierRepository).findById(testId);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Supplier> result = supplierService.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
        verify(supplierRepository).findById(nonExistentId);
    }

    @Test
    void save_shouldPersistSupplier() {
        // Arrange
        Supplier newSupplier = Supplier.builder()
                .name("New Supplier")
                .description("New Description")
                .build();
        Supplier savedSupplier = Supplier.builder()
                .id(UUID.randomUUID())
                .name("New Supplier")
                .description("New Description")
                .build();
        when(supplierRepository.save(newSupplier)).thenReturn(savedSupplier);

        // Act
        Supplier result = supplierService.save(newSupplier);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Supplier");
        verify(supplierRepository).save(newSupplier);
    }

    @Test
    void update_shouldModifySupplier_whenExists() {
        // Arrange
        Supplier updateData = Supplier.builder()
                .name("Updated Name")
                .description("Updated Description")
                .contactEmail("updated@supplier.com")
                .contactPhone("+1-555-9999")
                .address("999 Updated St")
                .build();

        when(supplierRepository.findById(testId)).thenReturn(Optional.of(testSupplier));
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Supplier result = supplierService.update(testId, updateData);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getContactEmail()).isEqualTo("updated@supplier.com");
        assertThat(result.getContactPhone()).isEqualTo("+1-555-9999");
        assertThat(result.getAddress()).isEqualTo("999 Updated St");
        verify(supplierRepository).findById(testId);
        verify(supplierRepository).save(testSupplier);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        Supplier updateData = Supplier.builder().name("Updated").build();
        when(supplierRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> supplierService.update(nonExistentId, updateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: " + nonExistentId);
        verify(supplierRepository).findById(nonExistentId);
        verify(supplierRepository, never()).save(any());
    }

    @Test
    void deleteById_shouldRemoveSupplier_whenExists() {
        // Arrange
        when(supplierRepository.existsById(testId)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(testId);

        // Act
        supplierService.deleteById(testId);

        // Assert
        verify(supplierRepository).existsById(testId);
        verify(supplierRepository).deleteById(testId);
    }

    @Test
    void deleteById_shouldThrowException_whenNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(supplierRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> supplierService.deleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Supplier not found with id: " + nonExistentId);
        verify(supplierRepository).existsById(nonExistentId);
        verify(supplierRepository, never()).deleteById(any());
    }
}
