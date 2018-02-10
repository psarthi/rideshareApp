package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.component.RideComp;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.RidesListFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.common.ErrorMessage;
import com.parift.rideshare.model.ride.dto.BasicRide;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/9/17.
 */

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.ViewHolder>
        implements RideComp.RideCompListener{

    private static final String TAG = RideListAdapter.class.getName();
    private List<BasicRide> mRides;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;

    public RideListAdapter(List<BasicRide> rides, BaseFragment fragment) {
        mRides = rides;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
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
        RideComp rideComp = new RideComp(this, mBaseFragment, mRides.get(position));
        rideComp.setBasicRideLayout(holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rideId = Long.toString(mRides.get(position).getId());
                String GET_RIDE_URL = APIUrl.GET_RIDE_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.ID_KEY,rideId);
                mCommonUtil.showProgressDialog();
                RESTClient.get(GET_RIDE_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
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

    @Override
    public void onRideRefresh(FullRide ride) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Id with status:"
                +ride.getId()+":"+ride.getStatus());

        int i = 0 ;
        for (BasicRide basicRide: mRides){
            if (basicRide.getId() == ride.getId()){
                mRides.set(i,ride);
                //Somehow its not working, so using notifydatasetChanged
                //notifyItemChanged(i + 1);
                notifyDataSetChanged();
                Logger.debug(TAG, "Item matched at position:"+i);
                break;
            }
            i++;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
