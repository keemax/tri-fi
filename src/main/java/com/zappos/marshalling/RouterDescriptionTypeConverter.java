package com.zappos.marshalling;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappos.model.Router;

import java.io.IOException;
import java.util.Map;

/**
 * Simple {@link com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller} to convert the {@link com.zappos
 * .model.RouterSignature} objects to and from {@code JSON}.
 */
public class RouterDescriptionTypeConverter implements DynamoDBMarshaller<Map<String, Router> > {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String marshall(Map<String, Router>  getterReturnResult) {
        try {
            return mapper.writeValueAsString(getterReturnResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Router>  unmarshall(Class<Map<String, Router> > clazz, String obj) {
        try {
            return mapper.readValue(obj, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
