package com.mp.capstone.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

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
}
