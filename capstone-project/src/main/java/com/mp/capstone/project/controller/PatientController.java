package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.PatientEmployeeDto;
import com.mp.capstone.project.dto.request.PatientRequestDTO;
import com.mp.capstone.project.dto.response.PatientResponseDTO;
import com.mp.capstone.project.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins="*")
public class PatientController {

    @Autowired
    PatientService patientService;

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    @PostMapping("")
    public ResponseEntity<String> createPatient(@Valid @RequestBody PatientRequestDTO dto) {
        log.info("Received request to create patient");
        patientService.addPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable String id) {
        log.info("Fetching patient with id: {}", id);
        return ResponseEntity.ok(patientService.findPatById(id));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        log.info("Fetching all patients");
        return ResponseEntity.ok(patientService.findAllPatient());
    }

    @PutMapping("/{patId}")
    public ResponseEntity<Void> updatePatient(
            @PathVariable String patId,
            @Valid @RequestBody PatientRequestDTO dto) {
        log.info("Updating patient with id: {}", patId);
        patientService.updatePatient(patId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/employees")
    public ResponseEntity<String> assignEmployees(@Valid @RequestBody PatientEmployeeDto obj) {
        patientService.assignEmployee(obj.getPatientId(), obj.getEmpId());
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }
}
