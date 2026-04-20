package com.mp.capstone.project.repository;

import com.mp.capstone.project.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactInfoRepository extends JpaRepository<ContactInfo, String> {
}
