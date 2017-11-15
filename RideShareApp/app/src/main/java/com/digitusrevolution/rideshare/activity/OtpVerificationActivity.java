package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = OtpVerificationActivity.class.getName();
    private TextView mVerificationCodeSubHeading;
    private EditText mOTPCode1stNumber;
    private EditText mOTPCode2ndNumber;
    private EditText mOTPCode3rdNumber;
    private EditText mOTPCode4thNumber;
    private TextView mResendText;
    private Button mOTPConfirmationButton;
    private UserRegistration mUserRegistration;
    private String mExtraKeyName;
    private String mOTPInput;
    private boolean mOTPMatch;
    private UserSignInResult mUserSignInResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        getSupportActionBar().hide();

        mVerificationCodeSubHeading = findViewById(R.id.verify_mobile_sub_heading);
        mOTPCode1stNumber = findViewById(R.id.otp_code_1st_number);
        mOTPCode2ndNumber = findViewById(R.id.otp_code_2nd_number);
        mOTPCode3rdNumber = findViewById(R.id.otp_code_3rd_number);
        mOTPCode4thNumber = findViewById(R.id.otp_code_4th_number);
        mResendText = findViewById(R.id.resend_text);
        mOTPConfirmationButton = findViewById(R.id.otp_confirmation_button);
        mExtraKeyName = getPackageName()+".data";

        getExtraFromIntent();
        addTextChangedListenerOnOTPTextField();

        mOTPConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mOTPInput = mOTPCode1stNumber.getText().toString()
                            +mOTPCode2ndNumber.getText().toString()
                            +mOTPCode3rdNumber.getText().toString()
                            +mOTPCode4thNumber.getText().toString();

                Log.d(TAG,"OTP Input Code is - "+ mOTPInput);

                validateOTP();

            }
        });

    }

    private void addTextChangedListenerOnOTPTextField() {
        mOTPCode1stNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mOTPCode1stNumber.getText().length() == getResources().getInteger(R.integer.otp_text_max_length)){
                    mOTPCode2ndNumber.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mOTPCode2ndNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mOTPCode2ndNumber.getText().length() == getResources().getInteger(R.integer.otp_text_max_length)){
                    mOTPCode3rdNumber.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mOTPCode3rdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mOTPCode3rdNumber.getText().length() == getResources().getInteger(R.integer.otp_text_max_length)){
                    mOTPCode4thNumber.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mOTPCode4thNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mOTPCode4thNumber.getText().length() == getResources().getInteger(R.integer.otp_text_max_length)){
                    mOTPConfirmationButton.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getExtraFromIntent() {
        Intent intent = getIntent();
        String data = intent.getStringExtra(mExtraKeyName);
        mUserRegistration = new Gson().fromJson(data,UserRegistration.class);
        Log.d(TAG,"OTP:" + mUserRegistration.getOtp());
        Toast.makeText(this,"OTP:"+mUserRegistration.getOtp(),Toast.LENGTH_LONG).show();
    }

    private void validateOTP(){

        String VALIDATE_OTP_URL = APIUrl.VALIDATE_OTP_URL.replace(APIUrl.MOBILE_NUMBER_KEY,mUserRegistration.getMobileNumber())
                                  .replace(APIUrl.OTP_KEY,mOTPInput);
        RESTClient.get(VALIDATE_OTP_URL, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG,"Response Failed:"+responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                mOTPMatch = Boolean.parseBoolean(responseString);

                Log.d(TAG,"Response Success:"+responseString);

                if (mOTPMatch) {
                    Log.d(TAG,"OTP Validation Success");
                    mUserRegistration.setOtp(mOTPInput);
                    RegisterUser();

                } else {
                    Log.d(TAG,"OTP Validation failed");
                    Toast.makeText(OtpVerificationActivity.this,"OTP Validation Failed, please reenter valid OTP",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RegisterUser() {

        RESTClient.post(this,APIUrl.USER_REGISTRATION,mUserRegistration,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mUserSignInResult = new Gson().fromJson(response.toString(),UserSignInResult.class);
                Log.d(TAG,"User has been successfully registered, Redirect to Home Page");
                Log.d(TAG,"Access Token:"+mUserSignInResult.getToken());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG,"Response Failed:"+errorResponse);
            }
        });
    }

}
