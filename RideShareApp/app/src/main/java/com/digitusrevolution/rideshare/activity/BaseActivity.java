package com.digitusrevolution.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digitusrevolution.rideshare.config.Constant;
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

    public void saveAccessToken(UserSignInResult userSignInResult) {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constant.SHARED_PREFS_TOKEN_KEY, userSignInResult.getToken());
        editor.commit();
        String token = sharedPref.getString(Constant.SHARED_PREFS_TOKEN_KEY, null);
        Log.d(TAG, "Token Saved is:" + token);
    }

    public void startHomePageActivity(UserSignInResult userSignInResult){
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(getExtraDataKey(), new Gson().toJson(userSignInResult));
        startActivity(intent);
    }

    public String getAccessToken() {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName()+Constant.SHARED_PREFS_KEY_FILE,Context.MODE_PRIVATE);
        String token = sharedPref.getString(Constant.SHARED_PREFS_TOKEN_KEY,null);
        Log.d(TAG,"Token from SharedPrefs:"+token);
        return token;
    }

}
