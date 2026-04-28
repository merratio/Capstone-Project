package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.request.MedicalRecordRequestDTO;
import com.mp.capstone.project.dto.response.MedicalRecordResponseDTO;
import com.mp.capstone.project.exception.BlockchainException;
import com.mp.capstone.project.exception.DataIntegrityException;
import com.mp.capstone.project.exception.ResourceNotFoundException;
import com.mp.capstone.project.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicalrecords")
@CrossOrigin(origins="*")
public class MedicalRecordController {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    @PostMapping("/{patId}")
    public ResponseEntity<Map<String, String>> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO dto,
            @PathVariable String patId) {
        log.info("Received request to create medical record for patient: {}", patId);
        String generated = medicalRecordService.createMedicalRecord(dto, patId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", generated));
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecord(@PathVariable String id) {
        log.info("Fetching record with id: {}", id);
        return ResponseEntity.ok(medicalRecordService.getRecord(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllMedicalRecords() {
        log.info("Fetching all records");
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{patId}")
    public ResponseEntity<Void> updatePatientRecord(
            @PathVariable String patId,
            @Valid @RequestBody MedicalRecordRequestDTO dto) {
        log.info("Updating medical record of patient with id: {}", patId);
        medicalRecordService.updatePatientRecord(patId, dto);
        return ResponseEntity.noContent().build();
    }

    // ─── Exception Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
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

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityViolation(DataIntegrityException e) {
        log.error("Data integrity violation: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(),
                        "Patient data integrity check failed. The record may have been tampered with.", null));
    }

    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<ErrorResponse> handleBlockchain(BlockchainException e) {
        log.error("Blockchain error during patient operation", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.BAD_GATEWAY.value(),
                        "Blockchain service unavailable. Please try again later.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception in MedicalRecordController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred", null));
    }

    // ─── Error Response Record ─────────────────────────────────────────────────

    public record ErrorResponse(
            int status,
            String message,
            List<String> details,
            Instant timestamp
    ) {
        public ErrorResponse(int status, String message, List<String> details) {
            this(status, message, details, Instant.now());
        }
    }
}
