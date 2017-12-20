package com.digitusrevolution.rideshare.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.component.RideRequestComp;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.domain.UserFeedback;

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

        final FullRideRequest rideRequest= getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mBaseFragment.getActivity());
        convertView = inflater.inflate(R.layout.ride_co_traveller_layout, parent, false);

        UserComp userComp = new UserComp(mBaseFragment, null);
        userComp.setUserProfileSingleRow(convertView, rideRequest.getPassenger());

        RideRequestComp rideRequestComp = new RideRequestComp(mBaseFragment, rideRequest);
        rideRequestComp.setPickupTimeAndBillLayout(convertView, RideType.OfferRide);
        rideRequestComp.setRidePickupDropPointsLayout(convertView);

        RatingBar coTravellerRatingBar = convertView.findViewById(R.id.co_traveller_rating_bar);
        rideRequestComp.setRatingBar(coTravellerRatingBar);

        //This will show the user given rating
        for (UserFeedback feedback: rideRequest.getFeedbacks()) {
            if (feedback.getForUser().getId() == rideRequest.getPassenger().getId()) {
                coTravellerRatingBar.setRating(feedback.getRating());
                coTravellerRatingBar.setEnabled(false);
            }
        }

        RideComp rideComp = new RideComp(mBaseFragment, rideRequest.getAcceptedRide());
        //This will set the visibility of co traveller buttons initially
        rideComp.updateCoTravellerButtonsVisibility(convertView, rideRequest);
        //This will set the listeners on co traveller buttons
        rideComp.setCoTravellerButtonsOnClickListener(convertView, rideRequest);

        return convertView;
    }
}
