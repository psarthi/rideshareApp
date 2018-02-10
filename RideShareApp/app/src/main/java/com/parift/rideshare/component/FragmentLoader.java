package com.parift.rideshare.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.BaseActivity;
import com.parift.rideshare.fragment.AddVehicleFragment;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.BillFragment;
import com.parift.rideshare.fragment.CreateGroupFragment;
import com.parift.rideshare.fragment.CreateMembershipFormFragment;
import com.parift.rideshare.fragment.CreateRidesFragment;
import com.parift.rideshare.fragment.GroupHomePageFragment;
import com.parift.rideshare.fragment.GroupInfoFragment;
import com.parift.rideshare.fragment.HelpFragment;
import com.parift.rideshare.fragment.HelpQuestionAnswerFragment;
import com.parift.rideshare.fragment.LegalFragment;
import com.parift.rideshare.fragment.MembershipRequestFragment;
import com.parift.rideshare.fragment.SearchGroupFragment;
import com.parift.rideshare.fragment.SearchUserForGroupFragment;
import com.parift.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.parift.rideshare.fragment.RideInfoFragment;
import com.parift.rideshare.fragment.RideRequestInfoFragment;
import com.parift.rideshare.fragment.RidesListHomePageFragment;
import com.parift.rideshare.fragment.RidesOptionFragment;
import com.parift.rideshare.fragment.UserProfileFragment;
import com.parift.rideshare.fragment.WalletFragment;
import com.parift.rideshare.fragment.WebPageFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.app.FetchType;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.user.dto.GroupDetail;
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
        //Don't add to backstack else it will display blank container on back press which is the initial stage of activity
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

    public void loadSearchUserForGroupFragment(String group) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        SearchUserForGroupFragment searchUserForGroupFragment = SearchUserForGroupFragment.newInstance(group);
        fragmentTransaction.replace(R.id.home_page_container, searchUserForGroupFragment, SearchUserForGroupFragment.TAG);
        fragmentTransaction.addToBackStack(SearchUserForGroupFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadSearchGroupFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        SearchGroupFragment searchGroupFragment = SearchGroupFragment.newInstance();
        fragmentTransaction.replace(R.id.home_page_container, searchGroupFragment, SearchGroupFragment.TAG);
        fragmentTransaction.addToBackStack(SearchUserForGroupFragment.TAG);
        fragmentTransaction.commit();
    }


    public void loadCreateGroupFragment(boolean newGroup, String groupDetail) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        CreateGroupFragment createGroupFragment = CreateGroupFragment.newInstance(newGroup, groupDetail);
        fragmentTransaction.replace(R.id.home_page_container, createGroupFragment, CreateGroupFragment.TAG);
        fragmentTransaction.addToBackStack(CreateGroupFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadMembershipFormFragment(String group, byte[] rawImage) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        CreateMembershipFormFragment createMembershipFormFragment = CreateMembershipFormFragment.newInstance(group, rawImage);
        fragmentTransaction.replace(R.id.home_page_container, createMembershipFormFragment, CreateMembershipFormFragment.TAG);
        fragmentTransaction.addToBackStack(CreateMembershipFormFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadGroupInfoFragment(String groupDetail) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        GroupInfoFragment groupInfoFragment = GroupInfoFragment.newInstance(groupDetail);
        fragmentTransaction.replace(R.id.home_page_container, groupInfoFragment, GroupInfoFragment.TAG);
        fragmentTransaction.addToBackStack(GroupInfoFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadGroupInfoByRemovingBackStacks(GroupDetail groupDetail) {
        Logger.debug(TAG, "Removing all backstacks till GroupInfoFragment");
        //IMP - We are removing all fragments from transaction till Group Home Page
        //so that we don't go back to membership request form by pressing back
        //This will ensure Group Home page fragment doesn't get popped by setting
        //the flag as o instead of POP_BACK_STACK_INCLUSIVE
        //Note - If we pop only till Group Info then creation of group scenarion would not not
        //get popped as its before Group Info fragment and group info was never loaded
        getFragmentManager().popBackStack(GroupHomePageFragment.TAG, 0);
        loadGroupInfoFragment(new Gson().toJson(groupDetail));
    }

    public void loadMembershipRequestFragment(String membershipRequest, boolean adminRole, boolean newRequest) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        MembershipRequestFragment membershipRequestFragment = MembershipRequestFragment.
                newInstance(membershipRequest, adminRole, newRequest);
        fragmentTransaction.replace(R.id.home_page_container, membershipRequestFragment, MembershipRequestFragment.TAG);
        fragmentTransaction.addToBackStack(CreateMembershipFormFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadWebPageFragment(String url, String pageTitle) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        WebPageFragment webPageFragment = WebPageFragment.
                newInstance(url, pageTitle);
        fragmentTransaction.replace(R.id.home_page_container, webPageFragment, WebPageFragment.TAG);
        fragmentTransaction.addToBackStack(WebPageFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadHelpFragment(String param1, String param2) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        HelpFragment helpFragment = HelpFragment.
                newInstance(param1,param2);
        fragmentTransaction.replace(R.id.home_page_container, helpFragment, HelpFragment.TAG);
        fragmentTransaction.addToBackStack(HelpFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadLegalFragment(String param1, String param2) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        LegalFragment legalFragment = LegalFragment.
                newInstance(param1,param2);
        fragmentTransaction.replace(R.id.home_page_container, legalFragment, LegalFragment.TAG);
        fragmentTransaction.addToBackStack(LegalFragment.TAG);
        fragmentTransaction.commit();
    }

    public void loadHelpQuestionAnswerFragment(String data) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        HelpQuestionAnswerFragment helpQuestionAnswerFragment = HelpQuestionAnswerFragment.
                newInstance(data);
        fragmentTransaction.replace(R.id.home_page_container, helpQuestionAnswerFragment,
                HelpQuestionAnswerFragment.TAG);
        fragmentTransaction.addToBackStack(HelpQuestionAnswerFragment.TAG);
        fragmentTransaction.commit();
    }

}
