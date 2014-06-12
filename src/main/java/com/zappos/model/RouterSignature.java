package com.zappos.model;

import java.util.List;
import java.util.Map;

/**
 * Created by maxkeene on 6/11/14.
 */
public class RouterSignature {
    private String id;
    private Map<String, Double> routers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Double> getRouters() {
        return routers;
    }

    public void setRouters(Map<String, Double> routers) {
        this.routers = routers;
    }

    @Override
    public String toString() {
        return "LocationProfile{" +
                "id='" + id + '\'' +
                ", routers=" + routers +
                '}';
    }
}
