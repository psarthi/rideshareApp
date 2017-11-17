package com.digitusrevolution.rideshare.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;

public class CommonFunctions {
    private final AppCompatActivity mActivity;
    private static final String TAG = CommonFunctions.class.getName();
    private String mExtraKeyName;

    public CommonFunctions(AppCompatActivity activity) {
        this.mActivity = activity;
        mExtraKeyName = activity.getPackageName()+Constant.INTENT_EXTRA_DATA_NAME;
    }

    public void saveAccessTokenAndStartHomePageActivity(UserSignInResult userSignInResult) {
        SharedPreferences sharedPref = mActivity.getSharedPreferences(mActivity.getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SHARED_PREFS_TOKEN_KEY, userSignInResult.getToken());
        editor.commit();
        String token = sharedPref.getString(Constant.SHARED_PREFS_TOKEN_KEY, null);
        Log.d(TAG, "Token Saved is:" + token);
        Intent intent = new Intent(mActivity, HomePageActivity.class);
        intent.putExtra(Constant.INTENT_EXTRA_KEY, mExtraKeyName);
        intent.putExtra(mExtraKeyName, new Gson().toJson(userSignInResult));
        mActivity.startActivity(intent);
    }

    public String getAccessToken() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences(mActivity.getPackageName()+Constant.SHARED_PREFS_KEY_FILE,Context.MODE_PRIVATE);
        String token = sharedPref.getString(Constant.SHARED_PREFS_TOKEN_KEY,null);
        Log.d(TAG,"Token from SharedPrefs:"+token);
        return token;
    }

}