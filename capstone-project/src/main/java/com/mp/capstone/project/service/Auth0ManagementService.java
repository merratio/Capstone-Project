package com.mp.capstone.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.capstone.project.config.Auth0Properties;
import com.mp.capstone.project.dto.request.Auth0UserRequestDTO;
import com.mp.capstone.project.dto.response.Auth0UserResponseDTO;
import com.mp.capstone.project.exception.Auth0Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for creating users and assigning roles in Auth0
 * via the Auth0 Management API (v2).
 *
 * <p>Workflow:
 * <ol>
 *   <li>Fetch a Management API token using the client-credentials grant.</li>
 *   <li>Create the user in the Auth0 database connection.</li>
 *   <li>Resolve the role ID for the given role name.</li>
 *   <li>Assign the role to the newly created user.</li>
 * </ol>
 *
 * <p>All Auth0 credentials are injected via {@link Auth0Properties}.
 */
@Service
public class Auth0ManagementService {

    private static final Logger log = LoggerFactory.getLogger(Auth0ManagementService.class);

    private static final String USERS_ENDPOINT   = "https://%s/api/v2/users";
    private static final String ROLES_ENDPOINT   = "https://%s/api/v2/roles";
    private static final String ASSIGN_ENDPOINT  = "https://%s/api/v2/users/%s/roles";
    private static final String TOKEN_ENDPOINT   = "https://%s/oauth/token";

    private final Auth0Properties auth0Properties;
    private final RestTemplate    restTemplate;
    private final ObjectMapper    objectMapper;

