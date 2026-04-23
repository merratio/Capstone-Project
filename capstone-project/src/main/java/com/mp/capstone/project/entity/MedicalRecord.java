package com.mp.capstone.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {

    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String conditionName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Date diagnosisDate;

    @Column(nullable = false)
    private Boolean hereditary;

    @ManyToOne // Defines the relationship
    @JoinColumn(name = "patient_id") // Points to the foreign key column
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @ManyToMany(mappedBy = "records") // "courses" refers to the field name in Student
    private Set<Employee> employees = new HashSet<>();

    public MedicalRecord() {
        this.conditionName = "";
        this.diagnosisDate = new Date();
        this.hereditary = false;
        this.id = "";
        this.lastUpdated = LocalDateTime.now();
        this.patient = new Patient();
        this.status = "";
    }

    public MedicalRecord(String conditionName, Date diagnosisDate, Boolean hereditary, String id, LocalDateTime lastUpdated, String status) {
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.hereditary = hereditary;
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }

    public MedicalRecord(String conditionName, Date diagnosisDate, Boolean hereditary, LocalDateTime lastUpdated, Patient pat, String status) {
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.hereditary = hereditary;
        this.lastUpdated = lastUpdated;
        this.patient = pat;
        this.status = status;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public Date getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(Date diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public Boolean getHereditary() {
        return hereditary;
    }

    public void setHereditary(Boolean hereditary) {
        this.hereditary = hereditary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Patient getPat() {
        return patient;
    }

    public void setPat(Patient pat) {
        this.patient = pat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
