package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.domain.core.Vehicle;
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

    public ThumbnailVehicleAdapter(BaseFragment fragment, List<Vehicle> vehicles, Vehicle defaultVehicle) {
        mVehicles = vehicles;
        mBaseFragment = fragment;
        mSelectedVehicle = defaultVehicle;
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
        //This will ensure that on view holder reuse we don't get selected text color of earlier viewholder
        //otherwise you would see multiple vehicle selected
        holder.mTextView.setTextColor(holder.mDefaultTextColor);
        if (vehicle.getId()!=0){
            holder.mTextView.setText(vehicle.getModel());
            if (vehicle.getVehicleCategory().getName().equals("Car")){
                Picasso.with(mBaseFragment.getActivity()).load(R.drawable.ic_car).into(holder.mImageView);
            }
            if (vehicle.getId()==mSelectedVehicle.getId()){
                //Setting default vehicle as selected
                select(holder, position);
            }
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.debug(TAG, "Vehicle Clicked:"+vehicle.getModel());
                    if (mLastView != null) deselect(mLastView);
                    select(holder, position);
                    mListener.onVehicleClicked(vehicle);
                }
            });
        }
        //This will take care of adding drawable.add thumbnail at the end
        else {
            Picasso.with(mBaseFragment.getActivity()).load(R.drawable.ic_add).into(holder.mImageView);
            if (mBaseFragment.isAdded()){
                holder.mTextView.setText(mBaseFragment.getResources().getString(R.string.add_vehicle_thumbnail_text));
                //This will ensure to highlight in case of no vehicles are there. Reason for 1 as there is a dummy vehicle added for add vehicle
                if (mVehicles.size() == 1){
                    holder.mTextView.setTextColor(mBaseFragment.getResources().getColor(R.color.colorAccent));
                }
            }
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.debug(TAG, "Add Vehicle Clicked");
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadAddVehicleFragment(this.getClass().getName());
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
        if (mBaseFragment.isAdded()){
            holder.mTextView.setTextColor(mBaseFragment.getResources().getColor(R.color.colorAccent));
        }
        //Reason for setting it here instead of onClick in Bind,
        //so that this function can be used to set default vehicle on load as well without click
        //IMP - Even though the holder is reusable, this will take care of the visible holder selection/deselection
        //if the new item is getting renedered then by default based on the selected vehicle match, it will get highlighted
        //That's the reason we are storing the lastview as holder
        mLastView = holder;
        mSelectedVehicle = getItem(position);
    }

    private void deselect(ThumbnailVehicleAdapter.ViewHolder holder){
        holder.mTextView.setTextColor(holder.mDefaultTextColor);
    }

    public interface ThumbnailVehicleAdapterListener{
        public void onVehicleClicked(Vehicle vehicle);
    }

}
