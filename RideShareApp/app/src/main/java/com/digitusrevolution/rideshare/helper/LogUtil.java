package com.digitusrevolution.rideshare.helper;

import android.util.Log;

import com.digitusrevolution.rideshare.BuildConfig;

public class LogUtil {
    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
