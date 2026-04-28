package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.ContactInfoRequestDTO;
import com.mp.capstone.project.dto.response.ContactInfoResponseDTO;
import com.mp.capstone.project.entity.ContactInfo;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.mapper.ContactInfoMapper;
import com.mp.capstone.project.repository.ContactInfoRepository;
import com.mp.capstone.project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactInfoService {

    @Autowired private ContactInfoRepository contactInfoRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private ContactInfoMapper contactInfoMapper;

    // ─── Read ──────────────────────────────────────────────────────────────────

    public List<ContactInfoResponseDTO> getAllContactInfo() {
        return contactInfoRepository.findAll()
                .stream()
                .map(contactInfoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ContactInfoResponseDTO> getContactsByPatientId(String trn) {
        patientRepository.findById(trn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with TRN: " + trn));
        return contactInfoRepository.findByPat_Trn(trn)
                .stream()
                .map(contactInfoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    public ContactInfoResponseDTO addContact(String trn, ContactInfoRequestDTO dto) {
        Patient patient = patientRepository.findById(trn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with TRN: " + trn));

        if (contactInfoRepository.existsByPat_TrnAndContact(trn, dto.getContact())) {
            throw new IllegalArgumentException("Contact already exists for this patient.");
        }

        ContactInfo contactInfo = contactInfoMapper.toEntity(dto);
        contactInfo.setPat(patient);

        return contactInfoMapper.toResponseDTO(contactInfoRepository.save(contactInfo));
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    public ContactInfoResponseDTO updateContact(String trn, Long contactId, ContactInfoRequestDTO dto) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_Trn(contactId, trn)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this patient."));

        contactInfoMapper.updateEntityFromDTO(dto, existing);

        return contactInfoMapper.toResponseDTO(contactInfoRepository.save(existing));
    }

    // ─── Delete ────────────────────────────────────────────────────────────────

    public void deleteContact(String trn, Long contactId) {
        ContactInfo existing = contactInfoRepository
                .findByContactIdAndPat_Trn(contactId, trn)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found for this patient."));
        contactInfoRepository.delete(existing);
    }
}
