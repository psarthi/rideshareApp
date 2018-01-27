package com.digitusrevolution.rideshare.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.domain.Photo;
import com.digitusrevolution.rideshare.model.user.domain.RegistrationType;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GoogleSignInInfo;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.digitusrevolution.rideshare.model.user.dto.UserStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();
    private static final int RC_SIGN_IN = 9001;
    private CommonUtil mCommonUtil = new CommonUtil(this);

    public String getExtraDataKey(){
        //Reason for appending packageName as per recommendation on android docs so that
        // it doesn't get name clash with other objects
        return getPackageName() + Constant.INTENT_EXTRA_DATA_KEY;
    }

    public void startHomePageActivity(){
        Intent intent = new Intent(this, HomePageActivity.class);
        //This will clear all activity from the stack so that when user clicks on back it will not take you to home page
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void startLandingPageActivity(){
        Intent intent = new Intent(this, LandingPageActivity.class);
        //This will clear all activity from the stack so that when user clicks on back it will not take you to home page
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private GoogleSignInClient getGoogleSignInClient(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
        return googleSignInClient;
    }

    public void googleSignIn() {

        Log.d(TAG,"Google Sign In Button Clicked");
        Intent signInIntent = getGoogleSignInClient().getSignInIntent();
        mCommonUtil.showProgressDialog();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.d(TAG,"Sign in success");

            String CHECK_USER_EXIST_URL = APIUrl.CHECK_USER_EXIST_URL;
            CHECK_USER_EXIST_URL = CHECK_USER_EXIST_URL.replace(APIUrl.USER_EMAIL_KEY, account.getEmail());

            RESTClient.get(CHECK_USER_EXIST_URL,null,new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG,"CHECK_USER_EXIST_URL Response Success (JsonObject): "+response);
                    UserStatus status = new Gson().fromJson(response.toString(), UserStatus.class);
                    if (status.isUserExist()){
                        loadHomePage(account.getEmail(), account.getIdToken());
                    }
                    else {
                        Log.d(TAG,"User doesn't exist:" + account.getEmail());
                        mCommonUtil.dismissProgressDialog();
                        mobileRegistration(account);
                    }
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            mCommonUtil.dismissProgressDialog();
            if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED){
                Log.d(TAG, "Sign in Cancelled");
            } else {
                Toast.makeText(BaseActivity.this, R.string.system_exception_msg, Toast.LENGTH_LONG).show();
            }
            // updateUI(null);
        }
    }

    public void loadHomePage(String email, String googleSignInIdToken) {
        Log.d(TAG,"Redirect to Home Page as User exist");
        //Toast.makeText(LandingPageActivity.this,"User Exist, Redirecting to Home Page",Toast.LENGTH_SHORT).show();

        GoogleSignInInfo googleSignInInfo = new GoogleSignInInfo();
        googleSignInInfo.setEmail(email);
        googleSignInInfo.setSignInToken(googleSignInIdToken);
        //Show progress dialog as this can be called directly from splash screen when user exist in shared prefs
        mCommonUtil.showProgressDialog();
        RESTClient.post(this, APIUrl.GOOGLE_SIGN_IN_URL,
                googleSignInInfo,new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        //Dismiss progress dialog only when home page activity is loaded, else its confusing to the user
                        //It has been dismissed inside onCreate of HomePageActivity
                        //If we don't dismiss here, then android complains of Decorleak
                        mCommonUtil.dismissProgressDialog();
                        UserSignInResult userSignInResult = new Gson().fromJson(response.toString(),UserSignInResult.class);
                        mCommonUtil.saveUserSignInResult(userSignInResult);
                        startHomePageActivity();
                    }
                });
    }

    private void mobileRegistration(GoogleSignInAccount account){

        //TokenId is only useful if you want to revalidate signIn from backend server again
        Log.d(TAG,"DisplayName:"+account.getDisplayName()
                +"\nEmail:"+account.getEmail()
                +"\nFirst Name:"+account.getGivenName()
                +"\nLast Name:"+account.getFamilyName()
                +"\nTokenId:"+account.getIdToken()
                +"\nPhotoURL:"+account.getPhotoUrl());

        String data = getExtraData(account);

        Intent mobileRegistrationIntent = new Intent(this,MobileRegistrationActivity.class);
        mobileRegistrationIntent.putExtra(getExtraDataKey(),data);
        startActivity(mobileRegistrationIntent);
    }

    @NonNull
    private String getExtraData(GoogleSignInAccount account) {
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setFirstName(account.getGivenName());
        userRegistration.setLastName(account.getFamilyName());
        userRegistration.setEmail(account.getEmail());
        userRegistration.setSignInToken(account.getIdToken());
        Photo photo = new Photo();
        photo.setImageLocation(account.getPhotoUrl().toString());
        userRegistration.setPhoto(photo);
        userRegistration.setRegistrationType(RegistrationType.Google);
        return new Gson().toJson(userRegistration);
    }

    //Reason for having this in base fragment, as this can be called from
    //Mobile registration screen/otp verification screen as well
    public void signOut() {

        getGoogleSignInClient().signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mCommonUtil.removeSharedPref();
                startLandingPageActivity();
            }
        });
    }

}
