package com.mp.capstone.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "contact", nullable = false, length = 100)
    private String contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient pat;

    // Default constructor (required by JPA)
    public ContactInfo() {
        this.contact = "";
        this.pat = new Patient();
    }

    // Parameterized constructor
    public ContactInfo(String contact, Patient pat) {
        this.contact = contact;
        this.pat = pat;
    }

    // Getters & Setters
    public Long getContactId() { return contactId; }
    public void setContactId(Long contactId) { this.contactId = contactId; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Patient getPat() { return pat; }
    public void setPat(Patient pat) { this.pat = pat; }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "contactId=" + contactId +
                ", contact='" + contact + '\'' +
                '}';
    }
}
