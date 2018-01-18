package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.GroupListFragment;
import com.digitusrevolution.rideshare.fragment.UserMembershipRequestListFragment;
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
            Log.d(TAG, "Getting Group Membership List Fragment");
            fragment = GroupListFragment.newInstance(GroupListType.Member, null);
        }
        if (position == 1){
            Log.d(TAG, "Getting Group Invitation List Fragment");
            fragment = GroupListFragment.newInstance(GroupListType.Invite, null);
        }
        if (position == 2){
            Log.d(TAG, "Getting User Membership Request List Fragment");
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
        Log.d(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
