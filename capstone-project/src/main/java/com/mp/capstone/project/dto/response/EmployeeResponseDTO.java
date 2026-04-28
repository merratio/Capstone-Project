package com.mp.capstone.project.dto.response;

import com.mp.capstone.project.enums.Auth0Role;

import java.util.Date;
import java.util.Set;

/**
 * Response DTO returned for Employee read operations.
 *
 * <p>Exposes the Auth0 user ID so callers can correlate the local employee
 * record with the Auth0 identity. Role is typed as {@link Auth0Role} so
 * the client receives a consistent, validated value rather than a raw string.
 */
public class EmployeeResponseDTO {

    private String     id;
    private String     name;
    private Auth0Role  role;
    private String     gender;
    private String     religion;
    private Date       dob;

    /** Auth0 user_id assigned at registration, e.g. {@code auth0|64f1...}. */
    private String     auth0UserId;

    /** TRNs of patients this employee is assigned to. */
    private Set<String> patientTrns;

    /** IDs of medical records this employee handles. */
    private Set<String> recordIds;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public EmployeeResponseDTO() {}

    public EmployeeResponseDTO(String id, String name, Auth0Role role,
                               String gender, String religion, Date dob,
                               String auth0UserId,
                               Set<String> patientTrns, Set<String> recordIds) {
        this.id          = id;
        this.name        = name;
        this.role        = role;
        this.gender      = gender;
        this.religion    = religion;
        this.dob         = dob;
        this.auth0UserId = auth0UserId;
        this.patientTrns = patientTrns;
        this.recordIds   = recordIds;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getAuth0UserId() { return auth0UserId; }
    public void setAuth0UserId(String auth0UserId) { this.auth0UserId = auth0UserId; }

    public Set<String> getPatientTrns() { return patientTrns; }
    public void setPatientTrns(Set<String> patientTrns) { this.patientTrns = patientTrns; }

    public Set<String> getRecordIds() { return recordIds; }
    public void setRecordIds(Set<String> recordIds) { this.recordIds = recordIds; }
}