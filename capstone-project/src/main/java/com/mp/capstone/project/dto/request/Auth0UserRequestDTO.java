package com.mp.capstone.project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new Auth0 user with an assigned role.
 *
 * <p>Consumed by {@code Auth0ManagementService#createUserWithRole}.
 */
public class Auth0UserRequestDTO {

    @Email(message = "Email must be a valid address")
    @NotBlank(message = "Email must not be blank")
    private String email;

    /**
     * Password must meet Auth0's default policy:
     * at least 8 characters, mixed case, with a number or symbol.
     */
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    /**
     * Must match an existing role name in your Auth0 tenant exactly
     * (case-insensitive match is applied in the service).
     * Example values: {@code "ADMIN"}, {@code "DOCTOR"}, {@code "NURSE"}.
     */
    @NotBlank(message = "Role name must not be blank")
    private String roleName;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Auth0UserRequestDTO() {}

    public Auth0UserRequestDTO(String email, String password,
                               String firstName, String lastName,
                               String roleName) {
        this.email     = email;
        this.password  = password;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.roleName  = roleName;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}