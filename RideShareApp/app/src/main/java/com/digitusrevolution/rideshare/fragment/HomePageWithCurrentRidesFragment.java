package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.app.Activity;
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
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.digitusrevolution.rideshare.component.MapComp;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.component.RideRequestComp;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRidesInfo;
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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageWithCurrentRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageWithCurrentRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageWithCurrentRidesFragment extends BaseFragment
        implements OnMapReadyCallback{

    public static final String TAG = HomePageWithCurrentRidesFragment.class.getName();
    public static final String NO_RIDE_TITLE = "Ride Share";
    public static final String CURRENT_RIDE_TITLE = "Current Ride";
    public static final String CURRENT_RIDE_REQUEST_TITLE = "Current Ride Request";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_FETCH_RIDES_FROM_SERVER = "fetchRidesFromServer";
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
    private String mData;
    private boolean mFetchRidesFromServer;
    private BasicUser mUser;
    private FullRide mCurrentRide;
    private FullRideRequest mCurrentRideRequest;
    private OnFragmentInteractionListener mListener;
    private LinearLayout mCurrentRideLinearLayout;
    private LinearLayout mCurrentRideRequestLinearLayout;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;
    private GoogleMap mMap;
    private View mMapView;
    private MapComp mMapComp;
    private CurrentRidesStatus mCurrentRidesStatus;

    public HomePageWithCurrentRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fetchRidesFromServer status (true for fetch from server, else false for local from sharedPrefs)
     * @param data  Data in Json format
     * @return A new instance of fragment HomePageWithCurrentRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageWithCurrentRidesFragment newInstance(boolean fetchRidesFromServer, String data) {
        HomePageWithCurrentRidesFragment fragment = new HomePageWithCurrentRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        args.putBoolean(ARG_FETCH_RIDES_FROM_SERVER, fetchRidesFromServer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mFetchRidesFromServer = getArguments().getBoolean(ARG_FETCH_RIDES_FROM_SERVER);
        }
        mFragmentLoader = new FragmentLoader(this);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Called");

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home_page_with_current_rides, container, false);
        mCurrentRideLinearLayout = view.findViewById(R.id.current_ride_layout);
        mCurrentRideRequestLinearLayout = view.findViewById(R.id.current_ride_request_layout);
        mMapView = view.findViewById(R.id.home_page_map);
        //Initial title whenever page loads, otherwise it shows previous page title and then it transition to new one and if there is a lag its clearly visible
        getActivity().setTitle(NO_RIDE_TITLE);

        //Make Ride, Ride Request & Map layout invisible, so that it loads on clean slate otherwise,
        //if you load map first then change camera later, then it looks awkward
        mCurrentRideLinearLayout.setVisibility(View.GONE);
        mCurrentRideRequestLinearLayout.setVisibility(View.GONE);
        mMapView.setVisibility(View.GONE);
        showRidesLayoutVisibilityStatusForDebugging();

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

        //Reason for putting it here, so that whenever the fragment get loaded either new or from backstack, onCreateView would get the latest
        //Otherwise, it was showing old ride information
        if (!mFetchRidesFromServer){
            mCurrentRide = mCommonUtil.getCurrentRide();
            mCurrentRideRequest = mCommonUtil.getCurrentRideRequest();
            setHomePageView(view);
        } else {
            fetchRidesFromServer(view);
        }
        return view;
    }

    private void fetchRidesFromServer(final View view) {
        String GET_USER_CURRENT_RIDES = APIUrl.GET_USER_CURRENT_RIDES.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()));
        RESTClient.get(GET_USER_CURRENT_RIDES, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                FullRidesInfo fullRidesInfo = new Gson().fromJson(response.toString(), FullRidesInfo.class);
                mCurrentRide = fullRidesInfo.getRide();
                mCurrentRideRequest = fullRidesInfo.getRideRequest();
                mCommonUtil.updateCurrentRide(mCurrentRide);
                mCommonUtil.updateCurrentRideRequest(mCurrentRideRequest);
                setHomePageView(view);
            }
        });
    }

    private void setHomePageView(View view) {

        //This will get status based on current ride and ride request value
        mCurrentRidesStatus = getCurrentRidesStatus();

        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)){
            Log.d(TAG, "Setting Current Ride View");
            getActivity().setTitle(CURRENT_RIDE_TITLE);
            setCurrentRideView(view);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)){
            Log.d(TAG, "Setting Current Ride Request View");
            getActivity().setTitle(CURRENT_RIDE_REQUEST_TITLE);
            setCurrentRideRequestView(view);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
            getActivity().setTitle(NO_RIDE_TITLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_map);
        mapFragment.getMapAsync(this);
        mMapView.setVisibility(View.VISIBLE);

    }


    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady called");
        mMap = googleMap;
        mMapComp = new MapComp(this, googleMap);
        mMapComp.setPadding(true);

        //TODO think on how to move this in common location so that we don't have to repeat this
        //IMP - Its very important to draw on Map and move camera only when layout is ready and below listener would do the job
        //Ref - https://stackoverflow.com/questions/7733813/how-can-you-tell-when-a-layout-has-been-drawn
        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //IMP - This is very important to remove the listener else it will get called many times for every view's
                // and your setRideOnMap would also be called that many times
                //This will ensure only once this is called
                mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "Map Layout is ready");
                if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)) {
                    Log.d(TAG, "Setting Ride On Map");
                    mMapComp.setRideOnMap(mCurrentRide);
                }
                if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)) {
                    Log.d(TAG, "Setting Ride Request On Map");
                    mMapComp.setRideRequestOnMap(mCurrentRideRequest);
                }
                if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
                    Log.d(TAG, "No Ride On Map");
                    setCurrentLocation();
                }
            }
        });
    }

    // Don't move this to BaseFragment or MapComp for resuse, as this will unnecessarily required additional callbacks and lots of complication
   // Apart from that you can customize the marker icon, move camera to different zoom level which may be required for different fragements
    private void setCurrentLocation() {
        Context context = getActivity();
        Activity activity = getActivity();
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
        Log.d(TAG,"Current Map Visibility: " + Integer.toString(mMapView.getVisibility()));
    }

    private void setCurrentRideView(View view){

        //This will make Ride layout visible which was set to Gone at the start
        mCurrentRideLinearLayout.setVisibility(View.VISIBLE);

        RideComp rideComp = new RideComp(this, mCurrentRide);
        rideComp.setBasicRideLayout(view, (BasicRide) mCurrentRide);

        RecyclerView recyclerView = view.findViewById(R.id.current_ride_co_traveller_list);
        RecyclerView.Adapter adapter = new ThumbnailCoTravellerAdapter(this, (List<BasicRideRequest>) mCurrentRide.getAcceptedRideRequests());
        setRecyclerView(recyclerView, adapter, LinearLayoutManager.HORIZONTAL);
    }

    private void setCurrentRideRequestView(View view){

        //This will make Ride Request layout visible which was set to Gone at the start
        mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);

        RideRequestComp rideRequestComp = new RideRequestComp(this, mCurrentRideRequest);
        rideRequestComp.setRideRequestBasicLayout(view, (BasicRideRequest) mCurrentRideRequest);

        if (mCurrentRideRequest.getAcceptedRide()!=null){
            UserComp userComp = new UserComp(this, null);
            userComp.setUserProfileSingleRow(view, mCurrentRideRequest.getAcceptedRide().getDriver());
        } else {
            //This will take care of ride request where there is no accepted ride
            view.findViewById(R.id.user_profile_single_row_layout).setVisibility(View.GONE);
        }

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
