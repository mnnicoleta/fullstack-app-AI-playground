package msg.onlineshopapi.integration;

import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.SupplierRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for SupplierController.
 * Uses TestContainers with PostgreSQL and direct service calls.
 * Tests full stack: Service -> Repository -> Database.
 * Extends BaseIntegrationTest to use shared PostgreSQL container.
 */
class SupplierControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private msg.onlineshopapi.repository.ProductRepository productRepository;

    @Autowired
    private msg.onlineshopapi.repository.StockRepository stockRepository;

    @Autowired
    private msg.onlineshopapi.repository.OrderDetailRepository orderDetailRepository;

    @Autowired
    private msg.onlineshopapi.repository.OrderRepository orderRepository;

    @Autowired
    private msg.onlineshopapi.service.SupplierService supplierService;

    @Autowired
    private msg.onlineshopapi.dto.mapper.SupplierMapper supplierMapper;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        // Clean all data before each test
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();

        testSupplier = Supplier.builder()
                .name("Test Supplier")
                .description("Test Description")
                .contactEmail("test@supplier.com")
                .contactPhone("+1-555-0000")
                .address("123 Test Street")
                .build();
        testSupplier = supplierRepository.save(testSupplier);
    }

    @AfterEach
    void cleanUp() {
        orderDetailRepository.deleteAll();
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    void findAll_shouldReturnAllSuppliers() {
        // Arrange
        Supplier supplier2 = Supplier.builder()
                .name("Another Supplier")
                .description("Another Description")
                .build();
        supplierRepository.save(supplier2);

        // Act
        List<Supplier> suppliers = supplierService.findAll();

        // Assert
        assertThat(suppliers).hasSize(2);
        assertThat(suppliers.get(0).getName()).isEqualTo("Test Supplier");
        assertThat(suppliers.get(1).getName()).isEqualTo("Another Supplier");
    }

    @Test
    void findById_shouldReturnSupplier_whenExists() {
        // Act
        var result = supplierService.findById(testSupplier.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testSupplier.getId());
        assertThat(result.get().getName()).isEqualTo("Test Supplier");
        assertThat(result.get().getDescription()).isEqualTo("Test Description");
        assertThat(result.get().getContactEmail()).isEqualTo("test@supplier.com");
        assertThat(result.get().getContactPhone()).isEqualTo("+1-555-0000");
        assertThat(result.get().getAddress()).isEqualTo("123 Test Street");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        var result = supplierService.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistSupplier() {
        // Arrange
        Supplier newSupplier = Supplier.builder()
                .name("New Supplier")
                .description("New Description")
                .contactEmail("new@supplier.com")
                .contactPhone("+1-555-1111")
                .address("456 New Street")
                .build();

        // Act
        Supplier saved = supplierService.save(newSupplier);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Supplier");

        // Verify in database
        Supplier found = supplierRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("New Supplier");
    }

    @Test
    void update_shouldModifySupplier_whenExists() {
        // Arrange
        Supplier updateData = Supplier.builder()
                .name("Updated Name")
                .description("Updated Description")
                .contactEmail("updated@supplier.com")
                .contactPhone("+1-555-9999")
                .address("999 Updated Street")
                .build();

        // Act
        Supplier updated = supplierService.update(testSupplier.getId(), updateData);

        // Assert
        assertThat(updated.getId()).isEqualTo(testSupplier.getId());
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");

        // Verify in database
        Supplier found = supplierRepository.findById(testSupplier.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Updated Name");
    }

    @Test
    void deleteById_shouldRemoveSupplier_whenExists() {
        // Act
        supplierService.deleteById(testSupplier.getId());

        // Assert - Verify in database
        boolean exists = supplierRepository.existsById(testSupplier.getId());
        assertThat(exists).isFalse();
    }
}
