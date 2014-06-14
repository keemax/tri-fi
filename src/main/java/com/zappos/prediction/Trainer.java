package com.zappos.prediction;

import com.zappos.model.Router;
import com.zappos.util.TriFiConstants;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/12/14.
 */
public class Trainer {
    @Resource(name = "knownRouters")
    private List<String> knownRouters;

    public void train(Double value, Map<String, Router> routers, String setName) {
        List<Double> csvRow = new ArrayList<Double>();
        csvRow.add(value);
        for (String router : knownRouters) {
            Double entry = routers.get(router).getStrength() == null ? TriFiConstants.DEFAULT_NETWORK : routers.get
                    (router).getStrength();
            csvRow.add(entry);
        }
    }
}
