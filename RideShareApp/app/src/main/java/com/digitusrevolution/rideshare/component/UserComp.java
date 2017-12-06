package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRidePassenger;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

/**
 * Created by psarthi on 12/6/17.
 */

public class UserComp {

    public static final String TAG = UserComp.class.getName();
    BaseFragment mBaseFragment;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;

    public UserComp(BaseFragment fragment, BasicUser user){
        mBaseFragment = fragment;
        mUser = user;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setUserProfileSingleRow(View view){

        View user_profile_layout = view.findViewById(R.id.user_profile_single_row_layout);
        ImageView userProfileImageView = user_profile_layout.findViewById(R.id.user_profile_image);
        Picasso.with(mBaseFragment.getActivity()).load(mUser.getPhoto().getImageLocation()).into(userProfileImageView);
        TextView userNameTextView = user_profile_layout.findViewById(R.id.user_name_text);
        String userName = mUser.getFirstName() + " " + mUser.getLastName();
        userNameTextView.setText(userName);
        TextView userRatingTextView = user_profile_layout.findViewById(R.id.user_rating_text);
        String profileRating = Float.toString(mUser.getProfileRating());
        userRatingTextView.setText(profileRating);

        ImageView mobileImageView = user_profile_layout.findViewById(R.id.user_mobile_image);
        mobileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Needs to be replaced with proper logic of calling user
                Log.d(TAG, "Calling User Mobile - "+mUser.getCountry().getCode() + mUser.getMobileNumber());
            }
        });

        //Making it invisible for the time being as it will unnecessarily makes another call
        //to backend to check user relationship with signed in user
        user_profile_layout.findViewById(R.id.add_friend_image).setVisibility(View.GONE);
        user_profile_layout.findViewById(R.id.friend_image).setVisibility(View.GONE);
    }

    public void setCoTravellerButtonsOnClickListener(final View view, final BasicRidePassenger ridePassenger){
        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button rejectButton = view.findViewById(R.id.co_traveller_reject_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Rejected - " + ridePassenger.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, ridePassenger);
            }
        });

        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Picked - " + ridePassenger.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, ridePassenger);
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Passenger Dropped - " + ridePassenger.getPassenger().getFirstName());
                updateCoTravellerButtonsVisibility(view, ridePassenger);
            }
        });
    }

    public void updateCoTravellerButtonsVisibility(View view, BasicRidePassenger ridePassenger){

        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button rejectButton = view.findViewById(R.id.co_traveller_reject_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);
        RatingBar ratingBar = view.findViewById(R.id.co_traveller_rating_bar);
        View buttonsLayout = view.findViewById(R.id.co_traveller_buttons_layout);

        //Intial value of rating bar
        ratingBar.setVisibility(View.GONE);

        if (ridePassenger.getStatus().equals(PassengerStatus.Confirmed)){
            pickupButton.setVisibility(View.GONE);
            dropButton.setVisibility(View.GONE);
        }
        if (ridePassenger.getStatus().equals(PassengerStatus.Picked)){
            rejectButton.setVisibility(View.GONE);
            pickupButton.setVisibility(View.GONE);
        }
        if (ridePassenger.getStatus().equals(PassengerStatus.Dropped)){
            buttonsLayout.setVisibility(View.GONE);
            ratingBar.setVisibility(View.VISIBLE);
        }
    }
}
