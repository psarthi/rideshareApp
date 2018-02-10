package com.parift.rideshare.test;

import com.parift.rideshare.adapter.GsonDateAdapter;
import com.parift.rideshare.model.ride.domain.RidePoint;
import com.parift.rideshare.model.ride.domain.RidePointProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by psarthi on 11/7/17.
 */

public class SampleTest {

    public static void main (String args[]){

        SampleModel sampleModel = new SampleModel();

        sampleModel.setId(1);
        sampleModel.setName("Name-1");
        sampleModel.setEmail("Email-1");

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class,new GsonDateAdapter()).create();
        String json = gson.toJson(sampleModel);
        System.out.println(json);

        SampleModel model = gson.fromJson(json, SampleModel.class);
        model.setName(model.getName()+"Updated");

        json = gson.toJson(model);
        System.out.println(json);

        Date date = new Date();
        System.out.println("Date:"+date);

        System.out.println("Gson Date:"+gson.toJson(date));

        SampleStringLocalTimeModel sampleStringLocalTimeModel = new SampleStringLocalTimeModel();
        sampleStringLocalTimeModel.setTime("00:30");
        json = gson.toJson(sampleStringLocalTimeModel);
        System.out.println("Json using String time:"+json);

    }
}
