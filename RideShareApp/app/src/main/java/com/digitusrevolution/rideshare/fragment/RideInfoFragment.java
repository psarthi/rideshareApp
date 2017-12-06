package com.digitusrevolution.rideshare.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.CoTravellerAdapter;
import com.digitusrevolution.rideshare.component.MapComp;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.dialog.DropCoTravellerFragment;
import com.digitusrevolution.rideshare.dialog.RejectCoTravellerFragment;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRidePassenger;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

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
    private GoogleMap mMap;
    private MapComp mMapComp;
    private View mMapView;

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
        RideComp rideComp = new RideComp(this, mRide);
        rideComp.setBasicRideLayout(view);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.ride_info_map);
        mapFragment.getMapAsync(this);
        mMapView = view.findViewById(R.id.ride_info_map);

        mCoTravellerLinearLayout = view.findViewById(R.id.ride_info_co_traveller_layout);
        //Note - We are using the same adapter which is also applicable for listview,
        //but this can be used for our own prupose and in this case, its the best suitable option
        ArrayAdapter<BasicRideRequest> coTravellerAdapter = new CoTravellerAdapter(
                this, (List<BasicRideRequest>) mRide.getAcceptedRideRequests(), (List<BasicRidePassenger>) mRide.getRidePassengers());
        for (int i=0; i<coTravellerAdapter.getCount(); i++){
            View coTravellerView = coTravellerAdapter.getView(i, null, container);
            mCoTravellerLinearLayout.addView(coTravellerView);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMapComp = new MapComp(this, googleMap);
        //This will set standard padding for the map
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
                mMapComp.setRideOnMap(mRide);
            }
        });

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
