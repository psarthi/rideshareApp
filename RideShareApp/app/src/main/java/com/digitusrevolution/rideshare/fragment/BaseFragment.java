package com.digitusrevolution.rideshare.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
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
import com.google.android.gms.maps.model.LatLngBounds;
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

public class BaseFragment extends Fragment{

    public static final String TAG = BaseFragment.class.getName();

    public void showBackStackDetails(){

        int count = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG,"Fragment count in backstack is:"+count);
        for (int i=0; i< count;i++){
            String name = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName();
            int id = getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getId();
            Log.d(TAG, "Backstack entry [name,id] at index "+i+":"+name+","+id);
        }
    }

    public void showChildFragmentDetails(){

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        Log.d(TAG,"Child Fragment count is:"+fragments.size());
        for (int i=0; i< fragments.size();i++){
            Log.d(TAG, "Child Fragment Id:"+Integer.toString(fragments.get(i).getId()));
            if (fragments.get(i).getId()!=0) Log.d(TAG, "String Name of Id:"+getResources().getResourceName(fragments.get(i).getId()));
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

    public void setRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


}
