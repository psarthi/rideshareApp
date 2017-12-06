package com.digitusrevolution.rideshare.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.AddVehicleFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.digitusrevolution.rideshare.fragment.CreateRidesFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.fragment.RidesOptionFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.domain.Preference;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

public class HomePageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomePageWithCurrentRidesFragment.OnFragmentInteractionListener,
        CreateRidesFragment.OnFragmentInteractionListener,
        AddVehicleFragment.OnFragmentInteractionListener,
        RidesOptionFragment.OnFragmentInteractionListener,
        RideInfoFragment.OnFragmentInteractionListener,
        RideRequestInfoFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener{

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
        mFragmentLoader.loadHomePageWithCurrentRidesFragment();
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
        //This will remove all the fragment from backstack and would start from clean slate
        //So that we don't have too many backstacks items when user clicks on navigation items from anywhere
        while(getSupportFragmentManager().popBackStackImmediate()){
            Log.d(TAG,"Backstack Entry Count is:"+getSupportFragmentManager().getBackStackEntryCount());
        };
        Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();
        if (id == R.id.nav_home) {
            mFragmentLoader.loadHomePageWithCurrentRidesFragment();
        } else if (id == R.id.nav_rides) {
            mFragmentLoader.loadRideInfoFragment();

        } else if (id == R.id.nav_payments) {
            mFragmentLoader.loadRideRequestInfoFragment();
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_vehicles) {
            mFragmentLoader.loadAddVehicleFragment(RideType.RequestRide,null);
        } else if (id == R.id.nav_preference) {
            mFragmentLoader.loadRidesOptionFragment(RideType.OfferRide, null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onHomePageWithCurrentRidesFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Blank Fragment:"+data);

    }

    @Override
    public void onCreateRideFragmentInteraction(Uri uri) {

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
}
