package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.CommonComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.domain.VehicleCategory;
import com.digitusrevolution.rideshare.model.user.domain.VehicleSubCategory;
import com.digitusrevolution.rideshare.model.user.domain.core.Vehicle;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddVehicleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVehicleFragment extends BaseFragment implements CommonComp.onVehicleCategoriesReadyListener,
CommonComp.onSeatLuggageSelectionListener{

    public static final String TAG = AddVehicleFragment.class.getName();
    public static final String TITLE = "Add Vehicle";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_DATA = "data";

    private RideType mRideType;
    private String mData;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private Vehicle mVehicle = new Vehicle();
    private Button mAddButton;
    private Spinner mVehicleCategorySpinner;
    private Spinner mVehicleSubCategorySpinner;
    private EditText mRegistrationNumberText;
    private EditText mModelText;
    private Button mCancelButton;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;
    private CommonComp mCommonComp;
    private List<VehicleCategory> mVehicleCategories;
    private int mSeatCount;
    private int mLuggageCount;

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data  Data in Json format
     * @return A new instance of fragment AddVehicleFragment.
     */
    public static AddVehicleFragment newInstance(RideType rideType,  String data) {
        AddVehicleFragment fragment = new AddVehicleFragment();
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
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
            mData = getArguments().getString(ARG_DATA);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mFragmentLoader = new FragmentLoader(this);
        mCommonComp = new CommonComp(this);
        //This will set this fragment for vehicle categories ready listener callback
        mCommonComp.mOnVehicleCategoriesReadyListener = this;
        mCommonComp.mOnSeatLuggageSelectionListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        View vehicleCatSubCatLayout = view.findViewById(R.id.vehicle_category_sub_category_layout);

        View seatLuggageView = view.findViewById(R.id.seat_luggage_layout);
        //Max value is just based on max capacity of SUV but this values can be fetched from backend
        mCommonComp.setSeatPicker(seatLuggageView, Constant.MIN_SEAT, Constant.MIN_SEAT, Constant.MAX_SEAT);
        mCommonComp.setLuggagePicker(seatLuggageView, Constant.MIN_LUGGAGE,Constant.MIN_LUGGAGE,Constant.MAX_LUGGAGE);

        mVehicleCategorySpinner = vehicleCatSubCatLayout.findViewById(R.id.vehicle_category_spinner);
        mVehicleSubCategorySpinner = vehicleCatSubCatLayout.findViewById(R.id.vehicle_sub_category_spinner);
        mCommonComp.setVehicleCategoriesSpinner(mVehicleCategorySpinner, mVehicleSubCategorySpinner);

        mRegistrationNumberText = view.findViewById(R.id.add_vehicle_number_text);
        mModelText = view.findViewById(R.id.add_vehicle_model_text);

        mAddButton = view.findViewById(R.id.add_vehicle_add_button);
        mCancelButton = view.findViewById(R.id.add_vehicle_cancel_button);
        setButtonsOnClickListener();

        return view;
    }

    private void setButtonsOnClickListener() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ADD_VEHICLE_URL = APIUrl.ADD_VEHICLE_URL.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()));
                if (validateInput()){
                    setVehicle();
                    RESTClient.post(getActivity(),ADD_VEHICLE_URL, mVehicle, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mUser = new Gson().fromJson(response.toString(), BasicUser.class);
                            mCommonUtil.updateUser(mUser);
                            Log.d(TAG, "Vehicle Added");
                            //This will ensure that only when its first time adding vehicle through create rides flow, this will load Rides Option Fragment
                            //So that it can show the added vehicle to the user for other cases, we need to handle differently
                            if (mRideType.equals(RideType.OfferRide)){
                                mFragmentLoader.loadRidesOptionFragment(mRideType, null);
                            } else {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d(TAG, "Unable to Add Vehicle"+errorResponse);
                        }
                    });
                }
            }
        });
    }

    private void setVehicle() {
        int vehicleCategoryIndex = mVehicleCategorySpinner.getSelectedItemPosition();
        mVehicle.setVehicleCategory(mVehicleCategories.get(vehicleCategoryIndex));
        for (VehicleSubCategory vehicleSubCategory : mVehicleCategories.get(vehicleCategoryIndex).getSubCategories()){
            if (vehicleSubCategory.getName().equals(mVehicleSubCategorySpinner.getSelectedItem())){
                mVehicle.setVehicleSubCategory(vehicleSubCategory);
            }
        }
        mVehicle.setRegistrationNumber(mRegistrationNumberText.getText().toString());
        mVehicle.setModel(mModelText.getText().toString());
        mVehicle.setSeatCapacity(mSeatCount);
        mVehicle.setSmallLuggageCapacity(mLuggageCount);
    }

    private boolean validateInput(){
        if (mRegistrationNumberText.getText().length() == 0 || mModelText.getText().length() == 0){
            Toast.makeText(getActivity(), "Please ensure input is valid",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddVehicleFragmentFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
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
        // TODO: Update argument type and name
        void onAddVehicleFragmentFragmentInteraction(Uri uri);
    }
}
