package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by psarthi on 12/3/17.
 */

public class ThumbnailCoTravellerAdapter extends RecyclerView.Adapter<ThumbnailCoTravellerAdapter.ViewHolder> {

    private List<BasicRideRequest> mRideRequests;
    private FragmentActivity mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.thumbnail_name_text);
            mImageView = v.findViewById(R.id.thumbnail_image);
        }
    }

    public ThumbnailCoTravellerAdapter(FragmentActivity context, List<BasicRideRequest> rideRequests) {
        mRideRequests = rideRequests;
        mContext = context;
    }

    @Override
    public ThumbnailCoTravellerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ThumbnailCoTravellerAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mRideRequests.get(position).getPassenger().getFirstName());
        Picasso.with(mContext).load(mRideRequests.get(position).getPassenger().getPhoto().getImageLocation()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mRideRequests.size();
    }
}
