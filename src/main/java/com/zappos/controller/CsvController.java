package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.zappos.model.TrainingSignature;
import com.zappos.util.TriFiUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Controller
public class CsvController {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    @RequestMapping(value = "/csv/dimension/{dimension}/v/{version}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getCsvFromTable(@PathVariable("dimension") String dimension,
                                              @PathVariable("version") String version) throws IOException {

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        Map<String, Condition> filter = new HashMap<>();
        filter.put("version", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList
                (new AttributeValue().withS(version)));
        scanExpression.setScanFilter(filter);

        PaginatedScanList<TrainingSignature> scanResult = dynamoDBMapper.scan(TrainingSignature.class,
                scanExpression);



        List<String> rows = new ArrayList<>();
        for(TrainingSignature trainingSignature : scanResult) {
            List<String> row = new ArrayList<>();
            Double dim = TriFiUtils.getDimensionValue(trainingSignature.getLocation(), dimension);
            if(dim == null) {
                throw new IllegalArgumentException("Dimension must be 'x' 'X' 'y' 'Y' 'floor' 'Floor. Even though it " +
                        "would be more efficient to check this before scanning the database (probably) I didn't " +
                        "because I wanted to make you suffer... horribly.");
            }
            // First element is the X Y Floor dimension value
            row.add(String.valueOf(dim));
            for(String router : knownRouters) {
                row.add(String.valueOf(TriFiUtils.getSignalStrength(trainingSignature.getRouterSignature().getRouters()
                        .get(router))));
            }
            String rowString = StringUtils.join(row, ", ");
            rows.add(rowString);
        }

        String csvString = StringUtils.join(rows, "\n");
        byte[] stringBytes = csvString.getBytes();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text","csv"));
        httpHeaders.set("Content-Disposition", "attachment; filename=training-set-" +
                dimension + "-version-" + version + ".csv");
        httpHeaders.setContentLength(stringBytes.length);
        return new HttpEntity<>(stringBytes, httpHeaders);
    }
}
