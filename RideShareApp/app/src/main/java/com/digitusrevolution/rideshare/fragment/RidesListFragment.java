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

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.digitusrevolution.rideshare.adapter.RideListAdapter;
import com.digitusrevolution.rideshare.adapter.RideRequestListAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.FullUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

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
public class RidesListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private BasicUser mUser;
    private FullUser mFullUser;
    private CommonUtil mCommonUtil;
    private List<FullRide> mRides = new ArrayList<>();
    private List<FullRideRequest> mRideRequests = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

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
        RidesListFragment fragment = new RidesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mFullUser = mCommonUtil.getFullUser();
        mRides = mCommonUtil.getRecentRides();
        mRideRequests = mCommonUtil.getRecentRideRequests();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rides_list, container, false);

        mRecyclerView = view.findViewById(R.id.rides_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        mRecyclerView.addOnScrollListener(mScrollListener);
        setAdapter();

        return view;
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        if (mRideType.equals(RideType.OfferRide)){
            String GET_USER_RIDES_URL = APIUrl.GET_USER_RIDES_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()))
                    .replace(APIUrl.PAGE_KEY, Integer.toString(offset));

            RESTClient.get(GET_USER_RIDES_URL, null, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

                    Type listType = new TypeToken<ArrayList<FullRide>>(){}.getType();
                    List<FullRide> newRides = new Gson().fromJson(response.toString(), listType);
                    //Since object is pass by reference, so when you add in mRides, this will be reflected everywhere
                    mRides.addAll(newRides);
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRides.size()-1);
                    //mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            String GET_USER_RIDE_REQUESTS_URL = APIUrl.GET_USER_RIDE_REQUESTS_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()))
                    .replace(APIUrl.PAGE_KEY, Integer.toString(offset));

            RESTClient.get(GET_USER_RIDE_REQUESTS_URL, null, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Type listType = new TypeToken<ArrayList<FullRideRequest>>(){}.getType();
                    List<FullRideRequest> rideRequests = new Gson().fromJson(response.toString(), listType);
                    mRideRequests.addAll(rideRequests);
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRideRequests.size()-1);
                    //mAdapter.notifyDataSetChanged();
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
        //setRecyclerView(mRecyclerView, adapter, LinearLayoutManager.VERTICAL);
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
