package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.digitusrevolution.rideshare.fragment.AboutGroupFragment;
import com.digitusrevolution.rideshare.fragment.GroupMemberFragment;
import com.digitusrevolution.rideshare.fragment.UserListFragment;
import com.digitusrevolution.rideshare.model.app.UserListType;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

/**
 * Created by psarthi on 1/10/18.
 */

public class GroupInfoViewPager extends FragmentStatePagerAdapter {

    public static final String TAG = GroupInfoViewPager.class.getName();
    private int mPageCount;
    private GroupDetail mGroupDetail;

    public GroupInfoViewPager(FragmentManager fm, int pageCount, GroupDetail groupDetail) {
        super(fm);
        mPageCount = pageCount;
        mGroupDetail = groupDetail;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        String mGroupDetailJson = new Gson().toJson(mGroupDetail);
        if (position==0){
            fragment = AboutGroupFragment.newInstance(mGroupDetailJson);
        }
        if (position==1){
            fragment = GroupMemberFragment.newInstance(mGroupDetailJson);
        }
        if (position == 2){
            fragment = UserListFragment.newInstance(UserListType.Membership_Request, mGroupDetailJson);
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

}
