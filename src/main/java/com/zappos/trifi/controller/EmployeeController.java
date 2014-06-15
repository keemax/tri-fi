package com.zappos.trifi.controller;

import com.zappos.trifi.dao.EmployeeDAO;
import com.zappos.trifi.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestController for getting JSON {@link com.zappos.trifi.model.Employee} blobs.
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeDAO employeeDAO;

    @RequestMapping("/employee")
    public Employee getEmployee(@RequestParam(value = "username", required = false) String username,
                                          @RequestParam(value = "hostname", required = false) String hostname) {
        if(username != null) {
            return employeeDAO.getEmployeeByUsername(username);
        } else if (hostname != null) {
            return employeeDAO.getEmployeeByHostname(hostname);
        }
        return null;
    }

    @RequestMapping("/employee/all")
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees1();
    }
}

