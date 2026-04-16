package com.mp.capstone.project.repository;

import com.mp.capstone.project.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, String> {
}
