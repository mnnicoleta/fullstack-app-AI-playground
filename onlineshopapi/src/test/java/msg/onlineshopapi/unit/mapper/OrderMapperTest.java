package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.AddressDto;
import msg.onlineshopapi.dto.OrderItemRequestDto;
import msg.onlineshopapi.dto.OrderRequestDto;
import msg.onlineshopapi.dto.mapper.AddressMapper;
import msg.onlineshopapi.dto.mapper.OrderDetailMapper;
import msg.onlineshopapi.dto.mapper.OrderMapper;
import msg.onlineshopapi.model.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderMapper focusing on address mapping.
 * Note: OrderDetailMapper is mocked to null since we're only testing address functionality.
 */
class OrderMapperTest {

    private final AddressMapper addressMapper = new AddressMapper();
    private final OrderMapper orderMapper = new OrderMapper(null, addressMapper);

    @Test
    void toEntity_shouldMapAddressWhenPresent() {
        AddressDto addressDto = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto dto = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(addressDto)
                .build();

        Order entity = orderMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getAddress().getCountry()).isEqualTo("Romania");
        assertThat(entity.getAddress().getCity()).isEqualTo("Cluj-Napoca");
        assertThat(entity.getAddress().getCounty()).isEqualTo("Cluj");
        assertThat(entity.getAddress().getStreetAddress()).isEqualTo("123 Main Street");
    }

    @Test
    void toEntity_shouldHandleNullAddress() {
        OrderRequestDto dto = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(null)
                .build();

        Order entity = orderMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getAddress()).isNull();
    }

    @Test
    void toEntity_shouldMapDifferentAddresses() {
        AddressDto addressDto1 = AddressDto.builder()
                .country("USA")
                .city("New York")
                .county("Manhattan")
                .streetAddress("456 Broadway")
                .build();

        OrderRequestDto dto1 = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(3)
                        .build()))
                .address(addressDto1)
                .build();

        Order entity1 = orderMapper.toEntity(dto1);

        assertThat(entity1.getAddress()).isNotNull();
        assertThat(entity1.getAddress().getCountry()).isEqualTo("USA");
        assertThat(entity1.getAddress().getCity()).isEqualTo("New York");

        // Test with different address
        AddressDto addressDto2 = AddressDto.builder()
                .country("Germany")
                .city("Berlin")
                .county("Brandenburg")
                .streetAddress("789 Unter den Linden")
                .build();

        OrderRequestDto dto2 = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(1)
                        .build()))
                .address(addressDto2)
                .build();

        Order entity2 = orderMapper.toEntity(dto2);

        assertThat(entity2.getAddress()).isNotNull();
        assertThat(entity2.getAddress().getCountry()).isEqualTo("Germany");
        assertThat(entity2.getAddress().getCity()).isEqualTo("Berlin");
    }

    @Test
    void toEntity_shouldMapAllAddressFields() {
        AddressDto addressDto = AddressDto.builder()
                .country("France")
                .city("Paris")
                .county("Île-de-France")
                .streetAddress("42 Avenue des Champs-Élysées, Apt 5")
                .build();

        OrderRequestDto dto = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(1)
                        .build()))
                .address(addressDto)
                .build();

        Order entity = orderMapper.toEntity(dto);

        assertThat(entity.getAddress()).isNotNull();
        assertThat(entity.getAddress().getCountry()).isEqualTo("France");
        assertThat(entity.getAddress().getCity()).isEqualTo("Paris");
        assertThat(entity.getAddress().getCounty()).isEqualTo("Île-de-France");
        assertThat(entity.getAddress().getStreetAddress()).isEqualTo("42 Avenue des Champs-Élysées, Apt 5");
    }
}
