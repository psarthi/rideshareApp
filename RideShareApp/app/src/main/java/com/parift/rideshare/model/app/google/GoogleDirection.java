
package com.parift.rideshare.model.app.google;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleDirection {

    @SerializedName("geocoded_waypoints")
    @Expose
    private List<Geocoded_waypoint> geocoded_waypoints = null;
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Geocoded_waypoint> getGeocoded_waypoints() {
        return geocoded_waypoints;
    }

    public void setGeocoded_waypoints(List<Geocoded_waypoint> geocoded_waypoints) {
        this.geocoded_waypoints = geocoded_waypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
