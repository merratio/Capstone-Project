package com.mp.capstone.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.capstone.project.config.Auth0Properties;
import com.mp.capstone.project.dto.request.LoginRequestDTO;
import com.mp.capstone.project.dto.response.LoginResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.exception.Auth0Exception;
import com.mp.capstone.project.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class Auth0LoginService {

    private static final Logger log = LoggerFactory.getLogger(Auth0LoginService.class);

    private static final String TOKEN_ENDPOINT = "https://%s/oauth/token";

    private final Auth0Properties    auth0Properties;
    private final RestTemplate       restTemplate;
    private final ObjectMapper       objectMapper;
    private final EmployeeRepository employeeRepository;

    public Auth0LoginService(Auth0Properties auth0Properties,
                             RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             EmployeeRepository employeeRepository) {
        this.auth0Properties    = auth0Properties;
        this.restTemplate       = restTemplate;
        this.objectMapper       = objectMapper;
        this.employeeRepository = employeeRepository;
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Authenticates an employee and returns an access token with their role encoded.
     *
     * @param dto employee credentials
     * @return login response containing the JWT, expiry, local employee ID, and role
     * @throws Auth0Exception            if Auth0 rejects the credentials or the call fails
     * @throws jakarta.persistence.EntityNotFoundException if no local employee matches the Auth0 user
     */
    public LoginResponseDTO login(LoginRequestDTO dto) {
        log.info("Login attempt for email: {}", dto.getEmail());

        Map<String, Object> tokenResponse = fetchToken(dto.getEmail(), dto.getPassword());

        String accessToken = (String) tokenResponse.get("access_token");
        int    expiresIn   = (Integer) tokenResponse.get("expires_in");

        // Decode the JWT payload (middle segment) to extract the Auth0 sub claim.
        // We do NOT re-validate here — Spring Security validates on every protected request.
        String auth0UserId = extractSubFromToken(accessToken);

        Employee employee = employeeRepository.findByAuth0UserId(auth0UserId)
                .orElseThrow(() -> new Auth0Exception(
                        "Authenticated Auth0 user '" + auth0UserId
                                + "' has no matching local employee record."));

        log.info("Login successful for employee: {} (role: {})", employee.getId(), employee.getRole());

        return new LoginResponseDTO(accessToken, expiresIn, employee.getId(), employee.getRole());
    }

    // ─── Auth0 Token Request ──────────────────────────────────────────────────

    /**
     * Calls Auth0's {@code /oauth/token} endpoint with the Resource Owner Password grant.
     *
     * @return the raw token response map from Auth0
     * @throws Auth0Exception if Auth0 returns an error (e.g. wrong credentials)
     */
    private Map<String, Object> fetchToken(String email, String password) {
        String url = String.format(TOKEN_ENDPOINT, auth0Properties.getDomain());

        Map<String, String> body = Map.of(
                "grant_type", "password",
                "username",   email,
                "password",   password,
                "audience",   auth0Properties.getAudience(),
                "scope",      "openid profile email",
                "client_id",  auth0Properties.getSpaClientId(),
                "client_secret", auth0Properties.getSpaClientSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, request, String.class);

            if (response.getBody() == null) {
                throw new Auth0Exception("Auth0 returned an empty token response");
            }

            Map<String, Object> tokenMap = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );

            if (tokenMap.containsKey("error")) {
                String error       = (String) tokenMap.get("error");
                String description = (String) tokenMap.getOrDefault("error_description", "");
                log.warn("Auth0 login error: {} — {}", error, description);
                throw new Auth0Exception("Login failed: " + description);
            }

            return tokenMap;

        } catch (Auth0Exception e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("Auth0 token request failed ({}): {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            // 403 / 401 from Auth0 means wrong credentials
            if (e.getStatusCode() == HttpStatus.FORBIDDEN
                    || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new Auth0Exception("Invalid email or password.");
            }
            throw new Auth0Exception("Auth0 token request failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Auth0 token request", e);
            throw new Auth0Exception("Login failed: " + e.getMessage(), e);
        }
    }

    // ─── JWT Payload Extraction ───────────────────────────────────────────────

    /**
     * Extracts the {@code sub} (subject) claim from a JWT without re-validating it.
     *
     * <p>A JWT has three Base64-URL encoded segments separated by dots:
     * {@code header.payload.signature}. We decode the middle segment to read
     * the {@code sub} claim, which Auth0 sets to the user's unique ID
     * (e.g. {@code auth0|64f1a2b3c4d5e6f7a8b9c0d1}).
     *
     * @param jwtToken raw JWT string
     * @return the {@code sub} claim value
     * @throws Auth0Exception if the token cannot be decoded or lacks a {@code sub} claim
     */
    private String extractSubFromToken(String jwtToken) {
        try {
            String[] parts   = jwtToken.split("\\.");
            if (parts.length < 2) {
                throw new Auth0Exception("Malformed JWT — expected 3 segments");
            }
            // Pad Base64-URL encoded payload to a multiple of 4 characters
            String payload = parts[1];
            int    padding  = 4 - (payload.length() % 4);
            if (padding != 4) payload += "=".repeat(padding);

            byte[] decoded = java.util.Base64.getUrlDecoder().decode(payload);

            Map<String, Object> claims = objectMapper.readValue(
                    decoded, new TypeReference<Map<String, Object>>() {});

            String sub = (String) claims.get("sub");
            if (sub == null || sub.isBlank()) {
                throw new Auth0Exception("JWT is missing the 'sub' claim");
            }
            return sub;

        } catch (Auth0Exception e) {
            throw e;
        } catch (Exception e) {
            throw new Auth0Exception("Failed to decode JWT payload: " + e.getMessage(), e);
        }
    }
}