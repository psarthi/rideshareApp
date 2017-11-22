package com.digitusrevolution.rideshare.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRidesFragment extends BaseFragment implements BaseFragment.BaseFragmentListener,
        TimePickerFragment.TimePickerFragmentListener, DatePickerFragment.DatePickerFragmentListener {

    public static final String TAG = CreateRidesFragment.class.getName();
    public static final String OFFER_RIDE_TITLE = "Offer Ride";
    public static final String REQUEST_RIDE_TITLE = "Request Ride";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // These are the keys which would be used to store and retrieve the data
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_DATA = "data";

    // TODO: Rename and change types of parameters
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
    private List<Marker> mMarkers = new ArrayList<>();
    private BasicRide mBasicRide;
    private BasicRideRequest mBasicRideRequest;
    private TextView mDateTextView;
    private TextView mTimeTextView;

    public CreateRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data  Data in Json format
     * @return A new instance of fragment CreateRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //This will assign this fragment to base fragment for callbacks.
        mBaseFragmentListener = this;
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

        view.findViewById(R.id.create_rides_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = TimePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"timePicker" );
            }
        });

        view.findViewById(R.id.create_rides_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = DatePickerFragment.newInstance(CreateRidesFragment.this);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"datePicker" );
            }
        });

        view.findViewById(R.id.create_rides_trust_network_all_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                Log.d(TAG,"Clicked on the All Image View. Tint:"+ imageView.getColorFilter());
            }
        });

        return view;
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
                addMarker();
                moveCamera(mFromLatLng);
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
                addMarker();
                moveCamera(mToLatLng);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void addMarker(){

        //This will remove old markers so that we have clean slate and we don't end up with old data
        for (Marker marker: mMarkers){
            marker.remove();
        }

        if (mFromLatLng!=null){
            mMarkers.add(mMap.addMarker(new MarkerOptions().position(mFromLatLng)));
        }
        if (mToLatLng!=null){
            mMarkers.add(mMap.addMarker(new MarkerOptions().position(mToLatLng)));
        }
    }

    private void moveCamera(LatLng latLng) {

        if (mFromLatLng == null || mToLatLng == null){
            Log.d(TAG, "To Field is empty, so using location camera zoom");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
        } else {
            moveCameraBasedOnLatLngBounds();
        }
    }

    private void moveCameraBasedOnLatLngBounds(){

        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        latLngBoundsBuilder.include(mFromLatLng);
        latLngBoundsBuilder.include(mToLatLng);
        LatLngBounds latLngBounds = latLngBoundsBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));
        Log.d(TAG, "To Field exist, so using latlng bounds camera zoom");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOfferRideFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseFragmentListener");
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
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG,"Recieved callback. Value of HH:MM-"+hourOfDay+":"+minute);
        mTimeTextView.setText(hourOfDay+":"+minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        Log.d(TAG,"Recieved callback. Value of DD/MM/YY-"+day+"/"+month+"/"+year);
        String formattedDate = day+"/"+month+"/"+year;
        mDateTextView.setText(formattedDate);
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
        void onOfferRideFragmentInteraction(Uri uri);
    }

}
