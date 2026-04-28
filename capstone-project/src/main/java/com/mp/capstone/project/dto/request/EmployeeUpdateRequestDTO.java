package com.mp.capstone.project.dto.request;

import com.mp.capstone.project.enums.Auth0Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

/**
 * Request DTO for <b>updating</b> an existing employee (PUT /api/employees/{id}).
 *
 * <p>Does not include {@code email} or {@code password} — Auth0 credentials
 * are managed through the Auth0 dashboard or a dedicated password-reset flow,
 * not through this API.
 *
 * <p>All fields are required on update so the record is always fully specified.
 * If partial updates are needed in future, fields can be made optional and the
 * service can apply a null-safe merge strategy.
 */
public class EmployeeUpdateRequestDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Role must not be null")
    private Auth0Role role;

    private String gender;
    private String religion;
    private Date   dob;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public EmployeeUpdateRequestDTO() {}

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Auth0Role getRole() { return role; }
    public void setRole(Auth0Role role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
}