package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.RideRequestComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

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
    public void onBindViewHolder(RideRequestListAdapter.ViewHolder holder, final int position) {
        RideRequestComp rideRequestComp = new RideRequestComp(mBaseFragment, mRideRequests.get(position));
        rideRequestComp.setRideRequestBasicLayout(holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rideRequestId = Integer.toString(mRideRequests.get(position).getId());
                String GET_RIDE_REQUEST_URL = APIUrl.GET_RIDE_REQUEST_URL.replace(APIUrl.ID_KEY,rideRequestId);

                RESTClient.get(GET_RIDE_REQUEST_URL, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                        fragmentLoader.loadRideRequestInfoFragment(response.toString());
                    }
                });

            }
        });
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
