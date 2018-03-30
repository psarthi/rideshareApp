package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.app.BasicInterestWrapper;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by psarthi on 3/27/18.
 */

public class InterestWrapperAdapter extends RecyclerView.Adapter<InterestWrapperAdapter.ViewHolder> {

    private static final String TAG = InterestWrapperAdapter.class.getName();
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private List<BasicInterestWrapper> mInterests;

    public InterestWrapperAdapter(List<BasicInterestWrapper> interests, BaseFragment fragment){
        mInterests = interests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public InterestWrapperAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectable_interest_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final InterestWrapperAdapter.ViewHolder holder, int position) {
        final BasicInterestWrapper interest = mInterests.get(position);
        holder.mTextView.setText(interest.getName());
        Picasso.with(mBaseFragment.getActivity()).load(interest.getPhoto().getImageLocation()).
                transform(new CropCircleTransformation()).into(holder.mImageView);

        if (interest.isSelected()){
            holder.mSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mSelected.setVisibility(View.INVISIBLE);
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interest.isSelected()){
                    holder.mSelected.setVisibility(View.INVISIBLE);
                    interest.setSelected(false);
                } else {
                    holder.mSelected.setVisibility(View.VISIBLE);
                    interest.setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        ImageView mImageView;
        ImageView mSelected;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.interest_name);
            mImageView = v.findViewById(R.id.interest_image);
            mSelected = v.findViewById(R.id.interest_selected);
        }
    }
}