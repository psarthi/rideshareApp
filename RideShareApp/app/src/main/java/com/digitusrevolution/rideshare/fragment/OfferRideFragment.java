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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferRideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferRideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferRideFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public static final String TAG = OfferRideFragment.class.getName();
    public static final String TITLE = "Offer Ride";
    int PLACE_FROM_ADDRESS_REQUEST_CODE = 1;
    int PLACE_TO_ADDRESS_REQUEST_CODE = 2;
    private TextView mFromAddressTextView;
    private TextView mToAddressTextView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLngBounds mLatLngBounds;
    private LatLngBounds.Builder mLatLngBoundsBuilder;
    private Place mFromPlace;
    private Place mToPlace;

    public OfferRideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferRideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferRideFragment newInstance(String title, String param2) {
        OfferRideFragment fragment = new OfferRideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setTitle(getArguments().getString(ARG_PARAM1));
        mLatLngBoundsBuilder = new LatLngBounds.Builder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        mFromAddressTextView = view.findViewById(R.id.offer_ride_from_address_text);
        mToAddressTextView = view.findViewById(R.id.offer_ride_to_address_text);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.offer_ride_map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_FROM_ADDRESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mFromPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "From Place: " + mFromPlace.getName());
                mFromAddressTextView.setText(mFromPlace.getName());
                setAddressOnMap(mFromPlace.getLatLng());
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
                setAddressOnMap(mToPlace.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void setAddressOnMap(LatLng latLng) {
        mLatLngBoundsBuilder.include(latLng);
        mLatLngBounds = mLatLngBoundsBuilder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        //This is here for experience purpose, we can use either heigth or width whichever make sense
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * Constant.LAT_LNG_PADDING_PERCENT); // offset from edges of the map 10% of screen
        Log.d(TAG, "Width Pixel:"+width+",Heigth Pixel:"+height+",Padding Pixel:"+padding);

        mMap.addMarker(new MarkerOptions().position(latLng));
        if (mToAddressTextView.getText().equals(getResources().getString(R.string.to_address_label))){
            Log.d(TAG, "To Field is empty, so using location camera zoom");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mLatLngBounds,padding));
            Log.d(TAG, "To Field exist, so using latlng bounds camera zoom");
        }
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

    private void setCurrentLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
                        // Add a marker in User Current Location, and move the camera.
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
                        mFromAddressTextView.setText("Current Location");
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
        void onOfferRideFragmentInteraction(Uri uri);
    }

}
