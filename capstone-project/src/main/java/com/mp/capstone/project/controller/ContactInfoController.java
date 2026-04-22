package com.mp.capstone.project.controller;

import com.mp.capstone.project.entity.ContactInfo;
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
    ContactInfoService conService;

    private static final Logger log = LoggerFactory.getLogger(ContactInfoController.class);

    public ResponseEntity<ContactInfo> addContact(
            @PathVariable String patientId,
            @RequestBody ContactInfo contactInfo) {
        ContactInfo created = contactInfoService.addContact(patientId, contactInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ContactInfo>> getContactsByPatient(@PathVariable String patientId) {
        List<ContactInfo> contacts = contactInfoService.getContactsByPatientId(patientId);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping
    public ResponseEntity<List<ContactInfo>> getAllPatients() {
        log.info("Fetching all contacts");
        List<ContactInfo> contacts = conService.getAllContactInfo();
        return ResponseEntity.ok(contacts);
    }

     // PUT update an existing contact
    // PUT /api/contacts/patient/1/contact/3
    @PutMapping("/patient/{patientId}/contact/{contactId}")
    public ResponseEntity<ContactInfo> updateContact(
            @PathVariable Long patientId,
            @PathVariable Long contactId,
            @RequestBody ContactInfo contactInfo) {
        ContactInfo updated = contactInfoService.updateContact(patientId, contactId, contactInfo);
        return ResponseEntity.ok(updated);
    }

    // DELETE a contact
    // DELETE /api/contacts/patient/1/contact/3
    @DeleteMapping("/patient/{patientId}/contact/{contactId}")
    public ResponseEntity<String> deleteContact(
            @PathVariable Long patientId,
            @PathVariable Long contactId) {
        contactInfoService.deleteContact(patientId, contactId);
        return ResponseEntity.ok("Contact deleted successfully.");
    }
}
