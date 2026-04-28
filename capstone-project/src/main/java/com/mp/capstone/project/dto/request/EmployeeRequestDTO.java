package com.mp.capstone.project.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public class EmployeeRequestDTO {

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Role must not be blank")
    private String role;

    private String gender;
    private String religion;
    private Date dob;

    public EmployeeRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
}
