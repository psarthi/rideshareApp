package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.ThumbnailVehicleAdapter;
import com.digitusrevolution.rideshare.component.CommonComp;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideMode;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.domain.VehicleCategory;
import com.digitusrevolution.rideshare.model.user.domain.VehicleSubCategory;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RidesOptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RidesOptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesOptionFragment extends BaseFragment
        implements CommonComp.onVehicleCategoriesReadyListener,
        CommonComp.onSeatLuggageSelectionListener{

    public static final String TAG = RidesOptionFragment.class.getName();
    public static final String OFFER_RIDE_OPTION_TITLE = "Offer Ride Option";
    public static final String REQUEST_RIDE_OPTION_TITLE = "Request Ride Option";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_RIDES_OPTION = "ridesOption";

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private String mRidesOptionData;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private Preference mRidesOption;
    private Spinner mVehicleCategorySpinner;
    private Spinner mVehicleSubCategorySpinner;
    private SeekBar mPickupTimeVariationSeekBar;
    private SeekBar mPickupPointVariationSeekBar;
    private SeekBar mDropPointVariationSeekBar;
    private TextView mPickupTimeVariationProgressTextView;
    private TextView mPickupPointVariationProgressTextView;
    private TextView mDropPointVariationProgressTextView;
    private RadioButton mPaidRideRadioButton;
    private RadioButton mFreeRideRadioButton;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;
    private CommonComp mCommonComp;
    private List<VehicleCategory> mVehicleCategories;
    private ThumbnailVehicleAdapter mVehicleAdapter;
    private int mSeatCount;
    private int mLuggageCount;

    public RidesOptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param ridesOption  Data in Json format
     * @return A new instance of fragment RidesOptionFragment.
     */
    public static RidesOptionFragment newInstance(RideType rideType, String ridesOption) {
        RidesOptionFragment fragment = new RidesOptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        args.putString(ARG_RIDES_OPTION, ridesOption);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
            mRidesOptionData = getArguments().getString(ARG_RIDES_OPTION);
        }
        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mUser = mCommonUtil.getUser();
        //This will default preference of user which will be the default option for the ride
        //This will only be used when we don't pass the ridesOption while creating the fragement instance
        if (mRidesOptionData==null){
            mRidesOption = mUser.getPreference();
        } else {
            mRidesOption = new Gson().fromJson(mRidesOptionData, Preference.class);
        }
        mCommonComp = new CommonComp(this);
        //This will set this fragment for vehicle categories ready listener callback
        mCommonComp.mOnVehicleCategoriesReadyListener = this;
        mCommonComp.mOnSeatLuggageSelectionListener = this;
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
            setRequestRideView(view);
        }
        //This will disable trust category layout in Rides Option for this fragment
        view.findViewById(R.id.trust_category_layout).setVisibility(View.GONE);


        //Common for both the ride Type - offer ride and request ride
        View ride_mode_layout = view.findViewById(R.id.ride_mode_layout);
        mPaidRideRadioButton = ride_mode_layout.findViewById(R.id.paid_ride_radio_button);
        mFreeRideRadioButton = ride_mode_layout.findViewById(R.id.free_ride_radio_button);

        setButtonsOnClickListener(view);
        setInitialValue();

        return view;
    }

    private void setRequestRideView(View view) {
        View seatLuggageSingleRowLayout = view.findViewById(R.id.seat_luggage_single_row_layout);
        mSeatCount = mRidesOption.getSeatRequired();
        mLuggageCount = mRidesOption.getLuggageCapacityRequired();
        mCommonComp.setSeatPicker(seatLuggageSingleRowLayout, mSeatCount, Constant.MIN_SEAT, Constant.MAX_SEAT);
        mCommonComp.setLuggagePicker(seatLuggageSingleRowLayout,mLuggageCount,Constant.MIN_LUGGAGE,Constant.MAX_LUGGAGE);


        mVehicleCategorySpinner = view.findViewById(R.id.vehicle_category_spinner);
        mVehicleSubCategorySpinner = view.findViewById(R.id.vehicle_sub_category_spinner);
        mPickupTimeVariationSeekBar = view.findViewById(R.id.rides_option_pickup_time_variation_seekBar);
        mPickupPointVariationSeekBar = view.findViewById(R.id.rides_option_pickup_distance_variation_seekBar);
        mDropPointVariationSeekBar = view.findViewById(R.id.rides_option_drop_distance_variation_seekBar);

        mPickupTimeVariationProgressTextView = view.findViewById(R.id.rides_option_pickup_time_variation_seekBar_progress_text);
        mPickupPointVariationProgressTextView = view.findViewById(R.id.rides_option_pickup_distance_variation_seekBar_progress_text);
        mDropPointVariationProgressTextView = view.findViewById(R.id.rides_option_drop_distance_variation_seekBar_progress_text);

        //This will populate the vehicle category and sub category drop down
        mCommonComp.setVehicleCategoriesSpinner(mVehicleCategorySpinner, mVehicleSubCategorySpinner);
        setSeekBarsChangeListener();
    }

    private void setOfferRideView(View view) {
        View seatLuggageView = view.findViewById(R.id.seat_luggage_layout);
        mSeatCount = mRidesOption.getSeatOffered();
        mLuggageCount = mRidesOption.getLuggageCapacityOffered();
        //Max value is just based on max capacity of SUV but this values can be fetched from backend
        mCommonComp.setSeatPicker(seatLuggageView, mSeatCount, Constant.MIN_SEAT, Constant.MAX_SEAT);
        mCommonComp.setLuggagePicker(seatLuggageView, mLuggageCount,Constant.MIN_LUGGAGE,Constant.MAX_LUGGAGE);

        List<Vehicle> vehicleList = new ArrayList<>();
        for (Vehicle vehicle: mUser.getVehicles()){
            vehicleList.add(vehicle);
        }
        //Adding dummy vehicle for drawable.add sign at the end
        vehicleList.add(new Vehicle());

        RecyclerView recyclerView = view.findViewById(R.id.offer_ride_vehicles_list);
        mVehicleAdapter = new ThumbnailVehicleAdapter(this, vehicleList, mRidesOption.getDefaultVehicle().getId());
        setRecyclerView(recyclerView, mVehicleAdapter, LinearLayoutManager.HORIZONTAL);
    }

    private void setInitialValue(){

        //Common for both Ride Type
        if (mRidesOption.getRideMode().equals(RideMode.Paid)){
            mPaidRideRadioButton.setChecked(true);
            Log.d(TAG, "Paid Ride is the user default ride mode");
        } else {
            mFreeRideRadioButton.setChecked(true);
            Log.d(TAG, "Free Ride is the user default ride mode");
        }

        //Specific for Ride Types
        if (mRideType.equals(RideType.RequestRide)){
            //IMP - Note the sequence of setMax and Progress. First you should set the value of Max and then progress,
            //otherwise progress value gets changed if the sequence is not correct
            mPickupPointVariationSeekBar.setMax(Constant.PICKUP_POINT_DISTANCE_MAX_VALUE);
            mPickupPointVariationSeekBar.setProgress(mRidesOption.getPickupPointVariation());

            mDropPointVariationSeekBar.setMax(Constant.DROP_POINT_DISTANCE_MAX_VALUE);
            mDropPointVariationSeekBar.setProgress(mRidesOption.getDropPointVariation());

            //This will get minute value from LocalTime of 00:30 format
            int pickupTimeVariation = mCommonUtil.getMinsFromLocalTimeString(mRidesOption.getPickupTimeVariation());

            mPickupTimeVariationSeekBar.setMax(Constant.PICKUP_TIME_MAX_VALUE);
            mPickupTimeVariationSeekBar.setProgress(pickupTimeVariation);

            Log.d(TAG, "Pickup Time Seekbar value - Current, Max:"+mPickupTimeVariationSeekBar.getProgress() +","+mPickupTimeVariationSeekBar.getMax());
            Log.d(TAG, "Pickup Seekbar value - Current, Max:"+mPickupPointVariationSeekBar.getProgress() +","+mPickupPointVariationSeekBar.getMax());
            Log.d(TAG, "Drop Seekbar value - Current, Max:"+mDropPointVariationSeekBar.getProgress() +","+mDropPointVariationSeekBar.getMax());
        }
    }

    private void setRidesOption(){

        //Common for both Ride Type
        if (mPaidRideRadioButton.isChecked()){
            mRidesOption.setRideMode(RideMode.Paid);
        } else {
            mRidesOption.setRideMode(RideMode.Free);
        }

        //Specific for Ride Types
        if (mRideType.equals(RideType.OfferRide)){
            mRidesOption.setSeatOffered(mSeatCount);
            mRidesOption.setLuggageCapacityOffered(mLuggageCount);
            mRidesOption.setDefaultVehicle(mVehicleAdapter.getSelectedVehicle());
        } else {
            mRidesOption.setSeatRequired(mSeatCount);
            mRidesOption.setLuggageCapacityRequired(mLuggageCount);
            String pickupTimeVariation = mCommonUtil.getLocalTimeStringFromMins(Integer.parseInt(mPickupTimeVariationProgressTextView.getText().toString()));
            mRidesOption.setPickupTimeVariation(pickupTimeVariation);
            mRidesOption.setPickupPointVariation(Integer.parseInt(mPickupPointVariationProgressTextView.getText().toString()));
            mRidesOption.setDropPointVariation(Integer.parseInt(mDropPointVariationProgressTextView.getText().toString()));
            for (VehicleCategory vehicleCategory: mVehicleCategories){
                if (vehicleCategory.getName().equals(mVehicleCategorySpinner.getSelectedItem().toString())){
                    mRidesOption.setVehicleCategory(vehicleCategory);
                    for (VehicleSubCategory vehicleSubCategory: vehicleCategory.getSubCategories()){
                        if (vehicleSubCategory.getName().equals(mVehicleSubCategorySpinner.getSelectedItem().toString())) {
                            mRidesOption.setVehicleSubCategory(vehicleSubCategory);
                            break;
                        }
                    }
                    break;
                }
            }
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
                setRidesOption();
                if (mListener != null) {
                    if (mRideType.equals(RideType.OfferRide)){
                        mListener.onRidesOptionFragmentInteraction(mRidesOption);
                    } else {
                        mListener.onRidesOptionFragmentInteraction(mRidesOption);
                    }
                }
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
    public void onVehicleCategoriesReady(List<VehicleCategory> vehicleCategories) {
        mVehicleCategories = vehicleCategories;
        for (int i=0; i<mVehicleCategorySpinner.getCount();i++){
            if (mVehicleCategorySpinner.getItemAtPosition(i).equals(mRidesOption.getVehicleCategory().getName())){
                mVehicleCategorySpinner.setSelection(i);
                Log.d(TAG, "Setting Default value of Vehicle Category as:"+mRidesOption.getVehicleCategory().getName());
                break;
            }
        }

        for (int i=0; i<mVehicleSubCategorySpinner.getCount();i++){
            if (mVehicleSubCategorySpinner.getItemAtPosition(i).equals(mRidesOption.getVehicleSubCategory().getName())){
                mVehicleSubCategorySpinner.setSelection(i);
                Log.d(TAG, "Setting Default value of Vehicle Sub Category as:"+mRidesOption.getVehicleSubCategory().getName());
                break;
            }
        }
    }

    @Override
    public void onSeatSelection(int seatCount) {
        mSeatCount = seatCount;
        Log.d(TAG, "Updating seat count");
    }

    @Override
    public void onLuggageSelection(int luggageCount) {
        mLuggageCount = luggageCount;
        Log.d(TAG, "Updating luggage count");
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
        void onRidesOptionFragmentInteraction(Preference ridesOption);
    }

}
