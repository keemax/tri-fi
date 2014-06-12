package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.model.RouterSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class UpdateController {
    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsyncClient;

    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    private static final String locationUpdateTable = "locationUpdates";

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeUpdate(@RequestBody RouterSignature routerSignature) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(routerSignature.getId()));
        item.put("timestamp", new AttributeValue().withN(String.valueOf(System.currentTimeMillis())));

        Map<String, Double> routers = routerSignature.getRouters();
        for (String router : knownRouters) {
            Double strength = routers.get(router) == null ? -100 : routers.get(router);
            item.put(router, new AttributeValue().withN(String.valueOf(strength)));
        }

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(locationUpdateTable)
                .withItem(item);
        dynamoDBAsyncClient.putItemAsync(putItemRequest);
        return "sweet request bro";
    }
}
