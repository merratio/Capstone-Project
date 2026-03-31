package controller;

import dto.PatientDto;
import exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PatientService;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Void> createPatient(@Valid @RequestBody PatientDto dto) {
        log.info("Creating patient with id: {}", dto.getId());
        patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ─── Read ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable String id) {
        log.info("Fetching patient with id: {}", id);
        PatientDto patient = patientService.getPatient(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        log.info("Fetching all patients");
        List<PatientDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientDto dto) {
        log.info("Updating patient with id: {}", id);
        patientService.updatePatient(id, dto);
        return ResponseEntity.noContent().build();
    }

    // ─── Exception Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException e) {

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(exception.DataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityViolation(exception.DataIntegrityException e) {
        log.error("Data integrity violation: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(),
                        "Patient data integrity check failed. The record may have been tampered with.",
                        null));
    }

    @ExceptionHandler(exception.BlockchainException.class)
    public ResponseEntity<ErrorResponse> handleBlockchain(exception.BlockchainException e) {
        log.error("Blockchain error during patient operation", e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(HttpStatus.BAD_GATEWAY.value(),
                        "Blockchain service unavailable. Please try again later.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception in PatientController", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
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
        // Convenience constructor — timestamp defaults to now
        public ErrorResponse(int status, String message, List<String> details) {
            this(status, message, details, Instant.now());
        }
    }
}