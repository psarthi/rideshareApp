package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by psarthi on 11/21/17.
 */

public class BaseFragment extends Fragment implements OnMapReadyCallback{

    public static final String TAG = BaseFragment.class.getName();
    GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    BaseFragmentListener mBaseFragmentListener;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mBaseFragmentListener instanceof CreateRidesFragment){
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_SINGLE_LOCATION_ZOOM_LEVEL));
                        //Calling back the interface implementor i.e. whatever has been set in BaseFragmentListener member variable,
                        // it will get call back
                        mBaseFragmentListener.onSetCurrentLocationOnMap(latLng);
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



    public interface BaseFragmentListener {

        void onSetCurrentLocationOnMap(LatLng latLng);
    }

}