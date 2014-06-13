package com.zappos.controller;

import com.zappos.dao.LocationDAO;
import com.zappos.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
}
