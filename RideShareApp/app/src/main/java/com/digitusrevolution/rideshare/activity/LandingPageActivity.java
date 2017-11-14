package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequest;
import com.digitusrevolution.rideshare.model.user.domain.Photo;
import com.digitusrevolution.rideshare.model.user.dto.SignInInfo;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.digitusrevolution.rideshare.model.user.dto.UserStatus;
import com.digitusrevolution.rideshare.test.SampleDateModel;
import com.digitusrevolution.rideshare.test.SampleStringLocalTimeModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public class LandingPageActivity extends AppCompatActivity{

    private static final String TAG = LandingPageActivity.class.getName();
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleSignInButton;
    private Button mSignUpButton;
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        mGoogleSignInButton = findViewById(R.id.google_sign_in_button);
        mSignUpButton = findViewById(R.id.sign_up_button);
        mSignInButton = findViewById(R.id.sign_in_button);

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
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    signIn();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void signIn() throws URISyntaxException {
        Log.d(TAG,"Sign In Button clicked");

        String POST_URL = APIUrl.SIGN_IN_URL;

        SignInInfo signInInfo = new SignInInfo();
        signInInfo.setEmail("email-1");
        signInInfo.setPassword("password");
        Gson gson = new Gson();
        Log.d(TAG,"Post Initial Value:"+ gson.toJson(signInInfo));

        RESTClient.post(this,POST_URL,signInInfo, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG,"POST Response Success: "+response);
                Gson gson = new Gson();
                UserSignInResult userSignInResult = gson.fromJson(response.toString(), UserSignInResult.class);
                Log.d(TAG,"POST: From Gson Model:"+ userSignInResult.getToken());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.d(TAG,"POST Response Failure:" + errorResponse);
            }
        });

    }

    private void signUp() {
        Log.d(TAG,"Sign Up Button clicked");

        String GET_URL= APIUrl.GET_RIDE_URL;
        GET_URL = GET_URL.replace(APIUrl.ID_KEY, "1");

        RESTClient.get(GET_URL, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d(TAG,"GET Response Success: "+response);
                Gson gson = new Gson();
                RideRequest rideRequest = gson.fromJson(response.toString(), RideRequest.class);
                Log.d(TAG,"GET: Ride Request TimeVariation: "+ rideRequest.getPickupPoint().getTimeVariation());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Log.d(TAG,"GET Response Failure: "+errorResponse);
            }
        });
    }

    private void googleSignIn() {
        Log.d(TAG,"Google Sign In Button Clicked");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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
                    } else {
                        Log.d(TAG,"User doesn't exist:" + account.getEmail());
                        mobileRegistration(account);
                    }
                }
            });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
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

        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setFirstName(account.getGivenName());
        userRegistration.setLastName(account.getFamilyName());
        userRegistration.setEmail(account.getEmail());
        Photo photo = new Photo();
        photo.setImageLocation(account.getPhotoUrl().toString());
        userRegistration.setPhoto(photo);

        Intent mobileRegistrationIntent = new Intent(this,MobileRegistrationActivity.class);
        //Reason for appending packageName as per recommendation on android docs so that
        // it doesn't get name clash with other objects
        mobileRegistrationIntent.putExtra(getPackageName()+".data",new Gson().toJson(userRegistration));
        startActivity(mobileRegistrationIntent);
    }


}
