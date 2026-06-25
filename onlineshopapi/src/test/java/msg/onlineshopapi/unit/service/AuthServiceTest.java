package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.DuplicateResourceException;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.User;
import msg.onlineshopapi.model.UserRole;
import msg.onlineshopapi.repository.UserRepository;
import msg.onlineshopapi.security.JwtService;
import msg.onlineshopapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 * Tests user registration, login, and profile retrieval.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("plainPassword")
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "john@example.com",
                "hashedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
    }

    @Test
    void register_withNewEmail_shouldSucceed() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        authService.register(testUser);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void register_withExistingEmail_shouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> authService.register(testUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_shouldEncodePassword() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("$2a$10$hashedPassword");

        // Act
        authService.register(testUser);

        // Assert
        verify(passwordEncoder).encode("plainPassword");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("$2a$10$hashedPassword");
    }

    @Test
    void register_shouldSetCustomerRole() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        User userWithNoRole = User.builder()
                .email("john@example.com")
                .password("plain")
                .build();

        // Act
        authService.register(userWithNoRole);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void getProfile_withExistingEmail_shouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // Act
        User result = authService.getProfile("john@example.com");

        // Assert
        assertThat(result).isEqualTo(testUser);
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void getProfile_withNonExistingEmail_shouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.getProfile("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-123");

        // Act
        String token = authService.login("john@example.com", "password");

        // Assert
        assertThat(token).isEqualTo("jwt-token-123");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_withInvalidCredentials_shouldThrowException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login("john@example.com", "wrongPassword"))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_shouldUseAuthenticationManager() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("token");

        // Act
        authService.login("john@example.com", "password");

        // Assert
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());

        UsernamePasswordAuthenticationToken authToken = authCaptor.getValue();
        assertThat(authToken.getPrincipal()).isEqualTo("john@example.com");
        assertThat(authToken.getCredentials()).isEqualTo("password");
    }

    @Test
    void login_shouldLoadUserDetails() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token");

        // Act
        authService.login("john@example.com", "password");

        // Assert
        verify(userDetailsService).loadUserByUsername("john@example.com");
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void register_shouldNotSetAdminRole() {
        // Arrange - Security: ensure registration always creates CUSTOMER
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        User attemptedAdmin = User.builder()
                .email("john@example.com")
                .password("plain")
                .role(UserRole.ADMIN)  // User tries to register as admin
                .build();

        // Act
        authService.register(attemptedAdmin);

        // Assert - Role should be overwritten to CUSTOMER
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(userCaptor.getValue().getRole()).isNotEqualTo(UserRole.ADMIN);
    }
}
