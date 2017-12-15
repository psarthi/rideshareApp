package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/9/17.
 */

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.ViewHolder> {

    private List<FullRide> mRides;
    private BaseFragment mBaseFragment;

    public RideListAdapter(List<FullRide> rides, BaseFragment fragment) {
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
    public void onBindViewHolder(RideListAdapter.ViewHolder holder, final int position) {
        RideComp rideComp = new RideComp(mBaseFragment, mRides.get(position));
        rideComp.setBasicRideLayout(holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rideId = Integer.toString(mRides.get(position).getId());
                String GET_RIDE_URL = APIUrl.GET_RIDE_URL.replace(APIUrl.ID_KEY,rideId);

                RESTClient.get(GET_RIDE_URL, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                        fragmentLoader.loadRideInfoFragment(response.toString());
                    }

                });
            }
        });
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
