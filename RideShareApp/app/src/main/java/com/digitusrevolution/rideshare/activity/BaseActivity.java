package com.digitusrevolution.rideshare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();
    private ProgressDialog mProgressDialog;

    public String getExtraDataKey(){
        //Reason for appending packageName as per recommendation on android docs so that
        // it doesn't get name clash with other objects
        return getPackageName() + Constant.INTENT_EXTRA_DATA_KEY;
    }

    public void startHomePageActivity(UserSignInResult userSignInResult){
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    public void startLandingPageActivity(){
        Intent intent = new Intent(this, LandingPageActivity.class);
        //This will clear all activity from the stack so that when user clicks on back it will not take you to home page
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void signOut() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);

        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startLandingPageActivity();
            }
        });

    }

}
