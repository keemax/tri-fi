package com.zappos.trifi.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.zappos.trifi.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/13/14.
 */
@Repository
public class LocationDAO {
    private static final String LOCATION_TABLE = "locations";

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;

    public Location getMostRecentLocation(String id) {
        return null;
    }

    public List<Location> getAllLocations(String id) {
        Map<String, Condition> queryConditions = new HashMap<String, Condition>();
        queryConditions.put("id", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(id)));
        return scanLocations(queryConditions);
    }

    public Location getLastLocation(String id) {
        Map<String, Condition> queryConditions = new HashMap<String, Condition>();
        queryConditions.put("hostname", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(id)));
        QueryRequest locationQuery = new QueryRequest().withTableName(LOCATION_TABLE)
                .withKeyConditions(queryConditions)
                .withScanIndexForward(false);

        QueryResult result = dynamoDBAsync.query(locationQuery);
        if (result.getItems().isEmpty()) {
            return null;
        } else {
            return mapEntryToLocation(result.getItems().get(0));
        }


    }

    public List<Location> getLocationsInBeforeDate(String id, String until) {
        Map<String, Condition> queryConditions = new HashMap<String, Condition>();
        queryConditions.put("id", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(id)));
        queryConditions.put("timestamp", new Condition().withComparisonOperator(ComparisonOperator.LE)
            .withAttributeValueList(new AttributeValue().withN(until)));
        return scanLocations(queryConditions);

    }

    public List<Location> getLocationsAfterDate(String id, String from) {
        Map<String, Condition> queryConditions = new HashMap<String, Condition>();
        queryConditions.put("id", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(id)));
        queryConditions.put("timestamp", new Condition().withComparisonOperator(ComparisonOperator.GE)
                .withAttributeValueList(new AttributeValue().withN(from)));
        return scanLocations(queryConditions);
    }

    public List<Location> getLocationsInTimeRange(String id, String from, String until) {
        Map<String, Condition> queryConditions = new HashMap<String, Condition>();
        queryConditions.put("id", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(id)));
        queryConditions.put("timestamp", new Condition().withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(new AttributeValue().withN(from),
                                        new AttributeValue().withN(until)));
        return scanLocations(queryConditions);
    }

    private List<Location> scanLocations(Map<String, Condition> conditions) {
        List<Location> results = new ArrayList<Location>();
        QueryRequest locationQuery = new QueryRequest().withTableName(LOCATION_TABLE)
                .withKeyConditions(conditions);

        QueryResult result = dynamoDBAsync.query(locationQuery);
        Map<String, AttributeValue> lastEvaluated = result.getLastEvaluatedKey();
        do {
            List<Map<String, AttributeValue>> items = result.getItems();
            for (Map<String, AttributeValue> item : items) {
                results.add(mapEntryToLocation(item));
            }
            locationQuery.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.query(locationQuery);
        } while (lastEvaluated != null);
        return results;
    }

    private Location mapEntryToLocation(Map<String, AttributeValue> item) {
        Location location = new Location();
        location.setFloor(Double.valueOf(item.get("floor").getN()));
        location.setX(Double.valueOf(item.get("x").getN()));
        location.setY(Double.valueOf(item.get("y").getN()));
        location.setTimestamp(item.get("timestamp").getN());
        return location;
    }
}
