package com.mp.capstone.project.dto.response;

/**
 * Response DTO returned after successfully creating an Auth0 user
 * and assigning their role.
 */
public class Auth0UserResponseDTO {

    /** Auth0-assigned user identifier, e.g. {@code auth0|64f1a2b3c4d5e6f7a8b9c0d1}. */
    private String userId;

    private String email;

    /** Name of the role that was assigned to this user. */
    private String assignedRole;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Auth0UserResponseDTO() {}

    public Auth0UserResponseDTO(String userId, String email, String assignedRole) {
        this.userId       = userId;
        this.email        = email;
        this.assignedRole = assignedRole;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAssignedRole() { return assignedRole; }
    public void setAssignedRole(String assignedRole) { this.assignedRole = assignedRole; }
}