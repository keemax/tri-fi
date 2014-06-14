package com.zappos.trifi.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.zappos.trifi.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Repository
public class EmployeeDAO {
    private static final String EMPLOYEE_TABLE = "employees";

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;

    public List<Employee> getAllEmployees() {
        List<Employee> results = new ArrayList<Employee>();
        ScanRequest employeeScan = new ScanRequest().withTableName(EMPLOYEE_TABLE);

        ScanResult result = dynamoDBAsync.scan(employeeScan);
        Map<String, AttributeValue> lastEvaluated = result.getLastEvaluatedKey();
        do {
            List<Map<String, AttributeValue>> items = result.getItems();
            for (Map<String, AttributeValue> item : items) {
                Employee employee = new Employee();
                employee.setId(item.get("id").getS());
                employee.setName(item.get("name").getS());
                results.add(employee);
            }
            employeeScan.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.scan(employeeScan);
        } while (lastEvaluated != null);
        return results;
    }

    public Employee getEmployee(String id) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("id", new AttributeValue().withS(id));
        GetItemRequest getItemRequest = new GetItemRequest().withTableName(EMPLOYEE_TABLE)
                .withKey(key);
        GetItemResult result = dynamoDBAsync.getItem(getItemRequest);
        Map<String, AttributeValue> resultItem = result.getItem();
        if (resultItem == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setName(resultItem.get("name").getS());
        employee.setId(id);
        return employee;
    }
}
