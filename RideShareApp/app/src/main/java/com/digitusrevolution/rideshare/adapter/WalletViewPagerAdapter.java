package com.digitusrevolution.rideshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.digitusrevolution.rideshare.fragment.RedeemFragment;
import com.digitusrevolution.rideshare.fragment.TopUpFragment;
import com.digitusrevolution.rideshare.fragment.TransactionFragment;

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
            Log.d(TAG, "Get Top Up Fragment");
            fragment = TopUpFragment.newInstance(mRequiredBalanceVisiblity, mRequiredBalanceAmount);
        }
        if (position == 1){
            Log.d(TAG, "Get Transaction Fragment");
            fragment = TransactionFragment.newInstance(null, null);
        }
        if (position == 2){
            Log.d(TAG, "Get Redeem Fragment");
            fragment = RedeemFragment.newInstance(null, null);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
