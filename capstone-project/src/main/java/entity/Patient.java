package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Patient {

    @Id
    private String id;

    private String name;
    private String diagnosis;
    private LocalDateTime lastUpdated;

    public Patient(String diagnosis, String id, LocalDateTime lastUpdated, String name) {
        this.diagnosis = diagnosis;
        this.id = id;
        this.lastUpdated = lastUpdated;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
