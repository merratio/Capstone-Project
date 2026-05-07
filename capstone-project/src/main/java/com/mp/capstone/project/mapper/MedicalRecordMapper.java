package com.mp.capstone.project.mapper;

import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MedicalRecordMapper {

    /**
     * Converts a MedicalRecord entity to a MedicalRecordResponseDTO.
     * Patient is represented by TRN only; employees are represented by IDs only.
     */
    public MedicalRecordResponseDTO toResponseDTO(MedicalRecord record) {
        if (record == null) return null;

        String patientTrn = (record.getPat() != null) ? record.getPat().getTrn() : null;

        return new MedicalRecordResponseDTO(
                record.getId(),
                record.getConditionName(),
                record.getStatus(),
                record.getDiagnosisDate(),
                record.getHereditary(),
                record.getLastUpdated(),
                patientTrn,
                record.getEmployees()
                        .stream()
                        .map(Employee::getId)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Converts a MedicalRecordRequestDTO to a new MedicalRecord entity.
     * The patient association and lastUpdated must be set by the service layer
     * so the timestamp is set once, used for hashing, and persisted consistently.
     */
    public MedicalRecord toEntity(MedicalRecordRequestDTO dto) {
        if (dto == null) return null;

        MedicalRecord record = new MedicalRecord();
        record.setId(dto.getId() != null ? dto.getId() : "");
        record.setConditionName(dto.getConditionName());
        record.setStatus(dto.getStatus());
        record.setDiagnosisDate(dto.getDiagnosisDate());
        record.setHereditary(dto.getHereditary());
        // lastUpdated intentionally omitted — set once in the service layer
        // before hashing so the hash and the persisted value are always in sync
        return record;
    }

    /**
     * Applies MedicalRecordRequestDTO fields onto an existing managed entity.
     * Used for PUT (update) operations.
     * lastUpdated intentionally omitted — set once in the service layer
     * before hashing so the hash and the persisted value are always in sync.
     */
    public void updateEntityFromDTO(MedicalRecordRequestDTO dto, MedicalRecord record) {
        record.setConditionName(dto.getConditionName());
        record.setStatus(dto.getStatus());
        record.setDiagnosisDate(dto.getDiagnosisDate());
        record.setHereditary(dto.getHereditary());
        // lastUpdated intentionally omitted — set once in the service layer
    }
}