package com.digitusrevolution.rideshare.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;

import java.util.List;

/**
 * Created by psarthi on 12/1/17.
 */

public class CoTravellerAdapter extends ArrayAdapter<BasicRideRequest>{

    private List<BasicRideRequest> mRideRequests;
    private FragmentActivity mContext;

    public CoTravellerAdapter(FragmentActivity context, List<BasicRideRequest> rideRequests){
        super(context,-1,rideRequests);
        mRideRequests = rideRequests;
        mContext = context;
    }

    private static class ViewHolder {
        TextView mTextView;
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
        CoTravellerAdapter.ViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new CoTravellerAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.ride_co_traveller_layout, parent, false);
            //text1 is the id of the TextView configured in spinner_item
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.co_travller_info_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CoTravellerAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.mTextView.setText(rideRequest.getPassenger().getFirstName());
        return convertView;
    }

    //Its important to note that in Spinner this function plays key role in getting the view when you click on drop down
    //If you don't have this and you have just set the setDropDownView on adapter using countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //Then adapter would use Object.toString method to get the view on dropdown. So its mandatory to override this whenever you have custom object which is not String
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position,convertView,parent);
    }


}
