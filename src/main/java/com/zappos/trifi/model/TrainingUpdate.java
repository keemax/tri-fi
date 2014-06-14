package com.zappos.trifi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingUpdate {
    private Location location;
    private RouterSignature routerSignature;
    private int version;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
