package msg.onlineshopapi.unit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import msg.onlineshopapi.security.JwtProperties;
import msg.onlineshopapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService.
 * Tests token generation, validation, and claim extraction.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private String testSecret;
    private long testExpiration;

    @BeforeEach
    void setUp() {
        // Use a test secret (Base64 encoded)
        testSecret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi10ZXN0aW5nLXB1cnBvc2Vz";
        testExpiration = 3600000; // 1 hour

        JwtProperties properties = new JwtProperties(testSecret, testExpiration);
        jwtService = new JwtService(properties);

        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    void generateToken_shouldCreateValidJwt() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    void generateToken_shouldIncludeUsername() {
        // Act
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo("test@example.com");
    }

    @Test
    void generateToken_withExtraClaims_shouldIncludeClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("customClaim", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(testSecret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("role")).isEqualTo("ADMIN");
        assertThat(claims.get("customClaim")).isEqualTo("customValue");
        assertThat(claims.getSubject()).isEqualTo("test@example.com");
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_withWrongUsername_shouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = User.builder()
                .username("different@example.com")
                .password("password")
                .roles("USER")
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() throws InterruptedException {
        // Arrange - Create service with 1ms expiration
        JwtProperties shortExpirationProps = new JwtProperties(testSecret, 1);
        JwtService shortExpirationService = new JwtService(shortExpirationProps);

        String token = shortExpirationService.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(50);

        // Act & Assert - extractUsername will throw ExpiredJwtException
        assertThatThrownBy(() -> shortExpirationService.isTokenValid(token, userDetails))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    void generateToken_shouldIncludeIssuedAtDate() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(testSecret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        assertThat(issuedAt).isNotNull();
        assertThat(issuedAt).isBefore(new Date(System.currentTimeMillis() + 1000)); // Within next second
    }

    @Test
    void generateToken_shouldIncludeExpirationDate() {
        // Arrange
        Date beforeGeneration = new Date();

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(testSecret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(beforeGeneration);

        // Verify expiration is approximately 1 hour from now
        long expectedExpiration = beforeGeneration.getTime() + testExpiration;
        assertThat(expiration.getTime()).isBetween(expectedExpiration - 1000, expectedExpiration + 1000);
    }

    @Test
    void extractUsername_withInvalidToken_shouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void isTokenValid_withMalformedToken_shouldThrowException() {
        // Arrange
        String malformedToken = "not.a.jwt";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.isTokenValid(malformedToken, userDetails))
                .isInstanceOf(Exception.class);
    }

    @Test
    void generateToken_shouldBeValidForDifferentUserDetails() {
        // Arrange
        UserDetails user1 = User.builder()
                .username("user1@example.com")
                .password("password")
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("user2@example.com")
                .password("password")
                .roles("USER")
                .build();

        // Act
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtService.extractUsername(token1)).isEqualTo("user1@example.com");
        assertThat(jwtService.extractUsername(token2)).isEqualTo("user2@example.com");
    }

    // Phase 3 edge cases

    @Test
    void extractUsername_withEmptyToken_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(""))
                .isInstanceOf(Exception.class);
    }

    @Test
    void isTokenValid_withTokenForDifferentUser_shouldReturnFalse() {
        // Arrange
        UserDetails user1 = User.builder()
                .username("user1@example.com")
                .password("password")
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("user2@example.com")
                .password("password")
                .roles("USER")
                .build();

        String tokenForUser1 = jwtService.generateToken(user1);

        // Act
        boolean isValid = jwtService.isTokenValid(tokenForUser1, user2);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void extractUsername_withNullToken_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(null))
                .isInstanceOf(Exception.class);
    }
}
