package com.mp.capstone.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MedicalRecordRequestDTO {

    /** Optional — service generates one if absent */
    private String id;

    @NotBlank(message = "Condition name must not be blank")
    private String conditionName;

    @NotBlank(message = "Status must not be blank")
    private String status;

    @NotNull(message = "Diagnosis date must not be null")
    private Date diagnosisDate;

    @NotNull(message = "Hereditary flag must not be null")
    private Boolean hereditary;

    public MedicalRecordRequestDTO() {}

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
}
