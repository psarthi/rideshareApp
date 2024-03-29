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
import com.parift.rideshare.adapter.RidesListViewPagerAdapter;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.FullUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RidesListHomePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RidesListHomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesListHomePageFragment extends BaseFragment {

    public static final String TAG = RidesListHomePageFragment.class.getName();
    public static final String TITLE = "All Rides";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private FullUser mFullUser;
    private CommonUtil mCommonUtil;
    private ViewPager mViewPager;
    private RidesListViewPagerAdapter mRidesListViewPagerAdapter;
    private int mTabPosition;

    public RidesListHomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RidesListHomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesListHomePageFragment newInstance(String param1, String param2) {
        RidesListHomePageFragment fragment = new RidesListHomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mRidesListViewPagerAdapter = new RidesListViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreateView");
        showChildFragmentDetails();
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_rides_list_home_page, container, false);

        final TabLayout tabLayout = view.findViewById(R.id.rides_tab);
        mViewPager = view.findViewById(R.id.rides_viewPager);
        mViewPager.setAdapter(mRidesListViewPagerAdapter);

        /* Commenting this as we have added the page title in viewpager adapter itself
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        */

        //This is very important else you will have issue in syncing tab selection with view pager content
        //i.e. view pager may show request ride but tab selection would show offer ride
        tabLayout.setupWithViewPager(mViewPager);

        //This is very important else Tab heading would be blank and don't set this upfront while adding tab, as that doesn't come into effect
        /* Commenting this as we have added the page title in viewpager adapter itself
        tabLayout.getTabAt(0).setText("Offered Rides");
        tabLayout.getTabAt(1).setText("Requested Rides");
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
        ((HomePageActivity)getActivity()).showBackButton(false);
        getActivity().setTitle(TITLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onRidesListHomePageFragmentInteraction(data);
        }
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
        Logger.debug(TAG,"onDetach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabPosition = mViewPager.getCurrentItem();
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
        void onRidesListHomePageFragmentInteraction(String data);
    }

    public void refresh(){
        Logger.debug(TAG,"refresh called");
        //Needs to validate if this works or not as what i have observed, that this will only work when view pager
        // is already loaded with all fragments else you will get exception that fragment doesn't exist
        //mRidesListViewPagerAdapter.notifyDataSetChanged();
    }

}
