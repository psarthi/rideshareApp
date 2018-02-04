package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.GroupListFragment;
import com.digitusrevolution.rideshare.fragment.UserMembershipRequestListFragment;
import com.digitusrevolution.rideshare.helper.Logger;
import com.digitusrevolution.rideshare.model.user.dto.GroupListType;

/**
 * Created by psarthi on 1/8/18.
 */

public class GroupHomePageViewPagerAdapter extends FragmentStatePagerAdapter{

    public static final String TAG = GroupHomePageViewPagerAdapter.class.getName();
    private int mPageCount;

    public GroupHomePageViewPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Logger.debug(TAG, "Getting Group Membership List Fragment");
            fragment = GroupListFragment.newInstance(GroupListType.Member, null);
        }
        if (position == 1){
            Logger.debug(TAG, "Getting Group Invitation List Fragment");
            fragment = GroupListFragment.newInstance(GroupListType.Invite, null);
        }
        if (position == 2){
            Logger.debug(TAG, "Getting User Membership Request List Fragment");
            fragment = UserMembershipRequestListFragment.newInstance();
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
                return "My Groups";
            case 1:
                return "Invites";
            case 2:
                return "Requests";
            default:
                return "My Groups";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
