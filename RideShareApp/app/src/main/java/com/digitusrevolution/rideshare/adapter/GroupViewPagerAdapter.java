package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.GroupListFragment;
import com.digitusrevolution.rideshare.model.app.GroupResultType;

/**
 * Created by psarthi on 1/8/18.
 */

public class GroupViewPagerAdapter extends FragmentStatePagerAdapter{

    public static final String TAG = GroupViewPagerAdapter.class.getName();

    public GroupViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Log.d(TAG, "Getting Group Membership List Fragment");
            fragment = GroupListFragment.newInstance(GroupResultType.Member, null);
        }
        if (position == 1){
            Log.d(TAG, "Getting Group Invitation List Fragment");
            fragment = GroupListFragment.newInstance(GroupResultType.Invite, null);
        }
        return fragment;

    }

    @Override
    public int getCount() {
        return 2;
    }
}
