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
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequestStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.auth.GoogleAuthException;

import java.util.Date;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideRequestComp{

    public static final String TAG = RideRequestComp.class.getName();
    BaseFragment mBaseFragment;
    private FullRideRequest mRideRequest;
    private CommonUtil mCommonUtil;

    private Button mCancelButton;
    private Button mDestinationNavigationButton;
    private LinearLayout mBasicRideRequestButtonsLayout;
    private Button mRejectButton;
    private Button mPickupPointNavigationButton;
    private LinearLayout mRideOwnerButtonsLayout;

    public RideRequestComp(BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setRideRequestBasicLayout(View view){
        View layout = view.findViewById(R.id.basic_ride_request_layout);
        String rideRequestNumberText = mBaseFragment.getResources().getString(R.string.ride_request_id_text)+ mRideRequest.getId();
        ((TextView) layout.findViewById(R.id.ride_request_id_text)).setText(rideRequestNumberText);
        ((TextView) layout.findViewById(R.id.ride_request_status_text)).setText(mRideRequest.getStatus().toString());
        String pickupTime = mCommonUtil.getFormattedDateTimeString(mRideRequest.getPickupTime());
        ((TextView) layout.findViewById(R.id.ride_request_pickup_time_text)).setText(pickupTime);
        //TODO get confirmation code post backend finalization
        //((TextView) layout.findViewById(R.id.ride_request_confirmation_code_text)).setText();

        ((TextView) layout.findViewById(R.id.ride_request_pickup_point_text)).setText(mRideRequest.getPickupPointAddress());
        ((TextView) layout.findViewById(R.id.ride_request_drop_point_text)).setText(mRideRequest.getDropPointAddress());

        mCancelButton = layout.findViewById(R.id.ride_request_cancel_button);
        mDestinationNavigationButton = layout.findViewById(R.id.ride_request_navigate_to_destination_button);
        mBasicRideRequestButtonsLayout = layout.findViewById(R.id.ride_request_buttons_layout);

        //This will setup initial button visibility
        updateRideRequestBasicLayoutButtonsVisiblity();
        setRideRequestBasicLayoutButtonsOnClickListener();
    }

    private void setRideRequestBasicLayoutButtonsOnClickListener(){
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Request Cancelled for id:"+mRideRequest.getId());
                updateRideRequestBasicLayoutButtonsVisiblity();
            }
        });
        mDestinationNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Destination for Id:"+mRideRequest.getId());
                updateRideRequestBasicLayoutButtonsVisiblity();
            }
        });
    }

    private void updateRideRequestBasicLayoutButtonsVisiblity(){
        if (mRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled)) {
            mDestinationNavigationButton.setVisibility(View.GONE);
        }
        if (mRideRequest.getStatus().equals(RideRequestStatus.Cancelled)){
            mBasicRideRequestButtonsLayout.setVisibility(View.GONE);
        }
        if (mRideRequest.getStatus().equals(RideRequestStatus.Fulfilled)){
            if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){
                mDestinationNavigationButton.setVisibility(View.GONE);
            } else {
                mCancelButton.setVisibility(View.GONE);
            }
        }
    }

    public void setRideOwnerLayout(View view){

        UserComp userComp = new UserComp(mBaseFragment, null);
        userComp.setUserProfileSingleRow(view, mRideRequest.getAcceptedRide().getDriver());

        RideComp rideComp = new RideComp(mBaseFragment, null);
        rideComp.setPickupTimeAndBillLayout(view, (BasicRideRequest) mRideRequest);

        ((TextView) view.findViewById(R.id.ride_pickup_point_text)).setText(mRideRequest.getRidePickupPointAddress());
        ((TextView) view.findViewById(R.id.ride_drop_point_text)).setText(mRideRequest.getRideDropPointAddress());

        String pickupDistance = Integer.toString((int)mRideRequest.getRidePickupPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        String dropDistance = Integer.toString((int)mRideRequest.getRideDropPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        ((TextView) view.findViewById(R.id.ride_pickup_point_variation_text)).setText(pickupDistance);
        ((TextView) view.findViewById(R.id.ride_drop_point_variation_text)).setText(dropDistance);

        mRideOwnerButtonsLayout = view.findViewById(R.id.ride_owner_buttons_layout);
        mRejectButton = view.findViewById(R.id.ride_owner_reject_button);
        mPickupPointNavigationButton = view.findViewById(R.id.navigate_to_ride_pickup_point_button);

        updateRideOwnerLayoutButtonsVisiblity(view);
        setRideOwnerLayoutButtonsOnClickListener(view);

    }

    private void setRideOwnerLayoutButtonsOnClickListener(final View view){

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Owner Rejected:"+mRideRequest.getAcceptedRide().getDriver().getFirstName());
                updateRideOwnerLayoutButtonsVisiblity(view);
            }
        });

        mPickupPointNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Pickup Point for Id:"+mRideRequest.getId());
                updateRideOwnerLayoutButtonsVisiblity(view);
            }
        });
    }

    private void updateRideOwnerLayoutButtonsVisiblity(View view){

        RatingBar ratingBar = view.findViewById(R.id.ride_owner_rating_bar);

        //This means that only when passenger is in confirmed state i.e. he/she has not been picked up, he can reject / navigate
        //As far as rating is concerned, he can rate post pickup up or if reject. In case of Reject, we will ask for rating in dialog bar itself
        if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){
            ratingBar.setVisibility(View.GONE);
        } else {
            mRideOwnerButtonsLayout.setVisibility(View.GONE);
        }

    }

}
































