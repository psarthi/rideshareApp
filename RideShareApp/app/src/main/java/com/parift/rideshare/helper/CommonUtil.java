package com.parift.rideshare.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.BaseActivity;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.model.billing.domain.core.Account;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.serviceprovider.domain.core.Company;
import com.parift.rideshare.model.serviceprovider.domain.core.AppInfo;
import com.parift.rideshare.model.user.domain.Country;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.UserSignInResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        saveInSharedPref(Constant.SHARED_PREFS_USER_IS_GROUP_MEMBER_KEY,Boolean.toString(userSignInResult.isGroupMember()));
        saveInSharedPref(Constant.SHARED_PREFS_COMPANY_KEY,new Gson().toJson(userSignInResult.getCompany()));
    }

    public void saveInSharedPref(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
        String savedValue = getSharedPreferences().getString(key,null);
        Logger.debug(TAG, "Saved Key/Value - " + key+":"+savedValue);
    }

    public void updateInSharedPref(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        String savedValue = getSharedPreferences().getString(key,null);
        Logger.debug(TAG, "Updated Key/Value - " + key+":"+savedValue);
    }

    public void removeSharedPref(){
        Logger.debug(TAG, "Removing shared preference all keys");
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

    public Company getCompany() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_COMPANY_KEY,null);
        return new Gson().fromJson(user, Company.class);
    }

    public AppInfo getAppInfo() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_APP_INFO_KEY,null);
        return new Gson().fromJson(user, AppInfo.class);
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

    public boolean isUserGroupMember() {
        boolean status = Boolean.valueOf(getSharedPreferences().getString(Constant.SHARED_PREFS_USER_IS_GROUP_MEMBER_KEY,"false"));
        return status;
    }

    public void updateAccessToken(String token){
        updateInSharedPref(Constant.SHARED_PREFS_TOKEN_KEY,token);
    }

    public void updateUser(BasicUser user){
        updateInSharedPref(Constant.SHARED_PREFS_USER_KEY,new Gson().toJson(user));
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

    public void updateIsUserGroupMember(boolean status){
        updateInSharedPref(Constant.SHARED_PREFS_USER_IS_GROUP_MEMBER_KEY,Boolean.toString(status));
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

    public Calendar getRideRequestMaxPickupTime(BasicRideRequest rideRequest) {
        Calendar maxPickupTime = Calendar.getInstance();
        maxPickupTime.setTime(rideRequest.getPickupTime());
        maxPickupTime.add(Calendar.MINUTE, getMinsFromLocalTimeString(rideRequest.getPickupTimeVariation()));
        return maxPickupTime;
    }

    public String getDecimalFormattedString(double number){
        Logger.debug(TAG, "Number is:"+number);
        String result = String.format("%.2f", number);
        return result;
    }

    public String getCurrencySymbol(Country country) {
        return Currency.getInstance(country.getCurrency().getName()).getSymbol();
    }

    public void showProgressDialog(){
        Logger.debug(TAG,getActivity().getLocalClassName());
        mProgressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        //VERY IMP - This will take care of NPE and crash when you press back intentionally and async call is in progress
        //This is the scenario in splash screen if you go back immediately after launch, you will get the exception
        //BinderProxy@45d459c0 is not valid; is your activity running?
        if(!(getActivity().isFinishing())){
            Logger.debug(TAG, "Showing Progress Dialog");
            mProgressDialog.show();
        } else {
            Logger.debug(TAG, "Activity is null, so not showing progress dialog");
        }
    }

    public void dismissProgressDialog(){
        Logger.debug(TAG,getActivity().getLocalClassName());
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            Logger.debug(TAG, "Dismissing Progress Dialog");
            mProgressDialog.dismiss();
        } else {
            Logger.debug(TAG, "Progress Dialog is not visible, so not dismissing");
        }
    }

    //IMP - We are doing all this as we need to support tint at API 19 and standard tint is not supported below API 21
    public void setDrawableTint(Drawable drawable, int color) {
        if (drawable != null) {
            Logger.debug(TAG, "Applying Tint");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setTint(color);
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(drawable), color);
            }
        }
    }

    public void removeDrawableTint(Drawable drawable) {
        if (drawable != null) {
            Logger.debug(TAG, "Removing Tint");
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
                Logger.debug(TAG, "Updating user push notification token as:"+refreshedToken);
                String url = APIUrl.UPDATE_PUSH_NOTIFICATION_TOKEN.replace(APIUrl.USER_ID_KEY, Long.toString(user.getId()))
                        .replace(APIUrl.TOKEN_KEY, refreshedToken);
                RESTClient.get(url, null, new RSJsonHttpResponseHandler(this){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                    }
                });
            } else {
                Logger.debug(TAG, "User push notification token is current, so no need to update");
            }
        } else {
            Logger.debug(TAG, "User is null, so can't update push notification token");
        }
    }

    public int getAppVersionCode(){
        int versionCode = 0;
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

}
