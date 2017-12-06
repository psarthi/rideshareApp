package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequestStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRidePassenger;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;

import java.util.Date;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideRequestComp{

    public static final String TAG = RideRequestComp.class.getName();
    BaseFragment mBaseFragment;
    FullRideRequest mRideRequest;
    private CommonUtil mCommonUtil;

    public RideRequestComp(BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
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
}
































