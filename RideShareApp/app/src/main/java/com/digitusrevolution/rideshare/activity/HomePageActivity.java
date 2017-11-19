package com.digitusrevolution.rideshare.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.fragment.HomePageWithNoRidesFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithRideFragment;
import com.digitusrevolution.rideshare.fragment.OfferRideFragment;
import com.digitusrevolution.rideshare.helper.CommonFunctions;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomePageWithNoRidesFragment.OnFragmentInteractionListener,
        OfferRideFragment.OnFragmentInteractionListener,
        HomePageWithRideFragment.OnFragmentInteractionListener {

    private static final String TAG = HomePageActivity.class.getName();

    private Toolbar mToolbar;
    private String mExtraKeyName;
    private UserSignInResult mUserSignInResult;
    private CommonFunctions mCommonFunctions;
    private ImageView mProfilePhotoImageView;
    private TextView mUserNameTextView;
    private TextView mUserEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //This will set up action bar for this activity
        setSupportActionBar(mToolbar);

        //This will show up Hamburger icon on action bar and on click of it, drawer would open up
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //This will set up listener on Navigation items
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mCommonFunctions = new CommonFunctions(this);
        Log.d(TAG,"Access Token retrieved from mCommonFunctions:"+ mCommonFunctions.getAccessToken());
        getExtraFromIntent();
        setNavHeader(navigationView);
        loadHomePageWithNoRidesFragment();
    }

    private void setNavHeader(NavigationView navigationView) {

        //This is very important as findViewById would return null if you don't do on headerview as
        // default drawerlayout view doesn't contain image view directly and its inside the headerlayout
        View headerView = navigationView.getHeaderView(0);

        mProfilePhotoImageView = headerView.findViewById(R.id.userPhotoImageView);
        Picasso.with(this).load(mUserSignInResult.getUserProfile().getPhoto().getImageLocation()).into(mProfilePhotoImageView);

        mUserNameTextView = headerView.findViewById(R.id.userNameTextView);
        mUserNameTextView.setText(mUserSignInResult.getUserProfile().getFirstName()+" "
                +mUserSignInResult.getUserProfile().getLastName());

        mUserEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        mUserEmailTextView.setText(mUserSignInResult.getUserProfile().getEmail());
    }

    private void getExtraFromIntent() {
        Intent intent = getIntent();
        mExtraKeyName = intent.getStringExtra(Constant.INTENT_EXTRA_KEY);
        String data = intent.getStringExtra(mExtraKeyName);
        mUserSignInResult = new Gson().fromJson(data,UserSignInResult.class);
        Log.d(TAG,"Token from Intent:" + mUserSignInResult.getToken());
    }

    //This method is just to close drawer if system back button is pressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();
        if (id == R.id.nav_home) {
            loadHomePageWithNoRidesFragment();
        } else if (id == R.id.nav_rides) {
            loadHomePageWithRideFragment();

        } else if (id == R.id.nav_payments) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_vehicles) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomePageWithNoRidesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomePageWithNoRidesFragment homePageWithNoRidesFragment = HomePageWithNoRidesFragment.newInstance(HomePageWithNoRidesFragment.TITLE, null);
        //Don't add to backstack else it will display blank container on back press which is the initial stage of activity
        fragmentTransaction.add(R.id.home_page_container, homePageWithNoRidesFragment,HomePageWithNoRidesFragment.TAG);
        fragmentTransaction.commit();
    }

    private void loadHomePageWithRideFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomePageWithRideFragment homePageWithRideFragment = HomePageWithRideFragment.newInstance("Dummy Data 1st Param","Dummy Data 2nd Param");
        fragmentTransaction.add(R.id.home_page_container, homePageWithRideFragment, HomePageWithRideFragment.TAG).addToBackStack(HomePageWithRideFragment.TAG);
        fragmentTransaction.commit();
        mToolbar.setTitle("RideFragment");
    }

    @Override
    public void onHomePageWithNoRidesFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Blank Fragment:"+data);
        loadHomePageWithRideFragment();

    }

    @Override
    public void onHomePageWithRideFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Ride Fragment:"+data);
    }

    @Override
    public void onOfferRideFragmentInteraction(Uri uri) {

    }

}
