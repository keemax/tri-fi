package com.zappos.trifi.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.model.TrainingSignature;
import com.zappos.trifi.util.TriFiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 */
@Repository
public class LocationDAO {

    @Deprecated
    private static final String LOCATION_TABLE = "test-locations";

    private static final String BEGINNING_OF_TIME = "0000-00-00T00-00-00";

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;

    @Resource
    private DynamoDBMapper dynamoDBMapper;


    public Location getMostRecentLocation(String id) {
        return null;
    }


    public Location getLatestLocationForHost(String hostname) {
        Location rval = new Location().withTimestamp(BEGINNING_OF_TIME);

        for(Location l : getAllLocationsForHost(hostname)) {
            if(rval.getTimestamp().compareTo(l.getTimestamp()) < 0) {
                rval = l;
            }
        }

        return rval.getTimestamp().equals(BEGINNING_OF_TIME) ? null : rval;
    }

    public Location getAvgLatestLocationForHost(String hostname) {


        List<Location> recentLocations = getAllLocationsForHostInRange(hostname, TriFiUtils.getTimestampMinutesBefore
                        (5),
                TriFiUtils.getTimestamp());

        if(recentLocations.size() < 1) {
            return null;
        }

        List<Location> regularListUgh = new ArrayList<>();
        for(Location l : recentLocations) {
            regularListUgh.add(l);
        }

        Collections.sort(regularListUgh, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        Double x = 0.0;
        Double y = 0.0;
        Double floor = 0.0;

        Double restrictedFloor = regularListUgh.get(0).getFloor();

        int cnt = 0;

        for(Location l : regularListUgh) {
            if(TriFiUtils.isFloorEqual(l.getFloor(), restrictedFloor)) {
                System.out.println("Using x: " + l.getX() + " y: " + l.getY() + " f: " + l.getFloor() + " T: " + l
                        .getTimestamp
                        ());
                x += l.getX();
                y += l.getY();
                floor += l.getFloor();
                cnt++;
                if(cnt > 3) {
                    break;
                }
            }
        }

        System.out.println();
        x /= cnt;
        y /= cnt;
        floor /= cnt;

        Location rval = new Location();
        rval.setOriginRouterSignature(regularListUgh.get(0).getOriginRouterSignature());
        rval.setOriginModel(regularListUgh.get(0).getOriginModel());
        rval.setHostname(regularListUgh.get(0).getHostname());
        rval.setTimestamp(regularListUgh.get(0).getTimestamp());
        rval.setX(x);
        rval.setY(y);
        rval.setFloor(floor);

        return rval;
    }

    public List<Location> getAllLocationsForHost(String hostname) {
        return dynamoDBMapper.query(Location.class, new DynamoDBQueryExpression<Location>().withHashKeyValues(new
                Location()
                .withHostname(hostname)));
    }



    public List<Location> getAllLocationsForHostInRange(String hostname, String start, String end) {
        return dynamoDBMapper.query(Location.class, new DynamoDBQueryExpression<Location>().withHashKeyValues(new
                Location().withHostname(hostname)).withRangeKeyCondition("timestamp",
                new Condition().withComparisonOperator(ComparisonOperator.BETWEEN).withAttributeValueList(new
                        AttributeValue().withS(start), new AttributeValue().withS(end))
        ));
    }


    public List<Location> getLatestLocationsForFloor(String floor, Integer timeToLookBack) {
        Double dFloor = Double.parseDouble(floor);
        List<Location> timeSinceLocations = getAllLocationsInTimeRange(TriFiUtils.getTimestampMinutesBefore
                        (timeToLookBack), TriFiUtils.getTimestamp());
        List<Location> latestLocationsForFloor = new ArrayList<>();
        Set<String> seenHosts = new HashSet<>();


        List<Location> regularListUgh = new ArrayList<>();
        for(Location l : timeSinceLocations) {
            regularListUgh.add(l);
        }

        Collections.sort(regularListUgh, new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });


        for(Location l : regularListUgh) {
            if (TriFiUtils.isFloorEqual(l.getFloor(), dFloor)) {
                if (!seenHosts.contains(l.getHostname())) {
                    seenHosts.add(l.getHostname());
                    latestLocationsForFloor.add(l);
                }
            }
        }
        return latestLocationsForFloor;
    }


    public List<Location> getAllLocationsInTimeRange(String start, String end) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Map<String, Condition> filter = new HashMap<>();

        filter.put("timestamp", new Condition().withComparisonOperator(ComparisonOperator.BETWEEN)
                .withAttributeValueList(new AttributeValue().withS(start), new AttributeValue().withS(end)));

        scanExpression.setScanFilter(filter);
        return dynamoDBMapper.scan(Location.class, scanExpression);
    }




    /*** older ***/
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
