package com.parift.rideshare.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.serviceprovider.domain.core.AppInfo;
import com.parift.rideshare.model.user.dto.BasicUser;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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
                mCommonUtil.showProgressDialog();
                RESTClient.get(APIUrl.GET_APP_INFO, null, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
                        AppInfo appInfo = new Gson().fromJson(response.toString(), AppInfo.class);
                        mCommonUtil.saveInSharedPref(Constant.SHARED_PREFS_APP_INFO_KEY, response.toString());
                        if (mCommonUtil.getAppVersionCode() < appInfo.getMinAppVersionCode()){
                            Logger.debug(TAG, "Update App");
                            DialogFragment dialogFragment = UpdateDialog.newInstance(appInfo.getAppUrl());
                            dialogFragment.show(getSupportFragmentManager(), UpdateDialog.TAG);
                        } else {
                            if (user!=null){
                                loadHomePage(user.getEmail(), null);
                            } else {
                                startLandingPageActivity();
                            }
                        }
                    }
                });
                //VERY IMP - Don't call finish() here else this will close the activity
                //before even launching the next activity
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    public static class UpdateDialog extends DialogFragment {

        public static final String TAG = UpdateDialog.class.getName();
        private String mUrl;

        public UpdateDialog(){}

        public static UpdateDialog newInstance(String url){
            UpdateDialog updateDialog = new UpdateDialog();
            updateDialog.mUrl = url;
            return updateDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("New update available! Please update to proceed.")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(mUrl));
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
