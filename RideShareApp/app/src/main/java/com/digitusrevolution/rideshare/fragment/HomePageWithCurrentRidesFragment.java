package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageWithCurrentRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageWithCurrentRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageWithCurrentRidesFragment extends BaseFragment implements BaseFragment.OnFragmentInteractionListener{

    public static final String TAG = HomePageWithCurrentRidesFragment.class.getName();
    public static final String TITLE = "Ride Share";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
    private String mData;

    private OnFragmentInteractionListener mListener;
    private TextView mTextView;
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
        mUserSignInResult = new Gson().fromJson(mData,UserSignInResult.class);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //This will assign this fragment to base fragment for callbacks.
        mBaseFragmentListener = this;
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

        //This will make appropriate layout visible
        setRidesLayoutVisibility();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_map);
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.home_page_offer_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreatesRideFragment(RideType.OfferRide);
            }
        });

        view.findViewById(R.id.home_page_request_ride_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreatesRideFragment(RideType.RequestRide);            }
        });


        return view;
    }

    private void loadCreatesRideFragment(RideType rideType) {
        Fragment createRidesFragment = CreateRidesFragment.newInstance(rideType, null);
        //Add to back stack as user may want to go back to home page and choose alternate option
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_container,createRidesFragment, CreateRidesFragment.TAG)
                .addToBackStack(CreateRidesFragment.TAG)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Its important to set Title here else while loading fragment from backstack, title would not change
        getActivity().setTitle(TITLE);
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
    public void onSetCurrentLocationOnMap(LatLng latLng) {
        Log.d(TAG,"Recieved Callback with LatLng:"+latLng.latitude+","+latLng.latitude);
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
