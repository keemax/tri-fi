package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.util.StringInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/12/14.
 */
@Controller
public class CsvController {
    
    @Autowired
    private AmazonDynamoDBAsync dynamoDBAsync;
    
    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    @RequestMapping(value = "/csv/{tableName}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getCsvFromTable(@PathVariable("tableName") String tableName,
                                  @RequestParam("key") String key) throws IOException {
        List<String> rows = new ArrayList<String>();

        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
        ScanResult result = dynamoDBAsync.scan(scanRequest);
        Map<String, AttributeValue> lastEvaluated = result.getLastEvaluatedKey();
        do {
            List<Map<String, AttributeValue>> items = result.getItems();
            for (Map<String, AttributeValue> item : items) {
                List<String> row = new ArrayList<String>();
                row.add(item.get(key).getN());
                for (String router : knownRouters) {
                    row.add(item.get(router) == null ? "-100" : item.get(router).getN());
                }
                String rowString = StringUtils.join(row, ", ");
                rows.add(rowString);
            }
            scanRequest.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.scan(scanRequest);
        }
        while (lastEvaluated != null);

        String csvString = StringUtils.join(rows, "\n");
        byte[] stringBytes = csvString.getBytes();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text","csv"));
        httpHeaders.set("Content-Disposition", "attachment; filename=trainingSet.csv");
        httpHeaders.setContentLength(stringBytes.length);
        return new HttpEntity<byte[]>(stringBytes, httpHeaders);
    }
    
    
}
