package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.ContactInfo;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.repository.ContactInfoRepository;
import com.mp.capstone.project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactInfoService {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Autowired
    private PatientRepository patientRepository;

    // Get all contacts for a patient
    public List<ContactInfo> getContactsByPatientId(String patientId) {
        // Verify patient exists before querying contacts
        patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        return contactInfoRepository.findByPat_PatientId(patientId);
    }

    // Add a new contact for a patient
    public ContactInfo addContact(String patientId, ContactInfo contactInfo) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        // Check for duplicate contact
        if (contactInfoRepository.existsByPat_PatientIdAndContact(patientId, contactInfo.getContact())) {
            throw new RuntimeException("Contact already exists for this patient.");
        }

        contactInfo.setPat(patient);
        return contactInfoRepository.save(contactInfo);
    }

    // Update an existing contact
    public ContactInfo updateContact(String patientId, Long contactId, ContactInfo updatedInfo) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_PatientId(contactId, patientId)
                .orElseThrow(() -> new RuntimeException("Contact not found for this patient."));

        existing.setContact(updatedInfo.getContact());

        return contactInfoRepository.save(existing);
    }

    // Delete a contact
    public void deleteContact(String patientId, Long contactId) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_PatientId(contactId, patientId)
                .orElseThrow(() -> new RuntimeException("Contact not found for this patient."));

        contactInfoRepository.delete(existing);
    }
}
