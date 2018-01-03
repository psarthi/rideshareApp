package com.digitusrevolution.rideshare.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequest;
import com.digitusrevolution.rideshare.model.user.domain.Photo;
import com.digitusrevolution.rideshare.model.user.domain.RegistrationType;
import com.digitusrevolution.rideshare.model.user.dto.GoogleSignInInfo;
import com.digitusrevolution.rideshare.model.user.dto.SignInInfo;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.digitusrevolution.rideshare.model.user.dto.UserStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public class LandingPageActivity extends BaseActivity{

    private static final String TAG = LandingPageActivity.class.getName();
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleSignInButton;
    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        mCommonUtil = new CommonUtil(this);
        mGoogleSignInButton = findViewById(R.id.google_sign_in_button);

        //Change the text of google sign in button
        for (int i=0;i<mGoogleSignInButton.getChildCount();i++){
            View view = mGoogleSignInButton.getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).setText(R.string.google_sign_in);
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn() {
        Log.d(TAG,"Google Sign In Button Clicked");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        showProgressDialog();
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

            RESTClient.get(CHECK_USER_EXIST_URL,null,new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG,"CHECK_USER_EXIST_URL Response Success (JsonObject): "+response);
                    UserStatus status = new Gson().fromJson(response.toString(), UserStatus.class);
                    if (status.isUserExist()){
                        Log.d(TAG,"Redirect to Home Page as User exist");
                        //Toast.makeText(LandingPageActivity.this,"User Exist, Redirecting to Home Page",Toast.LENGTH_SHORT).show();

                        GoogleSignInInfo googleSignInInfo = new GoogleSignInInfo();
                        googleSignInInfo.setEmail(account.getEmail());
                        RESTClient.post(LandingPageActivity.this,APIUrl.GOOGLE_SIGN_IN_URL,
                                googleSignInInfo,new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        UserSignInResult userSignInResult = new Gson().fromJson(response.toString(),UserSignInResult.class);
                                        mCommonUtil.saveUserSignInResult(userSignInResult);
                                        startHomePageActivity(userSignInResult);
                                        dismissProgressDialog();
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                        dismissProgressDialog();
                                        if (errorResponse!=null) {
                                            ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                                            Log.d(TAG, errorMessage.getErrorMessage());
                                            Toast.makeText(LandingPageActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                                            Toast.makeText(LandingPageActivity.this, R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                    else {
                        Log.d(TAG,"User doesn't exist:" + account.getEmail());
                        mobileRegistration(account);
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dismissProgressDialog();
                    //This will take care of all type of exception as we can't handle any exception here, so showing generic error
                    //in most likely chances this would Internal Server error or Time Out etc.
                    Log.d(TAG, "Request Failed with error:" + throwable.getMessage());
                    Toast.makeText(LandingPageActivity.this, R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            dismissProgressDialog();
            Toast.makeText(LandingPageActivity.this, R.string.system_exception_msg, Toast.LENGTH_LONG).show();
            // updateUI(null);
        }
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
        Photo photo = new Photo();
        photo.setImageLocation(account.getPhotoUrl().toString());
        userRegistration.setPhoto(photo);
        userRegistration.setRegistrationType(RegistrationType.Google);
        return new Gson().toJson(userRegistration);
    }
}
