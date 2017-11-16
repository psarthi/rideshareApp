package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.CustomCountryAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.user.domain.Country;
import com.digitusrevolution.rideshare.model.user.dto.UserRegistration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class MobileRegistrationActivity extends AppCompatActivity {

    private static final String TAG = MobileRegistrationActivity.class.getName();
    private Spinner mCountryNameSpinner;
    private EditText mMobileNumber;
    private Button mSendOTPButton;
    private CircleImageView mPhotoImageView;
    private List<Country> mCountries;
    private String mOTP;
    private UserRegistration mUserRegistration;
    private String mSelectedCountryCode;
    private Country mSelectedCountry;
    private String mExtraKeyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);
        getSupportActionBar().hide();

        mCountryNameSpinner = findViewById(R.id.country_name_spinner);
        mMobileNumber = findViewById(R.id.mobile_number);
        mSendOTPButton = findViewById(R.id.send_otp_button);
        mPhotoImageView = findViewById(R.id.photo);

        getExtraFromIntent();
        displayProfilePhoto();
        setCountryList();

        mSendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        /*
        //Below is the sample snippet for dnownloading image from URL
        RESTClient.get(photoURL, null, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {

                String photo = Base64.encodeToString(binaryData,Base64.DEFAULT);
                Log.d(TAG,photo);
                mPhotoImageView.setImageBitmap(BitmapFactory.decodeByteArray(binaryData,0,binaryData.length));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

            }
        });
        */
    }

    private void getExtraFromIntent() {
        Intent intent = getIntent();
        mExtraKeyName = intent.getStringExtra(Constant.INTENT_EXTRA_KEY);
        String data = intent.getStringExtra(mExtraKeyName);
        mUserRegistration = new Gson().fromJson(data,UserRegistration.class);
        Log.d(TAG,"Photo URL:"+mUserRegistration.getPhoto().getImageLocation());
    }

    private void sendOTP() {
        mSelectedCountry = (Country) mCountryNameSpinner.getSelectedItem();
        mSelectedCountryCode = mSelectedCountry.getCode();
        Log.d(TAG,"Sending OTP to Mobile number "+
                mSelectedCountryCode +
                mMobileNumber.getText().toString());

        if (mCountryNameSpinner.getSelectedItem()!=null) {
            if (mMobileNumber.getText().length() == 10) {
                String GET_OTP_URL = APIUrl.GET_OTP_URL.replace(APIUrl.MOBILE_NUMBER_KEY,mSelectedCountryCode+
                        mMobileNumber.getText().toString());
                RESTClient.get(GET_OTP_URL, null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "Response Failure:" + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, "Response Success:" + responseString);
                        mOTP = responseString;
                        SaveExtra();
                        Intent otpVerificationIntent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                        //Reason for storing key name as well, so that calling class don't have to know the key name
                        otpVerificationIntent.putExtra(Constant.INTENT_EXTRA_KEY,mExtraKeyName);
                        otpVerificationIntent.putExtra(mExtraKeyName,new Gson().toJson(mUserRegistration));
                        startActivity(otpVerificationIntent);
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), R.string.invalid_mobile_number, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.system_exception_msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayProfilePhoto() {

        //This is easy way to get impage downloaded and displaying as well using Picaso Library
        Picasso.with(this).load(mUserRegistration.getPhoto().getImageLocation()).into(mPhotoImageView);
    }

    private void setCountryList(){
        RESTClient.get(APIUrl.GET_COUNTRIES_URL,null,new JsonHttpResponseHandler(){

            //Note - Its important to use proper OnSuccess method with JsonArray instead of JsonObject as we are expecting JsonArray from the response
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG,"Response Success:"+response);
                //This is important otherwise Gson is unable to convert JsonArray to List
                Type listType = new TypeToken<List<Country>>() {}.getType();
                mCountries = (List<Country>) new Gson().fromJson(response.toString(), listType);
                ArrayAdapter<Country> countryArrayAdapter = new CustomCountryAdapter(getApplicationContext(), mCountries);
                // Apply the adapter to the spinner
                mCountryNameSpinner.setAdapter(countryArrayAdapter);

                /*
                //Below snapshot is for reference which shows that no need to write custom adapter if the value is plan String
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("India");
                arrayList.add("US");
                ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
                // Specify the layout to use when the list of choices appears
                // Don't setDropDownView here and instead overwrite getDropDownView since we are using Object instead of String.
                //If you are using plan String in ArrayAdapter then no need to write custom adapter and below set function would do the job
                countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                */
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG,"Response Failed:"+errorResponse);
            }
        });
    }

    private void SaveExtra(){
        mUserRegistration.setOtp(mOTP);
        mUserRegistration.setMobileNumber(mSelectedCountryCode + mMobileNumber.getText().toString());
        mUserRegistration.setCountry(mSelectedCountry);
    }
}
