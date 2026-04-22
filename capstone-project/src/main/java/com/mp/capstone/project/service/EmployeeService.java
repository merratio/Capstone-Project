package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.entity.MedicalRecord;
import com.mp.capstone.project.entity.Patient;
import com.mp.capstone.project.exception.ResourceNotFoundException;
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
    public void assignPatientRecordsToEmployee(String patientTrn, String empId) {
        Employee emp = empRepo.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));

        List<MedicalRecord> records = recordService.getPatientRecords(patientTrn);

        for (MedicalRecord record : records) {
            emp.addRecord(record);
        }

        empRepo.save(emp);
    }
}
