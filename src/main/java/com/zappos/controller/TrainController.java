package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.model.RouterSignature;
import com.zappos.model.TrainingUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class TrainController {
    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsyncClient;

    private static final String trainingSetFloor = "trainingSet-floor";
    private static final String trainingSetX = "trainingSet-x-floor";
    private static final String trainingSetY = "trainingSet-y-floor";


    @RequestMapping(value = "/train", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeTrainingUpdate(@RequestBody TrainingUpdate update) {
        storeFloor(update);
        storeX(update);
        storeY(update);
        return "sweet request bro";
    }

    private void storeFloor(TrainingUpdate update) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
        item.put("floor", new AttributeValue().withN(String.valueOf(update.getLocation().getFloor())));
        putItemAsync(trainingSetFloor, item);
    }

    private void storeX(TrainingUpdate update) {
        Integer floor = update.getLocation().getFloor();
        String tableName = trainingSetX + floor.toString();
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
        item.put("x", new AttributeValue().withN(String.valueOf(update.getLocation().getX())));
        putItemAsync(tableName, item);
    }

    private void storeY(TrainingUpdate update) {
        Integer floor = update.getLocation().getFloor();
        String tableName = trainingSetY + floor.toString();
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
        item.put("y", new AttributeValue().withN(String.valueOf(update.getLocation().getY())));
        putItemAsync(tableName, item);
    }

    private void putItemAsync(String tableName, Map<String, AttributeValue> item) {
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(tableName)
                .withItem(item);
        dynamoDBAsyncClient.putItemAsync(putItemRequest);
    }
}
