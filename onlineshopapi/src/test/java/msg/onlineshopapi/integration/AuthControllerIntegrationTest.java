package msg.onlineshopapi.integration;

import msg.onlineshopapi.exception.DuplicateResourceException;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.User;
import msg.onlineshopapi.repository.OrderRepository;
import msg.onlineshopapi.repository.UserRepository;
import msg.onlineshopapi.security.JwtService;
import msg.onlineshopapi.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for authentication service layer.
 * Extends BaseIntegrationTest to use shared PostgreSQL container.
 */
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_withValidData_shouldCreateUser() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        // Act
        authService.register(user);

        // Assert
        assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
        User savedUser = userRepository.findByEmail("john.doe@example.com").get();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
        assertThat(savedUser.getLastName()).isEqualTo("Doe");
        assertThat(savedUser.getPassword()).isNotEqualTo("password123"); // Should be hashed
    }

    @Test
    void register_withDuplicateEmail_shouldThrowException() {
        // Arrange - Register user first
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);

        // Act & Assert - Try to register with same email
        User duplicateUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .password("password456")
                .build();

        assertThatThrownBy(() -> authService.register(duplicateUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // Arrange - Register user first
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);

        // Act
        String token = authService.login("john.doe@example.com", "password123");

        // Assert
        assertThat(token).isNotEmpty();
        assertThat(jwtService.extractUsername(token)).isEqualTo("john.doe@example.com");
    }

    @Test
    void login_withInvalidPassword_shouldThrowException() {
        // Arrange - Register user first
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);

        // Act & Assert
        assertThatThrownBy(() -> authService.login("john.doe@example.com", "wrongpassword"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withNonExistentEmail_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> authService.login("nonexistent@example.com", "password123"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void getProfile_withValidEmail_shouldReturnUser() {
        // Arrange - Register user
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);

        // Act
        User profile = authService.getProfile("john.doe@example.com");

        // Assert
        assertThat(profile).isNotNull();
        assertThat(profile.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(profile.getFirstName()).isEqualTo("John");
        assertThat(profile.getLastName()).isEqualTo("Doe");
    }

    @Test
    void getProfile_withNonExistentEmail_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> authService.getProfile("nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void jwtToken_shouldBeValidForAuthenticatedUser() {
        // Arrange - Register and login user
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);
        String token = authService.login("john.doe@example.com", "password123");

        // Act - Load user details and validate token
        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe@example.com");
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void jwtToken_shouldContainCorrectUsername() {
        // Arrange - Register and login user
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        authService.register(user);
        String token = authService.login("john.doe@example.com", "password123");

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo("john.doe@example.com");
    }

    @Test
    void register_shouldSetCustomerRoleByDefault() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        // Act
        authService.register(user);

        // Assert
        User savedUser = userRepository.findByEmail("john.doe@example.com").get();
        assertThat(savedUser.getRole().name()).isEqualTo("CUSTOMER");
    }
}
