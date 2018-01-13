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
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.digitusrevolution.rideshare.adapter.GroupListAdapter;
import com.digitusrevolution.rideshare.adapter.RideListAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.app.GroupResultType;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_RESULT_TYPE = "groupResultType";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private GroupResultType mGroupResultType;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener mScrollListener;
    private boolean mInitialDataLoaded;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private List<GroupDetail> mGroups = new ArrayList<>();

    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupResultType Group Result Type (e.g. All, Member, Invite)
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance(GroupResultType groupResultType, String param2) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_RESULT_TYPE, groupResultType.toString());
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupResultType = GroupResultType.valueOf(getArguments().getString(ARG_GROUP_RESULT_TYPE));
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        loadInitialData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        Log.d(TAG, "Group Result Type is:"+mGroupResultType);
        mRecyclerView = view.findViewById(R.id.group_list);

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
        String GET_USER_GROUPS = APIUrl.GET_USER_GROUPS.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()))
                .replace(APIUrl.PAGE_KEY, Integer.toString(0));

        showProgressDialog();
        RESTClient.get(GET_USER_GROUPS, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<GroupDetail>>(){}.getType();
                mGroups = new Gson().fromJson(response.toString(), listType);
                mInitialDataLoaded = true;
                //This will load adapter only when data is loaded
                setAdapter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dismissProgressDialog();
                if (errorResponse!=null) {
                    ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                    Log.d(TAG, errorMessage.getErrorMessage());
                    Toast.makeText(getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                    Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void setAdapter() {
        mAdapter = new GroupListAdapter(mGroups, this);
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
        String GET_USER_GROUPS = APIUrl.GET_USER_GROUPS.replace(APIUrl.ID_KEY,Integer.toString(mUser.getId()))
                .replace(APIUrl.PAGE_KEY, Integer.toString(offset));
        //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
        //and unnecssarily we will show multiple dialog which creates flicker on the screen
        if (offset != 1) showProgressDialog();
        RESTClient.get(GET_USER_GROUPS, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (offset != 1) dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<GroupDetail>>(){}.getType();
                List<GroupDetail> newGroups = new Gson().fromJson(response.toString(), listType);
                //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                mGroups.addAll(newGroups);
                Log.d(TAG, "Group Size changed. Current Size is:"+mGroups.size());
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mGroups.size()-1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (offset != 1) dismissProgressDialog();
                if (errorResponse!=null) {
                    ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                    Log.d(TAG, errorMessage.getErrorMessage());
                    Toast.makeText(getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                    Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onGroupListFragmentInteraction(data);
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
        void onGroupListFragmentInteraction(String data);
    }
}
