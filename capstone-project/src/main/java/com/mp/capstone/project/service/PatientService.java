package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PatientService {
    @Autowired
    PatientRepository patRepo;
    @Autowired
    EmployeeRepository empRepo;

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
}
