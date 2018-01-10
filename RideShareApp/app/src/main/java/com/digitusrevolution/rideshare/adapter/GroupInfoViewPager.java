package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.AboutGroupFragment;
import com.digitusrevolution.rideshare.fragment.GroupListFragment;
import com.digitusrevolution.rideshare.fragment.RidesListFragment;
import com.digitusrevolution.rideshare.fragment.UserListFragment;
import com.digitusrevolution.rideshare.model.app.GroupResultType;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;

/**
 * Created by psarthi on 1/10/18.
 */

public class GroupInfoViewPager extends FragmentStatePagerAdapter {

    public static final String TAG = GroupInfoViewPager.class.getName();
    private int mPageCount;

    public GroupInfoViewPager(FragmentManager fm, int pageCount) {
        super(fm);
        mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            fragment = AboutGroupFragment.newInstance(null, null);
        }
        if (position==1){
            fragment = UserListFragment.newInstance(null, null);
        }
        if (position == 2){
            fragment = UserListFragment.newInstance(null, null);
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
                return "About us";
            case 1:
                return "Members";
            case 2:
                return "Requests";
            default:
                return "About us";
        }
    }

}
