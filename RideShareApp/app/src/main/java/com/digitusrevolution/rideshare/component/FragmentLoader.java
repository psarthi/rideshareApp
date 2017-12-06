package com.digitusrevolution.rideshare.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.BaseActivity;
import com.digitusrevolution.rideshare.fragment.AddVehicleFragment;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.fragment.RidesOptionFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.google.gson.Gson;

/**
 * Created by psarthi on 12/6/17.
 */

public class FragmentLoader {

    public static final String TAG = FragmentLoader.class.getName();

    private BaseFragment mBaseFragment;
    private BaseActivity mBaseActivity;
    private CommonUtil mCommonUtil;

    public FragmentLoader(BaseFragment fragment){
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    public FragmentLoader(BaseActivity activity){
        mBaseActivity = activity;
        mCommonUtil = new CommonUtil(activity);
    }

    private FragmentManager getFragmentManager(){
        if (mBaseFragment!=null){
            return mBaseFragment.getActivity().getSupportFragmentManager();
        } else {
            return mBaseActivity.getSupportFragmentManager();
        }
    }

    public void loadRidesOptionFragment(RideType rideType, String data) {
        RidesOptionFragment ridesOptionFragment = RidesOptionFragment.
                newInstance(rideType,data);
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container, ridesOptionFragment, RidesOptionFragment.TAG)
                .addToBackStack(RidesOptionFragment.TAG)
                .commit();
    }

    public void loadAddVehicleFragment(RideType rideType, String data) {
        AddVehicleFragment addVehicleFragment = AddVehicleFragment.
                newInstance(rideType, data);
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container, addVehicleFragment, AddVehicleFragment.TAG)
                .addToBackStack(AddVehicleFragment.TAG)
                .commit();
    }

    public void loadCreatesRideFragment(RideType rideType, String data) {
        Fragment createRidesFragment = CreateRidesFragment.newInstance(rideType, data);
        //Add to back stack as user may want to go back to home page and choose alternate option
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container,createRidesFragment, CreateRidesFragment.TAG)
                .addToBackStack(CreateRidesFragment.TAG)
                .commit();
    }

    public void loadUserProfileFragment(String user, String data) {
        Fragment userProfileFragment = UserProfileFragment.newInstance(user, data);
        //Add to back stack as user may want to go back to home page and choose alternate option
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container,userProfileFragment, UserProfileFragment.TAG)
                .addToBackStack(UserProfileFragment.TAG)
                .commit();
    }

    public void loadHomePageWithCurrentRidesFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        HomePageWithCurrentRidesFragment homePageWithCurrentRidesFragment = HomePageWithCurrentRidesFragment.
                newInstance(null);
        //Don't add to backstack else it will display blank container on back press which is the initial stage of activity
        fragmentTransaction.replace(R.id.home_page_container, homePageWithCurrentRidesFragment, HomePageWithCurrentRidesFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadRideInfoFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RideInfoFragment rideInfoFragment = RideInfoFragment.
                newInstance(new Gson().toJson(mCommonUtil.getCurrentRide()));
        fragmentTransaction.replace(R.id.home_page_container, rideInfoFragment, RideInfoFragment.TAG);
        fragmentTransaction.addToBackStack(RideInfoFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadRideRequestInfoFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RideRequestInfoFragment rideRequestInfoFragment = RideRequestInfoFragment.
                newInstance(new Gson().toJson(mCommonUtil.getCurrentRideRequest()));
        fragmentTransaction.replace(R.id.home_page_container, rideRequestInfoFragment, RideRequestInfoFragment.TAG);
        fragmentTransaction.addToBackStack(RideRequestInfoFragment.TAG);
        fragmentTransaction.commit();
    }
}
