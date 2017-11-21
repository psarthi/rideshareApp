package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageWithCurrentRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageWithCurrentRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageWithCurrentRidesFragment extends BaseFragment implements OnMapReadyCallback{

    public static final String TAG = HomePageWithCurrentRidesFragment.class.getName();
    public static final String TITLE = "Ride Share";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title";
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
    private String mTitle;
    private String mData;

    private OnFragmentInteractionListener mListener;
    private TextView mTextView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private UserSignInResult mUserSignInResult;
    private LinearLayout mCurrentRideLinearLayout;
    private LinearLayout mCurrentRideRequestLinearLayout;

    public HomePageWithCurrentRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Fragment Title
     * @param data  Data in Json format
     * @return A new instance of fragment HomePageWithCurrentRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageWithCurrentRidesFragment newInstance(String title, String data) {
        HomePageWithCurrentRidesFragment fragment = new HomePageWithCurrentRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mData = getArguments().getString(ARG_DATA);
        }
        getActivity().setTitle(mTitle);
        mUserSignInResult = new Gson().fromJson(mData,UserSignInResult.class);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_with_no_rides, container, false);
        mCurrentRideLinearLayout = view.findViewById(R.id.current_ride_layout);
        mCurrentRideRequestLinearLayout = view.findViewById(R.id.current_ride_request_layout);

        //Make Ride & Ride Request layout invisible
        mCurrentRideLinearLayout.setVisibility(View.GONE);
        mCurrentRideRequestLinearLayout.setVisibility(View.GONE);
        showRidesLayoutVisibilityStatusForDebugging();

        //This will make appropriate layout visible
        setRidesLayoutVisibility();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_with_no_rides_map);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.home_page_offer_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment offerRideFragment = OfferRideFragment.newInstance(OfferRideFragment.TITLE, null);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_page_container,offerRideFragment,OfferRideFragment.TAG)
                        .addToBackStack(OfferRideFragment.TAG)
                        .commit();

            }
        });
        return view;
    }

    private void setRidesLayoutVisibility() {
        if (mUserSignInResult.getCurrentRide()!=null && mUserSignInResult.getCurrentRideRequest()!=null){
            if (mUserSignInResult.getCurrentRide().getStartTime().before(mUserSignInResult.getCurrentRideRequest().getPickupTime())) {
                Log.d(TAG,"Load Current Ride as its before Ride Request");
                mCurrentRideLinearLayout.setVisibility(View.VISIBLE);
                showRidesLayoutVisibilityStatusForDebugging();
            } else {
                Log.d(TAG,"Load Current Ride Request as its before Ride");
                mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);
                showRidesLayoutVisibilityStatusForDebugging();
            }
        }
        else if (mUserSignInResult.getCurrentRide()!=null){
            Log.d(TAG,"Load Current Ride as there is no Ride Request");
            mCurrentRideLinearLayout.setVisibility(View.VISIBLE);
            showRidesLayoutVisibilityStatusForDebugging();
        }
        else if (mUserSignInResult.getCurrentRideRequest()!=null){
            Log.d(TAG,"Load Current Ride Request as there is no Ride");
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Permission not there");
            //This is important for Fragment and not we are not using Activity requestPermissions method but we are using Fragment requestPermissions,
            // so that request can be handled in this class itself instead of handling it in Activity class
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location Permission already there");
            setCurrentLocationOnMap();
        }
    }

    public void setCurrentLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
                        // Add a marker in User Current Location, and move the camera.
                        LatLng latLng  = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission Result Recieved");
        switch (requestCode) {
            case Constant.ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Location Permission granted");
                    setCurrentLocationOnMap();
                } else {
                    Log.d(TAG, "Location Permission denied");
                }
            }
        }
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
