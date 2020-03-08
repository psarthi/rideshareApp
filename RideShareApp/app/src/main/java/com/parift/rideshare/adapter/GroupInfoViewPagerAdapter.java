package com.parift.rideshare.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.parift.rideshare.fragment.AboutGroupFragment;
import com.parift.rideshare.fragment.GroupMemberListFragment;
import com.parift.rideshare.fragment.GroupMembershipRequestListFragment;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.dto.GroupDetail;
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
