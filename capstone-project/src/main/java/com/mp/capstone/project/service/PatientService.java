package com.mp.capstone.project.service;

import com.mp.capstone.project.dto.request.PatientRequestDTO;
import com.mp.capstone.project.dto.response.PatientResponseDTO;
import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.mapper.PatientMapper;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.MedicalRecordRepository;
import com.mp.capstone.project.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired PatientRepository patRepo;
    @Autowired EmployeeRepository empRepo;
    @Autowired MedicalRecordRepository medRepo;
    @Autowired PatientMapper patientMapper;

    // ─── Create ────────────────────────────────────────────────────────────────

    public void addPatient(PatientRequestDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patRepo.save(patient);
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    public PatientResponseDTO findPatById(String trn) {
        Patient patient = patRepo.findById(trn).orElseThrow();
        return patientMapper.toResponseDTO(patient);
    }

    public List<PatientResponseDTO> findAllPatient() {
        return patRepo.findAll()
                .stream()
                .map(patientMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public void updatePatient(String patTrn, PatientRequestDTO dto) {
        Patient patient = patRepo.findById(patTrn).orElseThrow();
        patientMapper.updateEntityFromDTO(dto, patient);
        patRepo.save(patient);
    }

    // ─── Relationship Management ───────────────────────────────────────────────

    @Transactional
    public void assignEmployee(String patientId, String empId) {
        Patient pat = patRepo.findById(patientId).orElseThrow();
        Employee emp = empRepo.findById(empId).orElseThrow();

        pat.addEmployee(emp);
        patRepo.save(pat);

        // Automatically link all existing patient records to this employee
        List<MedicalRecord> records = medRepo.findByPatientTrn(pat.getTrn());
        for (MedicalRecord record : records) {
            emp.addRecord(record);
        }
        empRepo.save(emp);
    }
}
