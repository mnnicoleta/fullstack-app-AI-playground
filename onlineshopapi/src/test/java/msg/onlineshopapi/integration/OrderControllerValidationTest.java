package msg.onlineshopapi.integration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import msg.onlineshopapi.dto.AddressDto;
import msg.onlineshopapi.dto.OrderItemRequestDto;
import msg.onlineshopapi.dto.OrderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for address validation in OrderRequestDto.
 * Uses TestContainers with PostgreSQL and Jakarta Validation.
 *
 * Tests the validation annotations (@NotNull, @NotBlank, @Valid) on OrderRequestDto and AddressDto.
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class OrderControllerValidationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private Validator validator;

    @Test
    void orderRequest_withNullAddress_shouldFailValidation() {
        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(null)  // Address is required (@NotNull)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address") &&
                v.getMessage().contains("required")
        );
    }

    @Test
    void orderRequest_withBlankCountry_shouldFailValidation() {
        AddressDto address = AddressDto.builder()
                .country("")  // Blank - should fail @NotBlank validation
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(address)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address.country")
        );
    }

    @Test
    void orderRequest_withBlankCity_shouldFailValidation() {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("")  // Blank - should fail @NotBlank validation
                .county("Cluj")
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(address)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address.city")
        );
    }

    @Test
    void orderRequest_withBlankCounty_shouldFailValidation() {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("")  // Blank - should fail @NotBlank validation
                .streetAddress("123 Main Street")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(address)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address.county")
        );
    }

    @Test
    void orderRequest_withBlankStreetAddress_shouldFailValidation() {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("")  // Blank - should fail @NotBlank validation
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(address)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("address.streetAddress")
        );
    }

    @Test
    void orderRequest_withCompleteAddress_shouldPassValidation() {
        AddressDto address = AddressDto.builder()
                .country("Romania")
                .city("Cluj-Napoca")
                .county("Cluj")
                .streetAddress("123 Main Street, Apt 4B")
                .build();

        OrderRequestDto request = OrderRequestDto.builder()
                .items(List.of(OrderItemRequestDto.builder()
                        .productId(UUID.randomUUID())
                        .quantity(2)
                        .build()))
                .address(address)
                .build();

        Set<ConstraintViolation<OrderRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
