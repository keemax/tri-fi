package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.model.LocationProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class UpdateController {
    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsyncClient;

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeUpdate(@RequestBody LocationProfile locationProfile) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(locationProfile.getId()));
        item.put("timestamp", new AttributeValue().withN(String.valueOf(System.currentTimeMillis())));
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName("locationUpdates")
                .withItem(item);
        dynamoDBAsyncClient.putItemAsync(putItemRequest);
        return "sweet request bro";
    }
}
