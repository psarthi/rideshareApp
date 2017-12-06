package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;

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
    LinearLayout mButtonsLayout;
    private CommonUtil mCommonUtil;

    public RideComp(BaseFragment fragment, FullRide ride){
        mBaseFragment = fragment;
        mRide = ride;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setBasicRideLayout(View view){

        View basic_ride_layout = view.findViewById(R.id.basic_ride_layout);
        TextView rideIdTextView = basic_ride_layout.findViewById(R.id.ride_id_text);
        String rideIdText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text) + " " +mRide.getId();
        rideIdTextView.setText(rideIdText);
        TextView rideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        rideStatusTextView.setText(mRide.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(mCommonUtil.getFormattedDateTimeString(mRide.getStartTime()));
        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(mRide.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(mRide.getEndPointAddress());

        mCancelButton = basic_ride_layout.findViewById(R.id.ride_cancel_button);
        mStartButton = basic_ride_layout.findViewById(R.id.ride_start_button);
        mEndButton = basic_ride_layout.findViewById(R.id.ride_end_button);
        mButtonsLayout = basic_ride_layout.findViewById(R.id.ride_buttons_layout);

        //This will set the visibility of ride buttons
        updateBasicRideLayoutButtonsVisibility();
        //This will set listeners for ride buttons
        setBasicRideLayoutButtonsOnClickListener();
    }

    private void setBasicRideLayoutButtonsOnClickListener() {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Cancelled");
                updateBasicRideLayoutButtonsVisibility();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Started");
                updateBasicRideLayoutButtonsVisibility();
            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Ended");
                updateBasicRideLayoutButtonsVisibility();
            }
        });
    }

    private void updateBasicRideLayoutButtonsVisibility(){
        if (mRide.getStatus().equals(RideStatus.Planned)){
            mEndButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Started)){
            mCancelButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Finished) || mRide.getStatus().equals(RideStatus.Cancelled)){
            mButtonsLayout.setVisibility(View.GONE);
        }
    }
}
