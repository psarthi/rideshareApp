package com.digitusrevolution.rideshare.config;

import android.Manifest;

/**
 * Created by psarthi on 11/16/17.
 */

public class Constant {

    //This would be appended with getPackageName(), that's why it starts with .
    public static final String INTENT_EXTRA_DATA_KEY = ".data";
    //This would be appended with getPackageName(), that's why it starts with .
    public static final String SHARED_PREFS_KEY_FILE = ".rideshare";
    public static final String SHARED_PREFS_TOKEN_KEY = "token";
    public static final String SHARED_PREFS_USER_KEY = "user";
    public static final String SHARED_PREFS_FULL_USER_KEY = "fullUser";
    public static final String SHARED_PREFS_CURRENT_RIDE_KEY = "currentRide";
    public static final String SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY = "currentRideRequest";
    public static final int ACCESS_FINE_LOCATION_REQUEST_CODE=1;
    public static final float MAP_SINGLE_LOCATION_ZOOM_LEVEL = 15;
    public static final double LAT_LNG_TOP_PADDING_PERCENT = 0.30;
    public static final double LAT_LNG_STANDARD_PADDING_PERCENT = 0.05;
    public static final String CURRENT_LOCATION_TEXT = "Current Location";
    public static final int PICKUP_TIME_MAX_VALUE = 60;
    public static final int PICKUP_POINT_DISTANCE_MAX_VALUE = 1000;
    public static final int DROP_POINT_DISTANCE_MAX_VALUE = 1000;
    public static final int START_TIME_INCREMENT = 10;
}
