package com.mp.capstone.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {

    /**
     * Your Auth0 tenant domain (no scheme), e.g. {@code your-tenant.auth0.com}.
     */
    private String domain;

    /**
     * Client ID of the Machine-to-Machine application registered in Auth0
     * that has been granted access to the Management API.
     */
    private String clientId;

    /**
     * Client secret of the Machine-to-Machine application.
     * Store this in environment variables or a secrets manager — never hard-code it.
     */
    private String clientSecret;

    /**
     * Audience for the Management API token request.
     * Typically {@code https://<domain>/api/v2/}.
     */
    private String managementApiAudience;

    /**
     * Auth0 database connection name to create users in.
     * Default: {@code Username-Password-Authentication}.
     */
    private String connection = "Username-Password-Authentication";

    /**
     * The API Identifier (audience) set when creating the API in the Auth0 dashboard.
     * Used in the Resource Owner Password grant so Auth0 issues a token scoped to this API.
     * Must match {@code auth0.audience} in {@code application.properties}.
     * Example: {@code https://capstone-api}
     */
    private String audience;

    /**
     * Client ID of the Regular Web App / Native application in Auth0 that has the
     * Password grant type enabled. This is a DIFFERENT application from the M2M app
     * used for the Management API — it represents the Capstone frontend/client.
     */
    private String spaClientId;

    /**
     * Client Secret of the Regular Web App / Native application.
     * Store via environment variable: {@code AUTH0_SPA_CLIENT_SECRET}.
     */
    private String spaClientSecret;

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getManagementApiAudience() { return managementApiAudience; }
    public void setManagementApiAudience(String managementApiAudience) {
        this.managementApiAudience = managementApiAudience;
    }

    public String getConnection() { return connection; }
    public void setConnection(String connection) { this.connection = connection; }

    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }

    public String getSpaClientId() { return spaClientId; }
    public void setSpaClientId(String spaClientId) { this.spaClientId = spaClientId; }

    public String getSpaClientSecret() { return spaClientSecret; }
    public void setSpaClientSecret(String spaClientSecret) { this.spaClientSecret = spaClientSecret; }
}