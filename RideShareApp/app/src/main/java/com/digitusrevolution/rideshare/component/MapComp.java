package com.digitusrevolution.rideshare.component;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * Created by psarthi on 12/6/17.
 */

public class MapComp{

    public static final String TAG = MapComp.class.getName();
    BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private GoogleMap mMap;

    public MapComp(BaseFragment fragment, GoogleMap map){
        mBaseFragment = fragment;
        mMap = map;
        mCommonUtil = new CommonUtil(fragment);
    }

    public LatLngBounds getBounds(List<LatLng> latLngs){
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        for (LatLng latLng:latLngs){
            latLngBoundsBuilder.include(latLng);
        }
        return latLngBoundsBuilder.build();
    }

    public void setPadding(boolean standard){

        int width = mBaseFragment.getResources().getDisplayMetrics().widthPixels;
        //This is here for experience purpose, we can use either heigth or width whichever make sense
        int height = mBaseFragment.getResources().getDisplayMetrics().heightPixels;
        int topPadding = (int) (height * Constant.LAT_LNG_TOP_PADDING_PERCENT); // offset from edges of the map 10% of screen
        int standardPaddding = (int) (height * Constant.LAT_LNG_STANDARD_PADDING_PERCENT);
        Log.d(TAG, "Width Pixel:"+width+",Heigth Pixel:"+height+",Padding Pixel:"+topPadding);

        if (standard){
            Log.d(TAG, "Setting Standard Padding");
            mMap.setPadding(standardPaddding,standardPaddding, standardPaddding,standardPaddding);
        } else {
            Log.d(TAG, "Setting Custom Padding");
            //This is very important to customize the visibility range of camera
            mMap.setPadding(standardPaddding,topPadding, standardPaddding,standardPaddding);
        }
    }

}
