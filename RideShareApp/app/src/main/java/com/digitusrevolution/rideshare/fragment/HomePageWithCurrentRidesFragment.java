package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageWithCurrentRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageWithCurrentRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageWithCurrentRidesFragment extends BaseFragment
        implements BaseFragment.OnSetCurrentLocationOnMapListener,
                    ThumbnailCoTravellerAdapter.ThumbnailCoTravellerAdapterListener{

    public static final String TAG = HomePageWithCurrentRidesFragment.class.getName();
    public static final String TITLE = "Ride Share";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
    private String mData;
    private BasicUser mUser;
    private FullRide mCurrentRide;
    private FullRideRequest mCurrentRideRequest;
    private OnFragmentInteractionListener mListener;
    private TextView mTextView;
    private LinearLayout mCurrentRideLinearLayout;
    private LinearLayout mCurrentRideRequestLinearLayout;
    private TextView mCurrentRideTextView;
    private TextView mCurrentRideRequestTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public HomePageWithCurrentRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param data  Data in Json format
     * @return A new instance of fragment HomePageWithCurrentRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageWithCurrentRidesFragment newInstance(String data) {
        HomePageWithCurrentRidesFragment fragment = new HomePageWithCurrentRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
        }
        mUser = getUser();
        mCurrentRide = getCurrentRide();
        mCurrentRideRequest = getCurrentRideRequest();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //This will assign this fragment to base fragment for callbacks.
        mOnSetCurrentLocationOnMapListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_with_current_rides, container, false);
        mCurrentRideLinearLayout = view.findViewById(R.id.current_ride_layout);
        mCurrentRideRequestLinearLayout = view.findViewById(R.id.current_ride_request_layout);

        //Make Ride & Ride Request layout invisible
        mCurrentRideLinearLayout.setVisibility(View.GONE);
        mCurrentRideRequestLinearLayout.setVisibility(View.GONE);
        showRidesLayoutVisibilityStatusForDebugging();

        //This will set appropriate view and set its visibility accordingly
        setRidesLayoutView(view);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_map);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.home_page_offer_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreatesRideFragment(RideType.OfferRide, null);
            }
        });

        view.findViewById(R.id.home_page_request_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreatesRideFragment(RideType.RequestRide, null);            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //((HomePageActivity)getActivity()).showBackButton(false);
        //Its important to set Title here else while loading fragment from backstack, title would not change
        getActivity().setTitle(TITLE);
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
        showChildFragmentDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Inside Destroy View");
        showChildFragmentDetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_page_map);
        if (mapFragment != null) {
            //This needs to be called if you are facing duplicate map id on fragment reload
            //getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    private void setRidesLayoutView(View view) {
        if (mCurrentRide!=null && mCurrentRideRequest!=null){
            if (mCurrentRide.getStartTime().before(mCurrentRideRequest.getPickupTime())) {
                Log.d(TAG,"Load Current Ride as its before Ride Request");
                setCurrentRideView(view);
                mCurrentRideLinearLayout.setVisibility(View.VISIBLE);
                showRidesLayoutVisibilityStatusForDebugging();
            } else {
                Log.d(TAG,"Load Current Ride Request as its before Ride");
                setCurrentRideRequestView(view);
                mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);
                showRidesLayoutVisibilityStatusForDebugging();
            }
        }
        else if (mCurrentRide!=null){
            Log.d(TAG,"Load Current Ride as there is no Ride Request");
            setCurrentRideView(view);
            mCurrentRideLinearLayout.setVisibility(View.VISIBLE);
            showRidesLayoutVisibilityStatusForDebugging();
        }
        else if (mCurrentRideRequest!=null){
            Log.d(TAG,"Load Current Ride Request as there is no Ride");
            setCurrentRideRequestView(view);
            mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);
            showRidesLayoutVisibilityStatusForDebugging();
        }
        else {
            Log.d(TAG,"Load Home Page with No Rides");
            showRidesLayoutVisibilityStatusForDebugging();
        }
    }

    private void showRidesLayoutVisibilityStatusForDebugging(){
        Log.d(TAG,"Current Ride Visibility: " + Integer.toString(mCurrentRideLinearLayout.getVisibility()));
        Log.d(TAG,"Current Ride Request Visibility: " + Integer.toString(mCurrentRideRequestLinearLayout.getVisibility()));
    }

    private void setCurrentRideView(View view){
        mCurrentRideTextView = view.findViewById(R.id.ride_id_text);
        mCurrentRideTextView.setText("Current Ride Id: "+mCurrentRide.getId());
        mRecyclerView = view.findViewById(R.id.current_ride_co_traveller_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ThumbnailCoTravellerAdapter(getActivity(), HomePageWithCurrentRidesFragment.this, (List<BasicRideRequest>) mCurrentRide.getAcceptedRideRequests());
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setCurrentRideRequestView(View view){
        mCurrentRideRequestTextView = view.findViewById(R.id.ride_request_id_text);
        mCurrentRideRequestTextView.setText("Current Ride Request Id: "+mCurrentRideRequest.getId());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSetCurrentLocationOnMapListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSetCurrentLocationOnMap(LatLng latLng) {
        Log.d(TAG,"Recieved Callback with LatLng:"+latLng.latitude+","+latLng.latitude);
        //This will move the camera to cover current location. Reason behind having it here as in BaseFragment
        //we have removed move camera option so that it can support create rides as well as home page fragment together
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
    }

    @Override
    public void onClickOfThumbnailCoTravellerAdapter(BasicUser user) {
        loadUserProfileFragment(new Gson().toJson(user), null);
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
        void onHomePageWithCurrentRidesFragmentInteraction(String data);
    }

}
