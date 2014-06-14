package com.zappos.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

/**
 *
 */
@DynamoDBTable(tableName = "locations")
public class Location {

    /**
     * Uuid for this location
     */
    private String id;

    /**
     * Hostname for this location.
     */
    private String hostname;

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

    /**
     * Timestamp for the creation of this location.
     */
    private String timestamp;

    @DynamoDBHashKey
    @DynamoDBIndexHashKey
    @DynamoDBAutoGeneratedKey
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    @DynamoDBIndexRangeKey
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
