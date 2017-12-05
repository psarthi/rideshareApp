package com.digitusrevolution.rideshare.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.CoTravellerAdapter;
import com.digitusrevolution.rideshare.dialog.DropCoTravellerFragment;
import com.digitusrevolution.rideshare.dialog.RejectCoTravellerFragment;
import com.digitusrevolution.rideshare.model.ride.domain.RidePoint;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRidePassenger;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RideInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RideInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideInfoFragment extends BaseFragment implements
        DropCoTravellerFragment.DropCoTravellerFragmentListener,
        RejectCoTravellerFragment.RejectCoTravellerFragmentListener,
        OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE = "ride";

    public static final String TAG = RideInfoFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mRideData;

    private OnFragmentInteractionListener mListener;
    private FullRide mRide;
    private LinearLayout mCoTravellerLinearLayout;
    Button mRideCancelButton;
    Button mRideStartButton;
    Button mRideEndButton;
    LinearLayout mRideButtonLinearLayout;

    public RideInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ride data in json format
     * @return A new instance of fragment RideInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RideInfoFragment newInstance(String ride) {
        RideInfoFragment fragment = new RideInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideData = getArguments().getString(ARG_RIDE);
        }
        mRide = new Gson().fromJson(mRideData, FullRide.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_info, container, false);
        setRideView(view);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.ride_info_map);
        mapFragment.getMapAsync(this);

        mCoTravellerLinearLayout = view.findViewById(R.id.ride_info_co_traveller_layout);
        //Note - We are using the same adapter which is also applicable for listview,
        //but this can be used for our own prupose and in this case, its the best suitable option
        ArrayAdapter<BasicRideRequest> coTravellerAdapter = new CoTravellerAdapter(getActivity(), (List<BasicRideRequest>) mRide.getAcceptedRideRequests());
        for (int i=0; i<coTravellerAdapter.getCount(); i++){
            View coTravellerView = null;
            coTravellerView = coTravellerAdapter.getView(i, coTravellerView, container);
            coTravellerView.findViewById(R.id.co_traveller_drop_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DropCoTravellerFragment dropCoTravellerFragment = DropCoTravellerFragment.newInstance(
                            RideInfoFragment.this, "Partha", "1234");
                    dropCoTravellerFragment.show(getActivity().getFragmentManager(), DropCoTravellerFragment.TAG);
                }
            });
            coTravellerView.findViewById(R.id.co_traveller_reject_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RejectCoTravellerFragment rejectCoTravellerFragment = RejectCoTravellerFragment.newInstance(
                            RideInfoFragment.this, "Partha");
                    rejectCoTravellerFragment.show(getActivity().getFragmentManager(), RejectCoTravellerFragment.TAG);
                }
            });

            mCoTravellerLinearLayout.addView(coTravellerView);
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //This will set standard padding for the map
        setPadding(true);
        List<LatLng> latLngs = new ArrayList<>();

        LatLng fromLatLng = new LatLng(mRide.getStartPoint().getPoint().getLatitude(), mRide.getStartPoint().getPoint().getLongitude());
        LatLng toLatLng = new LatLng(mRide.getEndPoint().getPoint().getLatitude(), mRide.getEndPoint().getPoint().getLongitude());
        latLngs.add(fromLatLng);
        latLngs.add(toLatLng);

        //This will add marker for start and end point
        mMap.addMarker(new MarkerOptions().position(fromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_marker)));
        mMap.addMarker(new MarkerOptions().position(toLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_marker)));

        //This will draw polyline for route
        Collection<RidePoint> ridePoints = mRide.getRoute().getRidePoints();
        List<LatLng> routeLatLngs = new ArrayList<>();
        for (RidePoint ridePoint: ridePoints){
            routeLatLngs.add(new LatLng(ridePoint.getPoint().getLatitude(), ridePoint.getPoint().getLongitude()));
        }
        latLngs.addAll(routeLatLngs);
        mMap.addPolyline(new PolylineOptions().addAll(routeLatLngs));

        //This will add markers for all pickup points
        Collection<BasicRideRequest> acceptedRideRequests = mRide.getAcceptedRideRequests();
        for (BasicRideRequest rideRequest: acceptedRideRequests){
            LatLng pickupPointLatLng = new LatLng(rideRequest.getRidePickupPoint().getPoint().getLatitude(),
                    rideRequest.getRidePickupPoint().getPoint().getLongitude());
            latLngs.add(pickupPointLatLng);
            mMap.addMarker(new MarkerOptions().position(pickupPointLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_person_marker)));
        }

        LatLngBounds latLngBounds = getBounds(latLngs);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));
    }

    private void setRideView(View view){

        View basic_ride_layout = view.findViewById(R.id.basic_ride_layout);
        TextView rideIdTextView = basic_ride_layout.findViewById(R.id.ride_id_text);
        String rideIdText = getResources().getString(R.string.ride_offer_id_text) + mRide.getId();
        rideIdTextView.setText(rideIdText);
        TextView rideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        rideStatusTextView.setText(mRide.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(getFormattedDateTimeString(mRide.getStartTime()));
        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(mRide.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(mRide.getEndPointAddress());

        mRideButtonLinearLayout = basic_ride_layout.findViewById(R.id.ride_buttons_layout);
        mRideCancelButton = basic_ride_layout.findViewById(R.id.ride_cancel_button);
        mRideStartButton = basic_ride_layout.findViewById(R.id.ride_start_button);
        mRideEndButton = basic_ride_layout.findViewById(R.id.ride_end_button);
        //This will set the visibility of ride buttons
        setRideButtonsVisibility();
        //This will set listeners for ride buttons
        setRideButtonsOnClickListener(basic_ride_layout);

    }

    private void setRideButtonsOnClickListener(View basic_ride_layout) {

        mRideCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Cancelled");
                setRideButtonsVisibility();
            }
        });

        mRideStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Started");
                setRideButtonsVisibility();
            }
        });

        mRideEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ride Ended");
                setRideButtonsVisibility();
            }
        });
    }

    private void setRideButtonsVisibility(){
        if (mRide.getStatus().equals(RideStatus.Planned)){
            mRideEndButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Started)){
            mRideCancelButton.setVisibility(View.GONE);
            mRideStartButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Finished) || mRide.getStatus().equals(RideStatus.Cancelled)){
            mRideButtonLinearLayout.setVisibility(View.GONE);
        }
    }

    private void setCoTraveller(View view){

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
        showChildFragmentDetails();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRideInfoFragmentInteraction(uri);
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Inside Destroy View");
        showChildFragmentDetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ride_info_map);
        if (mapFragment != null) {
            // This needs to be called if you are facing duplicate map id on fragment reload
            // getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPositiveClickOfDropCoTravellerFragment(DialogFragment dialogFragment) {
        //IMP - Don't use getView else it will throw nullpointer. Right option is to use getDialog
        RadioButton paidRideButton = (RadioButton) dialogFragment.getDialog().findViewById(R.id.paid_ride_radio_button);
        RadioButton freeRideButton = (RadioButton) dialogFragment.getDialog().findViewById(R.id.free_ride_radio_button);
        Log.d(TAG, "CoTraveller Dropped");
        Log.d(TAG, "Paid Ride Status:"+paidRideButton.isChecked());
        Log.d(TAG, "Free Ride Status:"+freeRideButton.isChecked());

    }

    @Override
    public void onNegativeClickOfDropCoTravellerFragment(DialogFragment dialogFragment) {
        Log.d(TAG, "CoTraveller Not Dropped");
    }

    @Override
    public void onPositiveClickOfRejectCoTravellerFragment(DialogFragment dialogFragment) {
        RatingBar ratingBar = (RatingBar) dialogFragment.getDialog().findViewById(R.id.rating_bar);
        Log.d(TAG, "CoTraveller Rejected");
        Log.d(TAG, "Rating"+ratingBar.getRating());
    }

    @Override
    public void onNegativeClickOfRejectCoTravellerFragment(DialogFragment dialogFragment) {

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
        void onRideInfoFragmentInteraction(Uri uri);
    }
}
