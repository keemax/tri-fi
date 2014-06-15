package com.zappos.trifi.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@DynamoDBTable(tableName = "test-locations-2")
public class Location {

    /**
     * Hostname for this location.
     */
    private String hostname;

    /**
     * Timestamp for the creation of this location.
     */
    private String timestamp;

    /**
     * X-coordinate.
     */
    private Double x;

    /**
     * Y-coordinate.
     */
    private Double y;

    /**
     * Floor-coordinate.
     */
    private Double floor;

    @DynamoDBHashKey
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @DynamoDBRangeKey
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute
    public Double getX() {
        return x;
    }
    public void setX(Double x) {
        this.x = x;
    }

    @DynamoDBAttribute
    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @DynamoDBAttribute
    public Double getFloor() {
        return floor;
    }
    public void setFloor(Double floor) {
        this.floor = floor;
    }


    /**
     * Method to make DynamoMapper stuff easier.
     * @param hostname
     * @return
     */
    public Location withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }
}
