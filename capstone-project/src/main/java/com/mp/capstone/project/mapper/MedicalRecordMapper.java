package com.mp.capstone.project.mapper;

import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
     * The patient association must be set separately in the service layer.
     * lastUpdated is stamped here; id is assigned by the service if absent.
     */
    public MedicalRecord toEntity(MedicalRecordRequestDTO dto) {
        if (dto == null) return null;

        MedicalRecord record = new MedicalRecord();
        record.setId(dto.getId() != null ? dto.getId() : "");
        record.setConditionName(dto.getConditionName());
        record.setStatus(dto.getStatus());
        record.setDiagnosisDate(dto.getDiagnosisDate());
        record.setHereditary(dto.getHereditary());
        record.setLastUpdated(LocalDateTime.now());
        return record;
    }

    /**
     * Applies MedicalRecordRequestDTO fields onto an existing managed entity.
     * Used for PUT (update) operations.
     */
    public void updateEntityFromDTO(MedicalRecordRequestDTO dto, MedicalRecord record) {
        record.setConditionName(dto.getConditionName());
        record.setStatus(dto.getStatus());
        record.setDiagnosisDate(dto.getDiagnosisDate());
        record.setHereditary(dto.getHereditary());
        record.setLastUpdated(LocalDateTime.now());
    }
}
