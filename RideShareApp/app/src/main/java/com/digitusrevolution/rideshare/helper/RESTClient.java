package com.digitusrevolution.rideshare.helper;

import android.content.Context;
import android.util.Log;

import com.digitusrevolution.rideshare.activity.LandingPageActivity;
import com.digitusrevolution.rideshare.adapter.GsonDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by psarthi on 11/7/17.
 */

public class RESTClient {

    private static final String TAG = RESTClient.class.getName();
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.d(TAG,"GET URL:"+url);
        client.get(url, params, responseHandler);
    }

    public static void post(Context context, String url, Object model, AsyncHttpResponseHandler responseHandler) {

        /*This is very important as Gson default serializer would not convert the Date into ISO format with UTC timezone
        and in the backend Jackson expects the date in ISO format with UTC timezone - yyyy-MM-dd'T'HH:mm:ss.SSS'Z' (e.g. 2017-11-10T15:30:00Z).
        So we are using custom Gson Adapter which does the Job of changing Date into ISO format and convert it to UTC timezone.
        Note - ISO Format is a standard but jackson expects string in specific pattern of ISO where timezone is only UTC i.e. ending with Z
        No other ISO format pattern would work as Jackson default deserializer doesn't support that.
        */
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,new GsonDateAdapter())
                .create();
        String json = gson.toJson(model);

        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"POST URL:"+url);
        client.post(context, url, entity, "application/json", responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(url, params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(url, params, responseHandler);
    }

}
