package com.digitusrevolution.rideshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MobileRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "MobileRegistration";
    private Spinner mCountryNameSpinner;
    private EditText mMobileNumber;
    private Button mSendOTPButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_registration);
        getSupportActionBar().hide();

        mCountryNameSpinner = findViewById(R.id.country_name_spinner);
        mMobileNumber = findViewById(R.id.mobile_number);
        mSendOTPButton = findViewById(R.id.send_otp_button);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_name,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mCountryNameSpinner.setAdapter(adapter);


        mSendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Sending OTP to Mobile number "+
                        mCountryNameSpinner.getSelectedItem().toString()+" " +
                        mMobileNumber.getText().toString());
            }
        });


    }
}
