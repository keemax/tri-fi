package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.appengine.tools.cloudstorage.*;
import com.zappos.model.Router;
import com.zappos.util.TriFiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.HashMap;
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


    private final GcsService gcsService =
            GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

    @RequestMapping(value = "/csv/{tableName}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getCsvFromTable(@PathVariable("tableName") String tableName,
                                  @RequestParam("key") String key) throws IOException {
        List<String> rows = new ArrayList<String>();

        Map<String, Double> routerBands = new HashMap<>();
        ScanRequest routerRequest = new ScanRequest().withTableName("routerProfiles");
        ScanResult routerResult = dynamoDBAsync.scan(routerRequest);
        Map<String, AttributeValue> lastEvaluated = routerResult.getLastEvaluatedKey();
        do {
            List<Map<String, AttributeValue>> elements = routerResult.getItems();
            for(Map<String, AttributeValue> element : elements) {
                routerBands.put(element.get("id").getS(), Double.parseDouble(element.get("band").getN()));
            }
        } while (lastEvaluated != null);

        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
        ScanResult result = dynamoDBAsync.scan(scanRequest);
        lastEvaluated = result.getLastEvaluatedKey();

        do {
            List<Map<String, AttributeValue>> items = result.getItems();
            for (Map<String, AttributeValue> item : items) {

                List<String> row = new ArrayList<String>();
                row.add("\"" + item.get(key).getN()+"\"");


                for (String router : knownRouters) {
                    Router itemDescription = new Router();
                    itemDescription.setFreq(routerBands.get(router));
                    itemDescription.setStrength(Double.parseDouble(item.get(router).getN()));

                    row.add(String.valueOf(TriFiUtils.getSignalStrength(itemDescription)));
                }
                String rowString = StringUtils.join(row, ", ");
                rows.add(rowString);
            }
            scanRequest.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.scan(scanRequest);
        } while (lastEvaluated != null);

        String csvString = StringUtils.join(rows, "\n");
        byte[] stringBytes = csvString.getBytes();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text","csv"));
        httpHeaders.set("Content-Disposition", "attachment; filename=trainingSet-" +
                key + "-" + (System.currentTimeMillis() / 1000) + ".csv");
        httpHeaders.setContentLength(stringBytes.length);
        return new HttpEntity<byte[]>(stringBytes, httpHeaders);
    }

    @RequestMapping(value = "/file/upload/{tableName}")
    public @ResponseBody String upload(@PathVariable("tableName") String tableName,  @RequestParam("key") String key) throws IOException {

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
                    double e = Math.pow((27.55 - (20 * Math.log10(20)) + Math.abs(Integer.parseInt((item.get(router).getN()
                    )))) / 20.0, 10);
                    row.add(item.get(router) == null ? ".1" : String.valueOf(e));
                }
                String rowString = StringUtils.join(row, ", ");
                rows.add(rowString);
            }
            scanRequest.setExclusiveStartKey(lastEvaluated);
            result = dynamoDBAsync.scan(scanRequest);
        } while (lastEvaluated != null);
        String csvString = StringUtils.join(rows, "\n");

        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(new GcsFilename("tri-fi-training", tableName),
                        GcsFileOptions.getDefaultInstance());

        @SuppressWarnings("resource")
        ObjectOutputStream oout =
                new ObjectOutputStream(Channels.newOutputStream(outputChannel));
        oout.writeObject(csvString);
        oout.close();

        return "Done and Done.";

    }

}
