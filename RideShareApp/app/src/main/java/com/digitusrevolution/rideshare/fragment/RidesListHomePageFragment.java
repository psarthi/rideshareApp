package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.RidesListViewPagerAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.FullUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_rides_list_home_page, container, false);

        TabLayout tabLayout = view.findViewById(R.id.rides_tab);
        final ViewPager viewPager = view.findViewById(R.id.rides_viewPager);

        final RidesListViewPagerAdapter ridesListViewPagerAdapter = new RidesListViewPagerAdapter(getActivity().getSupportFragmentManager());

        String GET_USER_ALL_DATA_URL = APIUrl.GET_USER_ALL_DATA_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId())).
                replace(APIUrl.FETCH_CHILD_VALUE_KEY,"true");
        RESTClient.get(GET_USER_ALL_DATA_URL, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Successfully got Full User:"+mUser.getId());
                mFullUser = new Gson().fromJson(response.toString(), FullUser.class);
                mCommonUtil.updateFullUser(mFullUser);
                //This will set adapter only when we have got response
                viewPager.setAdapter(ridesListViewPagerAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Unable to get Full User:"+errorResponse.toString());
            }
        });

        TabLayout.Tab offerRidesTab = tabLayout.newTab();
        TabLayout.Tab requestedRidesTab = tabLayout.newTab();

        tabLayout.addTab(offerRidesTab.setText("Offered Rides"));
        tabLayout.addTab(requestedRidesTab.setText("Requested Rides"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                Log.d(TAG, "Before - Current Item in ViewPager is:"+viewPager.getCurrentItem());
                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "After - Current Item in ViewPager is:"+viewPager.getCurrentItem());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Unselected:"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Reselected:"+tab.getText());
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
}
