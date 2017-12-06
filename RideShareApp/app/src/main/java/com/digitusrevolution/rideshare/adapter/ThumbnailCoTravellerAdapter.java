package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by psarthi on 12/3/17.
 */

public class ThumbnailCoTravellerAdapter extends RecyclerView.Adapter<ThumbnailCoTravellerAdapter.ViewHolder> {

    public static final String TAG = ThumbnailCoTravellerAdapter.class.getName();
    private List<BasicRideRequest> mRideRequests;
    private FragmentActivity mContext;
    private ThumbnailCoTravellerAdapterListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.user_name_text);
            mImageView = v.findViewById(R.id.user_profile_image);
        }
    }

    public ThumbnailCoTravellerAdapter(FragmentActivity context, ThumbnailCoTravellerAdapterListener listener,
                                       List<BasicRideRequest> rideRequests) {
        mRideRequests = rideRequests;
        mContext = context;
        mListener = listener;
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
    public void onBindViewHolder(ThumbnailCoTravellerAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "Text Name:"+mRideRequests.get(position).getPassenger().getFirstName());
        holder.mTextView.setText(mRideRequests.get(position).getPassenger().getFirstName());
        Picasso.with(mContext).load(mRideRequests.get(position).getPassenger().getPhoto().getImageLocation()).into(holder.mImageView);


        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickOfThumbnailCoTravellerAdapter(mRideRequests.get(position).getPassenger());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRideRequests.size();
    }

    public interface ThumbnailCoTravellerAdapterListener{
        public void onClickOfThumbnailCoTravellerAdapter(BasicUser user);
    }

}
