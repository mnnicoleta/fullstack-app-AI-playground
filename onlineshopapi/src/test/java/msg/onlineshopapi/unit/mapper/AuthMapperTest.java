package msg.onlineshopapi.unit.mapper;

import msg.onlineshopapi.dto.RegisterRequestDto;
import msg.onlineshopapi.dto.UserDto;
import msg.onlineshopapi.dto.mapper.AuthMapper;
import msg.onlineshopapi.model.User;
import msg.onlineshopapi.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AuthMapper.
 */
class AuthMapperTest {

    private AuthMapper authMapper;

    @BeforeEach
    void setUp() {
        authMapper = new AuthMapper();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");

        // Act
        User user = authMapper.toEntity(dto);

        // Assert - AuthMapper doesn't set ID (service layer does)
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void toEntity_shouldNotSetId() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setEmail("jane@example.com");
        dto.setPassword("pass");

        // Act
        User user = authMapper.toEntity(dto);

        // Assert - ID is null, service layer sets it
        assertThat(user.getId()).isNull();
    }

    @Test
    void toDto_shouldMapAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice@example.com")
                .password("hashed_password")
                .role(UserRole.CUSTOMER)
                .build();

        // Act
        UserDto dto = authMapper.toDto(user);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(id.toString());
        assertThat(dto.getFirstName()).isEqualTo("Alice");
        assertThat(dto.getLastName()).isEqualTo("Johnson");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void toDto_shouldNotExposePassword() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Bob")
                .lastName("Brown")
                .email("bob@example.com")
                .password("secret_password_hash")
                .role(UserRole.ADMIN)
                .build();

        // Act
        UserDto dto = authMapper.toDto(user);

        // Assert - Password should not be in DTO
        assertThat(dto).isNotNull();
        assertThat(dto.toString()).doesNotContain("secret_password_hash");
        assertThat(dto.toString()).doesNotContain("password");
    }

    @Test
    void toDto_withAdminRole_shouldMapCorrectly() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("hash")
                .role(UserRole.ADMIN)
                .build();

        // Act
        UserDto dto = authMapper.toDto(user);

        // Assert
        assertThat(dto.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void toDto_withCustomerRole_shouldMapCorrectly() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Customer")
                .lastName("User")
                .email("customer@example.com")
                .password("hash")
                .role(UserRole.CUSTOMER)
                .build();

        // Act
        UserDto dto = authMapper.toDto(user);

        // Assert
        assertThat(dto.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    void roundTrip_shouldPreserveData() {
        // Arrange
        RegisterRequestDto registerDto = new RegisterRequestDto();
        registerDto.setFirstName("Charlie");
        registerDto.setLastName("Davis");
        registerDto.setEmail("charlie@example.com");
        registerDto.setPassword("original_password");

        // Act
        User user = authMapper.toEntity(registerDto);
        user.setId(UUID.randomUUID()); // Service layer would set this
        user.setRole(UserRole.CUSTOMER); // Service layer would set this
        UserDto userDto = authMapper.toDto(user);

        // Assert
        assertThat(userDto.getFirstName()).isEqualTo("Charlie");
        assertThat(userDto.getLastName()).isEqualTo("Davis");
        assertThat(userDto.getEmail()).isEqualTo("charlie@example.com");
        // Note: password is not in UserDto (security)
    }

    @Test
    void toEntity_withSpecialCharactersInEmail_shouldPreserve() {
        // Arrange
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("test+tag@example.co.uk");
        dto.setPassword("pass");

        // Act
        User user = authMapper.toEntity(dto);

        // Assert
        assertThat(user.getEmail()).isEqualTo("test+tag@example.co.uk");
    }

    @Test
    void toDto_shouldConvertUUIDToString() {
        // Arrange
        UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        User user = User.builder()
                .id(uuid)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("hash")
                .role(UserRole.CUSTOMER)
                .build();

        // Act
        UserDto dto = authMapper.toDto(user);

        // Assert
        assertThat(dto.getId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
    }
}
