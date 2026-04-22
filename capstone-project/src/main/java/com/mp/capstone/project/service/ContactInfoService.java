package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.ContactInfo;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.ResourceNotFoundException;
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

    public List<ContactInfo> getAllContactInfo() {
        return contactInfoRepository.findAll();
    }
    
    // Get all contacts for a patient
    public List<ContactInfo> getContactsByPatientId(String trn) {
        patientRepository.findById(trn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with TRN: " + trn));
        return contactInfoRepository.findByPat_Trn(trn);
    }

    // Add a new contact for a patient
    public ContactInfo addContact(String trn, ContactInfo contactInfo) {
        Patient patient = patientRepository.findById(trn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with TRN: " + trn));

        if (contactInfoRepository.existsByPat_TrnAndContact(trn, contactInfo.getContact())) {
            throw new IllegalArgumentException("Contact already exists for this patient.");
        }

        contactInfo.setPat(patient);
        return contactInfoRepository.save(contactInfo);
    }

    // Update an existing contact
    public ContactInfo updateContact(String trn, Long contactId, ContactInfo updatedInfo) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_Trn(contactId, trn)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this patient."));

        existing.setContact(updatedInfo.getContact());

        return contactInfoRepository.save(existing);
    }

    public void deleteContact(String trn, Long contactId) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_Trn(contactId, trn)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this patient."));

        contactInfoRepository.delete(existing);
    }
}
