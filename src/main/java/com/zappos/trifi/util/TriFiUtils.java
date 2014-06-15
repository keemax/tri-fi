package com.zappos.trifi.util;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.model.Router;
import com.zappos.trifi.model.TrainingSignature;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class TriFiUtils {

    public static final double DEFAULT_NETWORK = 180.0;

    private static final FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd'T'HH-mm-ss");

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

    public static String getCSVString(PaginatedScanList<TrainingSignature> signatures, String dimension,
                                      List<String> knownRouters) {
        List<String> rows = new ArrayList<>();
        for(TrainingSignature trainingSignature : signatures) {
            List<String> row = new ArrayList<>();
            Double dim = TriFiUtils.getDimensionValue(trainingSignature.getLocation(), dimension);
            if(dim == null) {
                throw new IllegalArgumentException("Dimension must be 'x' 'X' 'y' 'Y' 'floor' 'Floor. Even though it " +
                        "would be more efficient to check this before scanning the database (probably) I didn't " +
                        "because I wanted to make you suffer... horribly.");
            }
            // First element is the X Y Floor dimension value
            row.add(String.valueOf(dim));
            for(String router : knownRouters) {
                row.add(String.valueOf(TriFiUtils.getSignalStrength(trainingSignature.getRouterSignature().getRouters()
                        .get(router))));
            }
            String rowString = StringUtils.join(row, ", ");
            rows.add(rowString);
        }

        return StringUtils.join(rows, "\n");
    }

    public static String getTimestamp() {
        return fdf.format(new Date());
    }

    public static String getTimestampMinutesBefore(long minutes) {
        return fdf.format(new Date((new Date()).getTime() - (minutes * 60L * 1000L)));
    }

    public static Boolean isFloorEqual(Double f1, Double f2) {
        return f1 > f2 - .4 && f1 < f2 + .4;
    }
}


