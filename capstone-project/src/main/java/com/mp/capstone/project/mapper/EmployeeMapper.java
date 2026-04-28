package com.mp.capstone.project.mapper;

import com.mp.capstone.project.dto.request.EmployeeCreateRequestDTO;
import com.mp.capstone.project.dto.request.EmployeeUpdateRequestDTO;
import com.mp.capstone.project.dto.response.EmployeeResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Maps between {@link Employee} entity and its request/response DTOs.
 *
 * <p>Create and update use separate DTOs ({@link EmployeeCreateRequestDTO} and
 * {@link EmployeeUpdateRequestDTO}) to avoid requiring credentials on updates.
 *
 * <p>{@code toEntity} does NOT set {@code auth0UserId} — that value is returned
 * from Auth0 after user creation and set explicitly by the service layer.
 */
@Component
public class EmployeeMapper {

    /**
     * Converts an Employee entity to an EmployeeResponseDTO.
     * Patient and MedicalRecord relationships are flattened to ID sets.
     */
    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null) return null;

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getRole(),
                employee.getGender(),
                employee.getReligion(),
                employee.getDob(),
                employee.getAuth0UserId(),
                employee.getPatients()
                        .stream()
                        .map(Patient::getTrn)
                        .collect(Collectors.toSet()),
                employee.getRecords()
                        .stream()
                        .map(MedicalRecord::getId)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Converts an {@link EmployeeCreateRequestDTO} to a new Employee entity.
     *
     * <p>{@code auth0UserId} is intentionally left null — it must be set by
     * the service after the Auth0 registration call returns successfully.
     */
    public Employee toEntity(EmployeeCreateRequestDTO dto) {
        if (dto == null) return null;

        Employee emp = new Employee();
        emp.setName(dto.getName());
        emp.setRole(dto.getRole());
        emp.setGender(dto.getGender());
        emp.setReligion(dto.getReligion());
        emp.setDob(dto.getDob());
        return emp;
    }

    /**
     * Applies {@link EmployeeUpdateRequestDTO} fields onto an existing managed Employee.
     * Used for PUT (update) operations.
     *
     * <p>Does not touch {@code auth0UserId} — credentials are managed via Auth0 directly.
     */
    public void updateEntityFromDTO(EmployeeUpdateRequestDTO dto, Employee employee) {
        employee.setName(dto.getName());
        employee.setRole(dto.getRole());
        employee.setGender(dto.getGender());
        employee.setReligion(dto.getReligion());
        employee.setDob(dto.getDob());
    }
}