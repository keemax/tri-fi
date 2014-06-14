package com.zappos.trifi.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.trifi.model.Router;
import com.zappos.trifi.model.TrainingSignature;
import com.zappos.trifi.model.TrainingUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is the
 */
@Repository
public class TrainingDAO {
    private static final String trainingSetFloor = "trainingSet-floor";
    private static final String trainingSetX = "trainingSet-x-floor";
    private static final String trainingSetY = "trainingSet-y-floor";

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsyncClient;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;


    public PaginatedScanList<TrainingSignature> getSignatureList(String version, String floor) {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Map<String, Condition> filter = new HashMap<>();

        // Add version filter
        if(version != null) {
            filter.put("version", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList
                    (new AttributeValue().withS(version)));
            scanExpression.setScanFilter(filter);
        }

        // Add floor filter
        if(floor != null) {
            filter.put("floor", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList
                    (new AttributeValue().withN(floor)));
            scanExpression.setScanFilter(filter);
        }

        return dynamoDBMapper.scan(TrainingSignature.class, scanExpression);
    }



    public void storeFloor(TrainingUpdate update) {
        storeTrainingExample("floor",
                update.getLocation().getFloor().doubleValue(),
                update.getRouterSignature().getRouters(),
                update.getVersion(),
                trainingSetFloor);

    }
    public void storeX(TrainingUpdate update) {
        String tableName = trainingSetX + update.getLocation().getFloor();
        storeTrainingExample("x",
                update.getLocation().getX(),
                update.getRouterSignature().getRouters(),
                update.getVersion(),
                tableName);
    }
    public void storeY(TrainingUpdate update) {
        String tableName = trainingSetY + update.getLocation().getFloor();
        storeTrainingExample("y",
                update.getLocation().getY(),
                update.getRouterSignature().getRouters(),
                update.getVersion(),
                tableName);
    }

    public void storeRouters(TrainingUpdate update) {
        storeRouterProfile(update.getRouterSignature().getRouters());
    }

    private void storeRouterProfile(Map<String, Router> routers) {

        for(String router : routers.keySet()) {
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("id", new AttributeValue().withS(router));
            item.put("band", new AttributeValue().withN(String.valueOf(routers.get(router).getFreq())));
            putItemAsync("routerProfiles", item);
        }

    }

    private void storeTrainingExample(String valueName, Double value, Map<String, Router> routers, int version,
                                      String tableName) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
        item.put("version", new AttributeValue().withN(String.valueOf(version)));
        item.put(valueName, new AttributeValue().withN(String.valueOf(value)));

        for (String router : routers.keySet()) {
            item.put(router, new AttributeValue().withN(String.valueOf(routers.get(router).getStrength())));
        }
        putItemAsync(tableName, item);
    }

    private void putItemAsync(String tableName, Map<String, AttributeValue> item) {
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(tableName)
                .withItem(item);
        dynamoDBAsyncClient.putItemAsync(putItemRequest);
    }
}
