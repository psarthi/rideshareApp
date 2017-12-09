package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;

import java.util.List;

/**
 * Created by psarthi on 12/9/17.
 */

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.ViewHolder> {

    private List<BasicRide> mRides;
    private BaseFragment mBaseFragment;

    public RideListAdapter(List<BasicRide> rides, BaseFragment fragment) {
        mRides = rides;
        mBaseFragment = fragment;
    }

    @Override
    public RideListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_ride_layout, parent, false);
        RideListAdapter.ViewHolder vh = new RideListAdapter.ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(RideListAdapter.ViewHolder holder, int position) {
        RideComp rideComp = new RideComp(mBaseFragment, null);
        rideComp.setBasicRideLayout(holder.itemView,mRides.get(position));
    }

    @Override
    public int getItemCount() {
        return mRides.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
