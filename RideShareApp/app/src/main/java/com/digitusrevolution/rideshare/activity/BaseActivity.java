package com.digitusrevolution.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    public String getExtraDataKey(){
        //Reason for appending packageName as per recommendation on android docs so that
        // it doesn't get name clash with other objects
        return getPackageName() + Constant.INTENT_EXTRA_DATA_KEY;
    }

    public void startHomePageActivity(UserSignInResult userSignInResult){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
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

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
    }

    public BasicUser getUser() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_USER_KEY,null);
        return new Gson().fromJson(user, BasicUser.class);
    }

    public FullRide getCurrentRide() {
        String currentRide = getSharedPreferences().getString(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,null);
        return new Gson().fromJson(currentRide, FullRide.class);
    }
}
