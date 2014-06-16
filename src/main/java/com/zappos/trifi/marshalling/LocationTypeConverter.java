package com.zappos.trifi.marshalling;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappos.trifi.model.Location;
import com.zappos.trifi.util.TriFiUtils;

import java.io.IOException;

/**
 *
 */
public class LocationTypeConverter implements DynamoDBMarshaller<Location> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String marshall(Location getterReturnResult) {
        try {
            return mapper.writeValueAsString(getterReturnResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Location unmarshall(Class<Location> clazz, String obj) {
        Location rval = null;
        try {
            rval = mapper.readValue(obj, clazz);
            rval.setFloorInt(TriFiUtils.determineFloor(rval.getFloor()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rval;
    }
}
