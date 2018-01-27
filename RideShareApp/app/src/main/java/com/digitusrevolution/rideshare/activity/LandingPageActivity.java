package com.digitusrevolution.rideshare.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.google.android.gms.common.SignInButton;

public class LandingPageActivity extends BaseActivity{

    private static final String TAG = LandingPageActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);

        //Change the text of google sign in button
        for (int i=0;i<googleSignInButton.getChildCount();i++){
            View view = googleSignInButton.getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).setText(R.string.google_sign_in);
            }
        }

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }
}
