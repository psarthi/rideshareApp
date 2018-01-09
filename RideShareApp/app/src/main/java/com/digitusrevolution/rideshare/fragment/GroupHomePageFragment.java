package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.adapter.GroupViewPagerAdapter;
import com.digitusrevolution.rideshare.component.FragmentLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupHomePageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupHomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupHomePageFragment extends BaseFragment {

    public static final String TAG = GroupHomePageFragment.class.getName();
    public static final String TITLE = "Groups";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private GroupViewPagerAdapter mGroupViewPagerAdapter;

    public GroupHomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupHomePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupHomePageFragment newInstance(String param1, String param2) {
        GroupHomePageFragment fragment = new GroupHomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mGroupViewPagerAdapter = new GroupViewPagerAdapter(getChildFragmentManager());
        //This will append items option menu by invoking fragment onCreateOptionMenu
        //So that you can have customer option menu for each fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_group_home_page, container, false);

        TabLayout tabLayout = view.findViewById(R.id.group_tab);
        final ViewPager viewPager = view.findViewById(R.id.group_viewPager);
        FloatingActionButton createGroupFab = view.findViewById(R.id.create_group);

        createGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(mGroupViewPagerAdapter);

        tabLayout.getTabAt(0).setText("My Groups");
        tabLayout.getTabAt(1).setText("Invites");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Unselected:"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Reselected:"+tab.getText());
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(false);
        showBackStackDetails();
        getActivity().setTitle(TITLE);
        hideSoftKeyBoard();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onGroupHomePageFragmentInteraction(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGroupHomePageFragmentInteraction(String data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_search_item, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        /*
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"Search Query is:"+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG,"Selected Item is-"+item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Log.d(TAG, "Search Clicked");
            FragmentLoader fragmentLoader = new FragmentLoader(GroupHomePageFragment.this);
            fragmentLoader.loadSearchFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
