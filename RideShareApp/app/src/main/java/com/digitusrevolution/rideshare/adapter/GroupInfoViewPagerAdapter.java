package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.AboutGroupFragment;
import com.digitusrevolution.rideshare.fragment.GroupMemberListFragment;
import com.digitusrevolution.rideshare.fragment.GroupMembershipRequestListFragment;
import com.digitusrevolution.rideshare.helper.Logger;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

/**
 * Created by psarthi on 1/10/18.
 */

public class GroupInfoViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = GroupInfoViewPagerAdapter.class.getName();
    private int mPageCount;
    private GroupDetail mGroupDetail;

    public GroupInfoViewPagerAdapter(FragmentManager fm, int pageCount, GroupDetail groupDetail) {
        super(fm);
        mPageCount = pageCount;
        mGroupDetail = groupDetail;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        String mGroupDetailJson = new Gson().toJson(mGroupDetail);
        if (position==0){
            Logger.debug(TAG, "Creating new instance of AboutGroupFragment");
            fragment = AboutGroupFragment.newInstance(mGroupDetailJson);
            Logger.debug(TAG, "Tag of AboutGroupFragment:"+fragment.getTag());
        }
        if (position==1){
            Logger.debug(TAG, "Creating new instance of GroupMemberListFragment");
            fragment = GroupMemberListFragment.newInstance(mGroupDetailJson);
        }
        if (position == 2){
            Logger.debug(TAG, "Creating new instance of GroupMembershipRequestListFragment");
            fragment = GroupMembershipRequestListFragment.newInstance(mGroupDetailJson);
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
                return "About Us";
            case 1:
                return "Members";
            case 2:
                return "Requests";
            default:
                return "About Us";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
