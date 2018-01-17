package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.digitusrevolution.rideshare.adapter.GroupMembershipRequestListAdapter;
import com.digitusrevolution.rideshare.adapter.UserMembershipRequestListAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.user.dto.BasicMembershipRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserMembershipRequestListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserMembershipRequestListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserMembershipRequestListFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener mScrollListener;
    private boolean mInitialDataLoaded;
    private CommonUtil mCommonUtil;
    private List<BasicMembershipRequest> mRequests = new ArrayList<>();
    private String GET_USER_MEMBERSHIP_REQUEST_URL;
    private BasicUser mUser;


    public UserMembershipRequestListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupMembershipRequestListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserMembershipRequestListFragment newInstance() {
        UserMembershipRequestListFragment fragment = new UserMembershipRequestListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        GET_USER_MEMBERSHIP_REQUEST_URL = APIUrl.GET_USER_MEMBERSHIP_REQUESTS.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()));
        loadInitialData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRecyclerView = view.findViewById(R.id.user_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        //VERY IMP - This will get called only when fragment is reloaded and without this it will show up blank screen as adapter is not set
        if (mInitialDataLoaded) {
            setAdapter();
        }

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };

        mRecyclerView.addOnScrollListener(mScrollListener);

        return view;
    }

    private void loadInitialData() {
        //Initial Data loading
        //Its important to use local variable else you will get updated string
        String URL = GET_USER_MEMBERSHIP_REQUEST_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));

        mCommonUtil.showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                mCommonUtil.dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<BasicMembershipRequest>>(){}.getType();
                mRequests = new Gson().fromJson(response.toString(), listType);
                mInitialDataLoaded = true;
                //This will load adapter only when data is loaded
                setAdapter();
            }
        });

    }

    private void setAdapter() {
        mAdapter = new UserMembershipRequestListAdapter(mRequests, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        //Its important to use local variable else you will get updated string
        String URL = GET_USER_MEMBERSHIP_REQUEST_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
        //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
        //and unnecssarily we will show multiple dialog which creates flicker on the screen
        if (offset != 1) mCommonUtil.showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (offset != 1) mCommonUtil.dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<BasicMembershipRequest>>(){}.getType();
                List<BasicMembershipRequest> newRequests = new Gson().fromJson(response.toString(), listType);
                //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                mRequests.addAll(newRequests);
                Log.d(TAG, "Membership Request Size changed. Current Size is:"+mRequests.size());
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mRequests.size()-1);
            }
        });
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
        void onUserMembershipRequestListFragmentInteraction(String data);
    }
}
