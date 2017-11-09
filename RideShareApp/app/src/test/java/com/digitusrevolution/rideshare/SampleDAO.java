package com.digitusrevolution.rideshare;

import com.digitusrevolution.rideshare.adapter.GsonDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Created by psarthi on 11/7/17.
 */

public class SampleDAO {

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

/*        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Prints the date in the CET timezone
        System.out.println(formatter.format(date));

        // Set the formatter to use a different timezone
        formatter.setTimeZone(TimeZone.getTimeZone("IST"));

        // Prints the date in the IST timezone
        System.out.println(formatter.format(date));

        System.out.println("Date:"+date);

        Point point = new Point();
        point.setLatitude(1.1);
        point.setLongitude(2.2);

        System.out.println(gson.toJson(point));

        RidePoint ridePoint = new RidePoint();
        RidePointProperty pointProperty = new RidePointProperty();
        pointProperty.setDateTime(ZonedDateTime.now());
        pointProperty.setId(1);
        ridePoint.getRidePointProperties().add(pointProperty);
        ridePoint.set_id("0");
        ridePoint.setPoint(point);
        ridePoint.setSequence(10);

        System.out.println(gson.toJson(ridePoint));

        System.out.println("DateTime:"+ridePoint.getRidePointProperties().get(0).getDateTime());
        */

    }
}
