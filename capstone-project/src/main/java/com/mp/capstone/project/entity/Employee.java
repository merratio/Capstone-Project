package com.mp.capstone.project.entity;

import com.mp.capstone.project.enums.Auth0Role;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a healthcare employee who is also a system user.
 *
 * <p>Each employee is registered in Auth0 on creation. The {@code auth0UserId}
 * field stores the Auth0-assigned {@code user_id} (e.g. {@code auth0|64f1a2b3...})
 * so the local record can always be correlated with the Auth0 identity.
 *
 * <p>{@code role} is stored as a string in the DB via {@link Auth0Role}
 * and drives both Auth0 role assignment and in-application permission checks.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    /**
     * Stored as the enum name string (e.g. "ADMIN", "DOCTOR").
     * Drives Auth0 role assignment and in-app permission checks.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Auth0Role role;

    /**
     * The Auth0 user_id returned after registering this employee in Auth0.
     * Unique per employee; nullable only until the Auth0 call completes.
     */
    @Column(name = "auth0_user_id", unique = true)
    private String auth0UserId;

    private String gender;
    private String religion;

    @Temporal(TemporalType.DATE)
    private Date dob;

    @ManyToMany(mappedBy = "employees")
    private Set<Patient> patients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "employee_medicalrecord",
            joinColumns = @JoinColumn(name = "emp_id"),
            inverseJoinColumns = @JoinColumn(name = "rec_id")
    )
    private Set<MedicalRecord> records = new HashSet<>();

    // ─── Constructors ─────────────────────────────────────────────────────────

    public Employee() {
        this.dob      = new Date();
        this.gender   = "";
        this.name     = "";
        this.religion = "";
        this.role     = Auth0Role.RECEPTIONIST;
    }

    public Employee(String name, Auth0Role role, String gender,
                    String religion, Date dob, String auth0UserId) {
        this.name        = name;
        this.role        = role;
        this.gender      = gender;
        this.religion    = religion;
        this.dob         = dob;
        this.auth0UserId = auth0UserId;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Auth0Role getRole() { return role; }
    public void setRole(Auth0Role role) { this.role = role; }

    public String getAuth0UserId() { return auth0UserId; }
    public void setAuth0UserId(String auth0UserId) { this.auth0UserId = auth0UserId; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public Set<Patient> getPatients() { return patients; }
    public Set<MedicalRecord> getRecords() { return records; }

    // ─── Relationship Helpers ─────────────────────────────────────────────────

    public void addRecord(MedicalRecord rec) {
        this.records.add(rec);
        rec.getEmployees().add(this);
    }

    public void removeRecord(MedicalRecord rec) {
        this.records.remove(rec);
        rec.getEmployees().remove(this);
    }

    // ─── equals / hashCode ────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee emp = (Employee) o;
        return id != null && id.equals(emp.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}