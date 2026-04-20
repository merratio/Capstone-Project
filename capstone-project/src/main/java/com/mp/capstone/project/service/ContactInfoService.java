package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.ContactInfo;
import com.mp.capstone.project.repository.ContactInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContactInfoService {
    @Autowired
    ContactInfoRepository conRepo;

    public void addContact(ContactInfo contact){
        conRepo.save(contact);
    }

    public List<ContactInfo> getContactInfo(String patientId){
        List<ContactInfo> con = conRepo.findById(patientId)
                .stream()
                .toList();
        return con;
    }

    public List<ContactInfo> getAllContactInfo(){
        List<ContactInfo> contacts = conRepo.findAll();
        return contacts;

    }
}
