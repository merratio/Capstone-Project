package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.DataIntegrityException;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.mapper.MedicalRecordMapper;
import com.mp.capstone.project.repository.MedicalRecordRepository;
import com.mp.capstone.project.repository.PatientRepository;
import com.mp.capstone.project.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repo;
    private final BlockchainService       blockchainService;
    private final PatientRepository       patientRepository;
    private final MedicalRecordMapper     medicalRecordMapper;

    public MedicalRecordService(MedicalRecordRepository repo,
                                BlockchainService blockchainService,
                                PatientRepository patientRepository,
                                MedicalRecordMapper medicalRecordMapper) {
        this.repo                = repo;
        this.blockchainService   = blockchainService;
        this.patientRepository   = patientRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    /**
     * Unconditional insert — always creates a new record.
     * The frontend signals create vs update by calling POST vs PUT respectively,
     * so no upsert logic is needed here.
     *
     * @param dto   record fields from the request
     * @param patId TRN of the patient this record belongs to
     * @return the newly generated record ID
     */
    @Transactional
    public String createMedicalRecord(MedicalRecordRequestDTO dto, String patId) {
        Patient pat = patientRepository.findById(patId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patId));

        MedicalRecord record = medicalRecordMapper.toEntity(dto);

        String recordId = (record.getId() == null || record.getId().isEmpty())
                ? generateRecordId()
                : record.getId();

        record.setId(recordId);
        record.setPat(pat);

        // Set lastUpdated explicitly before hashing so the hash includes the
        // correct timestamp rather than relying on @PrePersist which runs after
        // repo.save() — too late for the hash computation.
        record.setLastUpdated(LocalDateTime.now());

        String hash = HashUtil.generateHash(record);
        try {
            blockchainService.storeHash(recordId, hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store hash on blockchain: " + e.getMessage(), e);
        }

        repo.save(record);
        return recordId;
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    /**
     * Fetches a single record by ID and verifies its blockchain integrity
     * before returning.
     *
     * @throws ResourceNotFoundException if no record exists with {@code id}
     * @throws DataIntegrityException    if the DB hash does not match the blockchain
     */
    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getRecord(String id) {
        MedicalRecord record = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
        verifyIntegrity(record);
        return medicalRecordMapper.toResponseDTO(record);
    }

    /**
     * Fetches all records and verifies the blockchain integrity of every one
     * before returning. Any single failure raises a {@link DataIntegrityException}
     * and halts the response so corrupted data is never silently served.
     *
     * @throws DataIntegrityException if any record fails its integrity check
     */
    @Transactional(readOnly = true)
    public List<MedicalRecordResponseDTO> getAllRecords() {
        List<MedicalRecord> records = repo.findAll();
        records.forEach(this::verifyIntegrity);
        return records.stream()
                .map(medicalRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the raw entities for a patient (used internally by
     * {@link EmployeeService} for relationship management).
     * Integrity is verified for every record before the list is returned.
     *
     * @throws DataIntegrityException if any record fails its integrity check
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> getPatientRecords(String patId) {
        List<MedicalRecord> records = repo.findByPatientTrn(patId);
        records.forEach(this::verifyIntegrity);
        return records;
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Updates a record looked up by patient TRN.
     * Used by admin-facing {@code MedicalRecordController}.
     */
    @Transactional
    public void updatePatientRecord(String patTrn, MedicalRecordRequestDTO dto) {
        List<MedicalRecord> records = repo.findByPatientTrn(patTrn);

        MedicalRecord updatedRecord = records.stream()
                .filter(r -> patTrn.equals(r.getPat().getTrn()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("No medical record found for patient: " + patTrn));

        applyUpdateAndHash(updatedRecord, dto);
    }

    /**
     * Updates a record looked up directly by its own ID.
     * Called by the PUT endpoint and by {@link EmployeeService#updateAssignedRecord}
     * after assignment and permission checks have already passed.
     *
     * @param recordId the ID of the record to update
     * @param dto      fields to apply
     * @throws ResourceNotFoundException if no record with {@code recordId} exists
     */
    @Transactional
    public void updateRecordById(String recordId, MedicalRecordRequestDTO dto) {
        MedicalRecord record = repo.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + recordId));

        applyUpdateAndHash(record, dto);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Transactional
    public void deleteRecord(String id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Record not found: " + id);
        }
        try {
            blockchainService.deleteHash(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete hash from blockchain: " + e.getMessage(), e);
        }
        repo.deleteById(id);
    }

    // ─── Internal Helpers ─────────────────────────────────────────────────────

    /**
     * Applies DTO fields to a managed record, recomputes the hash, pushes it
     * to the blockchain first, then persists to DB. Ordering matters — if the
     * blockchain call fails the transaction rolls back before DB is written,
     * preventing a stale hash from ever being persisted.
     */
    private void applyUpdateAndHash(MedicalRecord record, MedicalRecordRequestDTO dto) {
        medicalRecordMapper.updateEntityFromDTO(dto, record);

        // Set before hashing so the timestamp is part of the hash
        record.setLastUpdated(LocalDateTime.now());

        String newHash = HashUtil.generateHash(record);
        try {
            blockchainService.updateHash(record.getId(), newHash); // blockchain first
        } catch (Exception e) {
            throw new RuntimeException("Failed to update hash on blockchain: " + e.getMessage(), e);
        }

        repo.save(record); // DB write only reaches here if blockchain succeeded
    }

    /**
     * Recomputes the hash of {@code record} from the DB state and compares it
     * against the hash stored on the blockchain.
     *
     * @throws DataIntegrityException if the hashes differ, or if the blockchain
     *                                call itself fails
     */
    private void verifyIntegrity(MedicalRecord record) {
        try {
            String dbHash    = HashUtil.generateHash(record);
            String chainHash = blockchainService.getHash(record.getId());
            if (!dbHash.equals(chainHash)) {
                throw new DataIntegrityException(
                        "Integrity check failed for record: " + record.getId()
                                + " — DB hash: " + dbHash
                                + ", Blockchain hash: " + chainHash);
            }
        } catch (DataIntegrityException e) {
            throw e;
        } catch (Exception e) {
            throw new DataIntegrityException(
                    "Failed to verify integrity for record: " + record.getId()
                            + " — " + e.getMessage(), e);
        }
    }

    private String generateRecordId() {
        return "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}