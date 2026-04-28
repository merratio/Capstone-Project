package com.mp.capstone.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds all {@code auth0.*} properties from {@code application.properties}
 * into a single, injectable configuration bean.
 *
 * <p>Required properties:
 * <pre>
 * auth0.domain=your-tenant.auth0.com
 * auth0.client-id=YOUR_M2M_CLIENT_ID
 * auth0.client-secret=YOUR_M2M_CLIENT_SECRET
 * auth0.management-api-audience=https://your-tenant.auth0.com/api/v2/
 * auth0.connection=Username-Password-Authentication
 * </pre>
 */
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
}