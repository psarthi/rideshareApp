package com.parift.rideshare.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.parift.rideshare.activity.LandingPageActivity;
import com.parift.rideshare.adapter.GsonDateAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.CreateRidesFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHeaders;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by psarthi on 11/7/17.
 */

public class RESTClient {

    private static final String TAG = RESTClient.class.getName();
    private static AsyncHttpClient client = new AsyncHttpClient();
    //This will avoid connection timeout issue, where we don't get response on time
    //Value is in milliseconds
    private static final int TIMEOUT_VALUE=20 * 1000;
    private static final int EXTENDED_TIMEOUT_VALUE=60 * 1000;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //This will overwrite default value of 10 seconds so that we are able to get response properly
        client.setTimeout(TIMEOUT_VALUE);
        Logger.debug(TAG,"GET URL:"+url);
        setHeader(url, responseHandler);
        client.get(url, params, responseHandler);
    }

    public static void post(Context context, String url, Object model, AsyncHttpResponseHandler responseHandler) {
        //This will overwrite default value of 10 seconds so that we are able to get response properly
        client.setTimeout(TIMEOUT_VALUE);
        BaseFragment fragment = ((RSJsonHttpResponseHandler) responseHandler).getCommonUtil().getBaseFragment();
        if (fragment instanceof CreateRidesFragment) {
            client.setTimeout(EXTENDED_TIMEOUT_VALUE);
            Logger.debug(TAG, "Setting extended timeout value of:"+client.getConnectTimeout());
        }
        /*This is very important as Gson default serializer would not convert the Date into ISO format with UTC timezone
        and in the backend Jackson expects the date in ISO format with UTC timezone - yyyy-MM-dd'T'HH:mm:ss.SSS'Z' (e.g. 2017-11-10T15:30:00Z).
        So we are using custom Gson Adapter which does the Job of changing Date into ISO format and convert it to UTC timezone.
        Note - ISO Format is a standard but jackson expects string in specific pattern of ISO where timezone is only UTC i.e. ending with Z
        No other ISO format pattern would work as Jackson default deserializer doesn't support that.
        */
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,new GsonDateAdapter())
                .create();
        String json = gson.toJson(model);

        //VERY IMP - By default json would be encoded with charset Text/Plain and we need to use UTF-8 encoding,
        //so we need to specify explicity else google api result would fail in json parsing as its not a standard text
        //Default way to convert to entity is = new StringEntity(json). This would be wrong for UTF-8 encoding
        StringEntity entity = new StringEntity(json, "UTF-8");
        Logger.debug(TAG,"POST URL:"+url);
        Logger.debug(TAG, "POST Message:"+json);
        setHeader(url,responseHandler);
        client.post(context, url, entity, "application/json", responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //This will overwrite default value of 10 seconds so that we are able to get response properly
        client.setTimeout(TIMEOUT_VALUE);
        setHeader(url,responseHandler);
        client.put(url, params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        //This will overwrite default value of 10 seconds so that we are able to get response properly
        client.setTimeout(TIMEOUT_VALUE);
        setHeader(url,responseHandler);
        client.delete(url, params, responseHandler);
    }

    private static void setHeader(String url, AsyncHttpResponseHandler responseHandler){
        if (responseHandler instanceof RSJsonHttpResponseHandler && url.contains(APIUrl.HOST_NAME)){
            String token = ((RSJsonHttpResponseHandler) responseHandler).getCommonUtil().getAccessToken();
            client.addHeader("Authorization","Bearer "+token);
        }
    }

}
