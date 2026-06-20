package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.ProductCategoryDto;
import msg.onlineshopapi.dto.mapper.ProductCategoryMapper;
import msg.onlineshopapi.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductCategoryMapper.
 */
class ProductCategoryMapperTest {

    private ProductCategoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductCategoryMapper();
    }

    @Test
    void toDto_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategory entity = ProductCategory.builder()
                .id(id)
                .name("Electronics")
                .description("Electronic devices and gadgets")
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Electronics");
        assertThat(dto.getDescription()).isEqualTo("Electronic devices and gadgets");
    }

    @Test
    void toDto_withNullDescription_shouldHandleGracefully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategory entity = ProductCategory.builder()
                .id(id)
                .name("Electronics")
                .description(null)
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Electronics");
        assertThat(dto.getDescription()).isNull();
    }

    @Test
    void toDto_withEmptyDescription_shouldMapEmptyString() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategory entity = ProductCategory.builder()
                .id(id)
                .name("Electronics")
                .description("")
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(entity);

        // Assert
        assertThat(dto.getDescription()).isEmpty();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategoryDto dto = ProductCategoryDto.builder()
                .id(id)
                .name("Electronics")
                .description("Electronic devices and gadgets")
                .build();

        // Act
        ProductCategory entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Electronics");
        assertThat(entity.getDescription()).isEqualTo("Electronic devices and gadgets");
    }

    @Test
    void toEntity_withNullDescription_shouldHandleGracefully() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategoryDto dto = ProductCategoryDto.builder()
                .id(id)
                .name("Electronics")
                .description(null)
                .build();

        // Act
        ProductCategory entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Electronics");
        assertThat(entity.getDescription()).isNull();
    }

    @Test
    void roundTrip_shouldPreserveData() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductCategory original = ProductCategory.builder()
                .id(id)
                .name("Books")
                .description("Books and publications")
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(original);
        ProductCategory roundTrip = mapper.toEntity(dto);

        // Assert
        assertThat(roundTrip.getId()).isEqualTo(original.getId());
        assertThat(roundTrip.getName()).isEqualTo(original.getName());
        assertThat(roundTrip.getDescription()).isEqualTo(original.getDescription());
    }

    @Test
    void toDto_withVeryLongName_shouldMapCorrectly() {
        // Arrange
        String longName = "A".repeat(255);
        ProductCategory entity = ProductCategory.builder()
                .id(UUID.randomUUID())
                .name(longName)
                .description("Test")
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(entity);

        // Assert
        assertThat(dto.getName()).isEqualTo(longName);
        assertThat(dto.getName()).hasSize(255);
    }

    @Test
    void toDto_withSpecialCharacters_shouldPreserveCharacters() {
        // Arrange
        ProductCategory entity = ProductCategory.builder()
                .id(UUID.randomUUID())
                .name("Electronics & Gadgets")
                .description("Devices with émojis 😀 and symbols: @#$%")
                .build();

        // Act
        ProductCategoryDto dto = mapper.toDto(entity);

        // Assert
        assertThat(dto.getName()).isEqualTo("Electronics & Gadgets");
        assertThat(dto.getDescription()).contains("émojis 😀");
        assertThat(dto.getDescription()).contains("@#$%");
    }
}
