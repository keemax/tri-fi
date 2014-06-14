package com.zappos.util;

import com.zappos.model.Location;
import com.zappos.model.Router;

/**
 * 
 */
public class TriFiUtils {

    public static final double DEFAULT_NETWORK = 180.0;

    /**
     * 10 ^ ((27.55 - (20 * log10(5825)) - (-87))/20)
     */
    public static Double getSignalStrength(Router router) {
        return router == null ? DEFAULT_NETWORK :  Math.pow(10,((27.55 - (20 * Math.log10(router
                .getFreq())) - router.getStrength()) / 20));
    }

    public static Double getDimensionValue(Location location, String dimension) {
        switch (dimension) {
            case "x":
            case "X":
                return location.getX();
            case "y":
            case "Y":
                return location.getY();
            case "floor":
            case "Floor":
                return location.getFloor();
        }
        return null;
    }
}


