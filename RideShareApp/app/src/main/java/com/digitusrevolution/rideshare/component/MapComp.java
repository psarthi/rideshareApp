package com.digitusrevolution.rideshare.component;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.domain.RidePoint;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by psarthi on 12/6/17.
 */

public class MapComp{

    public static final String TAG = MapComp.class.getName();
    BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private GoogleMap mMap;
    private int mWidth;
    private int mHeight;
    private int mStandardPadding;

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

        mWidth = mBaseFragment.getResources().getDisplayMetrics().widthPixels;
        //This is here for experience purpose, we can use either heigth or width whichever make sense
        mHeight = mBaseFragment.getResources().getDisplayMetrics().heightPixels;
        int topPadding = (int) (mHeight * Constant.LAT_LNG_TOP_PADDING_PERCENT); // offset from edges of the map 10% of screen
        mStandardPadding = (int) (mHeight * Constant.LAT_LNG_STANDARD_PADDING_PERCENT);
        Log.d(TAG, "Width Pixel:"+mWidth+",Heigth Pixel:"+mHeight+",Customm Top Padding Pixel:"+topPadding+",Standard Padding Pixel:"+mStandardPadding);

        if (standard){
            Log.d(TAG, "Setting Standard Padding");
            mMap.setPadding(mStandardPadding,mStandardPadding, mStandardPadding,mStandardPadding);
        } else {
            Log.d(TAG, "Setting Custom Padding");
            //This is very important to customize the visibility range of camera
            mMap.setPadding(mStandardPadding,topPadding, mStandardPadding,mStandardPadding);
        }
    }

    public void setRideOnMap(FullRide ride){

        Log.d(TAG,"Setting Ride on Map");

        List<LatLng> latLngs = new ArrayList<>();
        LatLng fromLatLng = new LatLng(ride.getStartPoint().getPoint().getLatitude(), ride.getStartPoint().getPoint().getLongitude());
        LatLng toLatLng = new LatLng(ride.getEndPoint().getPoint().getLatitude(), ride.getEndPoint().getPoint().getLongitude());
        latLngs.add(fromLatLng);
        latLngs.add(toLatLng);

        //This will add marker for start and end point
        mMap.addMarker(new MarkerOptions().position(fromLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(toLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //This will draw polyline for route
        Collection<RidePoint> ridePoints = ride.getRoute().getRidePoints();
        List<LatLng> routeLatLngs = new ArrayList<>();
        for (RidePoint ridePoint: ridePoints){
            routeLatLngs.add(new LatLng(ridePoint.getPoint().getLatitude(), ridePoint.getPoint().getLongitude()));
        }
        latLngs.addAll(routeLatLngs);
        mMap.addPolyline(new PolylineOptions().addAll(routeLatLngs));

        //This will add markers for all pickup points
        Collection<BasicRideRequest> acceptedRideRequests = ride.getAcceptedRideRequests();
        for (BasicRideRequest rideRequest: acceptedRideRequests){
            LatLng pickupPointLatLng = new LatLng(rideRequest.getRidePickupPoint().getPoint().getLatitude(),
                    rideRequest.getRidePickupPoint().getPoint().getLongitude());
            latLngs.add(pickupPointLatLng);
            mMap.addMarker(new MarkerOptions().position(pickupPointLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        LatLngBounds latLngBounds = getBounds(latLngs);

        //Don't use newLatLngBounds(bounds,0) option as its throwing error Error using newLatLngBounds(LatLngBounds, int): Map size can't be 0
        //Use this only with mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() in calling function
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,mWidth, mHeight, mStandardPadding));
    }
}
