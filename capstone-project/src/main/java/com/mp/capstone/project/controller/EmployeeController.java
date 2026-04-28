package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.request.EmployeeCreateRequestDTO;
import com.mp.capstone.project.dto.request.EmployeeUpdateRequestDTO;
import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.EmployeeResponseDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for Employee operations.
 *
 * <p>HTTP-layer access control is enforced by {@link com.mp.capstone.project.config.SecurityConfig}.
 * Additional business-rule checks (e.g. role cannot edit records) are enforced in
 * {@link EmployeeService}.
 *
 * <pre>
 *  POST   /api/employees                            → ADMIN
 *  GET    /api/employees                            → ADMIN
 *  GET    /api/employees/{id}                       → authenticated
 *  PUT    /api/employees/{empId}                    → ADMIN
 *  DELETE /api/employees/{empId}                    → ADMIN
 *
 *  GET    /api/employees/{empId}/records            → ADMIN
 *  GET    /api/employees/{empId}/records/{recordId} → authenticated (own assignments)
 *  PUT    /api/employees/{empId}/records/{recordId} → ADMIN, DOCTOR, NURSE
 *  DELETE /api/employees/{empId}/records/{recordId} → ADMIN
 * </pre>
 */
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    EmployeeService empService;

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    // ═══════════════════════════════════════════════════════════════════════════
    // Employee CRUD
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Creates a new employee and registers them as a user in Auth0.
     * Returns the local employee ID and the Auth0 user_id.
     * Permission: ADMIN.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createEmployee(
            @Valid @RequestBody EmployeeCreateRequestDTO dto) {
        log.info("Creating employee with role: {}", dto.getRole());
        String empId = empService.addEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", empId));
    }

    /**
     * Returns all employees. Permission: ADMIN.
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        log.info("Fetching all employees");
        return ResponseEntity.ok(empService.getAllEmployeeDTO());
    }

    /**
     * Returns a single employee by local ID.
     * Permission: authenticated (ADMIN or the employee themselves).
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable String id) {
        log.info("Fetching employee: {}", id);
        return ResponseEntity.ok(empService.getEmployeeDTO(id));
    }

    /**
     * Updates employee profile (name, role, gender, religion, dob).
     * Auth0 credentials are not affected. Permission: ADMIN.
     */
    @PutMapping("/{empId}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable String empId,
            @Valid @RequestBody EmployeeUpdateRequestDTO dto) {
        log.info("Updating employee: {}", empId);
        empService.updateEmployee(empId, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes an employee from the local DB. Auth0 account is not removed.
     * Permission: ADMIN.
     */
    @DeleteMapping("/{empId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String empId) {
        log.info("Deleting employee: {}", empId);
        empService.deleteEmployee(empId);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Medical Record access scoped to an Employee
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Returns all medical records assigned to an employee.
     * Permission: ADMIN.
     */
    @GetMapping("/{empId}/records")
    public ResponseEntity<Set<MedicalRecordResponseDTO>> getEmployeeRecords(
            @PathVariable String empId) {
        log.info("Fetching all records for employee: {}", empId);
        return ResponseEntity.ok(empService.getEmployeeRecords(empId));
    }

    /**
     * Returns a single record if it is assigned to this employee.
     * Blockchain integrity is verified before returning.
     * Permission: any authenticated employee (own assignments only).
     */
    @GetMapping("/{empId}/records/{recordId}")
    public ResponseEntity<MedicalRecordResponseDTO> getAssignedRecord(
            @PathVariable String empId,
            @PathVariable String recordId) {
        log.info("Employee {} fetching record {}", empId, recordId);
        return ResponseEntity.ok(empService.getAssignedRecord(empId, recordId));
    }

    /**
     * Updates a record assigned to this employee. Recomputes and stores
     * a new blockchain hash after the update.
     * Permission: ADMIN, DOCTOR, NURSE. RECEPTIONIST receives 403.
     */
    @PutMapping("/{empId}/records/{recordId}")
    public ResponseEntity<Void> updateAssignedRecord(
            @PathVariable String empId,
            @PathVariable String recordId,
            @Valid @RequestBody MedicalRecordRequestDTO dto) {
        log.info("Employee {} updating record {}", empId, recordId);
        empService.updateAssignedRecord(empId, recordId, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes the assignment between an employee and a record.
     * Does not delete the record. Permission: ADMIN.
     */
    @DeleteMapping("/{empId}/records/{recordId}")
    public ResponseEntity<Void> unassignRecord(
            @PathVariable String empId,
            @PathVariable String recordId) {
        log.info("Unassigning record {} from employee {}", recordId, empId);
        empService.unassignRecord(empId, recordId);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Exception Handlers
    // ═══════════════════════════════════════════════════════════════════════════

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        log.warn("Not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(SecurityException e) {
        log.warn("Forbidden: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException e) {
        log.warn("Bad request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred", null));
    }

    public record ErrorResponse(int status, String message, List<String> details, Instant timestamp) {
        public ErrorResponse(int status, String message, List<String> details) {
            this(status, message, details, Instant.now());
        }
    }
}