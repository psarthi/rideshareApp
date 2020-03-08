package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.user.dto.BasicInterest;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by psarthi on 3/27/18.
 */

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private static final String TAG = InterestAdapter.class.getName();
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private List<BasicInterest> mInterests;

    public InterestAdapter(List<BasicInterest> interests, BaseFragment fragment){
        mInterests = interests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public InterestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final InterestAdapter.ViewHolder holder, int position) {
        final BasicInterest interest = mInterests.get(position);
        holder.mTextView.setText(interest.getName());
        Picasso.with(mBaseFragment.getActivity()).load(interest.getPhoto().getImageLocation()).
                transform(new CropCircleTransformation()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mInterests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.interest_name);
            mImageView = v.findViewById(R.id.interest_image);
        }
    }
}
