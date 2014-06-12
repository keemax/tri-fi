package com.zappos.model;

/**
 * Created by maxkeene on 6/12/14.
 */
public class TrainingUpdate {
    private Location location;
    private RouterSignature routerSignature;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public RouterSignature getRouterSignature() {
        return routerSignature;
    }

    public void setRouterSignature(RouterSignature routerSignature) {
        this.routerSignature = routerSignature;
    }
}
