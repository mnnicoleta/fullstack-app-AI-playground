package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.AddressDto;
import msg.onlineshopapi.dto.mapper.AddressMapper;
import msg.onlineshopapi.model.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

    private AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        addressMapper = new AddressMapper();
    }

    @Test
    void toDto_shouldMapAllFields() {
        Address address = Address.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street, Apt 4B")
                .build();

        AddressDto dto = addressMapper.toDto(address);

        assertThat(dto.getCountry()).isEqualTo("Romania");
        assertThat(dto.getCity()).isEqualTo("Cluj-Napoca");
        assertThat(dto.getCounty()).isEqualTo("Cluj");
        assertThat(dto.getStreetAddress()).isEqualTo("123 Main Street, Apt 4B");
    }

    @Test
    void toEntity_shouldMapAllFields() {
        AddressDto dto = AddressDto.builder()
                .country("USA")
                .city("New York")
                .county("Manhattan")
                .streetAddress("456 Broadway, Suite 100")
                .build();

        Address entity = addressMapper.toEntity(dto);

        assertThat(entity.getCountry()).isEqualTo("USA");
        assertThat(entity.getCity()).isEqualTo("New York");
        assertThat(entity.getCounty()).isEqualTo("Manhattan");
        assertThat(entity.getStreetAddress()).isEqualTo("456 Broadway, Suite 100");
    }

    @Test
    void toDto_shouldHandleEmptyStrings() {
        Address address = Address.builder()
                .country("")
                .city("")
                .county("")
                .streetAddress("")
                .build();

        AddressDto dto = addressMapper.toDto(address);

        assertThat(dto.getCountry()).isEmpty();
        assertThat(dto.getCity()).isEmpty();
        assertThat(dto.getCounty()).isEmpty();
        assertThat(dto.getStreetAddress()).isEmpty();
    }

    @Test
    void toEntity_shouldHandleEmptyStrings() {
        AddressDto dto = AddressDto.builder()
                .country("")
                .city("")
                .county("")
                .streetAddress("")
                .build();

        Address entity = addressMapper.toEntity(dto);

        assertThat(entity.getCountry()).isEmpty();
        assertThat(entity.getCity()).isEmpty();
        assertThat(entity.getCounty()).isEmpty();
        assertThat(entity.getStreetAddress()).isEmpty();
    }

    @Test
    void roundTrip_shouldPreserveData() {
        Address original = Address.builder()
                .country("Germany")
                .city("Berlin")
                .county("Brandenburg")
                .streetAddress("789 Unter den Linden")
                .build();

        AddressDto dto = addressMapper.toDto(original);
        Address roundTrip = addressMapper.toEntity(dto);

        assertThat(roundTrip.getCountry()).isEqualTo(original.getCountry());
        assertThat(roundTrip.getCity()).isEqualTo(original.getCity());
        assertThat(roundTrip.getCounty()).isEqualTo(original.getCounty());
        assertThat(roundTrip.getStreetAddress()).isEqualTo(original.getStreetAddress());
    }
}
