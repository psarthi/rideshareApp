package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.digitusrevolution.rideshare.adapter.RideListAdapter;
import com.digitusrevolution.rideshare.adapter.RideRequestListAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private boolean mInitialDataLoaded;
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
        Log.d(TAG,"newInstance");
        RidesListFragment fragment = new RidesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        GET_USER_RIDES_URL = APIUrl.GET_USER_RIDES_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()));
        GET_USER_RIDE_REQUESTS_URL = APIUrl.GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()));
        loadInitialData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView. mRides,mRidesRequest Size:"+mRides.size()+","+mRideRequests.size());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rides_list, container, false);

        mRecyclerView = view.findViewById(R.id.rides_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        //VERY IMP - This will get called only when fragment is reloaded and without this it will show up blank screen as adapter is not set
        if (mInitialDataLoaded) {
            setAdapter();
        }

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
            GET_USER_RIDES_URL = GET_USER_RIDES_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));

            mCommonUtil.showProgressDialog();
            RESTClient.get(GET_USER_RIDES_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<BasicRide>>(){}.getType();
                    mRides = new Gson().fromJson(response.toString(), listType);
                    mInitialDataLoaded = true;
                    //This will load adapter only when data is loaded
                    setAdapter();
                }
            });
        } else {
            //Initial Data loading
            GET_USER_RIDE_REQUESTS_URL = GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));
            //Reason for commenting as we are already showing progress dialog for Rides
            //and in the same time we should get logically ride request as well
            //This will not show multiple progress dialog at the same time
            //showProgressDialog();
            RESTClient.get(GET_USER_RIDE_REQUESTS_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    //dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<BasicRideRequest>>(){}.getType();
                    mRideRequests = new Gson().fromJson(response.toString(), listType);
                    mInitialDataLoaded = true;
                    //This will load adapter only when data is loaded
                    setAdapter();
                }
            });
        }
    }

    private void setAdapter() {
        if (mRideType.equals(RideType.OfferRide)){
            Log.d(TAG, "Setting Adapter for Offer Ride. Size:" + mRides.size());
            mAdapter = new RideListAdapter(mRides, this);
        } else {
            Log.d(TAG, "Setting Adapter for Requested Ride. Size:" + mRideRequests.size());
            mAdapter = new RideRequestListAdapter(mRideRequests, this);
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
            GET_USER_RIDES_URL = GET_USER_RIDES_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
            //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
            //and unnecssarily we will show multiple dialog which creates flicker on the screen
            if (offset != 1) mCommonUtil.showProgressDialog();
            RESTClient.get(GET_USER_RIDES_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (offset != 1) mCommonUtil.dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<FullRide>>(){}.getType();
                    List<FullRide> newRides = new Gson().fromJson(response.toString(), listType);
                    //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                    mRides.addAll(newRides);
                    Log.d(TAG, "Ride Size changed. Current Size is:"+mRides.size());
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRides.size()-1);
                }
            });
        } else {
            GET_USER_RIDE_REQUESTS_URL = GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
            //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
            //and unnecssarily we will show multiple dialog which creates flicker on the screen
            if (offset != 1) mCommonUtil.showProgressDialog();
            RESTClient.get(GET_USER_RIDE_REQUESTS_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (offset != 1) mCommonUtil.dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<FullRideRequest>>(){}.getType();
                    List<FullRideRequest> rideRequests = new Gson().fromJson(response.toString(), listType);
                    mRideRequests.addAll(rideRequests);
                    Log.d(TAG, "Ride Request Size changed. Current Size is:"+mRideRequests.size());
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRideRequests.size()-1);
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
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"onDetach");
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
        void onRidesListFragmentInteraction(String data);
    }
}
