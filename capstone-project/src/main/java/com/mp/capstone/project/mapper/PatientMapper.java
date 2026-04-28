package com.mp.capstone.project.mapper;

import com.mp.capstone.project.dto.request.PatientRequestDTO;
import com.mp.capstone.project.dto.response.PatientResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PatientMapper {

    /**
     * Converts a Patient entity to a PatientResponseDTO.
     * Employee relationships are flattened to a set of IDs to prevent circular references.
     */
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) return null;

        return new PatientResponseDTO(
                patient.getTrn(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getGender(),
                patient.getReligion(),
                patient.getAddress(),
                patient.getBloodType(),
                patient.getDob(),
                patient.getEmployees()
                        .stream()
                        .map(Employee::getId)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Converts a PatientRequestDTO to a Patient entity.
     * The employees collection is left empty; relationships are managed separately.
     */
    public Patient toEntity(PatientRequestDTO dto) {
        if (dto == null) return null;

        return new Patient(
                dto.getAddress(),
                dto.getBloodType(),
                dto.getDob(),
                dto.getFirstName(),
                dto.getGender(),
                dto.getLastName(),
                dto.getReligion(),
                dto.getTrn()
        );
    }

    /**
     * Applies fields from a PatientRequestDTO onto an existing Patient entity.
     * Used for PUT (update) operations so JPA tracks the managed instance.
     */
    public void updateEntityFromDTO(PatientRequestDTO dto, Patient patient) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setGender(dto.getGender());
        patient.setReligion(dto.getReligion());
        patient.setAddress(dto.getAddress());
        patient.setBloodType(dto.getBloodType());
        patient.setDob(dto.getDob());
    }
}
