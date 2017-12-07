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
    private FullRideRequest mRideRequest;
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

    public void setCoTravellerButtonsOnClickListener(final View view, final BasicRideRequest rideRequest){
        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button rejectButton = view.findViewById(R.id.co_traveller_reject_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Rejected - " + rideRequest.getPassenger().getFirstName());
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
        Button rejectButton = view.findViewById(R.id.co_traveller_reject_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);
        RatingBar ratingBar = view.findViewById(R.id.co_traveller_rating_bar);
        View buttonsLayout = view.findViewById(R.id.co_traveller_buttons_layout);

        //Intial value of rating bar
        ratingBar.setVisibility(View.GONE);

        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){
            pickupButton.setVisibility(View.GONE);
            dropButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Picked)){
            rejectButton.setVisibility(View.GONE);
            pickupButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Dropped)){
            buttonsLayout.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }
}
































