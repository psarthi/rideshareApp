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
            Log.d(TAG, errorMessage.getErrorMessage());
            Toast.makeText(mCommonUtil.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Inside onFailure(JsonObject) - Request Failed with error:"+ throwable.getMessage());
            Toast.makeText(mCommonUtil.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        mCommonUtil.dismissProgressDialog();
        Log.d(TAG, "Inside onFailure(responseString) - Request Failed with error:"+ throwable.getMessage());
        Toast.makeText(mCommonUtil.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
    }
}
