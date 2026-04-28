package com.mp.capstone.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security configuration for the Capstone API.
 *
 * <p><b>Authentication model:</b><br>
 * Every request must carry a valid Bearer JWT issued by Auth0. The token is
 * validated against Auth0's JWKS endpoint (auto-discovered from the issuer URI)
 * and must include the correct audience claim. Stateless — no HTTP sessions.
 *
 * <p><b>Authorisation model:</b><br>
 * Roles are extracted from the {@value #ROLES_CLAIM} JWT claim (injected by the
 * Auth0 Action) and mapped to Spring {@code ROLE_*} authorities.
 *
 * <pre>
 *  ADMIN        → full control of all endpoints
 *  DOCTOR       → read + update assigned records; read own profile
 *  NURSE        → read + update assigned records; read own profile
 *  RECEPTIONIST → read-only access to assigned records; read own profile
 * </pre>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enables @PreAuthorize on individual methods if needed later
public class SecurityConfig {

    /**
     * Custom namespaced claim injected by the Auth0 Action.
     * Must match the namespace string in {@code auth0-add-roles-action.js}.
     */
    static final String ROLES_CLAIM = "https://capstone-api/roles";

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    // ─── Security Filter Chain ────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // No cookies / sessions — stateless REST API
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Disable CSRF (not needed for stateless JWT APIs)
                .csrf(csrf -> csrf.disable())

                // CORS — allow all origins in dev; tighten for production
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Endpoint authorisation rules ──────────────────────────────────
                .authorizeHttpRequests(auth -> auth

                        // ── Employee endpoints ────────────────────────────────────────
                        // Create employee: ADMIN only
                        .requestMatchers(HttpMethod.POST,   "/api/employees").hasRole("ADMIN")
                        // List all employees: ADMIN only
                        .requestMatchers(HttpMethod.GET,    "/api/employees").hasRole("ADMIN")
                        // Get single employee: ADMIN or any authenticated employee
                        // (employee-owns-self check is handled in service layer)
                        .requestMatchers(HttpMethod.GET,    "/api/employees/{id}").authenticated()
                        // Update employee profile: ADMIN only
                        .requestMatchers(HttpMethod.PUT,    "/api/employees/{empId}").hasRole("ADMIN")
                        // Delete employee: ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/{empId}").hasRole("ADMIN")

                        // ── Employee → Records endpoints ──────────────────────────────
                        // View all assigned records for an employee: ADMIN only
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/{empId}/records").hasRole("ADMIN")
                        // View a single assigned record: any authenticated employee
                        // (assignment check is enforced in EmployeeService)
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/{empId}/records/{recordId}").authenticated()
                        // Update an assigned record: ADMIN, DOCTOR, NURSE
                        // (read-only RECEPTIONIST is blocked in EmployeeService as a second guard)
                        .requestMatchers(HttpMethod.PUT,
                                "/api/employees/{empId}/records/{recordId}")
                        .hasAnyRole("ADMIN", "DOCTOR", "NURSE")
                        // Remove a record assignment: ADMIN only
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/employees/{empId}/records/{recordId}").hasRole("ADMIN")

                        // ── Patient endpoints ─────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,   "/api/patients").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/patients").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/patients/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/patients/{patId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/patients/employees").hasRole("ADMIN")

                        // ── Medical Record endpoints ──────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/medicalrecords/{patId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/medicalrecords").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/medicalrecords/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/api/medicalrecords/{patId}").hasRole("ADMIN")

                        // ── Contact Info endpoints ────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/contactinfo/patient/{trn}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/contactinfo/patient/{patientId}").authenticated()
                        .requestMatchers(HttpMethod.GET,
                                "/api/contactinfo").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/contactinfo/patient/{patientId}/contact/{contactId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/contactinfo/patient/{patientId}/contact/{contactId}").hasRole("ADMIN")

                        // Deny everything else by default
                        .anyRequest().denyAll()
                )

                // ── JWT Resource Server ───────────────────────────────────────────
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    // ─── JWT Decoder ──────────────────────────────────────────────────────────

    /**
     * Configures the JWT decoder with two validators:
     * <ol>
     *   <li>Issuer check — token must come from our Auth0 tenant.</li>
     *   <li>Audience check — token must target our specific API identifier.</li>
     * </ol>
     * The JWKS endpoint is auto-discovered from the issuer URI, so no
     * hard-coded public key is needed.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> issuerValidator  = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

        return decoder;
    }

    // ─── JWT → Authentication Converter ──────────────────────────────────────

    /**
     * Wires in {@link Auth0RoleConverter} so the roles from the custom JWT claim
     * become Spring {@code ROLE_*} authorities on the authenticated principal.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new Auth0RoleConverter(ROLES_CLAIM));
        return converter;
    }

    // ─── Audience Validator ───────────────────────────────────────────────────

    /**
     * Validates that the JWT {@code aud} claim contains the expected API audience.
     * Rejects tokens that were issued for a different API.
     */
    private static class AudienceValidator implements OAuth2TokenValidator<Jwt> {

        private final String expectedAudience;

        AudienceValidator(String expectedAudience) {
            this.expectedAudience = expectedAudience;
        }

        @Override
        public org.springframework.security.oauth2.core.OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> audiences = jwt.getAudience();
            if (audiences != null && audiences.contains(expectedAudience)) {
                return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
            }
            return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure(
                    new org.springframework.security.oauth2.core.OAuth2Error(
                            "invalid_token",
                            "Token does not contain the required audience: " + expectedAudience,
                            null
                    )
            );
        }
    }

    // ─── CORS ─────────────────────────────────────────────────────────────────

    /**
     * CORS configuration. Currently allows all origins — restrict
     * {@code allowedOrigins} to your frontend URL before going to production.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}