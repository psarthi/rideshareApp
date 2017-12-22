package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.AddVehicleFragment;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.user.domain.VehicleCategory;
import com.digitusrevolution.rideshare.model.user.domain.VehicleSubCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class CommonComp {

    public static final String TAG = CommonComp.class.getName();
    BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    //Don't expose this as public and instead use listeners which would invoke only when vehicle is ready and populated
    private List<VehicleCategory> mVehicleCategories;
    public onVehicleCategoriesReadyListener mOnVehicleCategoriesReadyListener;
    private int mSeatCount = 0;
    private int mLuggageCount = 0;
    public onSeatLuggageSelectionListener mOnSeatLuggageSelectionListener;

    public CommonComp(BaseFragment fragment){
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setVehicleCategoriesSpinner(final Spinner vehicleCategotySpinner, final Spinner vehicleSubCategotySpinner) {



        RESTClient.get(APIUrl.GET_VEHICLE_CATEGORIES_URL,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                //Getting the value in Arraylist would ensure that position would always remains the same
                //as compared to HashSet etc.
                Type listType = new TypeToken<ArrayList<VehicleCategory>>(){}.getType();
                mVehicleCategories = new Gson().fromJson(response.toString(), listType);
                ArrayList<String> vehicleCategoryNames = new ArrayList<>();
                for (VehicleCategory vehicleCategory : mVehicleCategories){
                    vehicleCategoryNames.add(vehicleCategory.getName());
                    Log.d(TAG,"Vehicle Category Name:"+vehicleCategory.getName());
                }
                mBaseFragment.populateSpinner(vehicleCategoryNames,vehicleCategotySpinner);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        //Reason for setting vehicle sub-category on setOnItemSelected of vehicle category to cater multiple vehicle categories
        vehicleCategotySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> vehicleSubCategoryNames = new ArrayList<>();
                for (VehicleSubCategory vehicleSubCategory : mVehicleCategories.get(position).getSubCategories()){
                    vehicleSubCategoryNames.add(vehicleSubCategory.getName());
                    Log.d(TAG,"Vehicle Sub Category Name:"+ vehicleSubCategory.getName());
                }
                Collections.sort(vehicleSubCategoryNames);

                if (mOnVehicleCategoriesReadyListener instanceof AddVehicleFragment) {
                    vehicleSubCategoryNames.remove("All");
                }
                mBaseFragment.populateSpinner(vehicleSubCategoryNames,vehicleSubCategotySpinner);

                //Imp - This should be called post when both spinner is setup otherwise we will get NPE
                //This will update the vehicle categories including its sub-category in the calling fragment e.g Add Vehicle, Rides Option
                if (mOnVehicleCategoriesReadyListener!=null) mOnVehicleCategoriesReadyListener.onVehicleCategoriesReady(mVehicleCategories);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public interface onVehicleCategoriesReadyListener{
        void onVehicleCategoriesReady(List<VehicleCategory> vehicleCategories);
    }

    public void setSeatPicker(View view, int initialValue, final int minValue, final int maxValue){
        mSeatCount = initialValue;
        final TextView seatCountTextView = view.findViewById(R.id.seat_count_text);
        //Setting initial value
        String count = Integer.toString(mSeatCount);
        seatCountTextView.setText(count);
        Log.d(TAG, "Setting Seat Count:"+mSeatCount);
        //Setting listeners
        ((ImageView) view.findViewById(R.id.seat_plus_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSeatCount < maxValue) {
                    mSeatCount++;
                    String count = Integer.toString(mSeatCount);
                    seatCountTextView.setText(count);
                    mOnSeatLuggageSelectionListener.onSeatSelection(mSeatCount);
                } else {
                    Toast.makeText(mBaseFragment.getActivity(), "Max Value Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ImageView) view.findViewById(R.id.seat_minus_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSeatCount > minValue) {
                    mSeatCount--;
                    String count = Integer.toString(mSeatCount);
                    seatCountTextView.setText(count);
                    mOnSeatLuggageSelectionListener.onSeatSelection(mSeatCount);
                } else {
                    Toast.makeText(mBaseFragment.getActivity(), "Min Value Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setLuggagePicker(View view, int initialValue, final int minValue, final int maxValue){
        mLuggageCount = initialValue;
        final TextView luggageCountTextView = view.findViewById(R.id.luggage_count_text);
        //Setting initial value
        String count = Integer.toString(mLuggageCount);
        luggageCountTextView.setText(count);
        Log.d(TAG, "Setting Luggage Count:"+mSeatCount);

        //Setting listeners
        ((ImageView) view.findViewById(R.id.luggage_plus_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLuggageCount < maxValue) {
                    mLuggageCount++;
                    String count = Integer.toString(mLuggageCount);
                    luggageCountTextView.setText(count);
                    mOnSeatLuggageSelectionListener.onLuggageSelection(mLuggageCount);
                } else {
                    Toast.makeText(mBaseFragment.getActivity(), "Max Value Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ImageView) view.findViewById(R.id.luggage_minus_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLuggageCount > minValue) {
                    mLuggageCount--;
                    String count = Integer.toString(mLuggageCount);
                    luggageCountTextView.setText(count);
                    mOnSeatLuggageSelectionListener.onLuggageSelection(mLuggageCount);
                } else {
                    Toast.makeText(mBaseFragment.getActivity(), "Min Value Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface onSeatLuggageSelectionListener{
        public void onSeatSelection(int seatCount);
        public void onLuggageSelection(int luggageCount);
    }
}
