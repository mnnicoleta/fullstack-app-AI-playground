package gatling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JwtTokenProvider {
    private final String loginUrl;
    private final String email;
    private final String password;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(String loginUrl, String email, String password) {
        this.loginUrl = loginUrl;
        this.email = email;
        this.password = password;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String getToken() {
        try {
            String requestBody = """
                    { "email": "%s", "password": "%s" }
                    """.formatted(email, password);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Login failed. HTTP status: " + response.statusCode() + ", response: " + response.body());
            }
            JsonNode json = objectMapper.readTree(response.body());
            JsonNode accessToken = json.get("access_token");
            if (accessToken == null || accessToken.isNull()) {
                throw new RuntimeException("Login succeeded but access_token was not found. " + "Response: " + response.body());
            }
            return accessToken.asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain JWT token", e);
        }
    }
}