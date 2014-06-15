package com.zappos.trifi.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.zappos.trifi.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeDAO.class);

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public List<Employee> getAllEmployees1() {
        return dynamoDBMapper.scan(Employee.class, new DynamoDBScanExpression());
    }

    public Employee getEmployeeByUsername(String username) {
        return dynamoDBMapper.load(Employee.class, username);
    }

    public Employee getEmployeeByHostname(String hostname) {
        List<Employee> employee = dynamoDBMapper.query(Employee.class, new DynamoDBQueryExpression<Employee>()
                .withHashKeyValues(new Employee().withHostname(hostname))
                .withIndexName("hostname-index")
                .withConsistentRead(false));
        return employee.isEmpty() ? null : employee.get(0);
    }



    public List<Employee> getAllEmployees() {
        List<Employee> results = new ArrayList<Employee>();
        ScanRequest employeeScan = new ScanRequest().withTableName(EMPLOYEE_TABLE);

        ScanResult result = dynamoDBAsync.scan(employeeScan);
        Map<String, AttributeValue> lastEvaluated = result.getLastEvaluatedKey();
        do {
            List<Map<String, AttributeValue>> items = result.getItems();
            for (Map<String, AttributeValue> item : items) {
                Employee employee = new Employee();
                employee.setHostname(item.get("id").getS());
                employee.setRealname(item.get("name").getS());
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
        employee.setRealname(resultItem.get("name").getS());
        employee.setHostname(id);
        return employee;
    }
}
