package com.mp.capstone.project.entity;

import jakarta.persistence.*;

import java.util.Date;

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
    @ManyToOne // Defines the relationship
    @JoinColumn(name = "supervisor_id") // Points to the foreign key column
    private Employee emp;

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
}
