package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.parift.rideshare.adapter.RideListAdapter;
import com.parift.rideshare.adapter.RideRequestListAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.dto.BasicRide;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RidesListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RidesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesListFragment extends BaseFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String TAG = RidesListFragment.class.getName();

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private List<BasicRide> mRides = new ArrayList<>();
    private List<BasicRideRequest> mRideRequests = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private TextView mEmptyTextView;
    private String GET_USER_RIDES_URL;
    private String GET_USER_RIDE_REQUESTS_URL;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener mScrollListener;

    public RidesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @return A new instance of fragment RidesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesListFragment newInstance(RideType rideType) {
        Logger.debug(TAG,"newInstance");
        RidesListFragment fragment = new RidesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        GET_USER_RIDES_URL = APIUrl.GET_USER_RIDES_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()));
        GET_USER_RIDE_REQUESTS_URL = APIUrl.GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreateView. mRides,mRidesRequest Size:"+mRides.size()+","+mRideRequests.size());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rides_list, container, false);

        mRecyclerView = view.findViewById(R.id.rides_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEmptyTextView = view.findViewById(R.id.empty_result_text);

        //VERY IMP - Ensure you load the data from the server whenever we create the view, so that we always have updated set of data
        //Don't set the adapter directly otherwise you will end up with old data set
        loadInitialData();

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        mRecyclerView.addOnScrollListener(mScrollListener);

        return view;
    }


    private void loadInitialData() {
        if (mRideType.equals(RideType.OfferRide)){
            //Initial Data loading
            //Its important to use local variable else you will get updated string
            String URL = GET_USER_RIDES_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));

            mCommonUtil.showProgressDialog();
            RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (isAdded()) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
                        Type listType = new TypeToken<ArrayList<BasicRide>>() {
                        }.getType();
                        mRides = new Gson().fromJson(response.toString(), listType);
                        //This will load adapter only when data is loaded
                        Logger.debug(TAG, "Size of initial set of data is: " + mRides.size());
                        setAdapter();
                    }
                }
            });
        } else {
            //Initial Data loading
            String URL = GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));
            //Reason for commenting as we are already showing progress dialog for Rides
            //and in the same time we should get logically ride request as well
            //This will not show multiple progress dialog at the same time
            //showProgressDialog();
            RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (isAdded()) {
                        super.onSuccess(statusCode, headers, response);
                        //dismissProgressDialog();
                        Type listType = new TypeToken<ArrayList<BasicRideRequest>>() {
                        }.getType();
                        mRideRequests = new Gson().fromJson(response.toString(), listType);
                        //This will load adapter only when data is loaded
                        Logger.debug(TAG, "Size of initial set of data is: " + mRideRequests.size());
                        setAdapter();
                    }
                }
            });
        }
    }

    private void setAdapter() {
        if (mRideType.equals(RideType.OfferRide)){
            Logger.debug(TAG, "Setting Adapter for Offer Ride. Size:" + mRides.size());
            mAdapter = new RideListAdapter(mRides, this);
            if (mRides.size()==0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.GONE);
            }
        } else {
            Logger.debug(TAG, "Setting Adapter for Requested Ride. Size:" + mRideRequests.size());
            mAdapter = new RideRequestListAdapter(mRideRequests, this);
            if (mRideRequests.size()==0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.GONE);
            }
        }
        mRecyclerView.setAdapter(mAdapter);
        //Don't use this method as we need to instantitate LinearLayout manager before in this case but below method instantiate locally
        //setRecyclerView(mRecyclerView, adapter, LinearLayoutManager.VERTICAL);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        if (mRideType.equals(RideType.OfferRide)){
            String URL = GET_USER_RIDES_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
            //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
            //and unnecssarily we will show multiple dialog which creates flicker on the screen
            if (offset != 1) mCommonUtil.showProgressDialog();
            RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (isAdded()) {
                        super.onSuccess(statusCode, headers, response);
                        if (offset != 1) mCommonUtil.dismissProgressDialog();
                        Type listType = new TypeToken<ArrayList<FullRide>>() {
                        }.getType();
                        List<FullRide> newRides = new Gson().fromJson(response.toString(), listType);
                        //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                        mRides.addAll(newRides);
                        Logger.debug(TAG, "Size of new set of data is: " + newRides.size() + " :Updated count is:" + mRides.size());
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRides.size() - 1);
                    }
                }
            });
        } else {
            String URL = GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
            //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
            //and unnecssarily we will show multiple dialog which creates flicker on the screen
            if (offset != 1) mCommonUtil.showProgressDialog();
            RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    if (isAdded()) {
                        super.onSuccess(statusCode, headers, response);
                        if (offset != 1) mCommonUtil.dismissProgressDialog();
                        Type listType = new TypeToken<ArrayList<FullRideRequest>>() {
                        }.getType();
                        List<FullRideRequest> rideRequests = new Gson().fromJson(response.toString(), listType);
                        mRideRequests.addAll(rideRequests);
                        Logger.debug(TAG, "Size of new set of data is: " + rideRequests.size() + " :Updated count is:" + mRideRequests.size());
                        mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRideRequests.size() - 1);
                    }
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onRidesListFragmentInteraction(data);
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
        void onRidesListFragmentInteraction(String data);
    }
}
