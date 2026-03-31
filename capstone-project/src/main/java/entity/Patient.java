package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String diagnosis;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    // Required by JPA
    protected Patient() {
        this.id = "";
        this.name = "";
        this.diagnosis = "";
        this.lastUpdated = localDateTime.now();
    }

    public Patient(String id, String name, String diagnosis, LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.diagnosis = diagnosis;
        this.lastUpdated = lastUpdated;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}