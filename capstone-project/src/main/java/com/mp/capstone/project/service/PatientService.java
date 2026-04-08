package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.PatientDto;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.DataIntegrityException;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mp.capstone.project.repository.PatientRepository;
import com.mp.capstone.project.util.HashUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository repo;
    private final BlockchainService blockchainService;

    public PatientService(PatientRepository repo,
                          BlockchainService blockchainService) {
        this.repo = repo;
        this.blockchainService = blockchainService;
    }

    @Transactional
    public String createPatient(PatientDto dto) {
        // Generate ID first (if not provided in DTO)
        String patientId = (dto.getId() == null || dto.getId().isEmpty())
                ? generatePatientId()
                : dto.getId();

        // Create patient with ID
        Patient patient = new Patient(
                patientId,
                dto.getName(),
                dto.getDiagnosis(),
                LocalDateTime.now()
        );

        // Generate hash before saving to DB
        String hash = HashUtil.generateHash(patient);

        // Store on blockchain FIRST (optional, can be after DB)
        try {
            blockchainService.storeHash(patientId, hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store hash on blockchain: " + e.getMessage(), e);
        }

        // Save to database
        repo.save(patient);

        return patientId;
    }

    @Transactional
    public void updatePatient(String id, PatientDto dto) {
        Patient patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));

        patient.setName(dto.getName());
        patient.setDiagnosis(dto.getDiagnosis());
        patient.setLastUpdated(LocalDateTime.now());

        // Generate new hash with updated data
        String newHash = HashUtil.generateHash(patient);

        // Update on blockchain first
        try {
            blockchainService.updateHash(id, newHash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update hash on blockchain: " + e.getMessage(), e);
        }

        // Then save to database
        repo.save(patient);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatient(String id) {
        Patient patient = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));

        verifyIntegrity(patient);

        return toDto(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDto> getAllPatients() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deletePatient(String id) {
        // Check if patient exists
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found: " + id);
        }

        // Delete from blockchain
        try {
            blockchainService.deleteHash(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete hash from blockchain: " + e.getMessage(), e);
        }

        // Delete from database
        repo.deleteById(id);
    }

    private void verifyIntegrity(Patient patient) {
        try {
            String dbHash = HashUtil.generateHash(patient);
            String chainHash = blockchainService.getHash(patient.getId());

            if (!dbHash.equals(chainHash)) {
                throw new DataIntegrityException(
                        "Integrity check failed for patient: " + patient.getId()
                                + " — database record does not match blockchain hash. "
                                + "DB Hash: " + dbHash + ", Blockchain Hash: " + chainHash);
            }
        } catch (Exception e) {
            throw new DataIntegrityException(
                    "Failed to verify integrity for patient: " + patient.getId()
                            + " - " + e.getMessage(), e);
        }
    }

    private Patient map(PatientDto dto) {
        String id = (dto.getId() == null || dto.getId().isEmpty())
                ? generatePatientId()
                : dto.getId();

        return new Patient(
                id,
                dto.getName(),
                dto.getDiagnosis(),
                LocalDateTime.now()
        );
    }

    private PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getId(),
                patient.getName(),
                patient.getDiagnosis()
        );
    }

    private String generatePatientId() {
        return "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}