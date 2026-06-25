package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SupplierMapper.
 * Tests entity-to-DTO and DTO-to-entity conversions.
 */
class SupplierMapperTest {

    private SupplierMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SupplierMapper();
    }

    @Test
    void toDto_withValidEntity_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Supplier supplier = Supplier.builder()
                .id(id)
                .name("TechVision Electronics")
                .description("Leading supplier of consumer electronics")
                .contactEmail("contact@techvision.com")
                .contactPhone("+1-555-0101")
                .address("123 Tech Street, San Francisco, CA")
                .build();

        // Act
        SupplierDto dto = mapper.toDto(supplier);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("TechVision Electronics");
        assertThat(dto.getDescription()).isEqualTo("Leading supplier of consumer electronics");
        assertThat(dto.getContactEmail()).isEqualTo("contact@techvision.com");
        assertThat(dto.getContactPhone()).isEqualTo("+1-555-0101");
        assertThat(dto.getAddress()).isEqualTo("123 Tech Street, San Francisco, CA");
    }

    @Test
    void toDto_withNullEntity_shouldReturnNull() {
        // Act
        SupplierDto dto = mapper.toDto(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void toDto_withMinimalFields_shouldHandleNullOptionalFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Supplier supplier = Supplier.builder()
                .id(id)
                .name("Minimal Supplier")
                .description(null)
                .contactEmail(null)
                .contactPhone(null)
                .address(null)
                .build();

        // Act
        SupplierDto dto = mapper.toDto(supplier);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Minimal Supplier");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getContactEmail()).isNull();
        assertThat(dto.getContactPhone()).isNull();
        assertThat(dto.getAddress()).isNull();
    }

    @Test
    void toEntity_withValidDto_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        SupplierDto dto = SupplierDto.builder()
                .id(id)
                .name("GlobalThreads Textiles")
                .description("International textile distributor")
                .contactEmail("sales@globalthreads.com")
                .contactPhone("+1-555-0202")
                .address("456 Fashion Avenue, New York, NY")
                .build();

        // Act
        Supplier entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("GlobalThreads Textiles");
        assertThat(entity.getDescription()).isEqualTo("International textile distributor");
        assertThat(entity.getContactEmail()).isEqualTo("sales@globalthreads.com");
        assertThat(entity.getContactPhone()).isEqualTo("+1-555-0202");
        assertThat(entity.getAddress()).isEqualTo("456 Fashion Avenue, New York, NY");
    }

    @Test
    void toEntity_withNullDto_shouldReturnNull() {
        // Act
        Supplier entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_withMinimalDto_shouldHandleNullOptionalFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        SupplierDto dto = SupplierDto.builder()
                .id(id)
                .name("Minimal Supplier DTO")
                .description(null)
                .contactEmail(null)
                .contactPhone(null)
                .address(null)
                .build();

        // Act
        Supplier entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Minimal Supplier DTO");
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getContactEmail()).isNull();
        assertThat(entity.getContactPhone()).isNull();
        assertThat(entity.getAddress()).isNull();
    }

    @Test
    void roundTrip_entityToDtoToEntity_shouldPreserveData() {
        // Arrange
        UUID id = UUID.randomUUID();
        Supplier originalEntity = Supplier.builder()
                .id(id)
                .name("Test Supplier")
                .description("Test Description")
                .contactEmail("test@supplier.com")
                .contactPhone("+1-555-TEST")
                .address("123 Test St")
                .build();

        // Act
        SupplierDto dto = mapper.toDto(originalEntity);
        Supplier roundTripEntity = mapper.toEntity(dto);

        // Assert
        assertThat(roundTripEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(roundTripEntity.getName()).isEqualTo(originalEntity.getName());
        assertThat(roundTripEntity.getDescription()).isEqualTo(originalEntity.getDescription());
        assertThat(roundTripEntity.getContactEmail()).isEqualTo(originalEntity.getContactEmail());
        assertThat(roundTripEntity.getContactPhone()).isEqualTo(originalEntity.getContactPhone());
        assertThat(roundTripEntity.getAddress()).isEqualTo(originalEntity.getAddress());
    }
}
