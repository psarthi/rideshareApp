package com.digitusrevolution.rideshare.helper;

import android.util.Log;

import com.digitusrevolution.rideshare.BuildConfig;

public class Logger {
    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
    public static void info(final String tag, String message) {
            Log.i(tag, message);
    }

    public static void error(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void warn(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void verbose(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

}
