package com.mp.capstone.project.dto.response;

public class ContactInfoResponseDTO {

    private Long contactId;
    private String contact;

    /** Patient TRN — replaces the full nested Patient object */
    private String patientTrn;

    public ContactInfoResponseDTO() {}

    public ContactInfoResponseDTO(Long contactId, String contact, String patientTrn) {
        this.contactId = contactId;
        this.contact = contact;
        this.patientTrn = patientTrn;
    }

    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getPatientTrn() { return patientTrn; }
    public void setPatientTrn(String patientTrn) { this.patientTrn = patientTrn; }
}
