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
    private Integer floorInt;

    /**
     * The {@link com.zappos.trifi.model.RouterSignature} that was used to predict this location.
     */
    private String originRouterSignature;

    /**
     * The model that was used to predict this location.
     */
    private String originModel;

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

    @DynamoDBIgnore
    public Integer getFloorInt() {
        return floorInt;
    }

    public void setFloorInt(Integer floorInt) {
        this.floorInt = floorInt;
    }

    @DynamoDBAttribute
    public String getOriginRouterSignature() {
        return originRouterSignature;
    }
    public void setOriginRouterSignature(String originRouterSignature) {
        this.originRouterSignature = originRouterSignature;
    }

    @DynamoDBAttribute
    public String getOriginModel() {
        return originModel;
    }
    public void setOriginModel(String originModel) {
        this.originModel = originModel;
    }

    /**
     * Builder type methods to set properties on the fly.
     */
    public Location withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public Location withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Location withFloorInt(Integer floorInt) {
        this.floorInt = floorInt;
        return this;
    }


}
