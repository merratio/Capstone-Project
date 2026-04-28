package com.mp.capstone.project.controller;

import com.mp.capstone.project.dto.request.ContactInfoRequestDTO;
import com.mp.capstone.project.dto.response.ContactInfoResponseDTO;
import com.mp.capstone.project.service.ContactInfoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contactinfo")
@CrossOrigin(origins="*")
public class ContactInfoController {

    @Autowired
    ContactInfoService contactInfoService;

    private static final Logger log = LoggerFactory.getLogger(ContactInfoController.class);

    @PostMapping("/patient/{trn}")
    public ResponseEntity<ContactInfoResponseDTO> addContact(
            @PathVariable String trn,
            @Valid @RequestBody ContactInfoRequestDTO dto) {
        log.info("Adding contact for patient TRN: {}", trn);
        ContactInfoResponseDTO created = contactInfoService.addContact(trn, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ContactInfoResponseDTO>> getContactsByPatient(
            @PathVariable String patientId) {
        log.info("Fetching contacts for patient: {}", patientId);
        return ResponseEntity.ok(contactInfoService.getContactsByPatientId(patientId));
    }

    @GetMapping
    public ResponseEntity<List<ContactInfoResponseDTO>> getAllContacts() {
        log.info("Fetching all contacts");
        return ResponseEntity.ok(contactInfoService.getAllContactInfo());
    }

    @PutMapping("/patient/{patientId}/contact/{contactId}")
    public ResponseEntity<ContactInfoResponseDTO> updateContact(
            @PathVariable String patientId,
            @PathVariable Long contactId,
            @Valid @RequestBody ContactInfoRequestDTO dto) {
        log.info("Updating contact {} for patient: {}", contactId, patientId);
        ContactInfoResponseDTO updated = contactInfoService.updateContact(patientId, contactId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/patient/{patientId}/contact/{contactId}")
    public ResponseEntity<String> deleteContact(
            @PathVariable String patientId,
            @PathVariable Long contactId) {
        log.info("Deleting contact {} for patient: {}", contactId, patientId);
        contactInfoService.deleteContact(patientId, contactId);
        return ResponseEntity.ok("Contact deleted successfully.");
    }
}