    public Auth0ManagementService(Auth0Properties auth0Properties,
                                  RestTemplate restTemplate,
                                  ObjectMapper objectMapper) {
        this.auth0Properties = auth0Properties;
        this.restTemplate    = restTemplate;
        this.objectMapper    = objectMapper;
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Creates a new Auth0 user and assigns the requested role.
     *
     * @param dto incoming user details (email, password, role name)
     * @return response DTO containing the Auth0 user_id and email
     * @throws Auth0Exception if any Auth0 API call fails
     */
    public Auth0UserResponseDTO createUserWithRole(Auth0UserRequestDTO dto) {
        log.info("Creating Auth0 user for email: {}", dto.getEmail());

        String token  = fetchManagementToken();
        String userId = createUser(token, dto);

        log.info("Auth0 user created with id: {}", userId);

        String roleId = resolveRoleId(token, dto.getRoleName());
        assignRoleToUser(token, userId, roleId);

        log.info("Role '{}' assigned to user '{}'", dto.getRoleName(), userId);

        return new Auth0UserResponseDTO(userId, dto.getEmail(), dto.getRoleName());
    }

    /**
     * Assigns an existing Auth0 role to an existing Auth0 user by their IDs.
     *
     * @param userId Auth0 user_id (e.g. "auth0|abc123")
     * @param roleId Auth0 role_id (e.g. "rol_xyz")
     * @throws Auth0Exception if the assignment fails
     */
    public void assignRole(String userId, String roleId) {
        log.info("Assigning role '{}' to user '{}'", roleId, userId);
        String token = fetchManagementToken();
        assignRoleToUser(token, userId, roleId);
    }

    // ─── Token ────────────────────────────────────────────────────────────────

    /**
     * Obtains a short-lived Management API access token using the
     * client-credentials OAuth 2.0 grant.
     */
    @SuppressWarnings("unchecked")
    private String fetchManagementToken() {
        String url = String.format(TOKEN_ENDPOINT, auth0Properties.getDomain());

        Map<String, String> body = Map.of(
                "grant_type",    "client_credentials",
                "client_id",     auth0Properties.getClientId(),
                "client_secret", auth0Properties.getClientSecret(),
                "audience",      auth0Properties.getManagementApiAudience()
        );

        HttpHeaders headers = jsonHeaders();
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                throw new Auth0Exception("Auth0 token response missing access_token");
            }
            return (String) response.getBody().get("access_token");

        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch Auth0 management token: {}", e.getResponseBodyAsString());
            throw new Auth0Exception("Failed to obtain Auth0 management token: " + e.getMessage(), e);
        }
    }

    // ─── User Creation ────────────────────────────────────────────────────────

    /**
     * POSTs a new user to the Auth0 /api/v2/users endpoint.
     *
     * @return the Auth0-assigned user_id
     */
    @SuppressWarnings("unchecked")
    private String createUser(String token, Auth0UserRequestDTO dto) {
        String url = String.format(USERS_ENDPOINT, auth0Properties.getDomain());

        Map<String, Object> body = Map.of(
                "email",       dto.getEmail(),
                "password",    dto.getPassword(),
                "connection",  auth0Properties.getConnection(),
                "given_name",  dto.getFirstName(),
                "family_name", dto.getLastName(),
                "name",        dto.getFirstName() + " " + dto.getLastName()
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, bearerHeaders(token));

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("user_id")) {
                throw new Auth0Exception("Auth0 user creation response missing user_id");
            }
            return (String) response.getBody().get("user_id");

        } catch (HttpClientErrorException e) {
            log.error("Auth0 user creation failed: {}", e.getResponseBodyAsString());
            throw new Auth0Exception("Failed to create Auth0 user: " + e.getMessage(), e);
        }
    }

    // ─── Role Resolution ──────────────────────────────────────────────────────

    /**
     * Fetches all roles from Auth0 and returns the ID of the role whose name
     * matches {@code roleName} (case-insensitive).
     *
     * <p>Uses {@link ObjectMapper} with a {@link TypeReference} to deserialize the
     * response body into a properly typed {@code List<Map<String, Object>>}, avoiding
     * raw-type casts and the associated unchecked-cast warnings.
     *
     * @throws Auth0Exception if no matching role is found or the API call fails
     */
    private String resolveRoleId(String token, String roleName) {
        String url = String.format(ROLES_ENDPOINT, auth0Properties.getDomain());

        HttpEntity<Void> request = new HttpEntity<>(bearerHeaders(token));

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getBody() == null || response.getBody().isBlank()) {
                throw new Auth0Exception("Auth0 returned an empty roles response");
            }

            // Deserialize into a typed list — no raw casts needed
            List<Map<String, Object>> roles = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            return roles.stream()
                    .filter(role -> roleName.equalsIgnoreCase((String) role.get("name")))
                    .map(role -> (String) role.get("id"))
                    .findFirst()
                    .orElseThrow(() ->
                            new Auth0Exception("Auth0 role not found: '" + roleName
                                    + "'. Ensure this role exists in your Auth0 tenant."));

        } catch (Auth0Exception e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("Failed to retrieve Auth0 roles: {}", e.getResponseBodyAsString());
            throw new Auth0Exception("Failed to retrieve Auth0 roles: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to parse Auth0 roles response", e);
            throw new Auth0Exception("Failed to parse Auth0 roles response: " + e.getMessage(), e);
        }
    }

    // ─── Role Assignment ─────────────────────────────────────────────────────

    /**
     * Assigns a role to a user via POST /api/v2/users/{userId}/roles.
     */
    private void assignRoleToUser(String token, String userId, String roleId) {
        String url = String.format(ASSIGN_ENDPOINT, auth0Properties.getDomain(), userId);

        Map<String, List<String>> body = Map.of("roles", List.of(roleId));

        HttpEntity<Map<String, List<String>>> request = new HttpEntity<>(body, bearerHeaders(token));

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpClientErrorException e) {
            log.error("Failed to assign role '{}' to user '{}': {}",
                    roleId, userId, e.getResponseBodyAsString());
            throw new Auth0Exception(
                    "Failed to assign role to user " + userId + ": " + e.getMessage(), e);
        }
    }

    // ─── Header Helpers ──────────────────────────────────────────────────────

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}