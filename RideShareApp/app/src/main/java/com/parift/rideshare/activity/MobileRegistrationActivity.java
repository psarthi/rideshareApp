package com.parift.rideshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.adapter.CustomCountryAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.common.ResponseMessage;
import com.parift.rideshare.model.user.domain.Country;
import com.parift.rideshare.model.user.dto.UserRegistration;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class MobileRegistrationActivity extends BaseActivity {

    private static final String TAG = MobileRegistrationActivity.class.getName();
    private Spinner mCountryNameSpinner;
    private EditText mMobileNumber;
    private Button mSendOTPButton;
    private CircleImageView mPhotoImageView;
    private UserRegistration mUserRegistration;
    private String mSelectedCountryCode;
    private Country mSelectedCountry;
    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);
        getSupportActionBar().hide();

        mCommonUtil = new CommonUtil(this);
        mCountryNameSpinner = findViewById(R.id.country_name_spinner);
        mMobileNumber = findViewById(R.id.mobile_number);
        mSendOTPButton = findViewById(R.id.send_otp_button);
        mPhotoImageView = findViewById(R.id.photo);


        Intent intent = getIntent();
        //Package name would always be same for the application, so key would also be the same and its independent of activity
        String data = intent.getStringExtra(getExtraDataKey());
        mUserRegistration = new Gson().fromJson(data,UserRegistration.class);
        Logger.debug(TAG,"Photo URL:"+mUserRegistration.getPhoto().getImageLocation());
        displayProfilePhoto();

        mSendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP();
            }
        });

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        /*
        //Below is the sample snippet for dnownloading image from URL
        RESTClient.get(photoURL, null, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {

                String photo = Base64.encodeToString(binaryData,Base64.DEFAULT);
                Logger.debug(TAG,photo);
                mPhotoImageView.setImageBitmap(BitmapFactory.decodeByteArray(binaryData,0,binaryData.length));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCountryList();
    }

    private void sendOTP(){
        mSelectedCountry = (Country) mCountryNameSpinner.getSelectedItem();
        mSelectedCountryCode = mSelectedCountry.getCode();
        Logger.debug(TAG,"Sending OTP to Mobile number "+
                mSelectedCountryCode +
                mMobileNumber.getText().toString());

        if (mCountryNameSpinner.getSelectedItem()!=null) {
            if (mMobileNumber.getText().length() == 10) {
                try {
                    //Reason for doing encoding as we have to send + sign in mobile number as query parameters and without encoding that data would interpreted differently
                    String encodedQueryString = URLEncoder.encode(mSelectedCountryCode + mMobileNumber.getText().toString(), "UTF-8");
                    String GET_OTP_URL = APIUrl.GET_OTP_URL.replace(APIUrl.MOBILE_NUMBER_KEY,encodedQueryString)
                            .replace(APIUrl.OTP_RETRY_STATUS, "false");
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(GET_OTP_URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Logger.debug(TAG, "Response Success:" + response);
                            String data = getExtraData();
                            Intent otpVerificationIntent = new Intent(getApplicationContext(), OtpVerificationActivity.class);
                            //Reason for storing key name as well, so that calling class don't have to know the key name
                            otpVerificationIntent.putExtra(getExtraDataKey(),data);
                            startActivity(otpVerificationIntent);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    //TODO This needs to be figured out how to handle this exception
                    e.printStackTrace();
                }
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
        mCommonUtil.showProgressDialog();
        RESTClient.get(APIUrl.GET_COUNTRIES_URL,null,new RSJsonHttpResponseHandler(mCommonUtil){

            //Note - Its important to use proper OnSuccess method with JsonArray instead of JsonObject as we are expecting JsonArray from the response
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Logger.debug(TAG,"Response Success:"+response);
                //This is important otherwise Gson is unable to convert JsonArray to List
                Type listType = new TypeToken<List<Country>>() {}.getType();
                List<Country> countries= (List<Country>) new Gson().fromJson(response.toString(), listType);
                ArrayAdapter<Country> countryArrayAdapter = new CustomCountryAdapter(getApplicationContext(), countries);
                // Apply the adapter to the spinner
                mCountryNameSpinner.setAdapter(countryArrayAdapter);

                /*
                //Below snapshot is for reference which shows that no need to write custom adapter if the value is plan String
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.drawable.add("India");
                arrayList.drawable.add("US");
                ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,arrayList);
                // Specify the layout to use when the list of choices appears
                // Don't setDropDownView here and instead overwrite getDropDownView since we are using Object instead of String.
                //If you are using plain String in ArrayAdapter then no need to write custom adapter and below set function would do the job
                countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                */
                mCommonUtil.dismissProgressDialog();
            }
        });
    }

    private String getExtraData(){
        mUserRegistration.setMobileNumber(mSelectedCountryCode + mMobileNumber.getText().toString());
        mUserRegistration.setCountry(mSelectedCountry);
        //This will take care of updating push notification token at the time of registration
        //as initially user would not have even existed
        //Note - We are doing this check and update at three points - Token Refresh, User Registration and User Login
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mUserRegistration.setPushNotificationToken(refreshedToken);
        return new Gson().toJson(mUserRegistration);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

}
