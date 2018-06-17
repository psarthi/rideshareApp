package com.parift.rideshare.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.parift.rideshare.component.MapComp;
import com.parift.rideshare.component.RideComp;
import com.parift.rideshare.component.RideRequestComp;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.app.FetchType;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.ride.dto.FullRidesInfo;
import com.parift.rideshare.model.user.dto.BasicUser;
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
        implements OnMapReadyCallback, RideComp.RideCompListener,
        RideRequestComp.RideRequestCompListener{

    public static final String TAG = HomePageWithCurrentRidesFragment.class.getName();
    public static final String NO_RIDE_TITLE = "Ride Share";
    public static final String CURRENT_RIDE_TITLE = "Current Ride";
    public static final String CURRENT_RIDE_REQUEST_TITLE = "Current Ride Request";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_FETCH_TYPE = "fetchType";
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
    private String mData;
    private FetchType mFetchType;
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
    private boolean mMapLoaded;
    private boolean mShowMessage;

    public HomePageWithCurrentRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param fetchType Local or Server
     * @param data  Data in Json format
     * @return A new instance of fragment HomePageWithCurrentRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageWithCurrentRidesFragment newInstance(FetchType fetchType, String data) {
        HomePageWithCurrentRidesFragment fragment = new HomePageWithCurrentRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        args.putString(ARG_FETCH_TYPE, fetchType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    public void setFetchType(FetchType fetchType) {
        Logger.debug(TAG, "Fetch Type has been set to:"+fetchType.toString());
        mFetchType = fetchType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreate Called of instance:"+this.hashCode());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mFetchType = FetchType.valueOf(getArguments().getString(ARG_FETCH_TYPE));
        }
        mFragmentLoader = new FragmentLoader(this);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mShowMessage = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreateView Called of instance:"+this.hashCode());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_with_current_rides, container, false);
        mCurrentRideLinearLayout = view.findViewById(R.id.current_ride_layout);
        mCurrentRideRequestLinearLayout = view.findViewById(R.id.current_ride_request_layout);
        mMapView = view.findViewById(R.id.home_page_map);

        //This will make Ride, Ride Request and Map layout invisible, so that it loads on clean slate otherwise
        //if you load map first then change camera later, then it looks awkward
        //Note - If you don't make Current Ride and Ride Request Invisible, it will show the intitial layout for
        //a second and then make it invisible later when setting HomePageView
        mMapView.setVisibility(View.GONE);
        mCurrentRideRequestLinearLayout.setVisibility(View.GONE);
        mCurrentRideLinearLayout.setVisibility(View.GONE);
        showRidesLayoutVisibilityStatusForDebugging();

        final TextView homePageMsgCloseTextView = view.findViewById(R.id.home_page_message_box_close);
        final TextView homePageMsgTextView = view.findViewById(R.id.home_page_message_box);

        if (mShowMessage){
            String msg = mCommonUtil.getAppInfo().getHomePageMsg();
            homePageMsgTextView.setText(msg);
            homePageMsgCloseTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowMessage = false;
                    homePageMsgCloseTextView.setVisibility(View.GONE);
                    homePageMsgTextView.setVisibility(View.GONE);
                }
            });
        } else {
            homePageMsgCloseTextView.setVisibility(View.GONE);
            homePageMsgTextView.setVisibility(View.GONE);
        }

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

    public void fetchRidesFromServer(final View view) {
        String GET_USER_CURRENT_RIDES = APIUrl.GET_USER_CURRENT_RIDES.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()));
        //VERY VERY IMP - Not Showing progress dialog as this fragment keeps loading and getting destroyed whenever we remove all fragments from stack
        //So what happens when all fragments gets removed by default this would load and in between some other frgament would load so it would not wait for
        //the response of the current rides and your progress dialog would not get dismissed, so we will not show progress dialog here
        //mCommonUtil.showProgressDialog();
        RESTClient.get(GET_USER_CURRENT_RIDES, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    //mCommonUtil.dismissProgressDialog();
                    FullRidesInfo fullRidesInfo = new Gson().fromJson(response.toString(), FullRidesInfo.class);
                    mCurrentRide = fullRidesInfo.getRide();
                    mCurrentRideRequest = fullRidesInfo.getRideRequest();
                    mCommonUtil.updateCurrentRide(mCurrentRide);
                    mCommonUtil.updateCurrentRideRequest(mCurrentRideRequest);
                    setHomePageView(view);
                }
            }
        });
    }

    private void setHomePageView(View view) {
        Logger.debug(TAG, "Setting Home Page View");
        //This will get status based on current ride and ride request value
        mCurrentRidesStatus = getCurrentRidesStatus();
        //This will ensure title is updated on page refresh as well, when full page is not reloaded and onResume function doesn't get called
        //This is the scenario when you are already on the fragment and when you take some action only view gets refreshed by calling setHomePageView
        //so no onResume and if you set title there, then title doesn't change.
        //setTitle();

        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)){
            Logger.debug(TAG, "Setting Current Ride View");
            setCurrentRideView(view);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)){
            Logger.debug(TAG, "Setting Current Ride Request View");
            setCurrentRideRequestView(view);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
            //This will make ride and ride request layout invisible
            mCurrentRideRequestLinearLayout.setVisibility(View.GONE);
            mCurrentRideLinearLayout.setVisibility(View.GONE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_map);
        mapFragment.getMapAsync(this);
        mMapView.setVisibility(View.VISIBLE);
    }


    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.debug(TAG, "onMapReady called of instance:"+this.hashCode());
        mMap = googleMap;
        mMapComp = new MapComp(this, googleMap);
        mMapComp.setPadding(true, null);

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
                Logger.debug(TAG, "Map Layout is ready");
                mMapLoaded = true;
                drawOnMap();
            }
        });

        //Reason behind this as on refresh of view, getViewTreeObserver would not get called
        //as its called only first time when map is loaded
        if (mMapLoaded){
            Logger.debug(TAG, "Map already loaded");
            drawOnMap();
        }
    }

    private void drawOnMap() {
        //This will ensure it just clears the marker
        mMap.clear();

        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)) {
            Logger.debug(TAG, "Setting Ride On Map");
            mMapComp.setRideOnMap(mCurrentRide);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)) {
            Logger.debug(TAG, "Setting Ride Request On Map");
            mMapComp.setRideRequestOnMap(mCurrentRideRequest);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
            Logger.debug(TAG, "No Ride On Map");
            setCurrentLocation();
        }
    }

    // Don't move this to BaseFragment or MapComp for resuse, as this will unnecessarily required additional callbacks and lots of complication
   // Apart from that you can customize the marker icon, move camera to different zoom level which may be required for different fragements
    private void setCurrentLocation() {
        Context context = getActivity();
        Activity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.debug(TAG, "Location Permission not there of instance:"+this.hashCode());
            //This is important for Fragment and not we are not using Activity requestPermissions method but we are using Fragment requestPermissions,
            // so that request can be handled in this class itself instead of handling it in Activity class
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            Logger.debug(TAG, "Location Permission already there of instance:"+this.hashCode());
            //This will update current location based on last known location
            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            locationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Logger.debug(TAG, "Updating current location based on last known location");
                    setCurrentLocationMarker(location);
                }
            });

            /* Commenting this as it will throw an exception if we are not on the same fragment when location update is recieved
            //So logically either we get last known location or we don't get it, that's better than seeing NPE
            //This will update current location based on current location
            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    Logger.debug(TAG, "Updating current location based on current location");
                    setCurrentLocationMarker(location);
                    locationManager.removeUpdates(this);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            */
        }
    }

    private void setCurrentLocationMarker(Location location) {
        if (location != null) {
            Logger.debug(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
            // Add a marker in User Current Location, and move the camera.
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
        } else {
            Logger.debug(TAG, "Location is null");
        }
    }

    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.debug(TAG, "Permission Result Recieved");
        switch (requestCode) {
            case Constant.ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Logger.debug(TAG, "Location Permission granted");
                    setCurrentLocation();
                } else {
                    Logger.debug(TAG, "Location Permission denied");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(false);
        Logger.debug(TAG,"Inside OnResume of instance:"+this.hashCode());

        //IMP - If you put this code in onCreateView it was not fetching the latest if it was coming from backstack i.e. if you move to another application
        //and come back to this, it was showing old data
        //Reason for putting it here, so that whenever the fragment get loaded either new or from backstack, we would get the latest
        //Otherwise, it was showing old ride information
        if (mFetchType.equals(FetchType.Local)){
            mCurrentRide = mCommonUtil.getCurrentRide();
            mCurrentRideRequest = mCommonUtil.getCurrentRideRequest();
            setHomePageView(getView());
            //This will ensure all future fetch is always from server
            //First time when this page loads post login we don't fetch the current rides as it was already there in the login response
            //but for any future reload of this page, data has to come from server as user can perform certain ride action in ride info / ride request info
            //The same doesn't get reflected if we don't refresh the data of current rides
            mFetchType = FetchType.Server;
        } else {
            fetchRidesFromServer(getView());
        }

        //We are using this temporarily as there is an issue with setting title on page load as whenever we click any item in left nav
        //Home page get refreshed as we are poppping all backstacks and home is the only fragment which would get reloaded
        //so that's having issue in the title as it overwrites the title when we get the response late
        //TODO Need to find better solution
        getActivity().setTitle(NO_RIDE_TITLE);
        showBackStackDetails();
        showChildFragmentDetails();
        //Don't set title anywhere else otherwise since this a base fragment and whenever backstack is cleared,
        //this will try to get loaded and in between if you load any other fragment then title of that page would change
    }

    private void setTitle(){
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRide)){
            getActivity().setTitle(CURRENT_RIDE_TITLE);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.CurrentRideRequest)){
            getActivity().setTitle(CURRENT_RIDE_REQUEST_TITLE);
        }
        if (mCurrentRidesStatus.equals(CurrentRidesStatus.NoRide)){
            getActivity().setTitle(NO_RIDE_TITLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.debug(TAG, "Inside Destroy View of instance:"+this.hashCode());
        showChildFragmentDetails();

        //Below remarks is for reference. Commented till we find cleaner solution to ensure maps get reloaded
        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_page_map);
        if (mapFragment != null) {
            //Note - Don't use below one of remove mapfrgament as it also kills HomePageFragment as well
            //This needs to be called if you are facing duplicate map id on fragment reload
            //getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
            //Note - Don't use this one as somehow this is not working, instead remove mapFragment completely as shown above
            //This will just ensure map will get reloaded from scratch on fragment reload. This is different than above solution
            //This is required else old markers is not getting cleared
            //mMap = null;
        }
        */
    }

    @Override
    public void onRideRefresh(FullRide ride) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Id with status:"
                +ride.getId()+":"+ride.getStatus());
        //Reason for refetching as we don't know what action user has performed e.g. if he has cancelled the ride
        //then our current rides status would itself change
        fetchRidesFromServer(getView());
    }

    @Override
    public void onRideRequestRefresh(FullRideRequest rideRequest) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Request Id with status:"
                +rideRequest.getId()+":"+rideRequest.getStatus());
        //Reason for refetching as we don't know what action user has performed e.g. if he has cancelled the ride
        //then our current rides status would itself change
        fetchRidesFromServer(getView());
    }

    private enum CurrentRidesStatus{
        CurrentRide, CurrentRideRequest, NoRide;
    }

    private CurrentRidesStatus getCurrentRidesStatus() {
        if (mCurrentRide!=null && mCurrentRideRequest!=null){
            if (mCurrentRide.getStartTime().before(mCurrentRideRequest.getPickupTime())) {
                Logger.debug(TAG,"Status - Current Ride as its before Ride Request");
                return CurrentRidesStatus.CurrentRide;
            } else {
                Logger.debug(TAG,"Status - Current Ride Request as its before Ride");
                return CurrentRidesStatus.CurrentRideRequest;
            }
        }
        else if (mCurrentRide!=null){
            Logger.debug(TAG,"Status - Current Ride as there is no Ride Request");
            return CurrentRidesStatus.CurrentRide;
        }
        else if (mCurrentRideRequest!=null){
            Logger.debug(TAG,"Status - Current Ride Request as there is no Ride");
            return CurrentRidesStatus.CurrentRideRequest;
        }
        else {
            Logger.debug(TAG,"Status - Home Page with No Rides");
            return CurrentRidesStatus.NoRide;
        }
    }

    private void showRidesLayoutVisibilityStatusForDebugging(){
        Logger.debug(TAG,"Current Ride Visibility: " + Integer.toString(mCurrentRideLinearLayout.getVisibility()));
        Logger.debug(TAG,"Current Ride Request Visibility: " + Integer.toString(mCurrentRideRequestLinearLayout.getVisibility()));
        Logger.debug(TAG,"Current Map Visibility: " + Integer.toString(mMapView.getVisibility()));
    }

    private void setCurrentRideView(View view){

        //This will make Ride layout visible which was set to Gone at the start
        mCurrentRideLinearLayout.setVisibility(View.VISIBLE);
        //This will make ride request layout invisible
        mCurrentRideRequestLinearLayout.setVisibility(View.GONE);

        RideComp rideComp = new RideComp(this, mCurrentRide);
        rideComp.setBasicRideLayout(view);

        RecyclerView recyclerView = view.findViewById(R.id.current_ride_co_traveller_list);
        RecyclerView.Adapter adapter = new ThumbnailCoTravellerAdapter(this, (List<FullRideRequest>) mCurrentRide.getAcceptedRideRequests());
        setRecyclerView(recyclerView, adapter, LinearLayoutManager.HORIZONTAL);
    }

    private void setCurrentRideRequestView(View view){

        //This will make Ride Request layout visible which was set to Gone at the start
        mCurrentRideRequestLinearLayout.setVisibility(View.VISIBLE);
        //This will make ride layout invisible
        mCurrentRideLinearLayout.setVisibility(View.GONE);

        RideRequestComp rideRequestComp = new RideRequestComp(this, mCurrentRideRequest);
        rideRequestComp.setRideRequestBasicLayout(view);

        if (mCurrentRideRequest.getAcceptedRide()!=null){
            UserComp userComp = new UserComp(this, mCurrentRideRequest.getAcceptedRide().getDriver());
            userComp.setUserProfileSingleRow(view, true);
        } else {
            //This will take care of ride request where there is no accepted ride
            view.findViewById(R.id.user_profile_single_row_layout).setVisibility(View.GONE);
        }

    }

    @Override
    public void onAttach(Context context) {
        Logger.debug(TAG, "Inside onAttach of instance:"+this.hashCode());
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mActivity = (FragmentActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSetCurrentLocationOnMapListener");
        }
    }

    @Override
    public void onDetach() {
        Logger.debug(TAG, "Inside onDetach of instance:"+this.hashCode());
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
        void onHomePageWithCurrentRidesFragmentInteraction(String data);
    }

}
