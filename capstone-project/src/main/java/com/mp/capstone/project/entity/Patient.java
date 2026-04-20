package com.mp.capstone.project.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Entity
@Table(name="patients")
public class Patient {
    @Id
    private String trn;
    @Column(nullable=false)
    private String firstName;
    @Column(nullable=false)
    private String lastName;
    private String gender;
    private String religion;
    private String address;
    @Column(nullable=false)
    private String bloodType;
    @Column(nullable=false)
    private Date dob;

    @ManyToMany
    @JoinTable(
            name = "patient_employee", // The name of the join table
            joinColumns = @JoinColumn(name = "patient_id"), // FK to Student
            inverseJoinColumns = @JoinColumn(name = "emp_id") // FK to Course
    )
    private Set<Employee> employees = new HashSet<>();

    public Patient() {
        this.address = "";
        this.bloodType = "";
        this.dob = new Date();
        this.firstName = "";
        this.gender = "";
        this.lastName = "";
        this.religion = "";
        this.trn = "";
    }

    public Patient(String address, String bloodType, Date dob, String firstName, String gender, String lastName, String religion, String trn) {
        this.address = address;
        this.bloodType = bloodType;
        this.dob = dob;
        this.firstName = firstName;
        this.gender = gender;
        this.lastName = lastName;
        this.religion = religion;
        this.trn = trn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getTrn() {
        return trn;
    }

    public void setTrn(String trn) {
        this.trn = trn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Use instanceof to handle Hibernate proxy objects
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        // Use a business key or ID (if not null) to check equality
        return id != null && id.equals(patient.getTrn());
    }

    @Override
    public int hashCode() {
        // Return a constant if using generated IDs to ensure
        // the hash doesn't change after the object is saved.
        return getClass().hashCode();
    }

}
