package com.parift.rideshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.AboutGroupFragment;
import com.parift.rideshare.fragment.AddVehicleFragment;
import com.parift.rideshare.fragment.BillFragment;
import com.parift.rideshare.fragment.CreateGroupFragment;
import com.parift.rideshare.fragment.GroupHomePageFragment;
import com.parift.rideshare.fragment.GroupInfoFragment;
import com.parift.rideshare.fragment.GroupMembershipRequestListFragment;
import com.parift.rideshare.fragment.HelpFragment;
import com.parift.rideshare.fragment.HelpQuestionAnswerFragment;
import com.parift.rideshare.fragment.InfoFragment;
import com.parift.rideshare.fragment.MembershipRequestFragment;
import com.parift.rideshare.fragment.SearchGroupFragment;
import com.parift.rideshare.fragment.SearchUserForGroupFragment;
import com.parift.rideshare.fragment.GroupListFragment;
import com.parift.rideshare.fragment.GroupMemberListFragment;
import com.parift.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.parift.rideshare.fragment.CreateRidesFragment;
import com.parift.rideshare.fragment.CreateMembershipFormFragment;
import com.parift.rideshare.fragment.RedeemFragment;
import com.parift.rideshare.fragment.RideInfoFragment;
import com.parift.rideshare.fragment.RideRequestInfoFragment;
import com.parift.rideshare.fragment.RidesListFragment;
import com.parift.rideshare.fragment.RidesListHomePageFragment;
import com.parift.rideshare.fragment.RidesOptionFragment;
import com.parift.rideshare.fragment.TopUpFragment;
import com.parift.rideshare.fragment.TransactionFragment;
import com.parift.rideshare.fragment.UserMembershipRequestListFragment;
import com.parift.rideshare.fragment.UserProfileFragment;
import com.parift.rideshare.fragment.WalletFragment;
import com.parift.rideshare.fragment.WebPageFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.app.FetchType;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.billing.domain.core.Bill;
import com.parift.rideshare.model.ride.domain.core.RideMode;
import com.parift.rideshare.model.user.domain.Preference;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomePageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomePageWithCurrentRidesFragment.OnFragmentInteractionListener,
        CreateRidesFragment.OnFragmentInteractionListener,
        AddVehicleFragment.OnFragmentInteractionListener,
        RidesOptionFragment.OnFragmentInteractionListener,
        RideInfoFragment.OnFragmentInteractionListener,
        RideRequestInfoFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener,
        RidesListHomePageFragment.OnFragmentInteractionListener,
        RidesListFragment.OnFragmentInteractionListener,
        BillFragment.OnFragmentInteractionListener,
        WalletFragment.OnFragmentInteractionListener,
        TopUpFragment.OnFragmentInteractionListener,
        TransactionFragment.OnFragmentInteractionListener,
        RedeemFragment.OnFragmentInteractionListener,
        GroupHomePageFragment.OnFragmentInteractionListener,
        GroupListFragment.OnFragmentInteractionListener,
        SearchUserForGroupFragment.OnFragmentInteractionListener,
        CreateGroupFragment.OnFragmentInteractionListener,
        CreateMembershipFormFragment.OnFragmentInteractionListener,
        GroupInfoFragment.OnFragmentInteractionListener,
        GroupMembershipRequestListFragment.OnFragmentInteractionListener,
        AboutGroupFragment.OnFragmentInteractionListener,
        GroupMemberListFragment.OnFragmentInteractionListener,
        SearchGroupFragment.OnFragmentInteractionListener,
        UserMembershipRequestListFragment.OnFragmentInteractionListener,
        MembershipRequestFragment.OnFragmentInteractionListener,
        WebPageFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        HelpQuestionAnswerFragment.OnFragmentInteractionListener{

    private static final String TAG = HomePageActivity.class.getName();

    private Toolbar mToolbar;
    private BasicUser mUser;
    private ImageView mProfilePhotoImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
    private TextView mUserMobileTextView;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private DrawerLayout mDrawer;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //This will set up action bar for this activity
        setSupportActionBar(mToolbar);

        //This will show up Hamburger icon on action bar and on click of it, drawer would open up
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mUser = mCommonUtil.getUser();

        //This will set up listener on Navigation items
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mUser.getCountry().getRideMode().equals(RideMode.Free)){
            navigationView.getMenu().findItem(R.id.nav_wallet).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);

        setNavHeader(navigationView);
        //No need to fetch from server again as while signing in, we refresh the current rides
        mFragmentLoader.loadHomePageWithCurrentRidesFragment(FetchType.Local, null);

        //Dismiss progress dialog when activity is loaded, else its confusing to the user
        //mCommonUtil.dismissProgressDialog();
    }

    private void setNavHeader(NavigationView navigationView) {

        //This is very important as findViewById would return null if you don't do on headerview as
        // default drawerlayout view doesn't contain image view directly and its inside the headerlayout
        View headerView = navigationView.getHeaderView(0);

        mProfilePhotoImageView = headerView.findViewById(R.id.thumbnail_image);
        Picasso.with(this).load(mUser.getPhoto().getImageLocation()).into(mProfilePhotoImageView);

        mProfilePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GET_USER_PROFILE = APIUrl.GET_USER_PROFILE.replace(APIUrl.SIGNEDIN_USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(GET_USER_PROFILE,null, new RSJsonHttpResponseHandler(mCommonUtil) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
                        FragmentLoader fragmentLoader = new FragmentLoader(HomePageActivity.this);
                        fragmentLoader.loadUserProfileFragment(response.toString(), null);
                    }
                });

            }
        });

        mUserNameTextView = headerView.findViewById(R.id.thumbnail_name_text);
        mUserNameTextView.setText(mUser.getFirstName()+" "
                +mUser.getLastName());

        mUserEmailTextView = headerView.findViewById(R.id.user_email_text);
        //mUserEmailTextView.setText(mUser.getEmail());
        mUserMobileTextView = headerView.findViewById(R.id.user_mobile_text);
        mUserMobileTextView.setText(mUser.getMobileNumber());
    }

    //This method is just to close drawer if system back button is pressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Logger.debug(TAG,"On Back Pressed: Inside Drawer Open State");
        } else {
            Logger.debug(TAG,"On Back Pressed: Inside Else Block");
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* Commented and kept for the reference on the problem faced
        if (id != R.id.nav_home){
            //This will ensure that Home page is never killed once you load first time otherwise you will get NPE as
            //when it loads it makes REST call and by the time response comes, you have already killed the instance and
            //reinitiated the new one, so for any page where we are making REST call on home page, we need to avoid killing fragment
            //In Case of home page fragment, i am updating the value in sharedPrefs post getting the data where BaseFragment reference is killed
            //but in other scenarios's e.g. Rides List i am not updating anything in sharedPrefs which is fine
            removeAllBackStacks();
        }
        */
        //Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();

        if (id == R.id.nav_home) {
            Logger.debug(TAG, "Home Clicked");
            //First set the fetchType
            HomePageWithCurrentRidesFragment fragment = (HomePageWithCurrentRidesFragment) getSupportFragmentManager()
                    .findFragmentByTag(HomePageWithCurrentRidesFragment.TAG);

            //This will update the fetch type from local to server, as initially post sign fetch type is local
            //This may not be required as we are forcefully calling the fetchFromServer but it would be good to
            //have this value set for any future purpose e.g. if we are taking some further action based on FetchType
            fragment.setFetchType(FetchType.Server);

            if (getSupportFragmentManager().getBackStackEntryCount() == 0){
                Logger.debug(TAG, "Refreshing Home Page View");
                //This will refresh the view if the fragment is already loaded which is based on the backstack count
                //Note - Don't do this on else block otherwise it will throw NPE as view would only be created post onCreateView is called
                //But if some other fragment is loaded then View is already destroyed
                fragment.fetchRidesFromServer(fragment.getView());
            } else {
                //This has to be post setting the FetchType else we will not get data refreshed from server
                //Note - Fragment would only get reloaded if you are not on the Home Page else it will not do anything
                //as home page is already loaded
                removeAllBackStacks();
            }
        } else {

            //This is applicable for all items
            removeAllBackStacks();

            if (id == R.id.nav_rides) {
                Logger.debug(TAG, "Rides Clicked");
                mFragmentLoader.loadRidesListFragment();
            } else if (id == R.id.nav_groups) {
                mFragmentLoader.loadGroupHomePageFragment();
                Logger.debug(TAG, "Groups Clicked");
            }
            else if (id == R.id.nav_wallet) {
                Logger.debug(TAG, "Wallet Clicked");
                mFragmentLoader.loadWalletFragment(false, 0);
            }
            else if (id == R.id.nav_info) {
                Logger.debug(TAG, "Info Clicked");
                mFragmentLoader.loadInfoFragment(null, null);
            }
            else if (id == R.id.nav_share) {
                Logger.debug(TAG, "Share Clicked");
                share();
            }
            else if (id == R.id.nav_signout) {
                Logger.debug(TAG, "Signout Clicked");
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.app_share_msg));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void removeAllBackStacks() {
        //This will remove all the fragment from backstack and would start from clean slate
        //So that we don't have too many backstacks items when user clicks on navigation items from anywhere
        //IMP Note - Don't use following one's - as it will make activity/context as null
        //  1. while (fragmentManager.getBackStackEntryCount() != 0)
        //  2. getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //Use following option only which works fine

        //ISSUE Resolved by not reloading the Home Fragment as popbackstack would by default load Home Fragment once all fragment is popped
        //NOTE - VERY IMP - This has serious issue in handling REST Response while popping backtack.
        //Actually what happens when you do popbackstack one fragment is moved out but previous one is reloaded and this process continues
        //till we come to an end. What it means - onCreateView would be called and then onDestroy immediately but if you are using REST call
        //then response of that is not handled as before that it has been killed by this popbackstack and if you are using any reference of the fragment
        //onSuccess response e.g. mCommonUtil etc. then you will get NPE

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Logger.debug(TAG, "Backstack Count is:"+count);
        for (int i = 0; i < count; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            String name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Logger.debug(TAG, "Removing Backstack [Name,Id]:"+name+","+backStackId);
            getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    @Override
    public void onHomePageWithCurrentRidesFragmentInteraction(String data) {
        Logger.debug(TAG,"Value returned to activity from Blank Fragment:"+data);

    }

    @Override
    public void onCreateRideFragmentInteraction(RideType rideType, String data) {
        Logger.debug(TAG, "Recieved callback post Create Rides Fragment");
        //This will clean up all back stacks and start from fresh
        //Reason for not doing popback as map was not getting reloaded properly and viewtreeobserver was not getting callback
        //which was causing old map to show up with previous markers/lines
        /* Commenting this as don't see any reason for loadingHomePage which will be default get loaded on pressing backstack
        removeAllBackStacks();
        FragmentLoader fragmentLoader = new FragmentLoader(this);
        fragmentLoader.loadHomePageWithCurrentRidesFragment(FetchType.Server, null);
        */
        removeAllBackStacks();
        if (rideType.equals(RideType.OfferRide)){
            mFragmentLoader.loadRideInfoFragment(data);
        } else {
            mFragmentLoader.loadRideRequestInfoFragment(data);
        }
    }

    public void showBackButton(boolean show) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if(show) {
            // Remove hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            //This will lock the swipe gesture as well of drawer
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to drawable.add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if(!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });
                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            //This will unlock the swipe gesture of drawer
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.option_menu_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Reason for mentiong this switch case, so that it can handle back button
        //By default even without this fragment back button is getting handled
        //but activity back button would not be handled if this is not there
        //so for consistency and future requirement, lets have this line
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        //Reason for returning false, so that onOptionItemsSelected method of fragment would get called,
        //else it won't get called
        return false;
        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRidesOptionFragmentInteraction(Preference ridesOption) {

        CreateRidesFragment createRidesFragment = (CreateRidesFragment) getSupportFragmentManager().findFragmentByTag(CreateRidesFragment.TAG);
        createRidesFragment.updateRidesOption(ridesOption);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onRideInfoFragmentInteraction(String data) {

    }

    @Override
    public void onRideRequestInfoFragmentInteraction(String data) {

    }

    @Override
    public void onUserProfileFragmentInteraction(String data) {

    }

    @Override
    public void onRidesListFragmentInteraction(String data) {

    }

    @Override
    public void onRidesListHomePageFragmentInteraction(String data) {

    }

    @Override
    public void onBillFragmentInteraction(RideType rideType, String bill) {

        if (rideType.equals(RideType.OfferRide)){
            RideInfoFragment fragment = (RideInfoFragment) getSupportFragmentManager().findFragmentByTag(RideInfoFragment.TAG);
            fragment.updateBill(new Gson().fromJson(bill, Bill.class));
            getSupportFragmentManager().popBackStack();
        } else {
            RideRequestInfoFragment fragment = (RideRequestInfoFragment) getSupportFragmentManager().findFragmentByTag(RideRequestInfoFragment.TAG);
            fragment.updateBill(new Gson().fromJson(bill, Bill.class));
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onWalletFragmentInteraction(String data) {

    }

    @Override
    public void onTopUpFragmentRefresh() {
        refreshWalletFragment();
    }

    @Override
    public void onRedeemFragmentRefresh() {
        refreshWalletFragment();
    }

    private void refreshWalletFragment(){
        WalletFragment fragment = (WalletFragment) getSupportFragmentManager().findFragmentByTag(WalletFragment.TAG);
        fragment.refresh();
    }

    @Override
    public void onTransactionFragmentInteraction(String data) {

    }

    @Override
    public void onAddVehicleFragmentFragmentInteraction(String data) {

    }

    @Override
    public void onGroupHomePageFragmentInteraction(String data) {

    }

    @Override
    public void onGroupListFragmentInteraction(String data) {

    }

    @Override
    public void onCreateGroupFragmentInteraction(String data) {

    }

    @Override
    public void onMembershipFormFragmentLoadGroup(GroupDetail groupDetail) {
        mFragmentLoader.loadGroupInfoByRemovingBackStacks(groupDetail);
    }

    @Override
    public void onGroupInfoFragmentInteraction(String data) {

    }

    @Override
    public void onAboutGroupFragmentRefresh(GroupDetail groupDetail) {
        mFragmentLoader.loadGroupInfoByRemovingBackStacks(groupDetail);
    }

    @Override
    public void onSearchGroupFragmentInteraction(String data) {

    }

    @Override
    public void onSearchUserForGroupFragmentInteraction(String data) {

    }

    @Override
    public void onGroupMemberFragmentInteraction(String data) {

    }

    @Override
    public void onGroupMembershipRequestListFragmentInteraction(String data) {

    }

    @Override
    public void onUserMembershipRequestListFragmentInteraction(String data) {
        
    }

    @Override
    public void onMembershipRequestFragmentRefreshGroupInfo(GroupDetail groupDetail) {
        mFragmentLoader.loadGroupInfoByRemovingBackStacks(groupDetail);
    }

    @Override
    public void onMembershipRequestFragmentRefreshBasicGroupInfo(GroupDetail groupDetail) {
        GroupInfoFragment fragment = (GroupInfoFragment) getSupportFragmentManager().findFragmentByTag(GroupInfoFragment.TAG);
        fragment.refreshBasicInfo(groupDetail);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onWebPageFragmentInteraction(String data) {

    }

    @Override
    public void onHelpFragmentInteraction(String data) {

    }

    @Override
    public void onLegalFragmentInteraction(String data) {

    }

    @Override
    public void onHelpQuestionAnswerFragmentInteraction(String data) {

    }
}