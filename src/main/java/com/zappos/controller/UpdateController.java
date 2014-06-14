package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.zappos.model.RouterDescription;
import com.zappos.model.RouterSignature;
import com.zappos.prediction.Predictor;
import com.zappos.util.TriFiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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

    @Autowired
    private Predictor predictor;

    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    private static final String locationUpdateTable = "locationUpdates";

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeUpdate(@RequestBody RouterSignature routerSignature) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("id", new AttributeValue().withS(routerSignature.getId()));
        item.put("timestamp", new AttributeValue().withN(String.valueOf(System.currentTimeMillis())));

        Map<String, RouterDescription> routers = routerSignature.getRouters();
        for (String router : knownRouters) {
            Double strength = TriFiUtils.getSignal(routers.get(router));
            item.put(router, new AttributeValue().withN(String.valueOf(strength)));
        }

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(locationUpdateTable)
                .withItem(item);
        dynamoDBAsyncClient.putItemAsync(putItemRequest);
        return "sweet request bro";
    }

    @RequestMapping("/predict/{model}")
    @ResponseBody
    public String predict(@PathVariable("model") String model, @RequestBody RouterSignature routerSignature) throws
            IOException,
            GeneralSecurityException {
        Map<String, RouterDescription> routers = routerSignature.getRouters();
        List<Object> input = new ArrayList<Object>();
        for (String router : knownRouters) {
            input.add(TriFiUtils.getSignal(routers.get(router)));
        }
        System.out.println(input);
        return "x = " + predictor.predict(input, "x-" + model) + " y = " + predictor.predict(input,
                "y-" + model) + " floor = " + predictor.predict(input, "floor-" + model);

    }
}
