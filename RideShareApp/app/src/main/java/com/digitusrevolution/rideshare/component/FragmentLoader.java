package com.digitusrevolution.rideshare.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.BaseActivity;
import com.digitusrevolution.rideshare.fragment.AddVehicleFragment;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.BillFragment;
import com.digitusrevolution.rideshare.fragment.CreateGroupFragment;
import com.digitusrevolution.rideshare.fragment.CreateMembershipFormFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.fragment.GroupHomePageFragment;
import com.digitusrevolution.rideshare.fragment.GroupInfoFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.fragment.RidesListHomePageFragment;
import com.digitusrevolution.rideshare.fragment.RidesOptionFragment;
import com.digitusrevolution.rideshare.fragment.SearchFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.fragment.WalletFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.app.FetchType;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;

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

    public void loadRidesOptionFragment(RideType rideType, String data, int travelDistance) {
        RidesOptionFragment ridesOptionFragment = RidesOptionFragment.
                newInstance(rideType,data, travelDistance);
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container, ridesOptionFragment, RidesOptionFragment.TAG)
                .addToBackStack(RidesOptionFragment.TAG)
                .commit();
    }

    public void loadAddVehicleFragment(String data) {
        AddVehicleFragment addVehicleFragment = AddVehicleFragment.
                newInstance(data);
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

    public void loadUserProfileFragment(String userProfile, String data) {
        Fragment userProfileFragment = UserProfileFragment.newInstance(userProfile, data);
        //Add to back stack as user may want to go back to home page and choose alternate option
        getFragmentManager().beginTransaction()
                .replace(R.id.home_page_container,userProfileFragment, UserProfileFragment.TAG)
                .addToBackStack(UserProfileFragment.TAG)
                .commit();
    }

    //We would like to load this fragment everytime so that we we can fetch latest data, so getting old fragment would not work
    public void loadHomePageWithCurrentRidesFragment(FetchType fetchType, String data) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        HomePageWithCurrentRidesFragment homePageWithCurrentRidesFragment = HomePageWithCurrentRidesFragment.
                    newInstance(fetchType, data);
        //Don't drawable.add to backstack else it will display blank container on back press which is the initial stage of activity
        fragmentTransaction.replace(R.id.home_page_container, homePageWithCurrentRidesFragment, HomePageWithCurrentRidesFragment.TAG);
        fragmentTransaction.commit();
    }

    //Don't implement the same logic of getting the old fragment and create only when null.
    //This may impact the content of the fragment as we are passing ride in newInstance
    // and if used old one then old data of another ride would come. Need to think better
    public void loadRideInfoFragment(String ride) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RideInfoFragment rideInfoFragment = RideInfoFragment.
                newInstance(ride);
        fragmentTransaction.replace(R.id.home_page_container, rideInfoFragment, RideInfoFragment.TAG);
        fragmentTransaction.addToBackStack(RideInfoFragment.TAG);
        fragmentTransaction.commit();
    }

    //Don't implement the same logic of getting the old fragment and create only when null.
    //This may impact the content of the fragment as we are passing rideRequest in newInstance
    // and if used old one then old data of another ride request would come. Need to think better
    public void loadRideRequestInfoFragment(String rideRequest) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RideRequestInfoFragment rideRequestInfoFragment = RideRequestInfoFragment.
                newInstance(rideRequest);
        fragmentTransaction.replace(R.id.home_page_container, rideRequestInfoFragment, RideRequestInfoFragment.TAG);
        fragmentTransaction.addToBackStack(RideRequestInfoFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadRidesListFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        RidesListHomePageFragment ridesListHomePageFragment = RidesListHomePageFragment.
                newInstance(null, null);
        fragmentTransaction.replace(R.id.home_page_container, ridesListHomePageFragment, RidesListHomePageFragment.TAG);
        fragmentTransaction.addToBackStack(RidesListHomePageFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadBillFragment(String bill, RideType rideType) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        BillFragment billFragment = BillFragment.newInstance(bill, rideType);
        fragmentTransaction.replace(R.id.home_page_container, billFragment, BillFragment.TAG);
        fragmentTransaction.addToBackStack(BillFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadWalletFragment(boolean requiredBalanceVisiblity, float requiredBalanceAmount) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        WalletFragment walletFragment = WalletFragment.
                newInstance(requiredBalanceVisiblity, requiredBalanceAmount);
        fragmentTransaction.replace(R.id.home_page_container, walletFragment, WalletFragment.TAG);
        fragmentTransaction.addToBackStack(WalletFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadGroupHomePageFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        GroupHomePageFragment groupHomePageFragment = GroupHomePageFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.home_page_container, groupHomePageFragment, GroupHomePageFragment.TAG);
        fragmentTransaction.addToBackStack(GroupHomePageFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadSearchFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        SearchFragment searchFragment = SearchFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.home_page_container, searchFragment, SearchFragment.TAG);
        fragmentTransaction.addToBackStack(SearchFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadCreateGroupFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        CreateGroupFragment createGroupFragment = CreateGroupFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.home_page_container, createGroupFragment, CreateGroupFragment.TAG);
        fragmentTransaction.addToBackStack(CreateGroupFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadMembershipFormFragment(String group) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        CreateMembershipFormFragment createMembershipFormFragment = CreateMembershipFormFragment.newInstance(group);
        fragmentTransaction.replace(R.id.home_page_container, createMembershipFormFragment, CreateMembershipFormFragment.TAG);
        fragmentTransaction.addToBackStack(CreateMembershipFormFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadGroupInfoFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.home_page_container, groupInfoFragment, GroupInfoFragment.TAG);
        fragmentTransaction.addToBackStack(GroupInfoFragment.TAG);
        fragmentTransaction.commit();
    }

}
