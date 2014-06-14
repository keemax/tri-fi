package com.zappos.trifi.prediction;

import com.zappos.trifi.model.Router;
import com.zappos.trifi.util.TriFiConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/12/14.
 */
@Component
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
