package com.digitusrevolution.rideshare.component;

import android.graphics.ColorFilter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.model.ride.domain.TrustCategory;
import com.digitusrevolution.rideshare.model.ride.domain.TrustCategoryName;
import com.digitusrevolution.rideshare.model.ride.domain.TrustNetwork;

/**
 * Created by psarthi on 12/6/17.
 */

public class TrustNetworkComp {

    public static final String TAG = TrustNetworkComp.class.getName();
    BaseFragment mBaseFragment;
    TrustNetwork mTrustNetwork;

    private boolean mAllSelected;
    private boolean mGroupsSelected;
    private ImageView mAllImageView;
    private ImageView mGroupsImageView;
    private TextView mAllTextView;
    private TextView mGroupsTextView;
    private int mSelectedColor;
    private int mDefaultTextColor;
    private ColorFilter mDefaultImageTint;


    public TrustNetworkComp(BaseFragment fragment, TrustNetwork trustNetwork){
        mBaseFragment = fragment;
        mTrustNetwork = trustNetwork;
        //Initial value on home page. It would be only set once so that on fragment reload it would not get reset and maintain its previous state
        mAllSelected = true;
    }

    public void setTrustCategoryViews(View view) {
        mAllImageView = view.findViewById(R.id.trust_category_all_image);
        mAllTextView = view.findViewById(R.id.trust_category_all_text);
        mGroupsImageView = view.findViewById(R.id.trust_category_groups_image);
        mGroupsTextView = view.findViewById(R.id.trust_category_groups_text);

        mSelectedColor = ContextCompat.getColor(mBaseFragment.getActivity(), R.color.colorAccent);
        mDefaultTextColor = mAllTextView.getTextColors().getDefaultColor();
        mDefaultImageTint = mAllImageView.getColorFilter();
        Log.d(TAG,"Text Default color:"+ mDefaultTextColor+":Image Default Tint:"+mAllImageView.getColorFilter());

        setTrustCategoryOnClickListener(view);

        updateTrustCategoryItemsColor();
    }

    private void setTrustCategoryOnClickListener(View view) {
        mAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked on the All Image View: Selected Status:" + mAllSelected);
                if (!mAllSelected){
                    mAllSelected = true;
                    mGroupsSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
        mGroupsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked on the Groups Image View: Selected Status:" + mGroupsSelected);
                if (!mGroupsSelected){
                    mGroupsSelected = true;
                    mAllSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
    }

    private void updateTrustCategoryItemsColor(){

        if (mAllSelected){
            mAllImageView.setColorFilter(mSelectedColor);
            mAllTextView.setTextColor(mSelectedColor);
        } else {
            mAllImageView.setColorFilter(mDefaultImageTint);
            mAllTextView.setTextColor(mDefaultTextColor);

        }
        if (mGroupsSelected){
            mGroupsImageView.setColorFilter(mSelectedColor);
            mGroupsTextView.setTextColor(mSelectedColor);
        } else {
            mGroupsImageView.setColorFilter(mDefaultImageTint);
            mGroupsTextView.setTextColor(mDefaultTextColor);
        }
    }

    public TrustNetwork getTrustNetworkFromView() {
        TrustNetwork trustNetwork = new TrustNetwork();
        if (mAllSelected) {
            TrustCategory trustCategory = new TrustCategory();
            trustCategory.setName(TrustCategoryName.Anonymous);
            trustNetwork.getTrustCategories().add(trustCategory);
        }
        if (mGroupsSelected) {
            TrustCategory trustCategory = new TrustCategory();
            trustCategory.setName(TrustCategoryName.Groups);
            trustNetwork.getTrustCategories().add(trustCategory);
        }
        return trustNetwork;
    }


}
