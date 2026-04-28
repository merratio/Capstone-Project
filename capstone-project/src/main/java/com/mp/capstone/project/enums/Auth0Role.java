package com.mp.capstone.project.enums;

/**
 * Represents the Auth0 roles used in this system.
 *
 * <p>These names must match exactly the role names created in your Auth0 tenant
 * (case-insensitive matching is applied in {@link com.mp.capstone.project.service.Auth0ManagementService}).
 *
 * <p>Permission model:
 * <ul>
 *   <li>{@code ADMIN}        — full control: create/read/update/delete employees, patients, records</li>
 *   <li>{@code DOCTOR}       — read + update medical records they are assigned to</li>
 *   <li>{@code NURSE}        — read + update medical records they are assigned to</li>
 *   <li>{@code RECEPTIONIST} — read-only access to records they are assigned to</li>
 * </ul>
 */
public enum Auth0Role {

    ADMIN,
    DOCTOR,
    NURSE,
    RECEPTIONIST;

    /**
     * Returns true if this role is permitted to edit (create/update/delete) medical records.
     * Admins, Doctors and Nurses can edit; Receptionists are read-only.
     */
    public boolean canEditRecords() {
        return this == ADMIN || this == DOCTOR || this == NURSE;
    }

    /**
     * Returns true if this role has full administrative control of the system.
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Parses a role string (case-insensitive) into an {@link Auth0Role}.
     *
     * @throws IllegalArgumentException if the string does not match any role
     */
    public static Auth0Role fromString(String value) {
        for (Auth0Role r : values()) {
            if (r.name().equalsIgnoreCase(value)) return r;
        }
        throw new IllegalArgumentException("Unknown role: " + value
                + ". Valid values: ADMIN, DOCTOR, NURSE, RECEPTIONIST");
    }
}