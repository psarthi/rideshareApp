package com.digitusrevolution.rideshare.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRidePassenger;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;

import java.util.List;

/**
 * Created by psarthi on 12/1/17.
 */

public class CoTravellerAdapter extends ArrayAdapter<BasicRideRequest>{

    public static final String TAG = CoTravellerAdapter.class.getName();
    private List<BasicRideRequest> mRideRequests;
    private BaseFragment mBaseFragment;
    private FullRide mRide;

    public CoTravellerAdapter(BaseFragment fragment, List<BasicRideRequest> rideRequests, FullRide ride){
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
    public BasicRideRequest getItem(int position) {
        return mRideRequests.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        BasicRideRequest rideRequest= getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mBaseFragment.getActivity());
        convertView = inflater.inflate(R.layout.ride_co_traveller_layout, parent, false);

        UserComp userComp = new UserComp(mBaseFragment, null);
        userComp.setUserProfileSingleRow(convertView, rideRequest.getPassenger());

        RideRequestComp rideRequestComp = new RideRequestComp(mBaseFragment, null);
        rideRequestComp.setPickupTimeAndBillLayout(convertView, rideRequest);
        rideRequestComp.setRidePickupDropPointsLayout(convertView, rideRequest);

        RideComp rideComp = new RideComp(mBaseFragment, mRide);
        //This will set the visibility of co traveller buttons initially
        rideComp.updateCoTravellerButtonsVisibility(convertView, rideRequest);
        //This will set the listeners on co traveller buttons
        rideComp.setCoTravellerButtonsOnClickListener(convertView, rideRequest);


        RatingBar ratingBar = convertView.findViewById(R.id.co_traveller_rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG,"Rating is:"+rating);
            }
        });

        return convertView;
    }
}
