package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.component.MapComp;
import com.parift.rideshare.component.RideRequestComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.billing.domain.core.Bill;
import com.parift.rideshare.model.ride.domain.core.RideRequestStatus;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.parift.rideshare.model.ride.dto.RideRequestResult;
import com.parift.rideshare.model.ride.dto.SuggestedMatchedRideInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private static final String ARG_RIDE_REQUEST_RESULT = "rideRequestResult";
    public static final String TAG = RideRequestInfoFragment.class.getName();
    private static final String TITLE = "Requested Ride Info";

    private OnFragmentInteractionListener mListener;
    private FullRideRequest mRideRequest;
    private GoogleMap mMap;
    private MapComp mMapComp;
    private View mMapView;
    private boolean mMapLoaded;
    private boolean mMapFullView;
    private View mBasicRideRequestLayout;
    private View mRideOwnerLayout;
    private CommonUtil mCommonUtil;
    private List<SuggestedMatchedRideInfo> mSuggestedMatchedRideInfos;
    private ViewGroup mContainer;


    public RideRequestInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideRequestResult data in Json format
     * @return A new instance of fragment RideRequestInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RideRequestInfoFragment newInstance(String rideRequestResult) {
        RideRequestInfoFragment fragment = new RideRequestInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_REQUEST_RESULT, rideRequestResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String data = getArguments().getString(ARG_RIDE_REQUEST_RESULT);
            RideRequestResult rideRequestResult = new Gson().fromJson(data, RideRequestResult.class);
            mRideRequest = rideRequestResult.getRideRequest();
            mSuggestedMatchedRideInfos = rideRequestResult.getSuggestedMatchedRideInfos();
        }
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_request_info, container, false);
        mMapView = view.findViewById(R.id.ride_request_info_map);
        mBasicRideRequestLayout = view.findViewById(R.id.basic_ride_request_layout);
        mRideOwnerLayout = view.findViewById(R.id.ride_owner_layout);
        mContainer = container;

        setRideRequestInfoView(view);

        return view;
    }

    private void setRideRequestInfoView(View view) {
        Logger.debug(TAG, "Setting Ride Request Info View");

        //This should be initialized here so that it can be reloaded on refresh
        RideRequestComp rideRequestComp = new RideRequestComp(this, mRideRequest);
        rideRequestComp.setRideRequestBasicLayout(view);
        boolean suggestedRideAvailable = false;

        if (mRideRequest.getAcceptedRide()!=null){
            view.findViewById(R.id.rides_suggestion_msg).setVisibility(View.GONE);
            view.findViewById(R.id.ride_owner_suggestion_layout).setVisibility(View.GONE);

            view.findViewById(R.id.ride_owner_layout).setVisibility(View.VISIBLE);
            rideRequestComp.setRideOwnerLayout(view);
        } else {
            //Applicable for both case so keeping it outside
            view.findViewById(R.id.ride_owner_layout).setVisibility(View.GONE);
            if (mSuggestedMatchedRideInfos!=null && mSuggestedMatchedRideInfos.size()!=0){
                view.findViewById(R.id.rides_suggestion_msg).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ride_owner_suggestion_layout).setVisibility(View.VISIBLE);
                suggestedRideAvailable = true;
                LinearLayout suggestionLayout = view.findViewById(R.id.ride_owner_suggestion_layout);
                for (SuggestedMatchedRideInfo suggestedMatchedRideInfo: mSuggestedMatchedRideInfos){
                    View rideOwnerView = getLayoutInflater().inflate(R.layout.ride_owner_layout, mContainer, false);
                    rideRequestComp.setSuggestedRideOwnerLayout(rideOwnerView, suggestedMatchedRideInfo);
                    suggestionLayout.addView(rideOwnerView);
                }
            } else {
                view.findViewById(R.id.rides_suggestion_msg).setVisibility(View.GONE);
                view.findViewById(R.id.ride_owner_suggestion_layout).setVisibility(View.GONE);
            }
        }

        //This will adjust height of map
        if (!mRideRequest.getStatus().equals(RideRequestStatus.Fulfilled)){
            expandMapLayout();
            //This will ensure toast msg is only shown for unfulfilled and not expired ride request
            //Note - Not equal to condition
            Calendar maxPickupTime = mCommonUtil.getRideRequestMaxPickupTime(mRideRequest);
            if (mRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled)
                    && !maxPickupTime.before(Calendar.getInstance())
                    && !suggestedRideAvailable) {
                Toast.makeText(mActivity, R.string.no_ride_partner_found_msg, Toast.LENGTH_LONG).show();
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.ride_request_info_map);
        mapFragment.getMapAsync(this);

    }

    private void expandMapLayout() {
        ViewGroup.LayoutParams layoutParams = mMapView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mMapView.setLayoutParams(layoutParams);
    }

    private void collapseMapLayout() {
        ViewGroup.LayoutParams layoutParams = mMapView.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.ride_request_info_map_fragment_height);
        mMapView.setLayoutParams(layoutParams);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Commenting this as post ride confirmation, this takes you back to create ride screen which looks awkward
        //This is fine for Rides List screen but not good for Create Rides post confirmation screen
        //Note - Working fine now as we have removed createRide fragment post ride creation from back stack
        ((HomePageActivity)getActivity()).showBackButton(true);
        getActivity().setTitle(TITLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mActivity = (FragmentActivity) context;
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
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMapComp = new MapComp(this, googleMap);
        //This will set standard padding for the map
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
                mMapComp.setRideRequestOnMap(mRideRequest);
            }
        });

        //Reason behind this as on refresh of view, getViewTreeObserver would not get called
        //as its called only first time when map is loaded
        if (mMapLoaded){
            Logger.debug(TAG, "Map already loaded");
            mMapComp.setRideRequestOnMap(mRideRequest);
        }

        /* Disabling this as its not looking well and too much of flickering is happening
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMapFullView){
                    Logger.debug(TAG, "Map Clicked, Current state is Full View");
                    mBasicRideRequestLayout.setVisibility(View.GONE);
                    mRideOwnerLayout.setVisibility(View.GONE);
                    expandMapLayout();
                    mMapFullView=false;
                } else {
                    Logger.debug(TAG, "Map Clicked, Current state is Small View");
                    mBasicRideRequestLayout.setVisibility(View.VISIBLE);
                    mRideOwnerLayout.setVisibility(View.VISIBLE);
                    collapseMapLayout();
                    mMapFullView=true;
                }
            }
        });
        */

    }

    public long getRideRequestId(){
        return mRideRequest.getId();
    }

    @Override
    public void onRideRequestRefresh(FullRideRequest rideRequest) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Request Id with status:"
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
        void onRideRequestInfoFragmentInteraction(String data);
    }

    public void updateBill(Bill bill){
        mRideRequest.setBill(bill);
    }
}
