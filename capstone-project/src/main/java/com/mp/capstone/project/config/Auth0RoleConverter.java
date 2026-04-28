package com.mp.capstone.project.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts an Auth0 JWT into a collection of Spring Security {@link GrantedAuthority} objects.
 *
 * <p><b>Why this class is needed:</b><br>
 * Auth0 does not put roles in the standard JWT {@code scope} or {@code roles} claim by default.
 * The Auth0 Action (see {@code auth0-add-roles-action.js}) injects roles under a custom
 * namespaced claim, e.g.:
 * <pre>
 *   "https://capstone-api/roles": ["ADMIN"]
 * </pre>
 * Spring Security's default JWT converter only reads {@code scope}, so without this converter
 * all role-based access checks would fail. This class reads the custom claim and maps each
 * role string to a {@code ROLE_<NAME>} authority (e.g. {@code ROLE_ADMIN}), which is the
 * convention Spring Security's {@code @PreAuthorize("hasRole('ADMIN')")} expects.
 *
 * <p><b>Claim namespace:</b><br>
 * The namespace ({@code https://capstone-api/roles}) must match exactly what is set
 * in the Auth0 Action script and in {@code SecurityConfig.ROLES_CLAIM}.
 */
public class Auth0RoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * The custom claim key injected by the Auth0 Action.
     * Must match the namespace used in {@code auth0-add-roles-action.js}.
     * Also referenced in {@link SecurityConfig} so the value is defined once there.
     */
    private final String rolesClaim;

    public Auth0RoleConverter(String rolesClaim) {
        this.rolesClaim = rolesClaim;
    }

    /**
     * Reads the roles list from the JWT custom claim and converts each entry
     * into a Spring {@link GrantedAuthority} prefixed with {@code ROLE_}.
     *
     * <p>Example: the claim value {@code ["ADMIN", "DOCTOR"]} produces
     * {@code [ROLE_ADMIN, ROLE_DOCTOR]}.
     *
     * @param jwt the validated JWT from the incoming request
     * @return collection of granted authorities, empty if the claim is absent
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList(rolesClaim);

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}