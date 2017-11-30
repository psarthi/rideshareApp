package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.domain.VehicleCategory;
import com.digitusrevolution.rideshare.model.user.domain.VehicleSubCategory;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseFragment extends Fragment implements OnMapReadyCallback{

    public static final String TAG = BaseFragment.class.getName();
    GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    OnSetCurrentLocationOnMapListener mOnSetCurrentLocationOnMapListener;
    OnVehicleCategoriesReadyListener mOnVehicleCategoriesReadyListener;
    List<VehicleCategory> mVehicleCategories;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mOnSetCurrentLocationOnMapListener instanceof CreateRidesFragment){
            Log.d(TAG,"Setting Padding");
            setPadding();
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location Permission not there");
            //This is important for Fragment and not we are not using Activity requestPermissions method but we are using Fragment requestPermissions,
            // so that request can be handled in this class itself instead of handling it in Activity class
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location Permission already there");
            setCurrentLocationOnMap();
        }
    }

    private void setCurrentLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "Current Location:"+location.getLatitude()+","+location.getLongitude());
                        // Add a marker in User Current Location, and move the camera.
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
                        // Calling back the interface implementor i.e. whatever has been set in OnSetCurrentLocationOnMapListener member variable,
                        // it will get call back
                        mOnSetCurrentLocationOnMapListener.onSetCurrentLocationOnMap(latLng);
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            });
        }
    }

    private void setPadding(){

        int width = getResources().getDisplayMetrics().widthPixels;
        //This is here for experience purpose, we can use either heigth or width whichever make sense
        int height = getResources().getDisplayMetrics().heightPixels;
        int topPadding = (int) (height * Constant.LAT_LNG_TOP_PADDING_PERCENT); // offset from edges of the map 10% of screen
        int standardPaddding = (int) (height * Constant.LAT_LNG_STANDARD_PADDING_PERCENT);
        Log.d(TAG, "Width Pixel:"+width+",Heigth Pixel:"+height+",Padding Pixel:"+topPadding);

        //This is very important to customize the visibility range of camera
        mMap.setPadding(standardPaddding,topPadding, standardPaddding,standardPaddding);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Permission Result Recieved");
        switch (requestCode) {
            case Constant.ACCESS_FINE_LOCATION_REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Location Permission granted");
                    setCurrentLocationOnMap();
                } else {
                    Log.d(TAG, "Location Permission denied");
                }
            }
        }
    }

    public String getFormattedDateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
        return dateFormat.format(date);
    }

    @NonNull
    public String getTimeIn12HrFormat(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        //Calendar.HOUR_OF_DAY is in 24-hour format
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        String hour = String.format("%02d", calendar.get(Calendar.HOUR));
        String min = String.format("%02d", calendar.get(Calendar.MINUTE));
        String AM_PM = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

        //Calendar.HOUR is in 12-hour format
        return hour+":"+min+" "+AM_PM;
    }

    public interface OnSetCurrentLocationOnMapListener {

        void onSetCurrentLocationOnMap(LatLng latLng);
    }

    public void showBackStackDetails(){

        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG,"Fragment count in backstack is:"+count);
        for (int i=0; i< count;i++){
            String name = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName();
            int id = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId();
            Log.d(TAG, "Backstack entry [name,id] at index "+i+":"+name+","+id);
        }
    }

    public void populateSpinner(ArrayList<String> arrayList, Spinner spinner){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,arrayList);
        // Specify the layout to use when the list of choices appears
        // Don't setDropDownView here and instead overwrite getDropDownView since we are using Object instead of String.
        //If you are using plan String in ArrayAdapter then no need to write custom adapter and below set function would do the job
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

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
                populateSpinner(vehicleCategoryNames,vehicleCategotySpinner);
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
                populateSpinner(vehicleSubCategoryNames,vehicleSubCategotySpinner);
                mOnVehicleCategoriesReadyListener.OnVehicleCategoriesReady();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //This function is duplicate in BaseActivity and BaseFragment
    //as getPackageName would work differently in Activity and Fragment
    private SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(getActivity().getPackageName() + Constant.SHARED_PREFS_KEY_FILE, Context.MODE_PRIVATE);
    }

    public void updateInSharedPref(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        String savedValue = getSharedPreferences().getString(key,null);
        Log.d(TAG, "Updated Key/Value - " + key+":"+savedValue);
    }

    public String getAccessToken() {
        return getSharedPreferences().getString(Constant.SHARED_PREFS_TOKEN_KEY,null);
    }

    public BasicUser getUser() {
        String user = getSharedPreferences().getString(Constant.SHARED_PREFS_USER_KEY,null);
        return new Gson().fromJson(user, BasicUser.class);
    }

    public FullRide getCurrentRide() {
        String currentRide = getSharedPreferences().getString(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,null);
        return new Gson().fromJson(currentRide, FullRide.class);
    }

    public FullRideRequest getCurrentRideRequest() {
        String currentRideRequest = getSharedPreferences().getString(Constant.SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY,null);
        return new Gson().fromJson(currentRideRequest,FullRideRequest.class);
    }

    public void updateAccessToken(String token){
        updateInSharedPref(Constant.SHARED_PREFS_TOKEN_KEY,token);
    }

    public void updateUser(BasicUser user){
        updateInSharedPref(Constant.SHARED_PREFS_USER_KEY,new Gson().toJson(user));
    }

    public void updateCurrentRide(FullRide currentRide){
        updateInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_KEY,new Gson().toJson(currentRide));
    }

    public void updateCurrentRideRequest(FullRideRequest currentRideRequest){
        updateInSharedPref(Constant.SHARED_PREFS_CURRENT_RIDE_REQUEST_KEY,new Gson().toJson(currentRideRequest));
    }

    public void loadRidesOptionFragment(RideType rideType, String data) {
        RidesOptionFragment ridesOptionFragment = RidesOptionFragment.
                newInstance(rideType,data);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_container, ridesOptionFragment, RidesOptionFragment.TAG)
                .addToBackStack(RidesOptionFragment.TAG)
                .commit();
    }

    public void loadAddVehicleFragment(RideType rideType, String data) {
        AddVehicleFragment addVehicleFragment = AddVehicleFragment.
                newInstance(rideType, data);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_container, addVehicleFragment, AddVehicleFragment.TAG)
                .addToBackStack(AddVehicleFragment.TAG)
                .commit();
    }

    public void loadCreatesRideFragment(RideType rideType, String data) {
        Fragment createRidesFragment = CreateRidesFragment.newInstance(rideType, data);
        //Add to back stack as user may want to go back to home page and choose alternate option
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_page_container,createRidesFragment, CreateRidesFragment.TAG)
                .addToBackStack(CreateRidesFragment.TAG)
                .commit();
    }

    public interface OnVehicleCategoriesReadyListener{
        void OnVehicleCategoriesReady();
    }
}
