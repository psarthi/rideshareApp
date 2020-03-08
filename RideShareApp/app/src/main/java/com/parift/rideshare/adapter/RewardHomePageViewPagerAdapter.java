package com.parift.rideshare.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.parift.rideshare.fragment.OfferListFragment;
import com.parift.rideshare.fragment.RewardCouponTransactionListFragment;
import com.parift.rideshare.fragment.RewardReimbursementTransactionListFragment;
import com.parift.rideshare.helper.Logger;

/**
 * Created by psarthi on 1/8/18.
 */

public class RewardHomePageViewPagerAdapter extends FragmentStatePagerAdapter{

    public static final String TAG = RewardHomePageViewPagerAdapter.class.getName();
    private int mPageCount;

    public RewardHomePageViewPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Logger.debug(TAG, "Getting Rewards Offer List Fragment");
            fragment = OfferListFragment.newInstance();
        }
        if (position == 1){
            Logger.debug(TAG, "Getting Rewards Reimbursement Transaction List Fragment");
            fragment = RewardReimbursementTransactionListFragment.newInstance();
        }
        if (position == 2){
            Logger.debug(TAG, "Getting Rewards Coupon Transaction List Fragment");
            fragment = RewardCouponTransactionListFragment.newInstance();
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
                return "Offers";
            case 1:
                return "Claims";
            case 2:
                return "Coupons";
            default:
                return "Offers";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
