package com.digitusrevolution.rideshare.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.AddVehicleFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.fragment.RidesListFragment;
import com.digitusrevolution.rideshare.fragment.RidesListHomePageFragment;
import com.digitusrevolution.rideshare.fragment.RidesOptionFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.model.app.FetchType;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
        RidesListFragment.OnFragmentInteractionListener{

    private static final String TAG = HomePageActivity.class.getName();

    private Toolbar mToolbar;
    private BasicUser mUser;
    private ImageView mProfilePhotoImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;
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

        //This will set up listener on Navigation items
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mUser = mCommonUtil.getUser();

        setNavHeader(navigationView);
        mFragmentLoader.loadHomePageWithCurrentRidesFragment(FetchType.Local, null);
    }

    private void setNavHeader(NavigationView navigationView) {

        //This is very important as findViewById would return null if you don't do on headerview as
        // default drawerlayout view doesn't contain image view directly and its inside the headerlayout
        View headerView = navigationView.getHeaderView(0);

        mProfilePhotoImageView = headerView.findViewById(R.id.thumbnail_image);
        Picasso.with(this).load(mUser.getPhoto().getImageLocation()).into(mProfilePhotoImageView);

        mUserNameTextView = headerView.findViewById(R.id.thumbnail_name_text);
        mUserNameTextView.setText(mUser.getFirstName()+" "
                +mUser.getLastName());

        mUserEmailTextView = headerView.findViewById(R.id.user_email_text);
        mUserEmailTextView.setText(mUser.getEmail());
    }

    //This method is just to close drawer if system back button is pressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            Log.d(TAG,"On Back Pressed: Inside Drawer Open State");
        } else {
            Log.d(TAG,"On Back Pressed: Inside Else Block");
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
            Log.d(TAG, "Home Clicked");
            //First set the fetchType
            ((HomePageWithCurrentRidesFragment) getSupportFragmentManager().findFragmentByTag(HomePageWithCurrentRidesFragment.TAG))
                    .setFetchType(FetchType.Server);
            //This has to be post setting the FetchType else we will not get data refreshed from server
            removeAllBackStacks();
            //mFragmentLoader.loadHomePageWithCurrentRidesFragment(true, null);
        } else {

            //This is applicable for all items
            removeAllBackStacks();

            if (id == R.id.nav_rides) {
                Log.d(TAG, "Rides Clicked");
                mFragmentLoader.loadRidesListFragment();
            } else if (id == R.id.nav_friends) {
                Log.d(TAG, "Friends Clicked");
                mFragmentLoader.loadRideInfoFragment(new Gson().toJson(mCommonUtil.getCurrentRide()));
            } else if (id == R.id.nav_groups) {
                Log.d(TAG, "Groups Clicked");
                mFragmentLoader.loadRideRequestInfoFragment(new Gson().toJson(mCommonUtil.getCurrentRideRequest()));
            } else if (id == R.id.nav_vehicles) {
                Log.d(TAG, "Vehicles Clicked");
                mFragmentLoader.loadAddVehicleFragment(RideType.RequestRide,null);
            } else if (id == R.id.nav_preference) {
                Log.d(TAG, "Preference Clicked");
                mFragmentLoader.loadRidesOptionFragment(RideType.OfferRide, null);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        Log.d(TAG, "Backstack Count is:"+count);
        for (int i = 0; i < count; i++) {
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            String name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Log.d(TAG, "Removing Backstack [Name,Id]:"+name+","+backStackId);
            getSupportFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    @Override
    public void onHomePageWithCurrentRidesFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Blank Fragment:"+data);

    }

    @Override
    public void onCreateRideFragmentInteraction(String data) {
        Log.d(TAG, "Recieved callback post Create Rides Fragment");
        //This will clean up all back stacks and start from fresh
        //Reason for not doing popback as map was not getting reloaded properly and viewtreeobserver was not getting callback
        //which was causing old map to show up with previous markers/lines
        removeAllBackStacks();
        FragmentLoader fragmentLoader = new FragmentLoader(this);
        fragmentLoader.loadHomePageWithCurrentRidesFragment(FetchType.Server, null);
    }

    @Override
    public void onAddVehicleFragmentFragmentInteraction(Uri uri) {

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
            // We need to add a listener, as in below, so DrawerToggle will forward
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG,item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRidesOptionFragmentInteraction(Preference ridesOption, String vehicleRegistrationNumber) {

        CreateRidesFragment createRidesFragment = (CreateRidesFragment) getSupportFragmentManager().findFragmentByTag(CreateRidesFragment.TAG);
        createRidesFragment.updateRidesOption(ridesOption, vehicleRegistrationNumber);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onRideInfoFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRideRequestInfoFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUserProfileFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRidesListFragmentInteraction(String data) {

    }

    @Override
    public void onRidesListHomePageFragmentInteraction(String data) {

    }
}
