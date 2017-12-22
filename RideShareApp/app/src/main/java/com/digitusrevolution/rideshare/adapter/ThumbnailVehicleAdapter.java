package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by psarthi on 12/3/17.
 */

public class ThumbnailVehicleAdapter extends RecyclerView.Adapter<ThumbnailVehicleAdapter.ViewHolder> {

    public static final String TAG = ThumbnailVehicleAdapter.class.getName();
    private List<Vehicle> mVehicles;
    private BaseFragment mBaseFragment;
    private ViewHolder mLastView;
    private Vehicle mSelectedVehicle;
    private ThumbnailVehicleAdapterListener mListener;

    private int mDefaultVehicleId;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public int mDefaultTextColor;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.thumbnail_name_text);
            mImageView = v.findViewById(R.id.thumbnail_image);
            mDefaultTextColor = mTextView.getTextColors().getDefaultColor();
        }
    }

    public ThumbnailVehicleAdapter(BaseFragment fragment, List<Vehicle> vehicles, int defaultVehicleId) {
        mVehicles = vehicles;
        mBaseFragment = fragment;
        mDefaultVehicleId = defaultVehicleId;
        mListener = (ThumbnailVehicleAdapterListener) fragment;
    }

    @Override
    public ThumbnailVehicleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ThumbnailVehicleAdapter.ViewHolder holder, final int position) {
        final Vehicle vehicle = getItem(position);

        if (vehicle.getId()!=0){
            holder.mTextView.setText(vehicle.getModel());
            if (vehicle.getVehicleCategory().getName().equals("Car")){
                Picasso.with(mBaseFragment.getActivity()).load(R.drawable.ic_car).into(holder.mImageView);
            }
            if (vehicle.getId()==mDefaultVehicleId){
                //Setting default vehicle as selected
                select(holder, position);
            }
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Vehicle Clicked:"+vehicle.getModel());
                    if (mLastView != null) deselect(mLastView);
                    select(holder, position);
                    mListener.onVehicleClicked(vehicle);
                }
            });
        }
        //This will take care of adding drawable.add thumbnail at the end
        else {
            Picasso.with(mBaseFragment.getActivity()).load(R.drawable.ic_add).into(holder.mImageView);
            holder.mTextView.setText(mBaseFragment.getResources().getString(R.string.add_vehicle_thumbnail_text));
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Add Vehicle Clicked");
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadAddVehicleFragment(RideType.OfferRide, null);
                }
            });
        }

    }

    private Vehicle getItem(int position){
        return mVehicles.get(position);
    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    public Vehicle getSelectedVehicle() {
        return mSelectedVehicle;
    }

    private void select(ThumbnailVehicleAdapter.ViewHolder holder, int position){
        holder.mTextView.setTextColor(mBaseFragment.getResources().getColor(R.color.colorAccent));
        //Reason for setting it here instead of onClick in Bind,
        //so that this function can be used to set default vehicle on load as well without click
        mLastView = holder;
        mSelectedVehicle = getItem(position);
    }

    private void deselect(ThumbnailVehicleAdapter.ViewHolder holder){
        holder.mTextView.setTextColor(holder.mDefaultTextColor);
    }

    public void setDefaultVehicleId(int defaultVehicleId) {
        mDefaultVehicleId = defaultVehicleId;
    }

    public interface ThumbnailVehicleAdapterListener{
        public void onVehicleClicked(Vehicle vehicle);
    }

}
