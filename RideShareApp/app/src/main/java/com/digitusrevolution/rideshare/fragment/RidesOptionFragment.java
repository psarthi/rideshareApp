package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RidesOptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RidesOptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesOptionFragment extends BaseFragment {

    public static final String TAG = RidesOptionFragment.class.getName();
    public static final String OFFER_RIDE_OPTION_TITLE = "Offer Ride Option";
    public static final String REQUEST_RIDE_OPTION_TITLE = "Request Ride Option";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_PARAM2 = "data";

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private String mData;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private Spinner mVehicleSpinner;
    private Preference mRidesOption;
    private TextView mSeatTextView;
    private TextView mLuggageTextView;
    private Spinner mVehicleCategorySpinner;
    private Spinner mVehicleSubCategorySpinner;
    private SeekBar mPickupTimeVariationSeekBar;
    private SeekBar mPickupPointVariationSeekBar;
    private SeekBar mDropPointVariationSeekBar;
    private TextView mPickupTimeVariationProgressTextView;
    private TextView mPickupPointVariationProgressTextView;
    private TextView mDropPointVariationProgressTextView;

    public RidesOptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data  Data in Json format
     * @return A new instance of fragment RidesOptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesOptionFragment newInstance(RideType rideType, String data) {
        RidesOptionFragment fragment = new RidesOptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        args.putString(ARG_PARAM2, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
            mData = getArguments().getString(ARG_PARAM2);
        }
        mUser = getUser();
        //This will default preference of user which will be the default option for the ride
        mRidesOption = mUser.getPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        Log.d(TAG,"RideType:"+mRideType);
        if (mRideType.equals(RideType.OfferRide)){
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_offer_ride_option, container, false);
            setOfferRideView(view);

        } else {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_ride_request_option, container, false);
            setRideRequestView(view);
        }
        //This will disable trust category layout in Rides Option for this fragment
        view.findViewById(R.id.trust_category_layout).setVisibility(View.GONE);

        setButtonsOnClickListener(view);
        setInitialValue();

        return view;
    }

    private void setRideRequestView(View view) {
        View seatLuggageSingleRowLayout = view.findViewById(R.id.seat_luggage_single_row_layout);
        mSeatTextView = seatLuggageSingleRowLayout.findViewById(R.id.seat_count_text);
        mLuggageTextView = seatLuggageSingleRowLayout.findViewById(R.id.luggage_count_text);

        mVehicleCategorySpinner = view.findViewById(R.id.vehicle_category_spinner);
        mVehicleSubCategorySpinner = view.findViewById(R.id.vehicle_sub_category_spinner);
        mPickupTimeVariationSeekBar = view.findViewById(R.id.rides_option_pickup_time_variation_seekBar);
        mPickupPointVariationSeekBar = view.findViewById(R.id.rides_option_pickup_distance_variation_seekBar);
        mDropPointVariationSeekBar = view.findViewById(R.id.rides_option_drop_distance_variation_seekBar);

        mPickupTimeVariationProgressTextView = view.findViewById(R.id.rides_option_pickup_time_variation_seekBar_progress_text);
        mPickupPointVariationProgressTextView = view.findViewById(R.id.rides_option_pickup_distance_variation_seekBar_progress_text);
        mDropPointVariationProgressTextView = view.findViewById(R.id.rides_option_drop_distance_variation_seekBar_progress_text);

        //This will populate the vehicle category and sub category drop down
        setVehicleCategoriesSpinner(mVehicleCategorySpinner, mVehicleSubCategorySpinner);
        setSeekBarsChangeListener();
    }

    private void setOfferRideView(View view) {
        //This is not same for Ride Request Option as there we are using single line seat luggage
        View seatLuggageView = view.findViewById(R.id.seat_luggage_layout);
        mSeatTextView = seatLuggageView.findViewById(R.id.seat_count_text);
        mLuggageTextView = seatLuggageView.findViewById(R.id.luggage_count_text);

        mVehicleSpinner = view.findViewById(R.id.offer_ride_option_vehicle_name_spinner);
        ArrayList<String> vehicleNameList = new ArrayList<>();
        for (Vehicle vehicle : mUser.getVehicles()){
            vehicleNameList.add(vehicle.getRegistrationNumber());
        }
        //This will make Vehicle List available or unavailable
        if (vehicleNameList.size() > 0){
            populateSpinner(vehicleNameList, mVehicleSpinner);
        } else {
            view.findViewById(R.id.offer_ride_option_vehicle_name_layout).setVisibility(View.GONE);
        }
    }

    private void setInitialValue(){

        if (mRideType.equals(RideType.OfferRide)){
            mSeatTextView.setText(String.valueOf(mRidesOption.getSeatOffered()));
            mLuggageTextView.setText(String.valueOf(mRidesOption.getLuggageCapacityOffered()));
        } else {
            mSeatTextView.setText(String.valueOf(mRidesOption.getSeatRequired()));
            mLuggageTextView.setText(String.valueOf(mRidesOption.getLuggageCapacityRequired()));

            //TODO Set Spinner Selection as well as per the preference value

            //IMP - Note the sequence of setMax and Progress. First you should set the value of Max and then progress,
            //otherwise progress value gets changed if the sequence is not correct
            mPickupPointVariationSeekBar.setMax(Constant.PICKUP_POINT_DISTANCE_MAX_VALUE);
            mPickupPointVariationSeekBar.setProgress(mRidesOption.getPickupPointVariation());

            mDropPointVariationSeekBar.setMax(Constant.DROP_POINT_DISTANCE_MAX_VALUE);
            mDropPointVariationSeekBar.setProgress(mRidesOption.getDropPointVariation());

            //This will get minute value from LocalTime of 00:30 format
            int pickupTimeVariation = Integer.parseInt(mRidesOption.getPickupTimeVariation().split(":")[1]);

            mPickupTimeVariationSeekBar.setMax(Constant.PICKUP_TIME_MAX_VALUE);
            mPickupTimeVariationSeekBar.setProgress(pickupTimeVariation);

            Log.d(TAG, "Pickup Time Seekbar value - Current, Max:"+mPickupTimeVariationSeekBar.getProgress() +","+mPickupTimeVariationSeekBar.getMax());
            Log.d(TAG, "Pickup Seekbar value - Current, Max:"+mPickupPointVariationSeekBar.getProgress() +","+mPickupPointVariationSeekBar.getMax());
            Log.d(TAG, "Drop Seekbar value - Current, Max:"+mDropPointVariationSeekBar.getProgress() +","+mDropPointVariationSeekBar.getMax());
        }
    }

    private void setSeekBarsChangeListener(){
        mPickupTimeVariationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mPickupTimeVariationProgressTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPickupPointVariationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPickupPointVariationProgressTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDropPointVariationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDropPointVariationProgressTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setButtonsOnClickListener(View view) {
        View buttonLayoutView = view.findViewById(R.id.button_layout);

        buttonLayoutView.findViewById(R.id.rides_option_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This will pop all fragments from the top till CreateRidesFragment. Reason for not doing just pop as this can be called from multiple places
                // e.g. from AddVehicle as well as CreateRides. In case of AddVehicle, if we do pop then it will go back to AddVehicle which is wrong
                getActivity().getSupportFragmentManager().popBackStack(CreateRidesFragment.TAG, 0);
            }
        });

        buttonLayoutView.findViewById(R.id.rides_option_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Rides Option Saved");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //((HomePageActivity)getActivity()).showBackButton(true);
        if (mRideType.equals(RideType.OfferRide)){
            getActivity().setTitle(OFFER_RIDE_OPTION_TITLE);
        } else {
            getActivity().setTitle(REQUEST_RIDE_OPTION_TITLE);
        }
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPreferenceFragmentInteraction(uri);
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
        void onPreferenceFragmentInteraction(Uri uri);
    }

}
