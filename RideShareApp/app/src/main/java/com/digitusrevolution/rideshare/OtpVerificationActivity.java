package com.digitusrevolution.rideshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG="RideShare";
    private TextView mVerificationCodeSubHeading;
    private EditText mOTPCode1stNumber;
    private EditText mOTPCode2ndNumber;
    private EditText mOTPCode3rdNumber;
    private EditText mOTPCode4thNumber;
    private TextView mResendText;
    private Button mOTPConfirmationButton;

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

        mOTPConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"OTP Confirmation code is - "
                        +mOTPCode1stNumber.getText().toString()
                        +mOTPCode2ndNumber.getText().toString()
                        +mOTPCode3rdNumber.getText().toString()
                        +mOTPCode4thNumber.getText().toString());
            }
        });
    }
}
