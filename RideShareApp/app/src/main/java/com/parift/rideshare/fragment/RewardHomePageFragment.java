package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.RewardHomePageViewPagerAdapter;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RewardHomePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RewardHomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardHomePageFragment extends BaseFragment {

    public static final String TAG = RewardHomePageFragment.class.getName();
    public static final String TITLE = "Rewards";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RewardHomePageViewPagerAdapter mRewardHomePageViewPagerAdapter;
    private FragmentLoader mFragmentLoader;
    private CommonUtil mCommonUtil;

    public RewardHomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupHomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RewardHomePageFragment newInstance(String param1, String param2) {
        RewardHomePageFragment fragment = new RewardHomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        int pageCount = 3;
        mRewardHomePageViewPagerAdapter = new RewardHomePageViewPagerAdapter(getChildFragmentManager(), pageCount);
        mFragmentLoader = new FragmentLoader(this);
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_reward_home_page, container, false);

        TabLayout tabLayout = view.findViewById(R.id.reward_tab);
        final ViewPager viewPager = view.findViewById(R.id.reward_viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(mRewardHomePageViewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                viewPager.setCurrentItem(tab.getPosition());
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
        ((HomePageActivity)getActivity()).showBackButton(false);
        showBackStackDetails();
        getActivity().setTitle(TITLE);
        hideSoftKeyBoard();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onRewardHomePageFragmentInteraction(data);
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
        void onRewardHomePageFragmentInteraction(String data);
    }

    public void refresh(){
        Logger.debug(TAG,"refresh called");
        //Needs to validate if this works or not as what i have observed, that this will only work when view pager
        // is already loaded with all fragments else you will get exception that fragment doesn't exist
        //mRewardHomePageViewPagerAdapter.notifyDataSetChanged();
    }

}
