package com.mp.capstone.project.service;

import com.mp.capstone.project.entity.Employee;
import com.mp.capstone.project.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmployeeService {
    @Autowired
    EmployeeRepository empRepo;

    public void addEmployee(Employee emp){
        empRepo.save(emp);
    }

    public Employee getEmployee(String id){
        Employee e = empRepo.findById(id).orElseThrow();
        return e;
    }

    public List<Employee> getAllEmployee(){
        return empRepo.findAll();
    }
}
