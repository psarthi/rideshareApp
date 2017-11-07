package com.digitusrevolution.rideshare.data;

import com.digitusrevolution.rideshare.model.SampleModel;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 11/7/17.
 */

public class SampleDAO {

    public static void main (String args[]){

        SampleModel sampleModel = new SampleModel();

        sampleModel.setId(1);
        sampleModel.setName("Name-1");
        sampleModel.setEmail("Email-1");

        Gson gson = new Gson();
        String json = gson.toJson(sampleModel);
        System.out.println(json);

        SampleModel model = gson.fromJson(json, SampleModel.class);
        model.setName(model.getName()+"Updated");

        json = gson.toJson(model);
        System.out.println(json);

    }
}
