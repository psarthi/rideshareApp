package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.adapter.EndlessRecyclerViewScrollListener;
import com.digitusrevolution.rideshare.adapter.GroupInviteUserSearchListAdapter;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.app.GroupInviteUserSearchResultWrapper;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.digitusrevolution.rideshare.model.user.dto.GroupInviteUserSearchResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchUserForGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchUserForGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchUserForGroupFragment extends BaseFragment {

    public static final String TAG = SearchUserForGroupFragment.class.getName();
    public static final String TITLE = "Search User";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_DETAIL = "groupDetail";

    // TODO: Rename and change types of parameters
    private String mGroupDetailData;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private String SEARCH_URL;
    private String SEARCH_URL_WITH_QUERY;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<GroupInviteUserSearchResultWrapper> mUserSearchResultsWrappers;
    private GroupDetail mGroupDetail;


    public SearchUserForGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupDetail GroupDetail in Json format
     * @return A new instance of fragment SearchUserForGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchUserForGroupFragment newInstance(String groupDetail) {
        SearchUserForGroupFragment fragment = new SearchUserForGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_DETAIL, groupDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupDetailData = getArguments().getString(ARG_GROUP_DETAIL);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mGroupDetail = new Gson().fromJson(mGroupDetailData, GroupDetail.class);
        SEARCH_URL = APIUrl.SEARCH_USER_FOR_GROUP_INVITE.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()))
                .replace(APIUrl.GROUP_ID_KEY,Integer.toString(mGroupDetail.getId()));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = view.findViewById(R.id.search_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

        final SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //IMP - Its important not to overwrite the original search url else you will loose the key in the url
                //and any subsequent replacement of query would not work as key has been overwritten
                SEARCH_URL_WITH_QUERY = SEARCH_URL.replace(APIUrl.SEARCH_NAME_KEY, query);
                loadInitialSearchResult();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Don't keep calling the adaper, as it will fire too many queries
                return false;
            }
        });

        // Catch event on [x] button inside search view
        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manage this event.
                //This will reset the query
                searchView.setQuery("",false);
                Log.d(TAG, "Search View - Clicked on close");
                if (mUserSearchResultsWrappers!=null) mUserSearchResultsWrappers.clear();
                if (mAdapter!=null) mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void loadInitialSearchResult() {
        String URL = SEARCH_URL_WITH_QUERY.replace(APIUrl.PAGE_KEY, Integer.toString(0));
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Type listType = new TypeToken<ArrayList<GroupInviteUserSearchResult>>() {}.getType();
                List<GroupInviteUserSearchResult> userSearchResults = new Gson().fromJson(response.toString(), listType);
                //IMP - This will ensure we get clean linkedList whenever a search is performed, else it will keep adding to the main list on subsequent search
                mUserSearchResultsWrappers = new LinkedList<>();
                for (GroupInviteUserSearchResult userSearchResult: userSearchResults) {
                    mUserSearchResultsWrappers.add(getWrapper(userSearchResult));
                }
                mAdapter = new GroupInviteUserSearchListAdapter(mUserSearchResultsWrappers, SearchUserForGroupFragment.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    private GroupInviteUserSearchResultWrapper getWrapper(GroupInviteUserSearchResult userSearchResult) {
            GroupInviteUserSearchResultWrapper userSearchResultWrapper = new GroupInviteUserSearchResultWrapper();
            userSearchResultWrapper.setUser(userSearchResult.getUser());
            userSearchResultWrapper.setMember(userSearchResult.isMember());
            userSearchResultWrapper.setInvited(userSearchResult.isInvited());
            return userSearchResultWrapper;
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
        String URL = SEARCH_URL_WITH_QUERY.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
        //This will ensure we don't show progress dialog on first page load as its called on the initial load itself
        //and unnecssarily we will show multiple dialog which creates flicker on the screen
        if (offset != 1) mCommonUtil.showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (offset != 1) mCommonUtil.dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<GroupInviteUserSearchResult>>(){}.getType();
                List<GroupInviteUserSearchResult> newSearchResults = new Gson().fromJson(response.toString(), listType);
                //Since object is pass by reference, so when you drawable.add in mRides, this will be reflected everywhere
                //mUserSearchResults.addAll(newSearchResults);
                for (GroupInviteUserSearchResult userSearchResult: newSearchResults) {
                    mUserSearchResultsWrappers.add(getWrapper(userSearchResult));
                }
                Log.d(TAG, "User Size changed. Current Size is:"+mUserSearchResultsWrappers.size());
                mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mUserSearchResultsWrappers.size()-1);
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
    public void onResume() {
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(true);
        getActivity().setTitle(TITLE);
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
        void onSearchUserForGroupFragmentInteraction(String data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_item);
        item.setTitle("Invite");

        /* Keeping this for reference purpose
        inflater.inflate(R.menu.option_menu_search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
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
        Log.d(TAG,"Selected Item is:"+item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item) {
            Log.d(TAG, "Invite Clicked");
            sendInvite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendInvite() {
        ArrayList<Integer> userIds = new ArrayList<>();
        //There is possibility user may click on Join even before any search, which will prevent from NPE
        //as Wrapper will only get initialized on first result set
        if (mUserSearchResultsWrappers!=null){
            for (GroupInviteUserSearchResultWrapper userSearchResultWrapper:mUserSearchResultsWrappers){
                Log.d(TAG, "User Selected:"+userSearchResultWrapper.getUser().getFirstName()+"-"+userSearchResultWrapper.isSelected());
                if (userSearchResultWrapper.isSelected()){
                    Log.d(TAG, "Adding to the invite list");
                    userIds.add(userSearchResultWrapper.getUser().getId());
                }
            }
        }
        if (userIds.size() > 0){
            String URL = APIUrl.INVITE_USER.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()))
                    .replace(APIUrl.GROUP_ID_KEY,Integer.toString(mGroupDetail.getId()));
            RESTClient.post(getActivity(), URL, userIds, new RSJsonHttpResponseHandler(mCommonUtil){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Toast.makeText(getActivity(), "Invite succesfully sent", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No User Selected", Toast.LENGTH_SHORT).show();
        }
    }

}
