package com.mp.capstone.project.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Entity
@Table(name="employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable=false)
    private String role;
    private String gender;
    @Column(nullable = false)
    private String Name;
    private String Religion;
    Date dob;

    @ManyToMany(mappedBy = "employees") // "courses" refers to the field name in Student
    private Set<Patient> patients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "employee_medicalrecord", // The name of the join table
            joinColumns = @JoinColumn(name = "emp_id"), // FK to Student
            inverseJoinColumns = @JoinColumn(name = "rec_id") // FK to Course
    )
    private Set<MedicalRecord> records = new HashSet<>();

    public Employee() {
        this.dob = new Date();
        this.gender = "";
        this.id = "";
        Name = "";
        Religion = "";
        this.role = "";
    }

    public Employee(Date dob, String gender, String id, String name, String religion, String role) {
        this.dob = dob;
        this.gender = gender;
        this.id = id;
        Name = name;
        Religion = religion;
        this.role = role;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getReligion() {
        return Religion;
    }

    public void setReligion(String religion) {
        Religion = religion;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public Set<MedicalRecord> getRecords() {
        return records;
    }

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
    public void addEmployee(MedicalRecord rec) {
        this.records.add(rec);
        rec.getEmployees().add(this); // Keeps both sides in sync
    }

    public void removeEmployee(MedicalRecord rec) {
        this.records.remove(rec);
        rec.getEmployees().remove(this);
    }
}
