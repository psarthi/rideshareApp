package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideComp {

    public static final String TAG = RideComp.class.getName();
    BaseFragment mBaseFragment;
    FullRide mRide;

    Button mCancelButton;
    Button mStartButton;
    Button mEndButton;
    LinearLayout mBasicRideButtonsLayout;
    private CommonUtil mCommonUtil;
    private RideCompListener mListener;

    public RideComp(BaseFragment fragment, FullRide ride){
        mBaseFragment = fragment;
        mRide = ride;
        mCommonUtil = new CommonUtil(fragment);
        //Its important to check instance of fragment as some fragment may not be required to implement Listener
        //And in those cases, it will throw ClassCasteException
        if (fragment instanceof RideCompListener) mListener = (RideCompListener) fragment;
    }

    public void setBasicRideLayout(View view){

        View basic_ride_layout = view.findViewById(R.id.basic_ride_layout);
        mCancelButton = basic_ride_layout.findViewById(R.id.ride_cancel_button);
        mStartButton = basic_ride_layout.findViewById(R.id.ride_start_button);
        mEndButton = basic_ride_layout.findViewById(R.id.ride_end_button);
        mBasicRideButtonsLayout = basic_ride_layout.findViewById(R.id.ride_buttons_layout);

        //Reason for setting it visible to handle view holder reuse where once item is invisible, it remains as it is
        //e.g. when one item make something invisble but the same view is used by another item, then it remains invisble
        mCancelButton.setVisibility(View.VISIBLE);
        mStartButton.setVisibility(View.VISIBLE);
        mEndButton.setVisibility(View.VISIBLE);
        mBasicRideButtonsLayout.setVisibility(View.VISIBLE);

        TextView rideIdTextView = basic_ride_layout.findViewById(R.id.ride_id_text);
        String rideIdText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text) +mRide.getId();
        rideIdTextView.setText(rideIdText);
        TextView rideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        rideStatusTextView.setText(mRide.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(mCommonUtil.getFormattedDateTimeString(mRide.getStartTime()));
        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(mRide.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(mRide.getEndPointAddress());

        //This will set the visibility of ride buttons initially
        updateBasicRideLayoutButtonsVisibility();
        //This will set listeners for ride buttons
        setBasicRideLayoutButtonsOnClickListener();

        RideInfoFragment fragment = (RideInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideInfoFragment.TAG);
        if (fragment!=null && mRide.getId() == fragment.getRideId()){
            Log.d(TAG, "Ride Info is already loaded");
        } else {
            basic_ride_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String GET_RIDE_URL = APIUrl.GET_RIDE_URL.replace(APIUrl.ID_KEY,Integer.toString(mRide.getId()));
                    RESTClient.get(GET_RIDE_URL, null, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadRideInfoFragment(response.toString());
                        }

                    });
                }
            });
        }
    }

    private void setBasicRideLayoutButtonsOnClickListener() {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CANCEL_RIDE = APIUrl.CANCEL_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                RESTClient.get(CANCEL_RIDE, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "Ride Cancelled");
                        mRide = new Gson().fromJson(response.toString(), FullRide.class);
                        mListener.onRideRefresh(mRide);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                        Log.d(TAG, errorMessage.getErrorMessage());
                    }
                });
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String START_RIDE = APIUrl.START_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                RESTClient.get(START_RIDE, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "Ride Started");
                        mRide = new Gson().fromJson(response.toString(), FullRide.class);
                        mListener.onRideRefresh(mRide);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                        Log.d(TAG, errorMessage.getErrorMessage());
                    }
                });
            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String END_RIDE = APIUrl.END_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                RESTClient.get(END_RIDE, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "Ride Ended");
                        mRide = new Gson().fromJson(response.toString(), FullRide.class);
                        mListener.onRideRefresh(mRide);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                        Log.d(TAG, errorMessage.getErrorMessage());
                    }
                });
            }
        });
    }

    private void updateBasicRideLayoutButtonsVisibility(){
        if (mRide.getStatus().equals(RideStatus.Planned)){
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(mRide.getStartTime());
            //This will subtract some buffer time from start time e.g. reduce 15 mins from current time
            //(using -) in add function for substraction
            startTime.add(Calendar.MINUTE, -Constant.START_TIME_BUFFER);
            //This will make Start button invisible if its early than start time by subtracting buffer
            //e.g. if ride start time is 3:00 PM and buffer is 15 mins, then it can only be started after 2:45 PM
            Log.d(TAG, "Ride Start Time - Buffer:"+startTime.getTime().toString());
            Log.d(TAG, "Current Time:"+Calendar.getInstance().getTime().toString());
            if (Calendar.getInstance().getTime().before(startTime.getTime())){
                mStartButton.setVisibility(View.GONE);
            }
            //This will make Start and Cancel button invisible post end time of ride
            if (mRide.getEndTime().before(Calendar.getInstance().getTime())){
                mCancelButton.setVisibility(View.GONE);
                mStartButton.setVisibility(View.GONE);
            }
            //This will make end button invisible as ride is never started
            mEndButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Started)){
            mCancelButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Finished) || mRide.getStatus().equals(RideStatus.Cancelled)){
            mBasicRideButtonsLayout.setVisibility(View.GONE);
        }
    }

    public interface RideCompListener{
        public void onRideRefresh(FullRide ride);
    }
}























