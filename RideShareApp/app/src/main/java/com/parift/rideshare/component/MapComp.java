package com.parift.rideshare.component;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.parift.rideshare.config.Constant;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.domain.RidePoint;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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

    public void setPadding(boolean standard, RideType rideType){
        if (mBaseFragment.isAdded()) {
            mWidth = mBaseFragment.getResources().getDisplayMetrics().widthPixels;
            //This is here for experience purpose, we can use either heigth or width whichever make sense
            mHeight = mBaseFragment.getResources().getDisplayMetrics().heightPixels;
            mStandardPadding = (int) (mHeight * Constant.LAT_LNG_MEDIUM_PADDING_PERCENT);

            int topPadding = (int) (mHeight * Constant.LAT_LNG_LARGE_PADDING_PERCENT);
            int smallPadding = (int) (mHeight * Constant.LAT_LNG_SMALL_PADDING_PERCENT);
            Logger.debug(TAG, "Width Pixel:" + mWidth + ",Heigth Pixel:" + mHeight + ",Customm Top Padding Pixel:" + topPadding + ",Standard Padding Pixel:" + mStandardPadding);

            if (standard) {
                Logger.debug(TAG, "Setting Standard Padding");
                // void setPadding (int left,int top,int right,int bottom)
                mMap.setPadding(smallPadding, mStandardPadding, smallPadding, smallPadding);
            } else {
                if (rideType.equals(RideType.RequestRide)) {
                    Logger.debug(TAG, "Setting Custom Padding for Request Ride");
                    //This is very important to customize the visibility range of camera
                    // void setPadding (int left,int top,int right,int bottom)
                    mMap.setPadding(smallPadding, topPadding, smallPadding, mStandardPadding);
                } else {
                    Logger.debug(TAG, "Setting Custom Padding for Offer Ride");
                    //This is very important to customize the visibility range of camera
                    // void setPadding (int left,int top,int right,int bottom)
                    mMap.setPadding(smallPadding, topPadding, smallPadding, smallPadding);

                }
            }
        }
    }

    public void setRideOnMap(FullRide ride){

        Logger.debug(TAG,"Setting Ride on Map");

        List<LatLng> latLngs = new ArrayList<>();
        LatLng fromLatLng = new LatLng(ride.getStartPoint().getPoint().getLatitude(), ride.getStartPoint().getPoint().getLongitude());
        LatLng toLatLng = new LatLng(ride.getEndPoint().getPoint().getLatitude(), ride.getEndPoint().getPoint().getLongitude());

        //This will drawable.add marker for start and end point
        mMap.addMarker(new MarkerOptions().position(fromLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(toLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //This will draw polyline for route
        Collection<RidePoint> ridePoints = ride.getRoute().getRidePoints();
        //VERY IMP - Please ensure your route points are sorted by sequence else you will get zig zag path
        //Currently i have set this in the backend so that you get sorted list but in case you need to handle it here
        //due to some reason code is for reference purpose so that you can sort the list
        //VERY IMP - Uncommenting this just as a double safetly measure in case something messed up while gson conversion etc.
        LinkedList<RidePoint> ridePointSorted = new LinkedList<>(ridePoints);
        Collections.sort(ridePointSorted);
        List<LatLng> routeLatLngs = new ArrayList<>();
        for (RidePoint ridePoint: ridePointSorted){
            routeLatLngs.add(new LatLng(ridePoint.getPoint().getLatitude(), ridePoint.getPoint().getLongitude()));
        }
        latLngs.addAll(routeLatLngs);
        mMap.addPolyline(new PolylineOptions().addAll(routeLatLngs));

        //This will drawable.add markers for all pickup points
        Collection<FullRideRequest> acceptedRideRequests = ride.getAcceptedRideRequests();
        for (FullRideRequest rideRequest: acceptedRideRequests){
            LatLng pickupPointLatLng = new LatLng(rideRequest.getRidePickupPoint().getPoint().getLatitude(),
                    rideRequest.getRidePickupPoint().getPoint().getLongitude());
            latLngs.add(pickupPointLatLng);
            mMap.addMarker(new MarkerOptions().position(pickupPointLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }

        LatLngBounds latLngBounds = getBounds(latLngs);

        //Don't use newLatLngBounds(bounds,0) option as its throwing error Error using newLatLngBounds(LatLngBounds, int): Map size can't be 0
        //Use this only with mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() in calling function
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,mWidth, mHeight, mStandardPadding));
    }

    public void setRideRequestOnMap(FullRideRequest rideRequest){

        List<LatLng> latLngs = new ArrayList<>();
        LatLng pickupPoint = new LatLng(rideRequest.getPickupPoint().getPoint().getLatitude(), rideRequest.getPickupPoint().getPoint().getLongitude());
        LatLng dropPoint = new LatLng(rideRequest.getDropPoint().getPoint().getLatitude(), rideRequest.getDropPoint().getPoint().getLongitude());
        LatLng ridePickupPoint = new LatLng(rideRequest.getRidePickupPoint().getPoint().getLatitude(), rideRequest.getRidePickupPoint().getPoint().getLongitude());
        LatLng rideDropPoint = new LatLng(rideRequest.getRideDropPoint().getPoint().getLatitude(), rideRequest.getRideDropPoint().getPoint().getLongitude());
        int pickupPointVariation = rideRequest.getPickupPointVariation();
        int dropPointVariation = rideRequest.getDropPointVariation();

        //Draw Pickup and Drop Point Marker
        mMap.addMarker(new MarkerOptions().position(pickupPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(dropPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //Draw Ride Pickup and Ride Drop Point Marker
        mMap.addMarker(new MarkerOptions().position(ridePickupPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mMap.addMarker(new MarkerOptions().position(rideDropPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        //Draw Circle with Pickup Distance and Drop Distance Variation
        //Commenting it as on lite mode map, circle is too small to have any effect and its coming as a spot on the map
        //mMap.addCircle(new CircleOptions().center(pickupPoint).radius(pickupPointVariation));
        //mMap.addCircle(new CircleOptions().center(dropPoint).radius(dropPointVariation));

        //Get Bound covering all points
        LatLngBounds pickupZonelatLngBounds = getCircleBounds(pickupPoint, pickupPointVariation);
        latLngs.add(pickupZonelatLngBounds.northeast);
        latLngs.add(pickupZonelatLngBounds.southwest);
        LatLngBounds dropZonelatLngBounds = getCircleBounds(dropPoint, dropPointVariation);
        latLngs.add(dropZonelatLngBounds.northeast);
        latLngs.add(dropZonelatLngBounds.southwest);

        LatLngBounds latLngBounds = getBounds(latLngs);

        //Move Camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,0));
    }

    public LatLngBounds getCircleBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
}
































