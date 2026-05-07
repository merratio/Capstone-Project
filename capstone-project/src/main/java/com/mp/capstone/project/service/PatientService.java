package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.PatientRequestDTO;
import com.mp.capstone.project.dto.response.PatientResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.mapper.PatientMapper;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.PatientRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository  patRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeService    empService;
    private final PatientMapper      patientMapper;

    public PatientService(PatientRepository patRepo,
                          EmployeeRepository empRepo,
                          EmployeeService empService,
                          PatientMapper patientMapper) {
        this.patRepo       = patRepo;
        this.empRepo       = empRepo;
        this.empService    = empService;
        this.patientMapper = patientMapper;
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public void addPatient(PatientRequestDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patRepo.save(patient);
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PatientResponseDTO findPatById(String trn) {
        Patient patient = patRepo.findById(trn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + trn));
        return patientMapper.toResponseDTO(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> findAllPatient() {
        return patRepo.findAll()
                .stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public void updatePatient(String patTrn, PatientRequestDTO dto) {
        Patient patient = patRepo.findById(patTrn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patTrn));
        patientMapper.updateEntityFromDTO(dto, patient);
        patRepo.save(patient);
    }

    // ─── Relationship Management ───────────────────────────────────────────────

    /**
     * Assigns an employee to a patient and automatically links all existing
     * patient records to that employee.
     *
     * <p>Record-linking is delegated to {@link EmployeeService#assignPatientRecordsToEmployee}
     * to avoid duplicating logic.
     */
    @Transactional
    public void assignEmployee(String patientId, String empId) {
        Patient pat = patRepo.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
        Employee emp = empRepo.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));

        pat.addEmployee(emp);
        patRepo.save(pat);

        // Delegate record-linking to EmployeeService to avoid duplicating logic
        empService.assignPatientRecordsToEmployee(patientId, empId);
    }
}