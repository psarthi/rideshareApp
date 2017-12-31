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
    public static final String SHARED_PREFS_VIRTUAL_ACCOUNT_KEY = "virtualAccount";
    public static final String SHARED_PREFS_CURRENT_RIDE_KEY = "currentRide";
    public static final String SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY = "currentRideRequest";
    public static final String SHARED_PREFS_RECENT_RIDE_LIST_KEY = "recentRideList";
    public static final String SHARED_PREFS_RECENT_RIDE_REQUEST_LIST_KEY = "recentRideRequestList";
    public static final int ACCESS_FINE_LOCATION_REQUEST_CODE=1;
    public static final float MAP_SINGLE_LOCATION_ZOOM_LEVEL = 15;
    public static final double LAT_LNG_LARGE_PADDING_PERCENT = 0.30;
    public static final double LAT_LNG_MEDIUM_PADDING_PERCENT = 0.10;
    public static final double LAT_LNG_SMALL_PADDING_PERCENT = 0.05;
    //This will ensure that markers are visible properly
    public static final int PICKUP_TIME_MAX_VALUE = 60;
    public static final int PICKUP_POINT_DISTANCE_MAX_VALUE = 1000;
    public static final int DROP_POINT_DISTANCE_MAX_VALUE = 1000;
    //This is used for setting start time in create rides screen i.e. all rides would have min. 10 mins from current time as start time
    //so that we don't end up having issues creating rides in the past
    public static final int START_TIME_INCREMENT = 10;
    //This would be used for enabling/disabling start button for offer rides i.e. any rides can be started only 15 mins before the ride start time
    public static final int START_TIME_BUFFER = 15;
    public static final int MIN_SEAT = 1;
    public static final int MAX_SEAT = 7;
    public static final int MIN_LUGGAGE = 0;
    public static final int MAX_LUGGAGE = 6;
    public static final int MIN_TOPUP_AMOUNT = 1;
    public static final int MAX_TOPUP_AMOUNT = 1000;
    public static final int LONG_DISTANCE=100;

}
