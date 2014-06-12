package com.zappos.model;

import java.util.List;

/**
 * Created by maxkeene on 6/11/14.
 */
public class LocationProfile {
    String id;
    List<Router> routers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Router> getRouters() {
        return routers;
    }

    public void setRouters(List<Router> routers) {
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
