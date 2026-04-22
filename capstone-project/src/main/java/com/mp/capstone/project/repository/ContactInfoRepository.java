package com.mp.capstone.project.repository;

import com.mp.capstone.project.entity.ContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactInfoRepository extends JpaRepository<ContactInfo, Long> {
    List<ContactInfo> findByPat_Trn(String trn);
    Optional<ContactInfo> findByContactIdAndPat_Trn(Long contactId, String trn);
    boolean existsByPat_TrnAndContact(String trn, String contact);
}

