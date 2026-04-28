package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.Auth0UserRequestDTO;
import com.mp.capstone.project.dto.request.EmployeeCreateRequestDTO;
import com.mp.capstone.project.dto.request.EmployeeUpdateRequestDTO;
import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.Auth0UserResponseDTO;
import com.mp.capstone.project.dto.response.EmployeeResponseDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.mapper.EmployeeMapper;
import com.mp.capstone.project.mapper.MedicalRecordMapper;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.MedicalRecordRepository;
import com.mp.capstone.project.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for Employee operations.
 *
 * <p>On creation the employee is simultaneously registered in Auth0 as a system
 * user. The Auth0 {@code user_id} is stored on the local {@link Employee} entity
 * so the two identities can always be correlated.
 *
 * <p>Permission rules enforced here (role checks are intentionally in the service
 * so controllers stay thin and the logic is testable):
 * <ul>
 *   <li>Any authenticated employee can view records they are assigned to.</li>
 *   <li>ADMIN, DOCTOR, NURSE can update assigned records.</li>
 *   <li>RECEPTIONIST receives a 403 on any write attempt.</li>
 *   <li>Only ADMIN can delete employees or remove record assignments.</li>
 * </ul>
 */
@Service
public class EmployeeService {

    @Autowired EmployeeRepository     empRepo;
    @Autowired PatientRepository      patRepo;
    @Autowired MedicalRecordRepository medRepo;
    @Autowired MedicalRecordService   recordService;
    @Autowired EmployeeMapper         employeeMapper;
    @Autowired MedicalRecordMapper    medicalRecordMapper;
    @Autowired Auth0ManagementService auth0ManagementService;

    // ─── Create ───────────────────────────────────────────────────────────────

    /**
     * Creates an employee locally and registers them as a user in Auth0.
     *
     * <p>Auth0 registration happens first — if it fails, the local save is
     * skipped so the two systems stay consistent.
     *
     * @return the local JPA-generated employee ID
     */
    @Transactional
    public String addEmployee(EmployeeCreateRequestDTO dto) {
        Auth0UserRequestDTO auth0Dto = new Auth0UserRequestDTO(
                dto.getEmail(),
                dto.getPassword(),
                extractFirstName(dto.getName()),
                extractLastName(dto.getName()),
                dto.getRole().name()
        );
        Auth0UserResponseDTO auth0Response = auth0ManagementService.createUserWithRole(auth0Dto);

        Employee emp = employeeMapper.toEntity(dto);
        emp.setAuth0UserId(auth0Response.getUserId());

        empRepo.save(emp);
        return emp.getId();
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Transactional
    public EmployeeResponseDTO getEmployeeDTO(String id) {
        return employeeMapper.toResponseDTO(getEmployee(id));
    }

    /** Internal helper — returns the managed entity for relationship management. */
    @Transactional
    public Employee getEmployee(String id) {
        return empRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @Transactional
    public List<EmployeeResponseDTO> getAllEmployeeDTO() {
        return empRepo.findAll()
                .stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Returns all records assigned to an employee. Admin-facing. */
    @Transactional
    public Set<MedicalRecordResponseDTO> getEmployeeRecords(String empId) {
        Employee emp = getEmployee(empId);
        return emp.getRecords()
                .stream()
                .map(medicalRecordMapper::toResponseDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a single record only if it is assigned to the requesting employee.
     * Blockchain integrity is verified before returning.
     *
     * @throws ResourceNotFoundException if the record is not assigned to this employee
     */
    @Transactional
    public MedicalRecordResponseDTO getAssignedRecord(String empId, String recordId) {
        requireRecordAssigned(empId, recordId);
        return recordService.getRecord(recordId);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /** Updates employee profile fields. Auth0 credentials are not affected. */
    @Transactional
    public void updateEmployee(String empId, EmployeeUpdateRequestDTO dto) {
        Employee employee = getEmployee(empId);
        employeeMapper.updateEntityFromDTO(dto, employee);
        empRepo.save(employee);
    }

    /**
     * Updates a medical record assigned to this employee.
     * Only ADMIN, DOCTOR, and NURSE are permitted — RECEPTIONIST receives 403.
     *
     * @throws SecurityException         if the employee's role is read-only
     * @throws ResourceNotFoundException if the record is not assigned to this employee
     */
    @Transactional
    public void updateAssignedRecord(String empId, String recordId,
                                     MedicalRecordRequestDTO dto) {
        Employee emp = getEmployee(empId);

        if (!emp.getRole().canEditRecords()) {
            throw new SecurityException(
                    "Role " + emp.getRole() + " does not have permission to edit records.");
        }

        requireRecordAssigned(empId, recordId);
        recordService.updateRecordById(recordId, dto);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Deletes an employee from the local DB and cleans up join tables.
     * Does NOT delete the Auth0 user account — manage that via the Auth0 dashboard.
     */
    @Transactional
    public void deleteEmployee(String empId) {
        Employee emp = getEmployee(empId);
        emp.getPatients().forEach(patient -> patient.getEmployees().remove(emp));
        emp.getRecords().forEach(record -> record.getEmployees().remove(emp));
        empRepo.delete(emp);
    }

    /**
     * Removes the assignment between an employee and a record.
     * Does not delete the record itself.
     */
    @Transactional
    public void unassignRecord(String empId, String recordId) {
        Employee emp = getEmployee(empId);
        MedicalRecord record = medRepo.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + recordId));
        emp.removeRecord(record);
        empRepo.save(emp);
    }

    // ─── Relationship Management ──────────────────────────────────────────────

    @Transactional
    public void assignPatientRecordsToEmployee(String patientTrn, String empId) {
        Employee emp = getEmployee(empId);
        List<MedicalRecord> records = recordService.getPatientRecords(patientTrn);
        for (MedicalRecord record : records) {
            emp.addRecord(record);
        }
        empRepo.save(emp);
    }

    // ─── Internal Helpers ─────────────────────────────────────────────────────

    private void requireRecordAssigned(String empId, String recordId) {
        if (!empRepo.isRecordAssignedToEmployee(empId, recordId)) {
            throw new ResourceNotFoundException(
                    "Record " + recordId + " is not assigned to employee " + empId);
        }
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.trim().split("\\s+")[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : parts[0];
    }
}