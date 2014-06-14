package com.zappos.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.model.RouterDescription;
import com.zappos.model.TrainingUpdate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by maxkeene on 6/12/14.
 */
public class TrainingDAO {
    private static final String trainingSetFloor = "trainingSet-floor";
    private static final String trainingSetX = "trainingSet-x-floor";
    private static final String trainingSetY = "trainingSet-y-floor";

    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsyncClient;

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

    private void storeRouterProfile(Map<String, RouterDescription> routers) {

        for(String router : routers.keySet()) {
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("id", new AttributeValue().withS(router));
            item.put("band", new AttributeValue().withN(String.valueOf(routers.get(router).getBand())));
            putItemAsync("routerProfiles", item);
        }

    }

    private void storeTrainingExample(String valueName, Double value, Map<String, RouterDescription> routers, int version,
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
