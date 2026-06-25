package msg.onlineshopapi.unit.exception;

import msg.onlineshopapi.exception.DuplicateResourceException;
import msg.onlineshopapi.exception.GlobalExceptionHandler;
import msg.onlineshopapi.exception.OrderNotProcessableException;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests all exception handlers to ensure correct HTTP status codes and error messages.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleDuplicateResource_shouldReturn409() {
        // Arrange
        DuplicateResourceException ex = new DuplicateResourceException("Email already exists");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResource(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(409);
        assertThat(response.getBody().get("error")).isEqualTo("Email already exists");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleResourceNotFound_shouldReturn404() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Product not found");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Product not found");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleOrderNotProcessable_shouldReturn422() {
        // Arrange
        OrderNotProcessableException ex = new OrderNotProcessableException("Insufficient stock");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleLocationNotFound(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(422);
        assertThat(response.getBody().get("error")).isEqualTo("Insufficient stock");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("User lacks admin role");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(403);
        assertThat(response.getBody().get("error")).isEqualTo("Access denied");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleAccessDenied_shouldNotExposeOriginalMessage() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Internal security info");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        // Assert - Generic message, not original exception message
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Access denied");
        assertThat(response.getBody().get("error")).isNotEqualTo("Internal security info");
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Wrong password");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationFailure(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("error")).isEqualTo("Invalid credentials");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    void handleUsernameNotFound_shouldReturn401() {
        // Arrange
        UsernameNotFoundException ex = new UsernameNotFoundException("User does not exist");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationFailure(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(401);
        assertThat(response.getBody().get("error")).isEqualTo("Invalid credentials");
    }

    @Test
    void handleAuthenticationFailure_shouldNotExposeOriginalMessage() {
        // Arrange - Security: Don't reveal if user exists
        BadCredentialsException ex = new BadCredentialsException("Password is incorrect");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationFailure(ex);

        // Assert - Generic message only
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Invalid credentials");
        assertThat(response.getBody().get("error")).isNotEqualTo("Password is incorrect");
    }

    @Test
    void handleDuplicateResource_responseBodyShouldContainTimestamp() {
        // Arrange
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate entry");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateResource(ex);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKeys("timestamp", "status", "error");
        assertThat(response.getBody().keySet()).hasSize(3);
    }

    @Test
    void allHandlers_shouldIncludeThreeFields() {
        // Test that all error responses have consistent structure: timestamp, status, error

        // DuplicateResource
        ResponseEntity<Map<String, Object>> response1 = handler.handleDuplicateResource(
            new DuplicateResourceException("test")
        );
        assertThat(response1.getBody()).containsOnlyKeys("timestamp", "status", "error");

        // ResourceNotFound
        ResponseEntity<Map<String, Object>> response2 = handler.handleResourceNotFound(
            new ResourceNotFoundException("test")
        );
        assertThat(response2.getBody()).containsOnlyKeys("timestamp", "status", "error");

        // OrderNotProcessable
        ResponseEntity<Map<String, Object>> response3 = handler.handleLocationNotFound(
            new OrderNotProcessableException("test")
        );
        assertThat(response3.getBody()).containsOnlyKeys("timestamp", "status", "error");

        // AccessDenied
        ResponseEntity<Map<String, Object>> response4 = handler.handleAccessDenied(
            new AccessDeniedException("test")
        );
        assertThat(response4.getBody()).containsOnlyKeys("timestamp", "status", "error");

        // Authentication
        ResponseEntity<Map<String, Object>> response5 = handler.handleAuthenticationFailure(
            new BadCredentialsException("test")
        );
        assertThat(response5.getBody()).containsOnlyKeys("timestamp", "status", "error");
    }
}
