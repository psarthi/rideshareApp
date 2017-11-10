package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
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
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequest;
import com.digitusrevolution.rideshare.test.SampleDateModel;
import com.digitusrevolution.rideshare.test.SampleStringLocalTimeModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class LandingPageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "RideShare";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
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

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        String GET_URL= APIUrl.GET_RIDE_URL;
        GET_URL = GET_URL.replace("{id}", "1");

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

    private void signUp() {
        Log.d(TAG,"Sign Up Button clicked");

        String POST_URL = APIUrl.POST_URL;

        SampleStringLocalTimeModel model = new SampleStringLocalTimeModel();
        model.setTime("00:30");
        Gson gson = new Gson();
        Log.d(TAG,"Post Initial Value:"+ gson.toJson(model));

        RESTClient.post(this,POST_URL,model, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG,"POST Response Success: "+response);
                        Gson gson = new Gson();
                        SampleStringLocalTimeModel sampleStringLocalTimeModel = gson.fromJson(response.toString(), SampleStringLocalTimeModel.class);
                        Log.d(TAG,"POST: From Gson Model:"+ sampleStringLocalTimeModel.getTime());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        Log.d(TAG,"POST Response Failure:" + errorResponse);
                    }
                });
    }

    private void googleSignIn() {
        Log.d(TAG,"Google Sign In Button Clicked");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);

        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d(TAG,"DisplayName:"+acct.getDisplayName()
                    +"\nEmail:"+acct.getEmail()
                    +"\nFirst Name:"+acct.getGivenName()
                    +"\nLast Name:"+acct.getFamilyName()
                    +"\nTokenId:"+acct.getIdToken()
                    +"\nPhotoURL:"+acct.getPhotoUrl());

            Intent mobileRegistrationIntent = new Intent(this,MobileRegistrationActivity.class);
            mobileRegistrationIntent.putExtra("photoURL",acct.getPhotoUrl().toString());
            startActivity(mobileRegistrationIntent);

        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG,"Google Sign In Failed");
        }
    }
}
