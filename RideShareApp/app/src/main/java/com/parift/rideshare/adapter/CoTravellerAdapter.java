package com.parift.rideshare.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import com.parift.rideshare.R;
import com.parift.rideshare.component.RideComp;
import com.parift.rideshare.component.RideRequestComp;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.domain.UserFeedback;

import java.util.List;

/**
 * Created by psarthi on 12/1/17.
 */

public class CoTravellerAdapter extends ArrayAdapter<FullRideRequest>{

    public static final String TAG = CoTravellerAdapter.class.getName();
    private List<FullRideRequest> mRideRequests;
    private BaseFragment mBaseFragment;
    private FullRide mRide;

    public CoTravellerAdapter(BaseFragment fragment, List<FullRideRequest> rideRequests, FullRide ride){
        super(fragment.getActivity(),-1,rideRequests);
        mRideRequests = rideRequests;
        mBaseFragment = fragment;
        mRide = ride;
    }

    @Override
    public int getCount() {
        return mRideRequests.size();
    }

    @Nullable
    @Override
    public FullRideRequest getItem(int position) {
        return mRideRequests.get(position);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView!=null) Logger.debug(TAG,"ConvertView Instance:"+convertView.hashCode());
        final FullRideRequest rideRequest= getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mBaseFragment.getActivity());
        convertView = inflater.inflate(R.layout.ride_co_traveller_layout, parent, false);

        UserComp userComp = new UserComp(mBaseFragment, rideRequest.getPassenger());
        userComp.setUserProfileSingleRow(convertView, true);

        RideRequestComp rideRequestComp = new RideRequestComp(mBaseFragment, rideRequest);
        rideRequestComp.setPickupTimeAndBillLayout(convertView);
        rideRequestComp.setRidePickupDropPointsLayout(convertView);

        RatingBar coTravellerRatingBar = convertView.findViewById(R.id.co_traveller_rating_bar);
        Logger.debug(TAG,"RatingBar Instance:"+coTravellerRatingBar.hashCode());
        Logger.debug(TAG, "Initial Rating-"+coTravellerRatingBar.getRating());
        boolean feedbackAvailable = false;
        //This will show the user given rating
        for (UserFeedback feedback: rideRequest.getFeedbacks()) {
            if (feedback.getForUser().getId() == rideRequest.getPassenger().getId() && feedback.getRideRequest().getId() == rideRequest.getId()) {
                Logger.debug(TAG,"Setting Rating:"+feedback.getRating());
                coTravellerRatingBar.setRating(feedback.getRating());
                coTravellerRatingBar.setEnabled(false);
                feedbackAvailable = true;
            }
        }

        RideComp rideComp = new RideComp(mBaseFragment, rideRequest.getAcceptedRide());
        Logger.debug(TAG, "Actual Rating-"+coTravellerRatingBar.getRating());
        if (!feedbackAvailable){
            rideComp.setCoTravellerRatingBar(coTravellerRatingBar, rideRequest);
        }

        //This will set the visibility of co traveller buttons initially
        rideComp.updateCoTravellerButtonsVisibility(convertView, rideRequest);
        //This will set the listeners on co traveller buttons
        rideComp.setCoTravellerButtonsOnClickListener(convertView, rideRequest);

        Logger.debug(TAG, "Final Rating before exiting-"+coTravellerRatingBar.getRating());
        return convertView;
    }

}
