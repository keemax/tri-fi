package com.zappos.util;

import com.zappos.model.Location;
import com.zappos.model.Router;

/**
 * Created by gradiep on 6/13/14.
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
        if(dimension.equals("x") || dimension.equals("X")) {
            return location.getX();
        } else if(dimension.equals("y") || dimension.equals("Y")) {
            return location.getY();
        } else if(dimension.equals("floor") || dimension.equals("Floor")) {
            return location.getFloor();
        }
        return null;
    }

}


