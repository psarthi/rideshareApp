package com.parift.rideshare.component;

import android.graphics.ColorFilter;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.domain.TrustCategory;
import com.parift.rideshare.model.ride.domain.TrustCategoryName;
import com.parift.rideshare.model.ride.domain.TrustNetwork;

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
    private LinearLayout mAllLayout;
    private LinearLayout mGroupLayout;
    private boolean mIsUserGroupMember;
    private CommonUtil mCommonUtil;


    public TrustNetworkComp(BaseFragment fragment, TrustNetwork trustNetwork){
        mBaseFragment = fragment;
        mTrustNetwork = trustNetwork;
        //Initial value on home page. It would be only set once so that on fragment reload it would not get reset and maintain its previous state
        mAllSelected = true;
        mCommonUtil = new CommonUtil(fragment);
        mIsUserGroupMember = mCommonUtil.isUserGroupMember();
    }

    public void setTrustCategoryViews(View view) {
        mAllImageView = view.findViewById(R.id.trust_category_all_image);
        mAllTextView = view.findViewById(R.id.trust_category_all_text);
        mGroupsImageView = view.findViewById(R.id.trust_category_groups_image);
        mGroupsTextView = view.findViewById(R.id.trust_category_groups_text);
        mAllLayout = view.findViewById(R.id.trust_category_all_layout);
        mGroupLayout = view.findViewById(R.id.trust_category_groups_layout);

        mSelectedColor = ContextCompat.getColor(mBaseFragment.getActivity(), R.color.colorAccent);
        mDefaultTextColor = mAllTextView.getTextColors().getDefaultColor();
        mDefaultImageTint = mAllImageView.getColorFilter();
        Logger.debug(TAG,"Text Default color:"+ mDefaultTextColor+":Image Default Tint:"+mAllImageView.getColorFilter());

        setTrustCategoryOnClickListener(view);

        updateTrustCategoryItemsColor();
    }

    private void setTrustCategoryOnClickListener(View view) {
        mAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG,"Clicked on the All Image View: Selected Status:" + mAllSelected);
                if (!mAllSelected){
                    mAllSelected = true;
                    mGroupsSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
        mGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG,"Clicked on the Groups Image View: Selected Status:" + mGroupsSelected);
                if (!mGroupsSelected){
                    mGroupsSelected = true;
                    mAllSelected = false;
                }
                updateTrustCategoryItemsColor();
                //This will only show up if user is not member of any group
                if (!mIsUserGroupMember){
                    Toast.makeText(mBaseFragment.getActivity(), R.string.no_group_membership_msg, Toast.LENGTH_LONG).show();
                }
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
