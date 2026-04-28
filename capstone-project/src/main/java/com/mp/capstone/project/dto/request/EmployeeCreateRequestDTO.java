package com.mp.capstone.project.dto.request;

import com.mp.capstone.project.enums.Auth0Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * Request DTO for <b>creating</b> a new employee (POST /api/employees).
 *
 * <p>Includes {@code email} and {@code password} which are forwarded to Auth0
 * to register the employee as a system user. These fields are intentionally
 * absent from {@link EmployeeUpdateRequestDTO} — credentials are managed
 * through Auth0 directly after the initial registration.
 */
public class EmployeeCreateRequestDTO {

    // ─── Required fields ──────────────────────────────────────────────────────

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Role must not be null")
    private Auth0Role role;

    @Email(message = "Email must be a valid address")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // ─── Optional profile fields ──────────────────────────────────────────────

    private String gender;
    private String religion;
    private Date   dob;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public EmployeeCreateRequestDTO() {}

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Auth0Role getRole() { return role; }
    public void setRole(Auth0Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
}