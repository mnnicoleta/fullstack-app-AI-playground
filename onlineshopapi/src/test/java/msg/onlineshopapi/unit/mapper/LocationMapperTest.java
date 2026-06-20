package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.AddressDto;
import msg.onlineshopapi.dto.LocationResponseDto;
import msg.onlineshopapi.dto.mapper.AddressMapper;
import msg.onlineshopapi.dto.mapper.LocationMapper;
import msg.onlineshopapi.model.Address;
import msg.onlineshopapi.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LocationMapper.
 */
class LocationMapperTest {

    private LocationMapper locationMapper;
    private AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        addressMapper = new AddressMapper();
        locationMapper = new LocationMapper(addressMapper);
    }

    @Test
    void toDto_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        Address address = Address.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main St")
                .build();

        Location location = Location.builder()
                .id(id)
                .name("Warehouse Cluj")
                .address(address)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Warehouse Cluj");
        assertThat(dto.getAddress()).isNotNull();
        assertThat(dto.getAddress().getCountry()).isEqualTo("Romania");
        assertThat(dto.getAddress().getCity()).isEqualTo("Cluj-Napoca");
        assertThat(dto.getAddress().getCounty()).isEqualTo("Cluj");
        assertThat(dto.getAddress().getStreetAddress()).isEqualTo("123 Main St");
    }

    @Test
    void toDto_withNullAddress_shouldHandleGracefully() {
        // Arrange
        UUID id = UUID.randomUUID();
        Location location = Location.builder()
                .id(id)
                .name("Warehouse")
                .address(null)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo("Warehouse");
        assertThat(dto.getAddress()).isNull();
    }

    @Test
    void toDto_addressMapping_shouldDelegateToAddressMapper() {
        // Arrange
        Address address = Address.builder()
                .country("USA")
                .city("New York")
                .county("Manhattan")
                .streetAddress("456 Broadway")
                .build();

        Location location = Location.builder()
                .id(UUID.randomUUID())
                .name("NYC Warehouse")
                .address(address)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert - Verify address was properly delegated
        AddressDto addressDto = dto.getAddress();
        assertThat(addressDto).isNotNull();
        assertThat(addressDto.getCountry()).isEqualTo("USA");
        assertThat(addressDto.getCity()).isEqualTo("New York");
        assertThat(addressDto.getCounty()).isEqualTo("Manhattan");
        assertThat(addressDto.getStreetAddress()).isEqualTo("456 Broadway");
    }

    @Test
    void toDto_withEmptyName_shouldMapEmptyString() {
        // Arrange
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .name("")
                .address(null)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert
        assertThat(dto.getName()).isEmpty();
    }

    @Test
    void toDto_withSpecialCharactersInName_shouldPreserveCharacters() {
        // Arrange
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .name("Warehouse #1 - (Main) & Distribution")
                .address(null)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert
        assertThat(dto.getName()).isEqualTo("Warehouse #1 - (Main) & Distribution");
    }

    @Test
    void toDto_withVeryLongName_shouldMapCorrectly() {
        // Arrange
        String longName = "A".repeat(255);
        Location location = Location.builder()
                .id(UUID.randomUUID())
                .name(longName)
                .address(null)
                .build();

        // Act
        LocationResponseDto dto = locationMapper.toDto(location);

        // Assert
        assertThat(dto.getName()).isEqualTo(longName);
        assertThat(dto.getName()).hasSize(255);
    }
}
