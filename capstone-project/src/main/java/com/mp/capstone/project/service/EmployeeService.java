package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.repository.EmployeeRepository;
import com.mp.capstone.project.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository empRepo;

    @Autowired
    PatientRepository patRepo;

    @Autowired
    MedicalRecordService recordService;

    @Transactional
    public void addEmployee(Employee emp){
        empRepo.save(emp);
    }

    @Transactional
    public Employee getEmployee(String id){
        Employee e = empRepo.findById(id).orElseThrow();
        return e;
    }

    @Transactional
    public List<Employee> getAllEmployee(){
        return empRepo.findAll();
    }

    @Transactional
    public void assignRecords(String patientId, String empId, String recId) {

        // 1. Fetch both from DB
        Patient pat = patRepo.findById(patientId).orElseThrow();
        Employee emp = empRepo.findById(empId).orElseThrow();

        List<MedicalRecord> records = new ArrayList<>();
        records = recordService.getPatientRecords(empId);

        // 2. Use the helper method to link them
        for(MedicalRecord record : records){
            emp.getRecords().add(record);
        }

        // 3. Save the OWNER (Student).
        // JPA updates the 'student_course' table automatically.
        empRepo.save(emp);
    }
}
