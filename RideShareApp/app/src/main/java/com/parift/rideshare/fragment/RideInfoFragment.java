package com.parift.rideshare.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.CoTravellerAdapter;
import com.parift.rideshare.component.MapComp;
import com.parift.rideshare.component.RideComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.domain.core.RideStatus;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

import java.util.Calendar;
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
        OnMapReadyCallback, RideComp.RideCompListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE = "ride";
    private static final String TITLE = "Offered Ride Info";

    public static final String TAG = RideInfoFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mRideData;

    private OnFragmentInteractionListener mListener;
    private FullRide mRide;
    private LinearLayout mCoTravellerLinearLayout;
    private GoogleMap mMap;
    private MapComp mMapComp;
    private View mMapView;
    private boolean mMapLoaded;
    private CommonUtil mCommonUtil;

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
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_info, container, false);
        mMapView = view.findViewById(R.id.ride_info_map);
        mCoTravellerLinearLayout = view.findViewById(R.id.ride_info_co_traveller_layout);

        //VERY VERY IMP - Rating bar value is getting overwritten in onResume function,
        // so moved the setRideInfoView in onResume post calling its super.onResume method
        // Don't move it here else rating bar would not show up the rating properly
        //No clarity on the reason, needs to be analyzed later

        return view;
    }

    private void setRideInfoView(View view) {
        Logger.debug(TAG, "Setting Ride Info View");

        //This should be initialized here so that it can be reloaded on refresh
        RideComp rideComp = new RideComp(this, mRide);
        rideComp.setBasicRideLayout(view);

        //This will ensure on refresh it clears all earlier views otherwise old views will remain there
        mCoTravellerLinearLayout.removeAllViews();
        //Note - We are using the same adapter which is also applicable for listview,
        //but this can be used for our own prupose and in this case, its the best suitable option
        ArrayAdapter<FullRideRequest> coTravellerAdapter = new CoTravellerAdapter(
                this, (List<FullRideRequest>) mRide.getAcceptedRideRequests(), mRide);

        //You can also get this by passing the container from onCreateView but in that case,
        //for refreshing the view again i need to store this container somewhere, so instead
        //i am getting the container directly here
        //Note - Can't call getView as view has not been set as of now and this function is in between the onCreateView call
        ViewGroup container = ((ViewGroup) view.getParent());
        for (int i=0; i<mRide.getAcceptedRideRequests().size(); i++){
            View coTravellerView = coTravellerAdapter.getView(i, null, container);
            mCoTravellerLinearLayout.addView(coTravellerView);
        }

        //This will adjust height of map
        if (mRide.getAcceptedRideRequests().size()==0){
            ViewGroup.LayoutParams layoutParams = mMapView.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mMapView.setLayoutParams(layoutParams);
            //This will ensure this msg would only show up if ride has not expired and there is no matched partner
            //Note - Not equal to condition
            if (mRide.getStatus().equals(RideStatus.Planned)
                    && !mRide.getEndTime().before(Calendar.getInstance().getTime())){
                Toast.makeText(mActivity, R.string.no_ride_partner_found_msg, Toast.LENGTH_LONG).show();
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.ride_info_map);
        mapFragment.getMapAsync(this);

        for (int i=0; i<mCoTravellerLinearLayout.getChildCount();i++){
            Logger.debug(TAG, "Rating is (Set Ride Info View):"+Float.toString(((RatingBar)
                    mCoTravellerLinearLayout.getChildAt(i).findViewById(R.id.co_traveller_rating_bar)).getRating()));
        }
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
                mMapComp.setRideOnMap(mRide);
            }
        });

        //Reason behind this as on refresh of view, getViewTreeObserver would not get called
        //as its called only first time when map is loaded
        if (mMapLoaded) {
            Logger.debug(TAG, "Map already loaded");
            mMapComp.setRideOnMap(mRide);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //Commenting this as post ride confirmation, this takes you back to create ride screen which looks awkward
        //This is fine for Rides List screen but not good for Create Rides post confirmation screen
        //Note - Working fine now as we have removed createRide fragment post ride creation from back stack
        ((HomePageActivity)getActivity()).showBackButton(true);

        getActivity().setTitle(TITLE);
        Logger.debug(TAG,"Inside OnResume");

        //VERY VERY IMP - Rating bar value is getting overwritten when super.onResume function is called,
        // so moved the setRideInfoView in onResume post calling its super.onResume method
        // Don't move it to onCreate else rating bar would not show up the rating properly
        //No clarity on the reason, needs to be analyzed later

        Logger.debug(TAG, "Setting Ride Info View from inside onResume");
        setRideInfoView(getView());

        for (int i=0; i<mCoTravellerLinearLayout.getChildCount();i++){
            Logger.debug(TAG, "Rating is (Inside OnResume):"+Float.toString(((RatingBar)
                    mCoTravellerLinearLayout.getChildAt(i).findViewById(R.id.co_traveller_rating_bar)).getRating()));
        }

        showBackStackDetails();
        showChildFragmentDetails();
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
    public void onDestroyView() {
        super.onDestroyView();
        Logger.debug(TAG, "Inside Destroy View");
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
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    public void onPositiveClickOfCancelCoTravellerFragment(DialogFragment dialogFragment) {
        RatingBar ratingBar = (RatingBar) dialogFragment.getDialog().findViewById(R.id.rating_bar);
        Logger.debug(TAG, "CoTraveller Rejected");
        Logger.debug(TAG, "Rating"+ratingBar.getRating());
    }

    public void onNegativeClickOfCancelCoTravellerFragment(DialogFragment dialogFragment) {

    }

    @Override
    public void onRideRefresh(FullRide ride) {
        Logger.debug(TAG, "Recieved Callback for Refresh for Ride Id with status:"
                +ride.getId()+":"+ride.getStatus());

        mRide = ride;
        setRideInfoView(getView());
    }

    public long getRideId(){
        return mRide.getId();
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
        void onRideInfoFragmentInteraction(String data);
    }
}
