package com.mp.capstone.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name="contact_info")
public class ContactInfo {
    @Column(nullable=false)
    private String contact;
    @Id
    @ManyToOne // Defines the relationship
    @JoinColumn(name = "patient_id") // Points to the foreign key column
    private Patient pat;
}
