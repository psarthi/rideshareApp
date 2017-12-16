package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.MapComp;
import com.digitusrevolution.rideshare.component.RideRequestComp;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequestStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RideRequestInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RideRequestInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideRequestInfoFragment extends BaseFragment implements OnMapReadyCallback,
        RideRequestComp.RideRequestCompListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_REQUEST = "rideRequest";
    public static final String TAG = RideRequestInfoFragment.class.getName();
    private static final String TITLE = "Requested Ride Info";

    // TODO: Rename and change types of parameters
    private String mRideRequestData;

    private OnFragmentInteractionListener mListener;
    private FullRideRequest mRideRequest;
    private GoogleMap mMap;
    private MapComp mMapComp;
    private View mMapView;
    private RideRequestComp mRideRequestComp;
    private boolean mMapLoaded;


    public RideRequestInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideRequest data in Json format
     * @return A new instance of fragment RideRequestInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RideRequestInfoFragment newInstance(String rideRequest) {
        RideRequestInfoFragment fragment = new RideRequestInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_REQUEST, rideRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideRequestData = getArguments().getString(ARG_RIDE_REQUEST);
        }
        mRideRequest = new Gson().fromJson(mRideRequestData, FullRideRequest.class);
        mRideRequestComp = new RideRequestComp(this, mRideRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_request_info, container, false);
        mMapView = view.findViewById(R.id.ride_request_info_map);

        setRideRequestInfoView(view);

        return view;
    }

    private void setRideRequestInfoView(View view) {
        Log.d(TAG, "Setting Ride Request Info View");

        mRideRequestComp.setRideRequestBasicLayout(view);

        if (mRideRequest.getAcceptedRide()!=null){
            mRideRequestComp.setRideOwnerLayout(view);
        } else {
            view.findViewById(R.id.ride_owner_layout).setVisibility(View.GONE);
        }

        //This will adjust height of map
        if (mRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled)){
            ViewGroup.LayoutParams layoutParams = mMapView.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mMapView.setLayoutParams(layoutParams);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.ride_request_info_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRideRequestInfoFragmentInteraction(uri);
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
        mMap.clear();
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
                mMapLoaded = true;
                mMapComp.setRideRequestOnMap(mRideRequest);
            }
        });

        //Reason behind this as on refresh of view, getViewTreeObserver would not get called
        //as its called only first time when map is loaded
        if (mMapLoaded){
            Log.d(TAG, "Map already loaded");
            mMapComp.setRideRequestOnMap(mRideRequest);
        }

    }

    public int getRideRequestId(){
        return mRideRequest.getId();
    }

    @Override
    public void onRideRequestRefresh(FullRideRequest rideRequest) {
        Log.d(TAG, "Recieved Callback for Refresh for Ride Request Id with status:"
                +rideRequest.getId()+":"+rideRequest.getStatus());
        mRideRequest = rideRequest;
        setRideRequestInfoView(getView());
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
        void onRideRequestInfoFragmentInteraction(Uri uri);
    }
}
