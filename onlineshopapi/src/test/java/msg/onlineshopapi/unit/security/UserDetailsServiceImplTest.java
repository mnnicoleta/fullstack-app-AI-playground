package msg.onlineshopapi.unit.security;

import msg.onlineshopapi.model.User;
import msg.onlineshopapi.model.UserRole;
import msg.onlineshopapi.repository.UserRepository;
import msg.onlineshopapi.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserDetailsServiceImpl.
 * Tests user loading and authority mapping for Spring Security.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User adminUser;
    private User customerUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("hashedPassword123")
                .role(UserRole.ADMIN)
                .build();

        customerUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("hashedPassword456")
                .role(UserRole.CUSTOMER)
                .build();
    }

    @Test
    void loadUserByUsername_withExistingUser_shouldReturnUserDetails() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin@example.com");
        assertThat(result.getPassword()).isEqualTo("hashedPassword123");
        verify(userRepository).findByEmail("admin@example.com");
    }

    @Test
    void loadUserByUsername_withNonExistingUser_shouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown@example.com");
        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void loadUserByUsername_withAdminUser_shouldHaveRoleAdmin() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        // Assert
        assertThat(result.getAuthorities()).hasSize(1);
        GrantedAuthority authority = result.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_withCustomerUser_shouldHaveRoleCustomer() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customerUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("john@example.com");

        // Assert
        assertThat(result.getAuthorities()).hasSize(1);
        GrantedAuthority authority = result.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_CUSTOMER");
    }

    @Test
    void loadUserByUsername_shouldMapEmailToUsername() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customerUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("john@example.com");

        // Assert - Email becomes Spring Security username
        assertThat(result.getUsername()).isEqualTo("john@example.com");
        assertThat(result.getUsername()).isEqualTo(customerUser.getEmail());
    }

    @Test
    void loadUserByUsername_shouldPreservePassword() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        // Assert - Password preserved for BCrypt comparison
        assertThat(result.getPassword()).isEqualTo("hashedPassword123");
        assertThat(result.getPassword()).isEqualTo(adminUser.getPassword());
    }

    @Test
    void loadUserByUsername_authorityPrefixShouldBeRoleUnderscore() {
        // Arrange - Test Spring Security convention ROLE_ prefix
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("admin@example.com");

        // Assert
        String authority = result.getAuthorities().iterator().next().getAuthority();
        assertThat(authority).startsWith("ROLE_");
        assertThat(authority).doesNotStartWith("ADMIN");
    }

    @Test
    void loadUserByUsername_withEmailCaseSensitivity_shouldUseRepositoryLogic() {
        // Arrange - Repository defines case sensitivity, not service
        String mixedCaseEmail = "Admin@Example.com";
        when(userRepository.findByEmail(mixedCaseEmail)).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername(mixedCaseEmail);

        // Assert
        assertThat(result).isNotNull();
        verify(userRepository).findByEmail(mixedCaseEmail);
    }

    @Test
    void loadUserByUsername_userDetailsShouldBeAccountNonExpired() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customerUser));

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("john@example.com");

        // Assert - Spring Security default behavior
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isEnabled()).isTrue();
    }
}
