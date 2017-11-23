
package com.digitusrevolution.rideshare.model.dto.google;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Overview_polyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

}
