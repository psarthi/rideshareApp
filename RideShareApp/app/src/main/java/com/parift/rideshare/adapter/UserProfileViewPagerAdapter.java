package com.parift.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.parift.rideshare.fragment.AboutGroupFragment;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.CommonGroupListFragment;
import com.parift.rideshare.fragment.CommonInterestListFragment;
import com.parift.rideshare.fragment.GroupListFragment;
import com.parift.rideshare.fragment.GroupMemberListFragment;
import com.parift.rideshare.fragment.GroupMembershipRequestListFragment;
import com.parift.rideshare.fragment.InterestFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.parift.rideshare.model.user.dto.UserProfile;
import com.parift.rideshare.test.Interest;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by psarthi on 1/10/18.
 */

public class UserProfileViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = UserProfileViewPagerAdapter.class.getName();
    private int mPageCount;
    private UserProfile mUserProfile;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private boolean mLoggedInUser;

    public UserProfileViewPagerAdapter(BaseFragment fragment, FragmentManager fm, int pageCount, UserProfile userProfile) {
        super(fm);
        mPageCount = pageCount;
        mUserProfile = userProfile;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
        if (mUser.getId() == mUserProfile.getUser().getId()){
            mLoggedInUser = true;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Logger.debug(TAG, "Creating new instance of Common Interest List");
            fragment = CommonInterestListFragment.newInstance(new Gson().toJson(mUserProfile.getCommonInterests()));
        }
        if (position==1){
            Logger.debug(TAG, "Creating new instance of Common Group List");
            fragment = CommonGroupListFragment.newInstance(new Gson().toJson(mUserProfile.getCommonGroups()));
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mPageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                if (mLoggedInUser) return "My Interest";
                return "Common Interest";
            case 1:
                if (mLoggedInUser) return "My Groups";
                return "Common Groups";
            default:
                if (mLoggedInUser) return "My Interest";
                return "Common Interest";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
