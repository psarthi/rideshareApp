package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.parift.rideshare.adapter.GroupListAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.user.dto.GroupListType;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
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
    private GroupListType mGroupListType;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private TextView mEmptyTextView;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener mScrollListener;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private List<GroupDetail> mGroups = new ArrayList<>();
    private String GET_USER_GROUPS_URL;

    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupListType Group Result Type (e.g. All, Member, Invite)
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance(GroupListType groupListType, String param2) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_RESULT_TYPE, groupListType.toString());
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupListType = GroupListType.valueOf(getArguments().getString(ARG_GROUP_RESULT_TYPE));
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        GET_USER_GROUPS_URL = APIUrl.GET_USER_GROUPS.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.GROUP_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.GROUP_LIST_TYPE_KEY,mGroupListType.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        Logger.debug(TAG, "Group Result Type is:"+ mGroupListType);
        mRecyclerView = view.findViewById(R.id.group_list);
        mEmptyTextView = view.findViewById(R.id.empty_result_text);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);


        //VERY IMP - Ensure you load the data from the server whenever we create the view, so that we always have updated set of data
        //Don't set the adapter directly otherwise you will end up with old data set
        loadInitialData();

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
        String URL = GET_USER_GROUPS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));

        mCommonUtil.showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    Logger.debug(TAG, "Raw Response:" + response.toString());
                    mCommonUtil.dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<GroupDetail>>() {
                    }.getType();
                    mGroups = new Gson().fromJson(response.toString(), listType);
                    if (mGroups.size() != 0) {
                        //This will ensure that whenever user membership is approved and if user is already
                        //logged in so once he checks his list of groups, then his grouo membership status in shared
                        //preference would get updated which will be used by create ride use case
                        mCommonUtil.updateIsUserGroupMember(true);
                    }
                    for (GroupDetail groupDetail : mGroups) {
                        Logger.debug(TAG, "Original Groups:" + new Gson().toJson(groupDetail));
                    }
                    Logger.debug(TAG, "Size of initial set of data is: " + mGroups.size());
                    //This will load adapter only when data is loaded
                    setAdapter();
                }
            }
        });
    }

    private void setAdapter() {
        mAdapter = new GroupListAdapter(mGroups, this);
        mRecyclerView.setAdapter(mAdapter);
        if (mGroups.size()==0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(final int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        String URL = GET_USER_GROUPS_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));

        //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
        //and unnecssarily we will show multiple dialog which creates flicker on the screen
        if (offset != 1) mCommonUtil.showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    if (offset != 1) mCommonUtil.dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<GroupDetail>>() {
                    }.getType();
                    List<GroupDetail> newGroups = new Gson().fromJson(response.toString(), listType);
                    //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                    mGroups.addAll(newGroups);
                    Logger.debug(TAG, "Size of new set of data is: " + newGroups.size() + " :Updated count is:" + mGroups.size());
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mGroups.size() - 1);
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
            mActivity = (FragmentActivity) context;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
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
