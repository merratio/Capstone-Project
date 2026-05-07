package com.mp.capstone.project.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Changed from java.util.Date to LocalDate — timezone-neutral and produces
    // a stable, consistent toString() output (ISO-8601: "2026-05-07")
    @Column(nullable = false)
    private LocalDate diagnosisDate;

    @Column(nullable = false)
    private Boolean hereditary;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Precision = 3 locks the DB column to milliseconds, preventing truncation
    // mismatch between the hashed value and what gets persisted and read back
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdated;

    // No-arg constructor kept clean — no field defaults that could interfere
    // with JPA hydration. Service layer is responsible for setting all values.
    public MedicalRecord() {}

    public MedicalRecord(String conditionName, LocalDate diagnosisDate, Boolean hereditary,
                         String id, LocalDateTime lastUpdated, String status) {
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.hereditary    = hereditary;
        this.id            = id;
        this.lastUpdated   = lastUpdated;
        this.status        = status;
    }

    public MedicalRecord(String conditionName, LocalDate diagnosisDate, Boolean hereditary,
                         LocalDateTime lastUpdated, Patient pat, String status) {
        this.conditionName = conditionName;
        this.diagnosisDate = diagnosisDate;
        this.hereditary    = hereditary;
        this.lastUpdated   = lastUpdated;
        this.patient       = pat;
        this.status        = status;
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

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
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

    @ManyToMany(mappedBy = "records")
    private Set<Employee> employees = new HashSet<>();
}