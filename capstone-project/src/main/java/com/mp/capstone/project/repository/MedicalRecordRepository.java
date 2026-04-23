package com.mp.capstone.project.repository;

import com.mp.capstone.project.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {
    //Optional<MedicalRecord> findByPatientId(String patId);
    List<MedicalRecord> findByPatientTrn(String patientTrn);
}
