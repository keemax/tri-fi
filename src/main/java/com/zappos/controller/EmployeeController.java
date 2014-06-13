package com.zappos.controller;

import com.zappos.dao.EmployeeDAO;
import com.zappos.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by maxkeene on 6/13/14.
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

