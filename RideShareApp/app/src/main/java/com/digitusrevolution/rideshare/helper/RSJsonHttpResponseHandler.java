package com.digitusrevolution.rideshare.helper;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.BaseActivity;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 1/14/18.
 */

public class RSJsonHttpResponseHandler extends JsonHttpResponseHandler {

    public static final String TAG = RSJsonHttpResponseHandler.class.getName();
    private CommonUtil mCommonUtil;

    public RSJsonHttpResponseHandler(CommonUtil commonUtil){
        mCommonUtil = commonUtil;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        mCommonUtil.dismissProgressDialog();
        if (errorResponse!=null) {
            ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
            if (!errorMessage.getErrorCause().equals("NA")){
                Log.d(TAG, "Request Failed with Proper ErrorMessage:"+ errorMessage.getErrorMessage());
                Toast.makeText(mCommonUtil.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
            } else {
                showSystemErrorMsg(errorMessage.getErrorMessage());
            }
        }
        //This would be the case, where we got Json response but errorResponse is not our custom ErrorMessage
        else {
            showSystemErrorMsg(throwable.getMessage());
        }
    }

    // This function would be called when we didn't get Json response e.g. in case of 500 error
    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        mCommonUtil.dismissProgressDialog();
        showSystemErrorMsg(throwable.getMessage());
    }

    private void showSystemErrorMsg(String msg){
        Log.d(TAG, "Request Failed with system error:"+ msg);
        Toast.makeText(mCommonUtil.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
    }
}
