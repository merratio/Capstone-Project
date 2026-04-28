package com.mp.capstone.project.dto.response;

import java.time.LocalDate;
import java.util.Set;

public class PatientResponseDTO {

    private String trn;
    private String firstName;
    private String lastName;
    private String gender;
    private String religion;
    private String address;
    private String bloodType;
    private LocalDate dob;

    /** IDs of assigned employees — avoids circular Employee→Patient→Employee nesting */
    private Set<String> employeeIds;

    public PatientResponseDTO() {}

    public PatientResponseDTO(String trn, String firstName, String lastName,
                               String gender, String religion, String address,
                               String bloodType, LocalDate dob, Set<String> employeeIds) {
        this.trn = trn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.religion = religion;
        this.address = address;
        this.bloodType = bloodType;
        this.dob = dob;
        this.employeeIds = employeeIds;
    }

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

    public Set<String> getEmployeeIds() { return employeeIds; }
    public void setEmployeeIds(Set<String> employeeIds) { this.employeeIds = employeeIds; }
}
