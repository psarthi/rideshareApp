package com.digitusrevolution.rideshare.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.dto.google.Bounds;
import com.digitusrevolution.rideshare.model.dto.google.GoogleDirection;
import com.digitusrevolution.rideshare.model.ride.domain.Point;
import com.digitusrevolution.rideshare.model.ride.domain.RidePoint;
import com.digitusrevolution.rideshare.model.ride.domain.TrustCategory;
import com.digitusrevolution.rideshare.model.ride.domain.TrustCategoryName;
import com.digitusrevolution.rideshare.model.ride.domain.TrustNetwork;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.RideOfferInfo;
import com.digitusrevolution.rideshare.model.ride.dto.RideOfferResult;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.domain.Role;
import com.digitusrevolution.rideshare.model.user.domain.RoleName;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
public class CreateRidesFragment extends BaseFragment implements BaseFragment.OnSetCurrentLocationOnMapListener,
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
    private boolean mAllSelected;
    private boolean mGroupsSelected;
    private boolean mFriendsSelected;
    private ImageView mAllImageView;
    private ImageView mGroupsImageView;
    private ImageView mFriendsImageView;
    private TextView mAllTextView;
    private TextView mGroupsTextView;
    private TextView mFriendsTextView;
    private int mSelectedColor;
    private int mDefaultTextColor;
    private ColorFilter mDefaultImageTint;
    private GoogleDirection mGoogleDirection;
    private Calendar mStartTimeCalendar;
    private boolean mTimeInPast;
    private static final int BUFFER_TIME_IN_MINUTE = 5;
    private BasicUser mUser;
    private boolean mRidesOptionUpdated = false;
    private Preference mUpdatedRidesOption;
    private String mVehicleRegistrationNumber;
    private BasicRide mBasicRide = new BasicRide();
    private RideOfferInfo mRideOfferInfo = new RideOfferInfo();
    private BasicRideRequest mBasicRideRequest = new BasicRideRequest();

    public CreateRidesFragment() {
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
        CreateRidesFragment fragment = new CreateRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This will append items option menu by invoking fragment onCreateOptionMenu
        //So that you can have customer option menu for each fragment
        //setHasOptionsMenu(true);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //This will assign this fragment to base fragment for callbacks.
        mOnSetCurrentLocationOnMapListener = this;
        //Setting calender to current time
        mStartTimeCalendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_rides, container, false);
        //Make fare view invisible initially and make it visible later for ride request cases only
        view.findViewById(R.id.create_rides_fare_text).setVisibility(View.GONE);
        mFromAddressTextView = view.findViewById(R.id.create_rides_from_address_text);
        mToAddressTextView = view.findViewById(R.id.create_rides_to_address_text);
        setAddressOnClickListener();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.create_rides_map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mDateTextView = view.findViewById(R.id.create_rides_date_text);
        mTimeTextView = view.findViewById(R.id.create_rides_time_text);
        //Set current time
        mDateTextView.setText(getFormattedDateString(mStartTimeCalendar.getTime()));
        mTimeTextView.setText(getTimeIn12HrFormat(mStartTimeCalendar.get(Calendar.HOUR_OF_DAY),mStartTimeCalendar.get(Calendar.MINUTE)));
        Log.d(TAG,"Current Time in Millis:"+mStartTimeCalendar.getTimeInMillis());
        setDateTimeOnClickListener();

        setTrustCategoryViews(view);
        //Initial value on home page
        mAllSelected = true;
        updateTrustCategoryItemsColor();
        setTrustCategoryOnClickListener(view);
        setButtonsOnClickListener(view);

        return view;
    }

    private void setButtonsOnClickListener(View view) {
        view.findViewById(R.id.create_rides_option_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRidesOptionFragment(mRideType, null);
            }
        });

        view.findViewById(R.id.create_rides_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean driverStatus = false;
                //Reason for not using contains directly as it was not working
                for (Role role : mUser.getRoles()){
                    if (role.getName().equals(RoleName.Driver)){
                        driverStatus = true;
                        break;
                    }
                }
                if (!driverStatus){
                    loadAddVehicleFragment(mRideType, null);
                } else {
                    Log.d(TAG, "User is a driver, so create ride directly");
                    setRideOffer();
                    RESTClient.post(getActivity(), APIUrl.OFFER_RIDE_URL, mRideOfferInfo, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            RideOfferResult rideOfferResult = new Gson().fromJson(response.toString(), RideOfferResult.class);
                            Log.d(TAG, "Ride Successfully created with id:"+rideOfferResult.getRide().getId());
                            if (rideOfferResult.isCurrentRide()){
                                updateCurrentRide(rideOfferResult.getRide());
                                Log.d(TAG, "Updated Current Ride");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    });
                }
            }
        });
    }

    private void setRideOffer(){

        //mRideOfferInfo.setGoogleDirection(mGoogleDirection);
        mRideOfferInfo.setRide(mBasicRide);
        mBasicRide.setStartTime(mStartTimeCalendar.getTime());
        RidePoint startRidePoint = new RidePoint();
        Point startPoint = new Point(mToLatLng.longitude, mToLatLng.latitude);
        startRidePoint.setPoint(startPoint);
        mBasicRide.setStartPoint(startRidePoint);
        RidePoint endRidePoint = new RidePoint();
        Point endPoint = new Point(mFromLatLng.longitude, mFromLatLng.latitude);
        endRidePoint.setPoint(endPoint);
        mBasicRide.setEndPoint(endRidePoint);
        TrustNetwork trustNetwork = new TrustNetwork();
        mBasicRide.setTrustNetwork(trustNetwork);
        if (mAllSelected) {
            TrustCategory trustCategory = new TrustCategory();
            trustCategory.setName(TrustCategoryName.Anonymous);
            trustNetwork.getTrustCategories().add(trustCategory);
        }
        if (mGroupsSelected) {
            TrustCategory trustCategory = new TrustCategory();
            trustCategory.setName(TrustCategoryName.Groups);
            trustNetwork.getTrustCategories().add(trustCategory);
        }
        if (mFriendsSelected) {
            TrustCategory trustCategory = new TrustCategory();
            trustCategory.setName(TrustCategoryName.Friends);
            trustNetwork.getTrustCategories().add(trustCategory);
        }
        mBasicRide.setDriver(mUser);
        if (mRidesOptionUpdated){
            mBasicRide.setSeatOffered(mUpdatedRidesOption.getSeatOffered());
            mBasicRide.setLuggageCapacityOffered(mUpdatedRidesOption.getLuggageCapacityOffered());
            for (Vehicle vehicle : mUser.getVehicles()){
                if (vehicle.getRegistrationNumber().equals(mVehicleRegistrationNumber)){
                    mBasicRide.setVehicle(vehicle);
                    break;
                }
            }
        } else {
            mBasicRide.setSeatOffered(mUser.getPreference().getSeatOffered());
            mBasicRide.setLuggageCapacityOffered(mUser.getPreference().getLuggageCapacityOffered());
            List<Vehicle> vehicles = (List<Vehicle>) mUser.getVehicles();
            mBasicRide.setVehicle(vehicles.get(0));
        }
    }

    private void setRideRequest(){

    }

    @Override
    public void onResume() {
        super.onResume();
        //Its important to set Title here else while loading fragment from backstack, title would not change
        if (mRideType.equals(RideType.OfferRide)){
            getActivity().setTitle(OFFER_RIDE_TITLE);
        } else {
            getActivity().setTitle(REQUEST_RIDE_TITLE);
        }
        mUser = getUser();
        Log.d(TAG,"User Name is:"+mUser.getFirstName());
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
    }

    private void setTrustCategoryViews(View view) {
        mAllImageView = view.findViewById(R.id.trust_category_all_image);
        mAllTextView = view.findViewById(R.id.trust_category_all_text);
        mGroupsImageView = view.findViewById(R.id.trust_category_groups_image);
        mGroupsTextView = view.findViewById(R.id.trust_category_groups_text);
        mFriendsImageView = view.findViewById(R.id.trust_category_friends_image);
        mFriendsTextView = view.findViewById(R.id.trust_category_friends_text);

        mSelectedColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        mDefaultTextColor = mAllTextView.getTextColors().getDefaultColor();
        mDefaultImageTint = mAllImageView.getColorFilter();
        Log.d(TAG,"Text Default color:"+ mDefaultTextColor+":Image Default Tint:"+mAllImageView.getColorFilter());
    }

    private void setTrustCategoryOnClickListener(View view) {
        mAllImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked on the All Image View: Selected Status:" + mAllSelected);
                mAllSelected = !mAllSelected;
                //If all is selected then by default Groups and Friends is already included, so no need to select them individually
                if (mAllSelected){
                    mFriendsSelected = false;
                    mGroupsSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
        mGroupsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked on the Groups Image View: Selected Status:" + mGroupsSelected);
                mGroupsSelected = !mGroupsSelected;
                if (mGroupsSelected){
                    mAllSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
        mFriendsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked on the Friends Image View: Selected Status:" + mFriendsImageView);
                mFriendsSelected = !mFriendsSelected;
                if (mFriendsSelected){
                    mAllSelected = false;
                }
                updateTrustCategoryItemsColor();
            }
        });
    }

    private void updateTrustCategoryItemsColor(){

        if (mAllSelected){
            mAllImageView.setColorFilter(mSelectedColor);
            mAllTextView.setTextColor(mSelectedColor);
        } else {
            mAllImageView.setColorFilter(mDefaultImageTint);
            mAllTextView.setTextColor(mDefaultTextColor);

        }
        if (mGroupsSelected){
            mGroupsImageView.setColorFilter(mSelectedColor);
            mGroupsTextView.setTextColor(mSelectedColor);
        } else {
            mGroupsImageView.setColorFilter(mDefaultImageTint);
            mGroupsTextView.setTextColor(mDefaultTextColor);
        }
        if (mFriendsSelected){
            mFriendsImageView.setColorFilter(mSelectedColor);
            mFriendsTextView.setTextColor(mSelectedColor);
        } else {
            mFriendsImageView.setColorFilter(mDefaultImageTint);
            mFriendsTextView.setTextColor(mDefaultTextColor);
        }


    }

    private void setDateTimeOnClickListener() {
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = DatePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"datePicker" );
            }
        });

        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = TimePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"timePicker" );
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
                Place fromPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "From Place: " + fromPlace.getName());
                mFromAddressTextView.setText(fromPlace.getName());
                mFromLatLng = fromPlace.getLatLng();
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
                Place toPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "To Place: " + toPlace.getName());
                mToAddressTextView.setText(toPlace.getName());
                mToLatLng = toPlace.getLatLng();
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
            if (mFromLatLng!=null) {
                latLngBounds = getCircleBounds(mFromLatLng,500);
                latLngBoundsBuilder.include(latLngBounds.northeast);
                latLngBoundsBuilder.include(latLngBounds.southwest);
            }
            if (mToLatLng!=null) {
                latLngBounds = getCircleBounds(mToLatLng,500);
                latLngBoundsBuilder.include(latLngBounds.northeast);
                latLngBoundsBuilder.include(latLngBounds.southwest);
            }
        }
        latLngBounds = latLngBoundsBuilder.build();
        return latLngBounds;
    }

    private LatLngBounds getCircleBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCreateRideFragmentInteraction(uri);
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

    @Override
    public void onSetCurrentLocationOnMap(LatLng latLng) {
        Log.d(TAG,"Recieved Callback with LatLng:"+latLng.latitude+","+latLng.latitude);
        mFromLatLng = latLng;
        mFromAddressTextView.setText(Constant.CURRENT_LOCATION_TEXT);
        //This will get called whenever map is reloaded and camera needs to be moved accordingly
        //Otherwise what would happen on backpress, camera move would not happen for all markers on the map
        //as it would assume there is only one marked which is current location marker and move camera accordinlgy
        //as per the initial fragment load logic
        moveCamera();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG,"Recieved callback. Value of HH:MM-"+hourOfDay+":"+minute);
        String timeIn12HrFormat = getTimeIn12HrFormat(hourOfDay, minute);
        mTimeTextView.setText(timeIn12HrFormat);
        mStartTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mStartTimeCalendar.set(Calendar.MINUTE, minute);
        Log.d(TAG,"Selected Date:"+mStartTimeCalendar.getTime());
        Log.d(TAG,"Current Date:"+Calendar.getInstance().getTime());
        Calendar currentTime = Calendar.getInstance();
        //This will subtract 5 minutes to the current time for comparision of selected time vs current time
        currentTime.add(Calendar.MINUTE,-BUFFER_TIME_IN_MINUTE);
        if (mStartTimeCalendar.getTime().before(currentTime.getTime())){
            Toast.makeText(getActivity(),"Start Time can't be in the past",Toast.LENGTH_SHORT).show();
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
        String formattedDate = getFormattedDateString(date);
        mDateTextView.setText(formattedDate);
        mStartTimeCalendar.set(year,month,day);
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
        void onCreateRideFragmentInteraction(Uri uri);
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

    public void updateRidesOption(Preference ridesOption, String vehicleRegistrationNumber){
        Log.d(TAG, "Ride Option has been updated");
        mRidesOptionUpdated = true;
        mUpdatedRidesOption = ridesOption;
        if (mRideType.equals(RideType.OfferRide)){
            mVehicleRegistrationNumber = vehicleRegistrationNumber;
        }
        Log.d(TAG,"Updated Value is:"+new Gson().toJson(ridesOption));
    }
}
