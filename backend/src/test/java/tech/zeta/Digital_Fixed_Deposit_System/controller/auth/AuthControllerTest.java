package tech.zeta.Digital_Fixed_Deposit_System.controller.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String uniqueEmail(String prefix) {
        return prefix + "+" + UUID.randomUUID() + "@example.com";
    }

    @Test
    void register_setsAuthCookies() {
        String email = uniqueEmail("test.user");
        String body = """
            {
              "name": "Test User",
              "email": "%s",
              "password": "Password@123"
            }
        """.formatted(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                url("/auth/register"),
                new HttpEntity<>(body, headers),
                Void.class
        );

        assertEquals(200, response.getStatusCode().value());
        String setCookie = String.join(";", response.getHeaders().get("Set-Cookie"));
        assertTrue(setCookie.contains("accessToken"));
        assertTrue(setCookie.contains("refreshToken"));
    }

    @Test
    void login_setsAuthCookies() {
        String email = uniqueEmail("login.user");
        String register = """
            {
              "name": "Login User",
              "email": "%s",
              "password": "Password@123"
            }
        """.formatted(email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(
                url("/auth/register"),
                new HttpEntity<>(register, headers),
                Void.class
        );

        String login = """
            {
              "email": "%s",
              "password": "Password@123"
            }
        """.formatted(email);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                url("/auth/login"),
                new HttpEntity<>(login, headers),
                Void.class
        );

        assertEquals(200, response.getStatusCode().value());
        String setCookie = String.join(";", response.getHeaders().get("Set-Cookie"));
        assertTrue(setCookie.contains("accessToken"));
        assertTrue(setCookie.contains("refreshToken"));
    }

    @Test
    void refresh_withoutCookie_returnsUnauthorized() {
        try {
            restTemplate.postForEntity(
                    url("/auth/refresh"),
                    HttpEntity.EMPTY,
                    String.class
            );
            fail("Expected 401 Unauthorized");
        } catch (HttpClientErrorException ex) {
            assertEquals(401, ex.getStatusCode().value());
        }
    }
}
