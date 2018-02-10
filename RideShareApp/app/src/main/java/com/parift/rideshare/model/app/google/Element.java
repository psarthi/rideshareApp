
package com.parift.rideshare.model.app.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Element {

    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;
    @SerializedName("duration_in_traffic")
    @Expose
    private Duration_in_traffic duration_in_traffic;
    @SerializedName("status")
    @Expose
    private String status;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration_in_traffic getDuration_in_traffic() {
        return duration_in_traffic;
    }

    public void setDuration_in_traffic(Duration_in_traffic duration_in_traffic) {
        this.duration_in_traffic = duration_in_traffic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
