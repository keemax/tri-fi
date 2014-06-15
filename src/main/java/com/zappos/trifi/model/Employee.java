package com.zappos.trifi.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

/**
 *
 */
@DynamoDBTable(tableName = "employees")
public class Employee {

    /**
     * The unique username of the employee.
     */
    private String username;

    /**
     * The hostname for the employee.
     */
    private String hostname;

    /**
     * The real name of the employee.
     */
    private String realname;

    @DynamoDBHashKey
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "hostname-index")
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @DynamoDBAttribute
    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Employee withUsername(String username) {
        this.username = username;
        return this;
    }

    public Employee withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }
}
