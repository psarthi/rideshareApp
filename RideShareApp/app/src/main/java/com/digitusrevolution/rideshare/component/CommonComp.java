package com.digitusrevolution.rideshare.component;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.digitusrevolution.rideshare.config.APIUrl;
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

        vehicleCategotySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> vehicleSubCategoryNames = new ArrayList<>();
                for (VehicleSubCategory vehicleSubCategory : mVehicleCategories.get(position).getSubCategories()){
                    vehicleSubCategoryNames.add(vehicleSubCategory.getName());
                    Log.d(TAG,"Vehicle Sub Category Name:"+ vehicleSubCategory.getName());
                }
                mBaseFragment.populateSpinner(vehicleSubCategoryNames,vehicleSubCategotySpinner);
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
}
