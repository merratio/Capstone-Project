package com.mp.capstone.project.controller;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
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

    @PostMapping("{empId}")
    public ResponseEntity<String> createEmployee(@Valid @RequestBody Employee emp) {
        log.info("Received request to create employee");
        empService.addEmployee(emp);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created");
    }

    @GetMapping("/records/{empId}")
    public ResponseEntity<Set<MedicalRecord>> getEmployeRecords(@PathVariable String empId) {
        log.info("Fetching records assigned to employee with id: {}", empId);
        Employee emp = empService.getEmployee(empId);
        return ResponseEntity.ok(emp.getRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmploye(@PathVariable String id) {
        log.info("Fetching employee with id: {}", id);
        Employee emp = empService.getEmployee(id);
        return ResponseEntity.ok(emp);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployes() {
        log.info("Fetching all employees");
        List<Employee> employees = empService.getAllEmployee();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{empId}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable String empId,
            @Valid @RequestBody Employee emp) {
        log.info("Updating employee with id: {}", empId);
        empService.updateEmployee(empId, emp);
        return ResponseEntity.noContent().build();
    }
}
