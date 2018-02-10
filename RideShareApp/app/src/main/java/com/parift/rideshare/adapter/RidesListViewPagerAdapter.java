package com.parift.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.parift.rideshare.fragment.RidesListFragment;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.domain.RideType;

/**
 * Created by psarthi on 12/8/17.
 *
 * Note - Don't use FragmentPageAdapter as it will not recreate the fragment once its killed
 * but FragmentStatePageAdapter would do that. And in our case, on click of left Nav items, we are clearning the backstack
 * where RidesListHomePageFragment would reside
 *
 */

public class RidesListViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = RidesListViewPagerAdapter.class.getName();

    public RidesListViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Logger.debug(TAG, "Getting Ride List Fragment with Offered Ride Type");
            fragment = RidesListFragment.newInstance(RideType.OfferRide);
        }
        if (position == 1){
            Logger.debug(TAG, "Getting Ride List Fragment with Requested Ride Type");
            fragment = RidesListFragment.newInstance(RideType.RequestRide);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Offered Rides";
            case 1:
                return "Requested Rides";
            default:
                return "Offered Rides";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
