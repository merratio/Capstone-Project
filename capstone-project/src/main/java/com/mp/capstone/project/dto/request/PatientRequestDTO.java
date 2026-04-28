package com.mp.capstone.project.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class PatientRequestDTO {

    @NotBlank(message = "TRN must not be blank")
    private String trn;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    private String gender;
    private String religion;
    private String address;

    @NotBlank(message = "Blood type must not be blank")
    private String bloodType;

    private LocalDate dob;

    public PatientRequestDTO() {}

    public String getTrn() { return trn; }
    public void setTrn(String trn) { this.trn = trn; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
}
