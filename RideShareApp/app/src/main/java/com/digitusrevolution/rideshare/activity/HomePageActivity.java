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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.DummyFragment;
import com.digitusrevolution.rideshare.fragment.HomePageWithCurrentRidesFragment;
import com.digitusrevolution.rideshare.fragment.OfferRideFragment;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class HomePageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomePageWithCurrentRidesFragment.OnFragmentInteractionListener,
        OfferRideFragment.OnFragmentInteractionListener,
        DummyFragment.OnFragmentInteractionListener {

    private static final String TAG = HomePageActivity.class.getName();

    private Toolbar mToolbar;
    private UserSignInResult mUserSignInResult;
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

        Log.d(TAG,"Access Token retrieved from mCommonFunctions:"+ getAccessToken());
        Intent intent = getIntent();
        String data = intent.getStringExtra(getExtraDataKey());
        mUserSignInResult = new Gson().fromJson(data,UserSignInResult.class);
        Log.d(TAG,"Token from Intent:" + mUserSignInResult.getToken());
        setNavHeader(navigationView);
        loadHomePageWithCurrentRidesFragment();
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
            loadHomePageWithCurrentRidesFragment();
        } else if (id == R.id.nav_rides) {
            loadDummyFragment();

        } else if (id == R.id.nav_payments) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_vehicles) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomePageWithCurrentRidesFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomePageWithCurrentRidesFragment homePageWithCurrentRidesFragment = HomePageWithCurrentRidesFragment.
                newInstance(HomePageWithCurrentRidesFragment.TITLE, new Gson().toJson(mUserSignInResult));
        //Don't add to backstack else it will display blank container on back press which is the initial stage of activity
        fragmentTransaction.replace(R.id.home_page_container, homePageWithCurrentRidesFragment, HomePageWithCurrentRidesFragment.TAG);
        fragmentTransaction.commit();
    }

    private void loadDummyFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DummyFragment dummyFragment = DummyFragment.newInstance("Dummy Data 1st Param","Dummy Data 2nd Param");
        fragmentTransaction.add(R.id.home_page_container, dummyFragment, DummyFragment.TAG).addToBackStack(DummyFragment.TAG);
        fragmentTransaction.commit();
        mToolbar.setTitle("DummyFragment");
    }

    @Override
    public void onHomePageWithCurrentRidesFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Blank Fragment:"+data);
        loadDummyFragment();

    }

    @Override
    public void onDummyFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Ride Fragment:"+data);
    }

    @Override
    public void onOfferRideFragmentInteraction(Uri uri) {

    }

}
