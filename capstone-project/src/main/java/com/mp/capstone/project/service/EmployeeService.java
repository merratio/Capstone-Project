package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.EmployeeRequestDTO;
import com.mp.capstone.project.dto.response.EmployeeResponseDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.mapper.EmployeeMapper;
import com.mp.capstone.project.mapper.MedicalRecordMapper;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired EmployeeRepository empRepo;
    @Autowired PatientRepository patRepo;
    @Autowired MedicalRecordService recordService;
    @Autowired EmployeeMapper employeeMapper;
    @Autowired MedicalRecordMapper medicalRecordMapper;

    // ─── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public void addEmployee(EmployeeRequestDTO dto) {
        Employee emp = employeeMapper.toEntity(dto);
        empRepo.save(emp);
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    @Transactional
    public EmployeeResponseDTO getEmployeeDTO(String id) {
        Employee emp = empRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return employeeMapper.toResponseDTO(emp);
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

    @Transactional
    public Set<MedicalRecordResponseDTO> getEmployeeRecords(String empId) {
        Employee emp = getEmployee(empId);
        return emp.getRecords()
                .stream()
                .map(medicalRecordMapper::toResponseDTO)
                .collect(Collectors.toSet());
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public void updateEmployee(String empId, EmployeeRequestDTO dto) {
        Employee employee = getEmployee(empId);
        employeeMapper.updateEntityFromDTO(dto, employee);
        empRepo.save(employee);
    }

    // ─── Relationship Management ───────────────────────────────────────────────

    @Transactional
    public void assignPatientRecordsToEmployee(String patientTrn, String empId) {
        Employee emp = getEmployee(empId);
        List<MedicalRecord> records = recordService.getPatientRecords(patientTrn);
        for (MedicalRecord record : records) {
            emp.addRecord(record);
        }
        empRepo.save(emp);
    }
}
