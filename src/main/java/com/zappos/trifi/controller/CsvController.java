package com.zappos.trifi.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.zappos.trifi.dao.TrainingDAO;
import com.zappos.trifi.model.TrainingSignature;
import com.zappos.trifi.util.TriFiUtils;
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
import java.util.List;

/**
 *
 */
@Controller
public class CsvController {


    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    @Autowired
    TrainingDAO trainingDAO;

    @RequestMapping(value = "/csv/dimension/{dimension}/v/{version}/floor/{floor}", method = RequestMethod.GET)
    public HttpEntity<byte[]> getCsvFromTable(@PathVariable("dimension") String dimension,
                                              @PathVariable("version") String version,
                                              @PathVariable("floor") String floor) throws IOException {

        // Get all the training signatures for the version/floor combo
        PaginatedScanList<TrainingSignature> signatures = trainingDAO.getSignatureList(version, floor);

        // Turn that into a CSV string
        String csvString = TriFiUtils.getCSVString(signatures, dimension, knownRouters);

        // Send that string out as a file, son
        byte[] stringBytes = csvString.getBytes();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text","csv"));
        httpHeaders.set("Content-Disposition", "attachment; filename=training-set-" +
                dimension + "-version-" + version + ".csv");
        httpHeaders.setContentLength(stringBytes.length);
        return new HttpEntity<>(stringBytes, httpHeaders);
    }
}
