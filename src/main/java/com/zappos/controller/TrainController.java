package com.zappos.controller;

import com.zappos.dao.TrainingDAO;
import com.zappos.model.Location;
import com.zappos.model.Router;
import com.zappos.model.TrainingUpdate;
import com.zappos.prediction.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/train", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String storeTrainingUpdate(@RequestBody TrainingUpdate update) {
        if (update.getLocation() == null ||
                update.getLocation().getFloor() == null ||
                update.getLocation().getX() == null ||
                update.getLocation().getY() == null ||
                update.getRouterSignature() == null ||
                update.getRouterSignature().getId() == null ||
                update.getRouterSignature().getRouters() == null ||
                update.getRouterSignature().getRouters().isEmpty()) {
            return "no nulls plz";
        }

        trainingDAO.storeFloor(update);
        trainingDAO.storeX(update);
        trainingDAO.storeY(update);
        trainingDAO.storeRouters(update);

        Map<String, Router> routers = update.getRouterSignature().getRouters();
        Location location = update.getLocation();
        trainer.train(location.getFloor().doubleValue(), routers, floorSet);
        trainer.train(location.getX(), routers, x4Set);
        trainer.train(location.getY(), routers, y4Set);

        return "sweet request bro";
    }

}
