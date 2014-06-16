package com.zappos.trifi.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.model.Router;
import com.zappos.trifi.model.RouterSignature;
import com.zappos.trifi.model.TrainingSignature;
import com.zappos.trifi.prediction.Predictor;
import com.zappos.trifi.util.TriFiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This controller handles {@link com.zappos.trifi.model.RouterSignature} updates to the system from all hosts.
 */
@Controller
public class UpdateController {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private Predictor predictor;

    @Resource(name = "knownRouters")
    private List<String> knownRouters;



    /**
     * This method gets called to update the {@code RouterSignature} of the caller as well as save the predicted
     * location of the caller.
     * @param routerSignature A {@code RouterSignature} that describes the networks visible to the caller.
     * @return a message indicating success.
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String updateSignature(@RequestBody RouterSignature routerSignature) {
        // Step 1 - Set timestamp
        routerSignature.setTimestamp(TriFiUtils.getTimestamp());

        // Save the RouterSignature to the dynamo database for long-term storage.
        dynamoDBMapper.save(routerSignature);

        // Fire off a prediction for this signature
        predictor.queuePrediction(routerSignature);

        // Return awesome job, you win.
        return "sweet request bro";
    }

    @RequestMapping(value = "/update/model", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String updateModel(@RequestBody Location location) throws IOException, GeneralSecurityException {
        location.setFloor((double)Math.round(location.getFloor()));

        List<RouterSignature> rs = dynamoDBMapper.query(RouterSignature.class,
                new DynamoDBQueryExpression<RouterSignature>
                ().withHashKeyValues(new RouterSignature().withId(location.getOriginRouterSignature())));

        if(rs.size() != 1) {
            return "ouch sukah! 1 RouterSignature not returned from Dynamo, or whatever.";
        }

        TrainingSignature ts = new TrainingSignature();
        ts.setRouterSignature(rs.get(0));
        ts.setVersion(location.getOriginModel());
        ts.setLocation(location);
        ts.setX(location.getX());
        ts.setY(location.getY());
        ts.setFloor(location.getFloor());

        dynamoDBMapper.save(ts);

        return predictor.updateModel(ts);
        //return "done and done.";
    }

    @RequestMapping("/predict/{model}")
    @ResponseBody
    public String predict(@PathVariable("model") String model, @RequestBody RouterSignature routerSignature) throws
            IOException,
            GeneralSecurityException {
        Map<String, Router> routers = routerSignature.getRouters();
        List<Object> input = new ArrayList<>();
        for (String router : knownRouters) {
            input.add(TriFiUtils.getSignalStrength(routers.get(router)));
        }
        System.out.println(input);
        return "x = " + predictor.predict(input, "x-" + model) + " y = " + predictor.predict(input,
                "y-" + model) + " floor = " + predictor.predict(input, "floor-" + model);

    }
}
