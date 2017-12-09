package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.RideRequestComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;

import java.util.List;

/**
 * Created by psarthi on 12/9/17.
 */

public class RideRequestListAdapter extends RecyclerView.Adapter<RideRequestListAdapter.ViewHolder> {

    private List<FullRideRequest> mRideRequests;
    private BaseFragment mBaseFragment;

    public RideRequestListAdapter(List<FullRideRequest> rideRequests, BaseFragment fragment) {
        mRideRequests = rideRequests;
        mBaseFragment = fragment;
    }

    @Override
    public RideRequestListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_ride_request_layout, parent, false);
        RideRequestListAdapter.ViewHolder vh = new RideRequestListAdapter.ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RideRequestListAdapter.ViewHolder holder, int position) {
        RideRequestComp rideRequestComp = new RideRequestComp(mBaseFragment, null);
        rideRequestComp.setRideRequestBasicLayout(holder.itemView,mRideRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return mRideRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
