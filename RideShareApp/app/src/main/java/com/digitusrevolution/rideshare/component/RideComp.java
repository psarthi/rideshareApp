package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;

import java.util.Calendar;
import java.util.Date;

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

    public RideComp(BaseFragment fragment, FullRide ride){
        mBaseFragment = fragment;
        mRide = ride;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setBasicRideLayout(View view, BasicRide ride){

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
        String rideIdText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text) +ride.getId();
        rideIdTextView.setText(rideIdText);
        TextView rideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        rideStatusTextView.setText(ride.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(mCommonUtil.getFormattedDateTimeString(ride.getStartTime()));
        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(ride.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(ride.getEndPointAddress());

        //This will set the visibility of ride buttons initially
        updateBasicRideLayoutButtonsVisibility(ride);
        //This will set listeners for ride buttons
        setBasicRideLayoutButtonsOnClickListener(ride);
    }

    private void setBasicRideLayoutButtonsOnClickListener(final BasicRide ride) {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Cancelled");
                updateBasicRideLayoutButtonsVisibility(ride);
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Started");
                updateBasicRideLayoutButtonsVisibility(ride);
            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Ended");
                updateBasicRideLayoutButtonsVisibility(ride);
            }
        });
    }

    private void updateBasicRideLayoutButtonsVisibility(BasicRide ride){
        if (ride.getStatus().equals(RideStatus.Planned)){
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(ride.getStartTime());
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
            if (ride.getEndTime().before(Calendar.getInstance().getTime())){
                mCancelButton.setVisibility(View.GONE);
                mStartButton.setVisibility(View.GONE);
            }
            //This will make end button invisible as ride is never started
            mEndButton.setVisibility(View.GONE);
        }
        if (ride.getStatus().equals(RideStatus.Started)){
            mCancelButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.GONE);
        }
        if (ride.getStatus().equals(RideStatus.Finished) || ride.getStatus().equals(RideStatus.Cancelled)){
            mBasicRideButtonsLayout.setVisibility(View.GONE);
        }
    }

    public void setPickupTimeAndBillLayout(View view, BasicRideRequest rideRequest){
        View layout = view.findViewById(R.id.pickup_time_bill_layout);
        TextView pickupTimeTextView = layout.findViewById(R.id.pickup_time_text);
        Date pickupTime = rideRequest.getRidePickupPoint().getRidePointProperties().get(0).getDateTime();
        String pickupTimeString = mCommonUtil.getFormattedDateTimeString(pickupTime);
        pickupTimeTextView.setText(pickupTimeString);

        TextView fareTextView = layout.findViewById(R.id.fare_text);
        //String amount = Float.toString(rideRequest.getBill().getAmount());
        //fareTextView.setText(amount);

        TextView billStatusTextView = layout.findViewById(R.id.bill_status);
        //billStatusTextView.setText(rideRequest.getBill().getStatus().toString());
    }

    public void setRidePickupDropPointsLayout(View view, BasicRideRequest rideRequest){
        View layout = view.findViewById(R.id.ride_pickup_drop_point_layout);
        TextView pickupPointTextView = layout.findViewById(R.id.ride_pickup_point_text);
        pickupPointTextView.setText(rideRequest.getRidePickupPointAddress());

        TextView dropPointTextView = layout.findViewById(R.id.ride_drop_point_text);
        dropPointTextView.setText(rideRequest.getRideDropPointAddress());

    }

    public void setCoTravellerButtonsOnClickListener(final View view, final BasicRideRequest rideRequest){
        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button rejectButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Cancelled - " + rideRequest.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, rideRequest);
            }
        });

        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Picked - " + rideRequest.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, rideRequest);
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Dropped - " + rideRequest.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, rideRequest);
            }
        });
    }

    public void updateCoTravellerButtonsVisibility(View view, final BasicRideRequest rideRequest){

        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button cancelButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);
        RatingBar ratingBar = view.findViewById(R.id.co_traveller_rating_bar);
        View buttonsLayout = view.findViewById(R.id.co_traveller_buttons_layout);

        //Intial value of rating bar
        ratingBar.setVisibility(View.GONE);

        Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(rideRequest);

        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){

            if (maxEndTime.before(Calendar.getInstance())){
                //This will make cancel button invisible post the ride request max end time has lapsed
                cancelButton.setVisibility(View.GONE);
            }
            pickupButton.setVisibility(View.GONE);
            dropButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Picked)){
            cancelButton.setVisibility(View.GONE);
            pickupButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Dropped)){
            buttonsLayout.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }
}























