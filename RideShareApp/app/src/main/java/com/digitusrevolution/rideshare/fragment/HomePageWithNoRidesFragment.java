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
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomePageWithNoRidesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomePageWithNoRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageWithNoRidesFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = HomePageWithNoRidesFragment.class.getName();
    public static final String TITLE = "Ride Share";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView mTextView;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public HomePageWithNoRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Fragment Title
     * @return A new instance of fragment HomePageWithNoRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageWithNoRidesFragment newInstance(String title, String param2) {
        HomePageWithNoRidesFragment fragment = new HomePageWithNoRidesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page_with_no_rides, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.home_page_with_no_rides_map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            });
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
        void onHomePageWithNoRidesFragmentInteraction(String data);
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


}
