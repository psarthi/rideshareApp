package com.digitusrevolution.rideshare.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.digitusrevolution.rideshare.activity.BaseActivity;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.FullUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by psarthi on 12/6/17.
 */

public class CommonUtil {

    public static final String TAG = CommonUtil.class.getName();

    private BaseFragment mBaseFragment;
    private BaseActivity mBaseActivity;

    public CommonUtil(BaseFragment fragment){
        mBaseFragment = fragment;
    }

    public CommonUtil(BaseActivity activity){
        mBaseActivity = activity;
    }

    //This function is duplicate in BaseActivity and BaseFragment
    //as getPackageName would work differently in Activity and Fragment
    private SharedPreferences getSharedPreferences() {
        if (mBaseFragment!=null){
            return mBaseFragment.getActivity().getSharedPreferences(
                    mBaseFragment.getActivity().getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        } else {
            return mBaseActivity.getSharedPreferences(mBaseActivity.getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        }
    }

    public void saveUserSignInResult(UserSignInResult userSignInResult) {
        saveInSharedPref(Constant.SHARED_PREFS_TOKEN_KEY,userSignInResult.getToken());
        saveInSharedPref(Constant.SHARED_PREFS_USER_KEY,new Gson().toJson(userSignInResult.getUserProfile()));
        saveInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,new Gson().toJson(userSignInResult.getCurrentRide()));
        saveInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY,new Gson().toJson(userSignInResult.getCurrentRideRequest()));
    }

    public void saveInSharedPref(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
        String savedValue = getSharedPreferences().getString(key,null);
        Log.d(TAG, "Saved Key/Value - " + key+":"+savedValue);
    }

    public void updateInSharedPref(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        String savedValue = getSharedPreferences().getString(key,null);
        Log.d(TAG, "Updated Key/Value - " + key+":"+savedValue);
    }

    public String getAccessToken() {
        return getSharedPreferences().getString(Constant.SHARED_PREFS_TOKEN_KEY,null);
    }

    public BasicUser getUser() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_USER_KEY,null);
        return new Gson().fromJson(user, BasicUser.class);
    }

    public FullUser getFullUser() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_FULL_USER_KEY,null);
        return new Gson().fromJson(user, FullUser.class);
    }

    public FullRide getCurrentRide() {
        String currentRide = getSharedPreferences().getString(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,null);
        return new Gson().fromJson(currentRide, FullRide.class);
    }

    public FullRideRequest getCurrentRideRequest() {
        String currentRideRequest = getSharedPreferences().getString(Constant.SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY,null);
        return new Gson().fromJson(currentRideRequest,FullRideRequest.class);
    }

    public List<FullRide> getRecentRides() {
        String rides = getSharedPreferences().getString(Constant.SHARED_PREFS_RECENT_RIDE_LIST_KEY,null);
        Type listType = new TypeToken<ArrayList<FullRide>>(){}.getType();
        List<FullRide> recentRides = new Gson().fromJson(rides, listType);
        return recentRides;
    }

    public List<FullRideRequest> getRecentRideRequests() {
        String rides = getSharedPreferences().getString(Constant.SHARED_PREFS_RECENT_RIDE_REQUEST_LIST_KEY,null);
        Type listType = new TypeToken<ArrayList<FullRideRequest>>(){}.getType();
        List<FullRideRequest> recentRideRequests = new Gson().fromJson(rides, listType);
        return recentRideRequests;
    }


    public void updateRecentRides(List<FullRide> recentRides){
        updateInSharedPref(Constant.SHARED_PREFS_RECENT_RIDE_LIST_KEY,new Gson().toJson(recentRides));
    }

    public void updateRecentRideRequests(List<FullRideRequest> recentRideRequests){
        updateInSharedPref(Constant.SHARED_PREFS_RECENT_RIDE_REQUEST_LIST_KEY,new Gson().toJson(recentRideRequests));
    }


    public void updateAccessToken(String token){
        updateInSharedPref(Constant.SHARED_PREFS_TOKEN_KEY,token);
    }

    public void updateUser(BasicUser user){
        updateInSharedPref(Constant.SHARED_PREFS_USER_KEY,new Gson().toJson(user));
    }

    public void updateFullUser(FullUser user){
        updateInSharedPref(Constant.SHARED_PREFS_FULL_USER_KEY,new Gson().toJson(user));
    }


    public void updateCurrentRide(FullRide currentRide){
        updateInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,new Gson().toJson(currentRide));
    }

    public void updateCurrentRideRequest(FullRideRequest currentRideRequest){
        updateInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY,new Gson().toJson(currentRideRequest));
    }

    public String getFormattedDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
        return dateFormat.format(date);
    }

    @NonNull
    public String getTimeIn12HrFormat(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        //Calendar.HOUR_OF_DAY is in 24-hour format
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        String hour = String.format("%02d", calendar.get(Calendar.HOUR));
        //This will handle special scenario when Hour is 00
        if (hour.equals("00")) hour = "12";

        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String AM_PM = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

        //Calendar.HOUR is in 12-hour format
        return hour+":"+min+" "+AM_PM;
    }

    public Calendar getCalendarFromDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public String getFormattedDateTimeString(Date date){
        Calendar calendar = getCalendarFromDate(date);
        String dateTimeString = getFormattedDateString(date) + " "+
                getTimeIn12HrFormat(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        return dateTimeString;
    }

    public int getTimeFromString(String time) {
        return Integer.parseInt(time.split(":")[1]);
    }

    public Calendar getRideRequestMaxEndTime(BasicRideRequest rideRequest) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(rideRequest.getPickupTime());
        endTime.add(Calendar.MINUTE, getTimeFromString(rideRequest.getPickupTimeVariation()));
        endTime.add(Calendar.SECOND, rideRequest.getTravelTime());
        return endTime;
    }

}
