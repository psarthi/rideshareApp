package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
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
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.common.ResponseMessage;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class OtpVerificationActivity extends BaseActivity {

    private static final String TAG = OtpVerificationActivity.class.getName();
    private TextView mVerificationCodeSubHeading;
    private EditText mOTPCode1stNumber;
    private EditText mOTPCode2ndNumber;
    private EditText mOTPCode3rdNumber;
    private EditText mOTPCode4thNumber;
    private TextView mResendText;
    private Button mOTPConfirmationButton;
    private UserRegistration mUserRegistration;
    private String mOTPInput;
    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        getSupportActionBar().hide();
        mCommonUtil = new CommonUtil(this);

        mVerificationCodeSubHeading = findViewById(R.id.otp_verification_sub_heading);
        mOTPCode1stNumber = findViewById(R.id.otp_code_1st_number);
        mOTPCode2ndNumber = findViewById(R.id.otp_code_2nd_number);
        mOTPCode3rdNumber = findViewById(R.id.otp_code_3rd_number);
        mOTPCode4thNumber = findViewById(R.id.otp_code_4th_number);
        mResendText = findViewById(R.id.resend_text);
        mOTPConfirmationButton = findViewById(R.id.otp_confirmation_button);

        Intent intent = getIntent();
        //Package name would always be same for the application, so key would also be the same and its independent of activity
        String data = intent.getStringExtra(getExtraDataKey());
        mUserRegistration = new Gson().fromJson(data,UserRegistration.class);
        Log.d(TAG,"OTP:" + mUserRegistration.getOtp());
        Log.d(TAG,"Mobile Number:" + mUserRegistration.getMobileNumber());
        Toast.makeText(this,"OTP:"+mUserRegistration.getOtp(),Toast.LENGTH_LONG).show();

        addTextChangedListenerOnOTPTextField();

        //Note - I have appended "+" sign as we are storing country code without + due to some technical issue
        mVerificationCodeSubHeading.append(mUserRegistration.getMobileNumber());

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

        mResendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reSendOTP();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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

    private void validateOTP(){
        //Reason for doing encoding as we have to send + sign in mobile number as query parameters and without encoding that data would interpreted differently
        try {
            String encodedQueryString = URLEncoder.encode(mUserRegistration.getMobileNumber(), "UTF-8");
            String VALIDATE_OTP_URL = APIUrl.VALIDATE_OTP_URL.replace(APIUrl.MOBILE_NUMBER_KEY,encodedQueryString)
                    .replace(APIUrl.OTP_KEY,mOTPInput);
            mCommonUtil.showProgressDialog();
            RESTClient.get(VALIDATE_OTP_URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG,"Response Success:"+response);
                    mCommonUtil.dismissProgressDialog();
                    ResponseMessage responseMessage = new Gson().fromJson(response.toString(), ResponseMessage.class);
                    boolean OTPMatch = Boolean.parseBoolean(responseMessage.getResult());
                    if (OTPMatch) {
                        Log.d(TAG,"OTP Validation Success");
                        mUserRegistration.setOtp(mOTPInput);
                        registerUser();

                    } else {
                        Log.d(TAG,"OTP Validation failed");
                        Toast.makeText(OtpVerificationActivity.this,"OTP Validation Failed, please reenter valid OTP",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            mCommonUtil.dismissProgressDialog();
            //TODO This needs to be figured out how to handle this exception
            e.printStackTrace();
        }
    }

    private void registerUser() {
        mCommonUtil.showProgressDialog();
        RESTClient.post(this,APIUrl.USER_REGISTRATION_URL,mUserRegistration,new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mCommonUtil.dismissProgressDialog();
                UserSignInResult userSignInResult = new Gson().fromJson(response.toString(),UserSignInResult.class);
                Log.d(TAG,"User has been successfully registered, Redirect to Home Page");
                Log.d(TAG,"Access Token:"+userSignInResult.getToken());
                CommonUtil commonUtil = new CommonUtil(OtpVerificationActivity.this);
                commonUtil.saveUserSignInResult(userSignInResult);
                startHomePageActivity();
            }
        });
    }

    private void reSendOTP() {
        try {
            String encodedQueryString = URLEncoder.encode(mUserRegistration.getMobileNumber(), "UTF-8");
            String GET_OTP_URL = APIUrl.GET_OTP_URL.replace(APIUrl.MOBILE_NUMBER_KEY,encodedQueryString);
            mCommonUtil.showProgressDialog();
            RESTClient.get(GET_OTP_URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Log.d(TAG, "Response Success:" + response);
                    ResponseMessage responseMessage = new Gson().fromJson(response.toString(), ResponseMessage.class);
                    Toast.makeText(OtpVerificationActivity.this,"OTP:"+responseMessage.getResult(),Toast.LENGTH_LONG).show();

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }

}
