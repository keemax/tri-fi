package com.zappos.util;

import com.zappos.model.Router;

/**
 * Created by gradiep on 6/13/14.
 */
public class TriFiUtils {

    public static final double DEFAULT_NETWORK = 255.0;
    /**
     *
     */
    public static Double getSignalStrength(Router router) {
        return router == null ? DEFAULT_NETWORK :  Math.pow(10,((27.55 - (20 * Math.log10(router
                .getFreq())) - (router.getStrength() + (-1 * router.getNoise()))) / 20)) * 1000;
    }

}


