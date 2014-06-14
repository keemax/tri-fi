package com.zappos.marshalling;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappos.model.RouterSignature;

import java.io.IOException;

/**
 *
 */
public class RouterSignatureTypeConverter implements DynamoDBMarshaller<RouterSignature> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String marshall(RouterSignature getterReturnResult) {
        try {
            return mapper.writeValueAsString(getterReturnResult);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RouterSignature  unmarshall(Class<RouterSignature> clazz, String obj) {
        try {
            return mapper.readValue(obj, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
