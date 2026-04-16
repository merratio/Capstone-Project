package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PatientService {
    @Autowired
    PatientRepository patRepo;

    public void addPatient(Patient pat){
        patRepo.save(pat);
    }

    public Patient findPatById(String trn){
        Patient p;
        p = patRepo.findById(trn).orElseThrow();
        return p;
    }

    public List<Patient> findAllPatient(){
        List<Patient> patientList = patRepo.findAll();
        return patientList;
    }
}
