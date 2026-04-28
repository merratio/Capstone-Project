package com.mp.capstone.project.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ContactInfoRequestDTO {

    @NotBlank(message = "Contact must not be blank")
    private String contact;

    public ContactInfoRequestDTO() {}

    public ContactInfoRequestDTO(String contact) {
        this.contact = contact;
    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
