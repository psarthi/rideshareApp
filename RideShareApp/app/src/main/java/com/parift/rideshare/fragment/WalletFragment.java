package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.WalletViewPagerAdapter;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalletFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends BaseFragment {
    public static final String TAG = WalletFragment.class.getName();
    public static final String TITLE = "Wallet";


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REQD_BALANCE_VISIBILITY = "requiredBalanceVisiblity";
    private static final String ARG_REQD_BALANCE_AMOUNT = "requiredBalanceAmount";

    private boolean mRequiredBalanceVisiblity;
    private float mRequiredBalanceAmount;

    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private WalletViewPagerAdapter mWalletViewPagerAdapter;
    private CommonUtil mCommonUtil;

    public WalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requiredBalanceVisiblity Do we need to show required balance amount text
     * @param requiredBalanceAmount Required balance amount
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletFragment newInstance(boolean requiredBalanceVisiblity, float requiredBalanceAmount) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_REQD_BALANCE_VISIBILITY, requiredBalanceVisiblity);
        args.putFloat(ARG_REQD_BALANCE_AMOUNT, requiredBalanceAmount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequiredBalanceVisiblity = getArguments().getBoolean(ARG_REQD_BALANCE_VISIBILITY);
            mRequiredBalanceAmount = getArguments().getFloat(ARG_REQD_BALANCE_AMOUNT);
        }
        mWalletViewPagerAdapter = new WalletViewPagerAdapter(getChildFragmentManager(), mRequiredBalanceVisiblity, mRequiredBalanceAmount);
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        final TabLayout tabLayout = view.findViewById(R.id.wallet_tab);
        mViewPager = view.findViewById(R.id.wallet_viewPager);

        /* Commenting this as we have added the page title in viewpager adapter itself
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        */

        //This is very important else you will have issue in syncing tab selection with view pager content
        //i.e. view pager may show request ride but tab selection would show offer ride
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mWalletViewPagerAdapter);
        //This is very important else Tab heading would be blank and don't set this upfront while adding tab, as that doesn't come into effect
        /* Commenting this as we have added the page title in viewpager adapter itself
        tabLayout.getTabAt(0).setText("Top Up");
        tabLayout.getTabAt(1).setText("Transaction");
        tabLayout.getTabAt(2).setText("Redeem");
        */

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                Logger.debug(TAG, "Before - Current Item in ViewPager is:"+mViewPager.getCurrentItem());
                mViewPager.setCurrentItem(tab.getPosition());
                Logger.debug(TAG, "After - Current Item in ViewPager is:"+mViewPager.getCurrentItem());
                showChildFragmentDetails();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Tab Unselected:"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Tab Reselected:"+tab.getText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        Logger.debug(TAG,"onResume");
        super.onResume();
        showBackStackDetails();
        if (mRequiredBalanceVisiblity){
            ((HomePageActivity)getActivity()).showBackButton(true);
        }
        getActivity().setTitle(TITLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mActivity = (FragmentActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onWalletFragmentInteraction(String data);
    }

    public void refresh(){
        Logger.debug(TAG,"refresh called");
        //IMP - This takes care of updating the dataset of transaction
        //else createView doesn't even get called for Transaction Fragment
        mWalletViewPagerAdapter.notifyDataSetChanged();
    }
}
