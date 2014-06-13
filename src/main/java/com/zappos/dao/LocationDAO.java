package com.zappos.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import com.zappos.model.Location;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/13/14.
 */
public class LocationDAO {
    private static final String LOCATION_TABLE = "locationUpdates";

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
                Location location = new Location();
                location.setFloor(Integer.valueOf(item.get("floor").getN()));
                location.setX(Double.valueOf(item.get("x").getN()));
                location.setY(Double.valueOf(item.get("y").getN()));
                location.setTimestamp(item.get("timestamp").getN());
                results.add(location);
            }
            locationQuery.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.query(locationQuery);
        } while (lastEvaluated != null);
        return results;
    }
}
