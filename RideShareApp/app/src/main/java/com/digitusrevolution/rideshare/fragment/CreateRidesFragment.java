package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.component.MapComp;
import com.digitusrevolution.rideshare.component.TrustNetworkComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.dialog.DatePickerFragment;
import com.digitusrevolution.rideshare.dialog.TimePickerFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.dto.google.Bounds;
import com.digitusrevolution.rideshare.model.dto.google.GoogleDirection;
import com.digitusrevolution.rideshare.model.ride.domain.Point;
import com.digitusrevolution.rideshare.model.ride.domain.RidePoint;
import com.digitusrevolution.rideshare.model.ride.domain.RideRequestPoint;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.RideOfferInfo;
import com.digitusrevolution.rideshare.model.ride.dto.RideOfferResult;
import com.digitusrevolution.rideshare.model.ride.dto.RideRequestResult;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.domain.Role;
import com.digitusrevolution.rideshare.model.user.domain.RoleName;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRidesFragment extends BaseFragment implements OnMapReadyCallback,
        TimePickerFragment.TimePickerFragmentListener, DatePickerFragment.DatePickerFragmentListener {

    public static final String TAG = CreateRidesFragment.class.getName();
    public static final String OFFER_RIDE_TITLE = "Offer Ride";
    public static final String REQUEST_RIDE_TITLE = "Request Ride";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_DATA = "data";

    private String mTitle;
    private String mData;
    private RideType mRideType;

    private OnFragmentInteractionListener mListener;
    int PLACE_FROM_ADDRESS_REQUEST_CODE = 1;
    int PLACE_TO_ADDRESS_REQUEST_CODE = 2;
    private TextView mFromAddressTextView;
    private TextView mToAddressTextView;
    private LatLng mFromLatLng;
    private LatLng mToLatLng;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private GoogleDirection mGoogleDirection;
    private Calendar mStartTimeCalendar;
    private boolean mTimeInPast;
    private BasicUser mUser;
    private boolean mRidesOptionUpdated = false;
    private Preference mUpdatedRidesOption;
    private BasicRide mRide = new BasicRide();
    private RideOfferInfo mRideOfferInfo = new RideOfferInfo();
    private BasicRideRequest mRideRequest = new BasicRideRequest();
    private Date mMinStartTime;
    private TrustNetworkComp mTrustNetworkComp;
    private int mDefaultTextColor;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;
    private GoogleMap mMap;
    private MapComp mMapComp;
    private View mMapView;
    private Place mFromPlace;
    private Place mToPlace;

    public CreateRidesFragment() {
        Log.d(TAG, "CreateRidesFragment() Called");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data Data in Json format
     * @return A new instance of fragment CreateRidesFragment.
     */
    public static CreateRidesFragment newInstance(RideType rideType, String data) {
        Log.d(TAG, "newInstance Called with RideType:"+rideType);
        CreateRidesFragment fragment = new CreateRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Called");
        super.onCreate(savedInstanceState);
        //This will append items option menu by invoking fragment onCreateOptionMenu
        //So that you can have customer option menu for each fragment
        //setHasOptionsMenu(true);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mUser = mCommonUtil.getUser();
        Log.d(TAG,"User Name is:"+mUser.getFirstName());
        //Setting calender to current time
        mStartTimeCalendar = Calendar.getInstance();
        //This will increment the start time by lets say 10 mins, so that we don't get into issues of google API trying to get data of the past time
        mStartTimeCalendar.add(Calendar.MINUTE, Constant.START_TIME_INCREMENT);
        mMinStartTime = mStartTimeCalendar.getTime();
        mTrustNetworkComp = new TrustNetworkComp(this, null);

        //This will enable or disable back button
        //((HomePageActivity) getActivity()).showBackButton(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_rides, container, false);
        //Make fare view invisible initially and make it visible later for ride request cases only
        view.findViewById(R.id.create_rides_fare_text).setVisibility(View.GONE);
        mFromAddressTextView = view.findViewById(R.id.create_rides_from_address_text);
        mToAddressTextView = view.findViewById(R.id.create_rides_to_address_text);
        setAddressOnClickListener();

        //This will take care of reloading fragment
        if (mFromPlace!=null){
            mFromAddressTextView.setText(mFromPlace.getName());
        }
        if (mToPlace!=null){
            mToAddressTextView.setText(mToPlace.getName());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.create_rides_map);
        mapFragment.getMapAsync(this);
        mMapView = view.findViewById(R.id.create_rides_map);

        mDateTextView = view.findViewById(R.id.create_rides_date_text);
        mTimeTextView = view.findViewById(R.id.create_rides_time_text);
        mDefaultTextColor = mTimeTextView.getTextColors().getDefaultColor();
        //Set current time
        mDateTextView.setText(mCommonUtil.getFormattedDateString(mStartTimeCalendar.getTime()));
        mTimeTextView.setText(mCommonUtil.getTimeIn12HrFormat(mStartTimeCalendar.get(Calendar.HOUR_OF_DAY),mStartTimeCalendar.get(Calendar.MINUTE)));
        Log.d(TAG,"Current Time in Millis:"+mStartTimeCalendar.getTimeInMillis());
        setDateTimeOnClickListener();

        //Since TrustnetworkComp has been initialized in OnCreate so member variable values would not get reset and state of trust category would be maintained
        mTrustNetworkComp.setTrustCategoryViews(view);
        setButtonsOnClickListener(view);

        return view;
    }

    //Keep this here instead of moving to BaseFragment, so that you have better control
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady Called");
        mMap = googleMap;
        mMapComp = new MapComp(this, googleMap);
        mMapComp.setPadding(false);

        setCurrentLocation();
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
                    //Reason for checking mFromPlace to consider reload scenario
                    if (location != null && mFromPlace == null) {
                        Log.d(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
                        // Add a marker in User Current Location, and move the camera.
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        mFromLatLng = latLng;
                        mFromAddressTextView.setText(R.string.current_location_text);
                        //This will get called whenever map is reloaded and camera needs to be moved accordingly
                        //Otherwise what would happen on backpress, camera move would not happen for all markers on the map
                        //as it would assume there is only one marked which is current location marker and move camera accordinlgy
                        //as per the initial fragment load logic
                        moveCamera();

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

    private void setButtonsOnClickListener(View view) {
        view.findViewById(R.id.create_rides_option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ridesOption;
                if (mRidesOptionUpdated){
                    ridesOption = new Gson().toJson(mUpdatedRidesOption);
                } else {
                    ridesOption = new Gson().toJson(mUser.getPreference());
                }
                mFragmentLoader.loadRidesOptionFragment(mRideType, ridesOption);
            }
        });

        view.findViewById(R.id.create_rides_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRideType.equals(RideType.OfferRide)){
                    boolean driverStatus = false;
                    //Reason for not using contains directly as it was not working
                    for (Role role : mUser.getRoles()){
                        if (role.getName().equals(RoleName.Driver)){
                            driverStatus = true;
                            break;
                        }
                    }
                    if (!driverStatus){
                        mFragmentLoader.loadAddVehicleFragment(mRideType, null);
                    } else {
                        Log.d(TAG, "User is a driver, so create ride directly");
                        if (validateInput()){
                            setRideOffer();
                            RESTClient.post(getActivity(), APIUrl.OFFER_RIDE_URL, mRideOfferInfo, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    RideOfferResult rideOfferResult = new Gson().fromJson(response.toString(), RideOfferResult.class);
                                    Log.d(TAG, "Ride Successfully created with id:"+rideOfferResult.getRide().getId());
                                    if (rideOfferResult.isCurrentRide()) {
                                        mCommonUtil.updateCurrentRide(rideOfferResult.getRide());
                                        Log.d(TAG, "Updated Current Ride");
                                    }
                                    mFragmentLoader.loadRideInfoFragment(new Gson().toJson(rideOfferResult.getRide()));
                                    /*
                                    //Blocked this for the time being as it was causing issue in loading proper page
                                    //e.g. even if its current ride but home should have current ride request, then current ride request
                                    //page would display and user would be confused.
                                    if (rideOfferResult.isCurrentRide()){
                                        mCommonUtil.updateCurrentRide(rideOfferResult.getRide());
                                        Log.d(TAG, "Updated Current Ride");
                                        mListener.onCreateRideFragmentInteraction(null);
                                    } else {
                                        //This will load the Ride Info fragment
                                        mFragmentLoader.loadRideInfoFragment(new Gson().toJson(rideOfferResult.getRide()));
                                    }*/
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                }
                            });
                        }
                    }
                } else {
                    if (validateInput()){
                        setRideRequest();
                        RESTClient.post(getActivity(), APIUrl.REQUEST_RIDE_URL, mRideRequest, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                RideRequestResult rideRequestResult = new Gson().fromJson(response.toString(), RideRequestResult.class);
                                Log.d(TAG, "Ride Request Successfully created with id:"+rideRequestResult.getRideRequest().getId());
                                if (rideRequestResult.isCurrentRideRequest()) {
                                    mCommonUtil.updateCurrentRideRequest(rideRequestResult.getRideRequest());
                                    Log.d(TAG, "Updated Current Ride Request");
                                }
                                mFragmentLoader.loadRideRequestInfoFragment(new Gson().toJson(rideRequestResult.getRideRequest()));
                                /*
                                    //Blocked this for the time being as it was causing issue in loading proper page
                                    //e.g. even if its current ride but home should have current ride request, then current ride request
                                    //page would display and user would be confused.
                                if (rideRequestResult.isCurrentRideRequest()){
                                    mCommonUtil.updateCurrentRideRequest(rideRequestResult.getRideRequest());
                                    Log.d(TAG, "Updated Current Ride Request");
                                    mListener.onCreateRideFragmentInteraction(null);
                                } else {
                                    //This will load the Ride Request Info fragment
                                    mFragmentLoader.loadRideRequestInfoFragment(new Gson().toJson(rideRequestResult.getRideRequest()));
                                }*/
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });
                    }

                }
            }
        });
    }

    private void setRideOffer(){

        mRideOfferInfo.setGoogleDirection(mGoogleDirection);
        mRideOfferInfo.setRide(mRide);
        mRide.setStartTime(mStartTimeCalendar.getTime());
        RidePoint startPoint = new RidePoint();
        startPoint.setPoint(getStartPoint());
        mRide.setStartPoint(startPoint);
        RidePoint endPoint = new RidePoint();
        endPoint.setPoint(getEndPoint());
        mRide.setEndPoint(endPoint);
        mRide.setTrustNetwork(mTrustNetworkComp.getTrustNetworkFromView());
        mRide.setDriver(mUser);
        if (mRidesOptionUpdated){
            mRide.setSeatOffered(mUpdatedRidesOption.getDefaultVehicle().getSeatCapacity());
            mRide.setLuggageCapacityOffered(mUpdatedRidesOption.getDefaultVehicle().getSmallLuggageCapacity());
            mRide.setRideMode(mUpdatedRidesOption.getRideMode());
            mRide.setVehicle(mUpdatedRidesOption.getDefaultVehicle());
        } else {
            mRide.setSeatOffered(mUser.getPreference().getDefaultVehicle().getSeatCapacity());
            mRide.setLuggageCapacityOffered(mUser.getPreference().getDefaultVehicle().getSmallLuggageCapacity());
            mRide.setVehicle(mUser.getPreference().getDefaultVehicle());
            mRide.setRideMode(mUser.getPreference().getRideMode());
        }
    }

    private Vehicle getVehicle(String vehicleRegistrationNumber) {
        for (Vehicle vehicle : mUser.getVehicles()){
            if (vehicle.getRegistrationNumber().equals(vehicleRegistrationNumber)){
                return vehicle;
            }
        }
        return null;
    }

    private void setRideRequest(){

        mRideRequest.setPassenger(mUser);
        RideRequestPoint pickupPoint = new RideRequestPoint();
        pickupPoint.setPoint(getStartPoint());
        mRideRequest.setPickupPoint(pickupPoint);
        RideRequestPoint dropPoint = new RideRequestPoint();
        dropPoint.setPoint(getEndPoint());
        mRideRequest.setDropPoint(dropPoint);
        mRideRequest.setPickupTime(mStartTimeCalendar.getTime());
        mRideRequest.setTrustNetwork(mTrustNetworkComp.getTrustNetworkFromView());

        if (mRidesOptionUpdated){
            mRideRequest.setSeatRequired(mUpdatedRidesOption.getSeatRequired());
            mRideRequest.setLuggageCapacityRequired(mUpdatedRidesOption.getLuggageCapacityRequired());
            mRideRequest.setPickupTimeVariation(mUpdatedRidesOption.getPickupTimeVariation());
            mRideRequest.setPickupPointVariation(mUpdatedRidesOption.getPickupPointVariation());
            mRideRequest.setDropPointVariation(mUpdatedRidesOption.getDropPointVariation());
            mRideRequest.setVehicleCategory(mUpdatedRidesOption.getVehicleCategory());
            mRideRequest.setVehicleSubCategory(mUpdatedRidesOption.getVehicleSubCategory());
            mRideRequest.setRideMode(mUpdatedRidesOption.getRideMode());
        } else {
            mRideRequest.setSeatRequired(mUser.getPreference().getSeatRequired());
            mRideRequest.setLuggageCapacityRequired(mUser.getPreference().getLuggageCapacityRequired());
            mRideRequest.setPickupTimeVariation(mUser.getPreference().getPickupTimeVariation());
            mRideRequest.setPickupPointVariation(mUser.getPreference().getPickupPointVariation());
            mRideRequest.setDropPointVariation(mUser.getPreference().getDropPointVariation());
            mRideRequest.setVehicleCategory(mUser.getPreference().getVehicleCategory());
            mRideRequest.setVehicleSubCategory(mUser.getPreference().getVehicleSubCategory());
            mRideRequest.setRideMode(mUser.getPreference().getRideMode());
        }
    }

    @NonNull
    private Point getStartPoint() {
        Point point = new Point(mFromLatLng.longitude, mFromLatLng.latitude);
        return point;
    }

    @NonNull
    private Point getEndPoint() {
        Point point = new Point(mToLatLng.longitude, mToLatLng.latitude);
        return point;
    }


    private boolean validateInput(){

        if (mToLatLng ==null || mFromLatLng == null || mTimeInPast){
            Toast.makeText(getActivity(), "Please ensure input is valid",Toast.LENGTH_LONG).show();
            return false;
        }
        if (mRideType.equals(RideType.OfferRide) && mGoogleDirection == null){
            Toast.makeText(getActivity(), "Please wait for route to show up",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume Called");
        super.onResume();
        //Its important to set Title here else while loading fragment from backstack, title would not change
        if (mRideType.equals(RideType.OfferRide)){
            getActivity().setTitle(OFFER_RIDE_TITLE);
        } else {
            getActivity().setTitle(REQUEST_RIDE_TITLE);
        }
        showBackStackDetails();
    }

    private void setDateTimeOnClickListener() {
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = DatePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        });

        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = TimePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerFragment.TAG );
            }
        });
    }

    private void setAddressOnClickListener() {
        mFromAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent placeAutoCompleteIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                    startActivityForResult(placeAutoCompleteIntent,PLACE_FROM_ADDRESS_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        mToAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent placeAutoCompleteIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                    startActivityForResult(placeAutoCompleteIntent,PLACE_TO_ADDRESS_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_FROM_ADDRESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mFromPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "From Place: " + mFromPlace.getName());
                mFromAddressTextView.setText(mFromPlace.getName());
                mFromLatLng = mFromPlace.getLatLng();
                drawOnMap();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == PLACE_TO_ADDRESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mToPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "To Place: " + mToPlace.getName());
                mToAddressTextView.setText(mToPlace.getName());
                mToLatLng = mToPlace.getLatLng();
                drawOnMap();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void drawOnMap(){
        //This will clear existing polylinem markers etc. from map
        mMap.clear();

        //Draw Marker
        if (mFromLatLng!=null) mMap.addMarker(new MarkerOptions().position(mFromLatLng).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        if (mToLatLng!=null) mMap.addMarker(new MarkerOptions().position(mToLatLng));

        if (mRideType.equals(RideType.OfferRide)){
            //Get Direction
            getDirection();
            //No Need to move camera as it would be done post drawing route in getDirection
            //This will also avoid moving camera twice
        } else {
            //Draw Pickup/Drop Zone using circle
            //TODO Replace with actual radius for each ride
            if (mFromLatLng!=null) mMap.addCircle(new CircleOptions().center(mFromLatLng).radius(500));
            if (mToLatLng!=null) mMap.addCircle(new CircleOptions().center(mToLatLng).radius(500));
            //Move camera
            moveCamera();
        }
    }

    private void getDirection() {

        String GET_GOOGLE_DIRECTION_URL = APIUrl.GET_GOOGLE_DIRECTION_URL.replace(APIUrl.originLat_KEY, Double.toString(mFromLatLng.latitude))
                .replace(APIUrl.originLng_KEY, Double.toString(mFromLatLng.longitude))
                .replace(APIUrl.destinationLat_KEY, Double.toString(mToLatLng.latitude))
                .replace(APIUrl.destinationLng_KEY, Double.toString(mToLatLng.longitude))
                .replace(APIUrl.departureEpochSecond_KEY, Long.toString(mStartTimeCalendar.getTimeInMillis()))
                .replace(APIUrl.GOOGLE_API_KEY, getResources().getString(R.string.GOOGLE_API_KEY));

        RESTClient.get(GET_GOOGLE_DIRECTION_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Google Direction Success Response:" + response);
                mGoogleDirection = new Gson().fromJson(response.toString(), GoogleDirection.class);
                //Draw Route
                if (mGoogleDirection.getStatus().equals("OK")){
                    List<LatLng> latLngs = PolyUtil.decode(mGoogleDirection.getRoutes().get(0).getOverview_polyline().getPoints());
                    mMap.addPolyline(new PolylineOptions().addAll(latLngs));
                    //Move Camera to cover all markers on Map
                    moveCamera();
                } else {
                    Toast.makeText(getActivity(),"No valid route found, please enter alternate location",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(TAG, "Failed Response:" + errorResponse);
            }
        });
    }

    private LatLngBounds getBounds(){
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        if (mFromLatLng!=null) latLngBoundsBuilder.include(mFromLatLng);
        if (mToLatLng!=null) latLngBoundsBuilder.include(mToLatLng);
        LatLngBounds latLngBounds;
        //This will return the bound of route and no need to extend as that would be default cover start and end point
        if (mGoogleDirection!=null){
            Bounds bounds = mGoogleDirection.getRoutes().get(0).getBounds();
            //This will save loop again for so many times to go through each points
            latLngBounds = new LatLngBounds(new LatLng(bounds.getSouthwest().getLat(), bounds.getSouthwest().getLng()),
                    new LatLng(bounds.getNortheast().getLat(), bounds.getNortheast().getLng()));
            return latLngBounds;
        }
        //This will extend the bound to cover circle
        //TODO Replace with actual radius of rides
        if (mRideType.equals(RideType.RequestRide)){
            Log.d(TAG, "mFromLatLng, mToLatLng - "+mFromLatLng.toString() +":"+mToLatLng.toString());
            if (mFromLatLng!=null) {
                //This will ensure we are getting the right variation as initally mRideRequest variations are not set
                int variation = mRideRequest.getPickupPointVariation();
                if (variation == 0) variation = mUser.getPreference().getPickupPointVariation();
                latLngBounds = mMapComp.getCircleBounds(mFromLatLng,variation);
                latLngBoundsBuilder.include(latLngBounds.northeast);
                latLngBoundsBuilder.include(latLngBounds.southwest);
            }
            if (mToLatLng!=null) {
                //This will ensure we are getting the right variation as initally mRideRequest variations are not set
                int variation = mRideRequest.getDropPointVariation();
                if (variation == 0) variation = mUser.getPreference().getDropPointVariation();
                latLngBounds = mMapComp.getCircleBounds(mToLatLng,variation);
                latLngBoundsBuilder.include(latLngBounds.northeast);
                latLngBoundsBuilder.include(latLngBounds.southwest);
            }
        }
        latLngBounds = latLngBoundsBuilder.build();
        Log.d(TAG, "Bound Northeast"+latLngBounds.northeast.toString());
        Log.d(TAG, "Bound Southwest"+latLngBounds.southwest.toString());
        return latLngBounds;
    }

    private void moveCamera() {
        if (mFromLatLng == null || mToLatLng == null){
            Log.d(TAG, "To Field is empty, so using location camera zoom");
            if (mFromLatLng!=null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mFromLatLng,Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
            if (mToLatLng!=null) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mToLatLng,Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
        } else {
            LatLngBounds bounds = getBounds();
            //Note - padding should be "0" as we have already set padding on map seperately using setPadding()
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
            Log.d(TAG, "To Field exist, so using latlng bounds camera zoom");
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG,"onAttach Called");
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
        Log.d(TAG,"onDetach Called");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG,"Recieved callback. Value of HH:MM-"+hourOfDay+":"+minute);
        String timeIn12HrFormat = mCommonUtil.getTimeIn12HrFormat(hourOfDay, minute);
        mTimeTextView.setText(timeIn12HrFormat);
        mStartTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mStartTimeCalendar.set(Calendar.MINUTE, minute);
        Log.d(TAG,"Selected Date:"+mStartTimeCalendar.getTime());
        Log.d(TAG,"Current Date:"+Calendar.getInstance().getTime());
        validateStartTime();
    }

    //This will validate if start time is valid or not
    private void validateStartTime() {
        if (mStartTimeCalendar.getTime().before(mMinStartTime)){
            Toast.makeText(getActivity(),"Earliest start time can be "+Constant.START_TIME_INCREMENT+"mins from now",Toast.LENGTH_LONG).show();
            mTimeTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            mTimeInPast = true;
        } else {
            mTimeTextView.setTextColor(mDefaultTextColor);
            mTimeInPast = false;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        Log.d(TAG,"Recieved callback. Value of DD/MM/YY-"+day+"/"+month+"/"+year);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        String formattedDate = mCommonUtil.getFormattedDateString(date);
        mDateTextView.setText(formattedDate);
        mStartTimeCalendar.set(year,month,day);
        validateStartTime();
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
        void onCreateRideFragmentInteraction(String data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_search_item, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"Search Query is:"+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG,item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateRidesOption(Preference ridesOption){
        Log.d(TAG, "Ride Option has been updated");
        mRidesOptionUpdated = true;
        mUpdatedRidesOption = ridesOption;
        Log.d(TAG,"Updated Value is:"+new Gson().toJson(ridesOption));
    }
}
