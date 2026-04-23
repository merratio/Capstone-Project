package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.PatientDto;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.exception.DataIntegrityException;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mp.capstone.project.repository.MedicalRecordRepository;
import com.mp.capstone.project.util.HashUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repo;
    private final BlockchainService blockchainService;

     @Autowired
    private PatientRepository patientRepository;

    public MedicalRecordService(MedicalRecordRepository repo,
                                BlockchainService blockchainService) {
        this.repo = repo;
        this.blockchainService = blockchainService;
    }

    @Transactional
    public String createMedicalRecord(MedicalRecord record String patId) {
        
        String recordId = (record.getId() == null || record.getId().isEmpty())
                ? generateRecordId()
                : record.getId();

        record.setId(recordId);

        // Generate hash before saving to DB
        String hash = HashUtil.generateHash(record);

        // Store on blockchain FIRST (optional, can be after DB)
        try {
            blockchainService.storeHash(recordId, hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store hash on blockchain: " + e.getMessage(), e);
        }

        // Save to database
        repo.save(record);

        return recordId;
    }

    /*
    @Transactional
    public void updatePatient(String id, PatientDto dto) {
        MedicalRecord patient = repo.findById(id)
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
    }*/

    @Transactional(readOnly = true)
    public MedicalRecord getRecord(String id) {
        MedicalRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));

        verifyIntegrity(record);

        return record;
    }

    //possible endpoint
    @Transactional
    public List<MedicalRecord> getPatientRecords(String patId){
        List<MedicalRecord> records = new ArrayList<>();
        records = repo.findByPatientTrn(patId)
                .stream()
                .toList();
        for(MedicalRecord rec:records){
            verifyIntegrity(rec);
        }
        return records;
    }

    @Transactional(readOnly = true)
    public List<MedicalRecord> getAllRecords() {
        return repo.findAll()
                .stream()
                .toList();
    }

    @Transactional
    public void deleteRecord(String id) {
        // Check if patient exists
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Record not found: " + id);
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

    private void verifyIntegrity(MedicalRecord record) {
        try {
            String dbHash = HashUtil.generateHash(record);
            String chainHash = blockchainService.getHash(record.getId());

            if (!dbHash.equals(chainHash)) {
                throw new DataIntegrityException(
                        "Integrity check failed for record: " + record.getId()
                                + " — database record does not match blockchain hash. "
                                + "DB Hash: " + dbHash + ", Blockchain Hash: " + chainHash);
            }
        } catch (Exception e) {
            throw new DataIntegrityException(
                    "Failed to verify integrity for record: " + record.getId()
                            + " - " + e.getMessage(), e);
        }
    }


    private String generateRecordId() {
        return "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
