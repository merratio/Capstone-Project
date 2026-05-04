package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.request.LoginRequestDTO;
import com.mp.capstone.project.dto.response.LoginResponseDTO;
import com.mp.capstone.project.exception.Auth0Exception;
import com.mp.capstone.project.service.Auth0LoginService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final Auth0LoginService loginService;

    public AuthController(Auth0LoginService loginService) {
        this.loginService = loginService;
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    /**
     * Authenticates an employee and returns a JWT access token.
     *
     * <p>The token is issued by Auth0 and contains the employee's role in a
     * custom claim ({@code https://capstone-api/roles}). Spring Security
     * validates the token on every subsequent request and enforces the role rules
     * defined in {@link com.mp.capstone.project.config.SecurityConfig}.
     *
     * @param dto employee credentials
     * @return 200 with {@link LoginResponseDTO} on success, 401 on bad credentials
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Login request received for email: {}", dto.getEmail());
        LoginResponseDTO response = loginService.login(dto);
        return ResponseEntity.ok(response);
    }

    // ─── Exception Handlers ───────────────────────────────────────────────────

    /**
     * Translates Auth0 login errors (bad credentials, disabled user, etc.)
     * into a clean 401 Unauthorized response.
     */
    @ExceptionHandler(Auth0Exception.class)
    public ResponseEntity<ErrorResponse> handleAuth0Exception(Auth0Exception e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authentication failed: " + e.getMessage(),
                        null));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unexpected error in AuthController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred", null));
    }

    // ─── Error Response Record ─────────────────────────────────────────────────

    public record ErrorResponse(
            int status,
            String message,
            List<String> details,
            String timestamp          // ← CHANGED from Instant to String
    ) {
        public ErrorResponse(int status, String message, List<String> details) {
            this(status, message, details, Instant.now().toString());  // ← CHANGED
        }
    }
}