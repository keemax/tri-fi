package com.zappos.trifi.controller;

import com.zappos.trifi.dao.LocationDAO;
import com.zappos.trifi.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by maxkeene on 6/13/14.
 */
@RestController
public class LocationController {

    @Autowired
    private LocationDAO locationDAO;

    @RequestMapping("/find")
    public List<Location> findPersonById(@RequestParam("id") String id,
                                   @RequestParam(value = "start", required = false) String start,
                                   @RequestParam(value = "end", required = false) String end) {

        if (start == null && end == null) {
            return locationDAO.getAllLocations(id);
        } else if (start == null) {
            return locationDAO.getLocationsInBeforeDate(id, end);
        } else if (end == null) {
            return locationDAO.getLocationsAfterDate(id, start);
        } else {
            return locationDAO.getLocationsInTimeRange(id, start, end);
        }
    }

    @RequestMapping("/location/all")
    public List<Location> getAllLocationByHostname(@RequestParam("hostname") String hostname) {
        return locationDAO.getAllLocationsForHost(hostname);
    }

    @RequestMapping("/location/range")
    public List<Location> getAllLocationByHostname(@RequestParam("hostname") String hostname,
                                                   @RequestParam("start") String start,
                                                   @RequestParam("end") String end) {
        return locationDAO.getAllLocationsForHostInRange(hostname, start, end);
    }

    @RequestMapping("/location/floor/latest")
    public List<Location> getLatestLocationByFloor(@RequestParam("floor") String floor,
                                                   @RequestParam("timeSince") Integer timeSince) {

        return locationDAO.getLatestLocationsForFloor(floor, timeSince);
    }

    @RequestMapping("/location/latest/average")
    public Location getLastestAverageLocationForHost(@RequestParam("hostname") String hostname) {
        return locationDAO.getAvgLatestLocationForHost(hostname);
    }

    @RequestMapping("/location/last")
    public Location findPersonsLastLocation(@RequestParam("hostname") String hostname) {
        return locationDAO.getLatestLocationForHost(hostname);
    }
}
