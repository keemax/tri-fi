package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/12/14.
 */
@RestController
public class RawDataController {
    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;


    @RequestMapping(value = "/scan/{tableName}", method = RequestMethod.GET)
    public List<Map<String, AttributeValue>> scanTable(@PathVariable("tableName") String tableName, @RequestParam("limit") Integer limit) {
        ScanRequest scanRequest = new ScanRequest().withTableName(tableName).withLimit(limit);
        ScanResult result = dynamoDBAsync.scan(scanRequest);
        return result.getItems();
    }

}
