package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.component.RideRequestComp;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.dto.BasicUser;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/9/17.
 */

public class RideRequestListAdapter extends RecyclerView.Adapter<RideRequestListAdapter.ViewHolder>
implements RideRequestComp.RideRequestCompListener{

    private static final String TAG = RideRequestListAdapter.class.getName();
    private List<BasicRideRequest> mRideRequests;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;

    public RideRequestListAdapter(List<BasicRideRequest> rideRequests, BaseFragment fragment) {
        mRideRequests = rideRequests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
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
        RideRequestComp rideRequestComp = new RideRequestComp(this,mBaseFragment, mRideRequests.get(position));
        rideRequestComp.setRideRequestBasicLayout(holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rideRequestId = Long.toString(mRideRequests.get(position).getId());
                String GET_RIDE_REQUEST_URL = APIUrl.GET_RIDE_REQUEST_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.ID_KEY,rideRequestId);
                mCommonUtil.showProgressDialog();
                RESTClient.get(GET_RIDE_REQUEST_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadRideRequestInfoFragment(response.toString());
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return mRideRequests.size();
    }

    @Override
    public void onRideRequestRefresh(FullRideRequest rideRequest) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Request Id with status:"
                +rideRequest.getId()+":"+rideRequest.getStatus());
        int i = 0 ;
        for (BasicRideRequest basicRideRequest: mRideRequests){
            if (basicRideRequest.getId() == rideRequest.getId()){
                mRideRequests.set(i,rideRequest);
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
