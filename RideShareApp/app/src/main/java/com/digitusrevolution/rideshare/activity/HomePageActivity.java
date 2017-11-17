package com.digitusrevolution.rideshare.activity;



import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BlankHomePageFragment;
import com.digitusrevolution.rideshare.fragment.RideHomePageFragment;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BlankHomePageFragment.OnFragmentInteractionListener, RideHomePageFragment.OnFragmentInteractionListener{

    private static final String TAG = HomePageActivity.class.getName();
    private Toolbar mToolbar;

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
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            BlankHomePageFragment blankHomePageFragment = new BlankHomePageFragment();
            fragmentTransaction.add(R.id.content_home, blankHomePageFragment).addToBackStack(null);
            fragmentTransaction.commit();
            mToolbar.setTitle("BlankFragment");

        } else if (id == R.id.nav_rides) {
            loadFragmentB();

        } else if (id == R.id.nav_payments) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_vehicles) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragmentB() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RideHomePageFragment rideHomePageFragment = RideHomePageFragment.newInstance("Dummy Data 1st Param","Dummy Data 2nd Param");
        fragmentTransaction.add(R.id.content_home, rideHomePageFragment).addToBackStack(null);
        fragmentTransaction.commit();
        mToolbar.setTitle("RideFragment");
    }

    @Override
    public void onBlankHomePageFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Blank Fragment:"+data);
        loadFragmentB();

    }

    @Override
    public void onRideHomePageFragmentInteraction(String data) {
        Log.d(TAG,"Value returned to activity from Ride Fragment:"+data);
    }
}
