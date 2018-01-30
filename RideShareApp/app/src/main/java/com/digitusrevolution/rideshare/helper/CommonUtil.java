package com.digitusrevolution.rideshare.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.BaseActivity;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.billing.domain.core.Account;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.domain.Country;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.FullUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class CommonUtil {

    public static final String TAG = CommonUtil.class.getName();
    private BaseFragment mBaseFragment;
    private BaseActivity mBaseActivity;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public CommonUtil(BaseFragment fragment){
        mBaseFragment = fragment;
    }

    public CommonUtil(BaseActivity activity){
        mBaseActivity = activity;
    }

    public CommonUtil(Context context){
        mContext = context;
    }

    public BaseFragment getBaseFragment() {
        return mBaseFragment;
    }

    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    //This function is duplicate in BaseActivity and BaseFragment
    //as getPackageName would work differently in Activity and Fragment
    private SharedPreferences getSharedPreferences() {
        if (mBaseFragment!=null){
            //IMP - We are using mActivity instead of getActivity here,
            // so that we can take care of NPE if fragment in not visible
            // but doing some work which needs activity reference
            return mBaseFragment.mActivity.getSharedPreferences(
                    mBaseFragment.mActivity.getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        }
        if (mBaseActivity!=null) {
            return mBaseActivity.getSharedPreferences(mBaseActivity.getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        }
        if (mContext!=null){
            return mContext.getSharedPreferences(mContext.getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        }
        return null;
    }

    public Activity getActivity(){
        if (mBaseFragment!=null){
            //IMP - We are using mActivity instead of getActivity here,
            // so that we can take care of NPE if fragment in not visible
            // but doing some work which needs activity reference
            return mBaseFragment.mActivity;
        } else {
            return mBaseActivity;
        }
    }

    public void saveUserSignInResult(UserSignInResult userSignInResult) {
        saveInSharedPref(Constant.SHARED_PREFS_TOKEN_KEY,userSignInResult.getToken());
        saveInSharedPref(Constant.SHARED_PREFS_USER_KEY,new Gson().toJson(userSignInResult.getUser()));
        saveInSharedPref(Constant.SHARED_PREFS_VIRTUAL_ACCOUNT_KEY,new Gson().toJson(((List<Account>) userSignInResult.getUser().getAccounts()).get(0)));
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

    public void removeSharedPref(){
        Log.d(TAG, "Removing shared preference all keys");
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().commit();
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

    public Account getAccount() {
        String account = getSharedPreferences().getString(Constant.SHARED_PREFS_VIRTUAL_ACCOUNT_KEY,null);
        return new Gson().fromJson(account, Account.class);
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

    public void updateAccount(Account account){
        updateInSharedPref(Constant.SHARED_PREFS_VIRTUAL_ACCOUNT_KEY,new Gson().toJson(account));
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

    public int getMinsFromLocalTimeString(String time) {
        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);
        int totalMins = hours * 60 + minutes;
        return totalMins;
    }

    public String getLocalTimeStringFromMins(int time) {
        int hours = time / 60; //since both are ints, you get an int
        int minutes = time % 60;
        String localTime = String.format("%02d:%02d", hours, minutes);
        return localTime;
    }


    public Calendar getRideRequestMaxEndTime(BasicRideRequest rideRequest) {
        Calendar endTime = Calendar.getInstance();
        //This will get the expected drop point time
        endTime.setTime(rideRequest.getDropPoint().getDateTime());
        //This will get time variation which includes drop time buffer e.g. 30 mins considering any delay in dropping etc.
        endTime.add(Calendar.MINUTE, getMinsFromLocalTimeString(rideRequest.getDropPoint().getTimeVariation()));
        return endTime;
    }

    public String getDecimalFormattedString(double number){
        Log.d(TAG, "Number is:"+number);
        String result = String.format("%.2f", number);
        return result;
    }

    public String getCurrencySymbol(Country country) {
        return Currency.getInstance(country.getCurrency().getName()).getSymbol();
    }

    public void showProgressDialog(){
        Log.d(TAG, "Showing Progress Dialog");
        mProgressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }

    public void dismissProgressDialog(){
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            Log.d(TAG, "Dismissing Progress Dialog");
            mProgressDialog.dismiss();
        } else {
            Log.d(TAG, "Progress Dialog is not visible, so not dismissing");
        }
    }

    //IMP - We are doing all this as we need to support tint at API 19 and standard tint is not supported below API 21
    public void setDrawableTint(Drawable drawable, int color) {
        if (drawable != null) {
            Log.d(TAG, "Applying Tint");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setTint(color);
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(drawable), color);
            }
        }
    }

    public void removeDrawableTint(Drawable drawable) {
        if (drawable != null) {
            Log.d(TAG, "Removing Tint");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setTintList(null);
            } else {
                DrawableCompat.setTintList(DrawableCompat.wrap(drawable), null);
            }
        }
    }

    public void updatePushNotificationToken() {
        BasicUser user = getUser();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //We need to cross check user is null else you will get NPE
        if (user!=null){
            if (user.getPushNotificationToken()==null || !user.getPushNotificationToken().equals(refreshedToken)){
                //Updating this from here as well just to ensure that updated token
                //is always there at the time of login
                //Note - We are doing this check and update at three points - Token Refresh, User Registration and User Login
                Log.d(TAG, "Updating user push notification token as:"+refreshedToken);
                String url = APIUrl.UPDATE_PUSH_NOTIFICATION_TOKEN.replace(APIUrl.USER_ID_KEY, Long.toString(user.getId()))
                        .replace(APIUrl.TOKEN_KEY, refreshedToken);
                RESTClient.get(url, null, new RSJsonHttpResponseHandler(this){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                    }
                });
            } else {
                Log.d(TAG, "User push notification token is current, so no need to update");
            }
        } else {
            Log.d(TAG, "User is null, so can't update push notification token");
        }
    }

}
