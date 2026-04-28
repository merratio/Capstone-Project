package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.request.EmployeeRequestDTO;
import com.mp.capstone.project.dto.response.EmployeeResponseDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins="*")
public class EmployeeController {

    @Autowired
    EmployeeService empService;

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @PostMapping("")
    public ResponseEntity<String> createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        log.info("Received request to create employee");
        empService.addEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    @GetMapping("/records/{empId}")
    public ResponseEntity<Set<MedicalRecordResponseDTO>> getEmployeeRecords(@PathVariable String empId) {
        log.info("Fetching records assigned to employee with id: {}", empId);
        return ResponseEntity.ok(empService.getEmployeeRecords(empId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable String id) {
        log.info("Fetching employee with id: {}", id);
        return ResponseEntity.ok(empService.getEmployeeDTO(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        log.info("Fetching all employees");
        return ResponseEntity.ok(empService.getAllEmployeeDTO());
    }

    @PutMapping("/{empId}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable String empId,
            @Valid @RequestBody EmployeeRequestDTO dto) {
        log.info("Updating employee with id: {}", empId);
        empService.updateEmployee(empId, dto);
        return ResponseEntity.noContent().build();
    }
}
