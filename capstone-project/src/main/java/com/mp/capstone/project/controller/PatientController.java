package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.PatientDto;
import com.mp.capstone.project.dto.PatientEmployeeDto;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins="*")
public class PatientController {
    @Autowired
    PatientService patientService;

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @PostMapping("")
    public ResponseEntity<String> createPatient(@Valid @RequestBody Patient pat) {
        log.info("Received request to create patient");
        patientService.addPatient(pat);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable String id) {
        log.info("Fetching patient with id: {}", id);
        Patient patient = patientService.findPatById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("Fetching all patients");
        List<Patient> patients = patientService.findAllPatient();
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{patId}")
    public ResponseEntity<Void> updatePatient(
            @PathVariable String patId,
            @Valid @RequestBody Patient pat) {
        log.info("Updating patient with id: {}", patId);
        patientService.updatePatient(patId, pat);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/employees")
    public ResponseEntity<String> assignEmployees(@Valid @RequestBody PatientEmployeeDto obj){
        patientService.assignEmployee(obj.getPatientId(), obj.getEmpId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created");
    }
}
