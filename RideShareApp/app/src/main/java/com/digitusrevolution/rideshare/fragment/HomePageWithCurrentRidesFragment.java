package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.digitusrevolution.rideshare.component.MapComp;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
        implements ThumbnailCoTravellerAdapter.ThumbnailCoTravellerAdapterListener, OnMapReadyCallback{

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
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;
    private GoogleMap mMap;
    private MapComp mMapComp;
    private CurrentRidesStatus mCurrentRidesStatus;

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
        mFragmentLoader = new FragmentLoader(this);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mCurrentRide = mCommonUtil.getCurrentRide();
        mCurrentRideRequest = mCommonUtil.getCurrentRideRequest();
        mCurrentRidesStatus = getCurrentRidesStatus();
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

        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)){
            setCurrentRideView(view);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)){
            setCurrentRideRequestView(view);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_map);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.home_page_offer_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadCreatesRideFragment(RideType.OfferRide, null);
            }
        });

        view.findViewById(R.id.home_page_request_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadCreatesRideFragment(RideType.RequestRide, null);
            }});

        return view;
    }

    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapComp = new MapComp(this, googleMap);
        mMapComp.setPadding(true);

        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)){
            mMapComp.setRideOnMap(mCurrentRide);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
            setCurrentLocation();
        }
    }

    // Don't move this to BaseFragment or MapComp for resuse, as this will unnecessarily required additional callbacks and lots of complication
    // Apart from that you can customize the marker icon, move camera to different zoom level which may be required for different fragements
    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Permission not there");
            //This is important for Fragment and not we are not using Activity requestPermissions method but we are using Fragment requestPermissions,
            // so that request can be handled in this class itself instead of handling it in Activity class
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location Permission already there");
            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            locationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
                        // Add a marker in User Current Location, and move the camera.
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            });
        }
    }

    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission Result Recieved");
        switch (requestCode) {
            case Constant.ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Location Permission granted");
                    setCurrentLocation();
                } else {
                    Log.d(TAG, "Location Permission denied");
                }
            }
        }
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

    private enum CurrentRidesStatus{
        CurrentRide, CurrentRideRequest, NoRide;
    }

    private CurrentRidesStatus getCurrentRidesStatus() {
        if (mCurrentRide!=null && mCurrentRideRequest!=null){
            if (mCurrentRide.getStartTime().before(mCurrentRideRequest.getPickupTime())) {
                Log.d(TAG,"Status - Current Ride as its before Ride Request");
                return CurrentRidesStatus.CurrentRide;
            } else {
                Log.d(TAG,"Status - Current Ride Request as its before Ride");
                return CurrentRidesStatus.CurrentRideRequest;
            }
        }
        else if (mCurrentRide!=null){
            Log.d(TAG,"Status - Current Ride as there is no Ride Request");
            return CurrentRidesStatus.CurrentRide;
        }
        else if (mCurrentRideRequest!=null){
            Log.d(TAG,"Status - Current Ride Request as there is no Ride");
            return CurrentRidesStatus.CurrentRideRequest;
        }
        else {
            Log.d(TAG,"Status - Home Page with No Rides");
            return CurrentRidesStatus.NoRide;
        }
    }

    private void showRidesLayoutVisibilityStatusForDebugging(){
        Log.d(TAG,"Current Ride Visibility: " + Integer.toString(mCurrentRideLinearLayout.getVisibility()));
        Log.d(TAG,"Current Ride Request Visibility: " + Integer.toString(mCurrentRideRequestLinearLayout.getVisibility()));
    }

    private void setCurrentRideView(View view){

        //This will make Ride layout visible which was set to Gone at the start
        mCurrentRideLinearLayout.setVisibility(View.VISIBLE);

        RideComp rideComp = new RideComp(this, mCurrentRide);
        rideComp.setBasicRideLayout(view);

        mRecyclerView = view.findViewById(R.id.current_ride_co_traveller_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ThumbnailCoTravellerAdapter(getActivity(), HomePageWithCurrentRidesFragment.this, (List<BasicRideRequest>) mCurrentRide.getAcceptedRideRequests());
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setCurrentRideRequestView(View view){

        //This will make Ride Request layout visible which was set to Gone at the start
        mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);

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
    public void onClickOfThumbnailCoTravellerAdapter(BasicUser user) {
        mFragmentLoader.loadUserProfileFragment(new Gson().toJson(user), null);
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
