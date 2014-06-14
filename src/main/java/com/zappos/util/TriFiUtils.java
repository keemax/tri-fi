package com.zappos.util;

import com.zappos.model.RouterDescription;

/**
 * Created by gradiep on 6/13/14.
 */
public class TriFiUtils {

    public static final double DEFAULT_NETWORK = 255.0;
    /**
     *
     */
    public static Double getSignal(RouterDescription routerDescription) {
        return routerDescription == null ? DEFAULT_NETWORK :  Math.pow(10,((27.55 - (20 * Math.log10(routerDescription
                .getBand())) - (routerDescription.getStrength() + 92)) / 20)) * 60000;
    }

}


