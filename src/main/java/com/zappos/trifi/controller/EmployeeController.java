package com.zappos.trifi.controller;

import com.zappos.trifi.dao.EmployeeDAO;
import com.zappos.trifi.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController for getting JSON {@link com.zappos.trifi.model.Employee} blobs.
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeDAO employeeDAO;

    @RequestMapping("/employee/{id}")
    public Employee getEmployee(@PathVariable("id") String id) {
        return employeeDAO.getEmployee(id);
    }

    @RequestMapping("/employee/all")
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
}

