package msg.onlineshopapi.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests using TestContainers.
 * <p>
 * This class provides a shared PostgreSQL container that is started once
 * and reused across all integration tests, preventing resource exhaustion.
 * <p>
 * The container is automatically stopped when the JVM exits.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    /**
     * Shared PostgreSQL container instance.
     * Marked as static to ensure it's created only once per test suite execution.
     */
    protected static final PostgreSQLContainer<?> postgres;
    private static final String POSTGRES_IMAGE = "postgres:18";

    static {
        postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true); // Enable container reuse
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
