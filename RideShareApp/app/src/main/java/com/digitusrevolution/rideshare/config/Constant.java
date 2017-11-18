package com.digitusrevolution.rideshare.config;

import android.Manifest;

/**
 * Created by psarthi on 11/16/17.
 */

public class Constant {

    public static final String INTENT_EXTRA_KEY = "key";
    //This would be appended with getPackageName(), that's why it starts with .
    public static final String INTENT_EXTRA_DATA_NAME = ".data";
    //This would be appended with getPackageName(), that's why it starts with .
    public static final String SHARED_PREFS_KEY_FILE = ".rideshare";
    public static final String SHARED_PREFS_TOKEN_KEY = "token";
    public static final int ACCESS_FINE_LOCATION_REQUEST_CODE=1;
    public static final float MAP_CURRENT_LOCATION_ZOOM_LEVEL = 15;

    public static final String HomePageWithNoRidesFragment_Title = "Ride Share";
}
