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

    @PostMapping("")
    public ResponseEntity<String> createPatient(@Valid @RequestBody ContactInfo con) {
        log.info("Received request to create contact information");
        conService.addContact(con);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("created");
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ContactInfo>> getPatient(@PathVariable String id) {
        log.info("Fetching contact info for patient with id: {}", id);
        List<ContactInfo> contacts = conService.getContactInfo(id);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping
    public ResponseEntity<List<ContactInfo>> getAllPatients() {
        log.info("Fetching all contacts");
        List<ContactInfo> contacts = conService.getAllContactInfo();
        return ResponseEntity.ok(contacts);
    }
}
