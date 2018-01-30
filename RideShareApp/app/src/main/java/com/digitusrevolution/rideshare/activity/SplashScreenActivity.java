package com.digitusrevolution.rideshare.activity;

import android.os.Bundle;
import android.os.Handler;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;

public class SplashScreenActivity extends BaseActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private static final String TAG = SplashScreenActivity.class.getName();
    private CommonUtil mCommonUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mCommonUtil = new CommonUtil(this);
        final BasicUser user = mCommonUtil.getUser();
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (user!=null){
                    loadHomePage(user.getEmail(), null);
                } else {
                    startLandingPageActivity();
                }
                //VERY IMP - Don't call finish() here else this will close the activity
                //before even launching the next activity
            }
        }, SPLASH_TIME_OUT);
    }
}
