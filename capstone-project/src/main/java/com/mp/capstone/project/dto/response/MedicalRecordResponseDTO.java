package com.mp.capstone.project.dto.response;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class MedicalRecordResponseDTO {

    private String id;
    private String conditionName;
    private String status;
    private Date diagnosisDate;
    private Boolean hereditary;
    private LocalDateTime lastUpdated;

    /** Patient TRN — avoids nesting full Patient object */
    private String patientTrn;

    /** IDs of employees assigned to this record */
    private Set<String> employeeIds;

    public MedicalRecordResponseDTO() {}

    public MedicalRecordResponseDTO(String id, String conditionName, String status,
                                     Date diagnosisDate, Boolean hereditary,
                                     LocalDateTime lastUpdated, String patientTrn,
                                     Set<String> employeeIds) {
        this.id = id;
        this.conditionName = conditionName;
        this.status = status;
        this.diagnosisDate = diagnosisDate;
        this.hereditary = hereditary;
        this.lastUpdated = lastUpdated;
        this.patientTrn = patientTrn;
        this.employeeIds = employeeIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConditionName() { return conditionName; }
    public void setConditionName(String conditionName) { this.conditionName = conditionName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(Date diagnosisDate) { this.diagnosisDate = diagnosisDate; }

    public Boolean getHereditary() { return hereditary; }
    public void setHereditary(Boolean hereditary) { this.hereditary = hereditary; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getPatientTrn() { return patientTrn; }
    public void setPatientTrn(String patientTrn) { this.patientTrn = patientTrn; }

    public Set<String> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(Set<String> employeeIds) { this.employeeIds = employeeIds; }
}
