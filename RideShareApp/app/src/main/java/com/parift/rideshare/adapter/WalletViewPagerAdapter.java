package com.parift.rideshare.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.parift.rideshare.fragment.RedeemFragment;
import com.parift.rideshare.fragment.TopUpFragment;
import com.parift.rideshare.fragment.TransactionFragment;
import com.parift.rideshare.helper.Logger;

/**
 * Created by psarthi on 12/8/17.
 *
 * Note - Don't use FragmentPageAdapter as it will not recreate the fragment once its killed
 * but FragmentStatePageAdapter would do that. And in our case, on click of left Nav items, we are clearning the backstack
 * where RidesListHomePageFragment would reside
 *
 */

public class WalletViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = WalletViewPagerAdapter.class.getName();
    private boolean mRequiredBalanceVisiblity;
    private float mRequiredBalanceAmount;

    public WalletViewPagerAdapter(FragmentManager fm, boolean requiredBalanceVisiblity, float requiredBalanceAmount) {
        super(fm);
        mRequiredBalanceVisiblity = requiredBalanceVisiblity;
        mRequiredBalanceAmount = requiredBalanceAmount;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position==0){
            Logger.debug(TAG, "Get Top Up Fragment");
            fragment = TopUpFragment.newInstance(mRequiredBalanceVisiblity, mRequiredBalanceAmount);
        }
        if (position == 1){
            Logger.debug(TAG, "Get Transaction Fragment");
            fragment = TransactionFragment.newInstance(null, null);
        }
        if (position == 2){
            Logger.debug(TAG, "Get Redeem Fragment");
            fragment = RedeemFragment.newInstance(null, null);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Top up";
            case 1:
                return "Transaction";
            case 2:
                return "Redeem";
            default:
                return "Top Up";
        }
    }

    //IMP - This will take care of all fragments reload when notifyDataSetChanged called on viewPagerAdapter
    @Override
    public int getItemPosition(Object object) {
        Logger.debug(TAG,"getItemPosition Called "+object.toString());
        return POSITION_NONE;
    }
}
