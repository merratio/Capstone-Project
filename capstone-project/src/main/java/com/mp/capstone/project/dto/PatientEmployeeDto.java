package com.mp.capstone.project.dto;

public class PatientEmployeeDto {
    private String patientId;
    private String empId;

    public PatientEmployeeDto() {
        this.empId = "";
        this.patientId = "";
    }

    public PatientEmployeeDto(String empId, String patientId) {
        this.empId = empId;
        this.patientId = patientId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
