package com.mp.capstone.project.dto.response;

import java.util.Date;
import java.util.Set;

public class EmployeeResponseDTO {

    private String id;
    private String name;
    private String role;
    private String gender;
    private String religion;
    private Date dob;

    /** IDs of patients this employee is assigned to */
    private Set<String> patientTrns;

    /** IDs of medical records this employee handles */
    private Set<String> recordIds;

    public EmployeeResponseDTO() {}

    public EmployeeResponseDTO(String id, String name, String role, String gender,
                                String religion, Date dob,
                                Set<String> patientTrns, Set<String> recordIds) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.religion = religion;
        this.dob = dob;
        this.patientTrns = patientTrns;
        this.recordIds = recordIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public Set<String> getPatientTrns() { return patientTrns; }
    public void setPatientTrns(Set<String> patientTrns) { this.patientTrns = patientTrns; }

    public Set<String> getRecordIds() { return recordIds; }
    public void setRecordIds(Set<String> recordIds) { this.recordIds = recordIds; }
}
