package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.MedicalRecordRepository;
import com.mp.capstone.project.repository.PatientRepository;
import com.mp.capstone.project.util.HashUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {
    @Autowired
    PatientRepository patRepo;
    @Autowired
    EmployeeRepository empRepo;
    @Autowired
    MedicalRecordRepository medRepo;

    public void addPatient(Patient pat){
        patRepo.save(pat);

    }

    @Transactional
    public void assignEmployee(String patientId, String empId) {
        // 1. Fetch both from DB
        Patient pat = patRepo.findById(patientId).orElseThrow();
        Employee emp = empRepo.findById(empId).orElseThrow();

        // 2. Use the helper method to link them
        pat.addEmployee(emp);

        // 3. Save the OWNER (Student).
        // JPA updates the 'student_course' table automatically.
        patRepo.save(pat);

        List<MedicalRecord> records = medRepo.findByPatientTrn(pat.getTrn());

        for(MedicalRecord record: records){
            emp.addRecord(record);
        }
        empRepo.save(emp);

    }

    public Patient findPatById(String trn){
        Patient p;
        p = patRepo.findById(trn).orElseThrow();
        return p;
    }

    public List<Patient> findAllPatient(){
        List<Patient> patientList = new ArrayList<>();
        patientList = patRepo.findAll();
        return patientList;
    }

    @Transactional
    public void updatePatient(String patTrn, Patient pat) {
        Patient patient = patRepo.findById(patTrn).orElseThrow();

        patient.setAddress(pat.getAddress());
        patient.setDob(pat.getDob());
        patient.setBloodType(pat.getBloodType());
        patient.setGender(pat.getGender());
        patient.setFirstName(pat.getFirstName());
        patient.setLastName(pat.getLastName());
        patient.setReligion(pat.getReligion());

        // Then save to database
        patRepo.save(patient);

    }
}
