package com.zappos.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.zappos.dao.TrainingDAO;
import com.zappos.model.Location;
import com.zappos.model.Router;
import com.zappos.model.TrainingSignature;
import com.zappos.model.TrainingUpdate;
import com.zappos.prediction.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by maxkeene on 6/11/14.
 */
@Controller
public class TrainController {
    @Autowired
    private TrainingDAO trainingDAO;

    @Autowired
    private Trainer trainer;

    private static final String floorSet = "floorSet";
    private static final String x4Set = "x4Set";
    private static final String y4Set = "y4Set";

    @Autowired
    DynamoDBMapper dynamoDBMapper;

    @RequestMapping(value = "/train/v/{version}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeTrainingUpdate(@PathVariable("version") String version, @RequestBody TrainingSignature update) {
        if (update.getLocation() == null ||
                update.getLocation().getFloor() == null ||
                update.getLocation().getX() == null ||
                update.getLocation().getY() == null ||
                update.getRouterSignature() == null ||
                update.getRouterSignature().getHostname() == null ||
                update.getRouterSignature().getRouters() == null ||
                update.getRouterSignature().getRouters().isEmpty()) {
            return "no nulls plz";
        }

        update.setX(update.getLocation().getX());
        update.setY(update.getLocation().getY());
        update.setFloor(update.getLocation().getFloor());
        update.setVersion(version);
        dynamoDBMapper.save(update);
        return "sweet request bro";
    }

}
