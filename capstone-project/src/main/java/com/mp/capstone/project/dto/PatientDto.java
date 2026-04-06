package com.mp.capstone.project.dto;

import jakarta.validation.constraints.NotBlank;

public class PatientDto {

    @NotBlank(message = "ID must not be blank")
    private String id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Diagnosis must not be blank")
    private String diagnosis;

    public PatientDto() {
        this.id = "";
        this.name = "";
        this.diagnosis = "";
    }

    public PatientDto(String id, String name, String diagnosis) {
        this.id = id;
        this.name = name;
        this.diagnosis = diagnosis;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
}
