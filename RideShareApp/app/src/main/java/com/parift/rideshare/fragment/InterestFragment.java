package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parift.rideshare.R;
import com.parift.rideshare.adapter.InterestWrapperAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.GridAutoFitLayoutManager;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.app.BasicInterestWrapper;
import com.parift.rideshare.model.user.dto.BasicInterest;
import com.parift.rideshare.model.user.dto.BasicUser;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InterestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InterestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterestFragment extends BaseFragment {
    public static final String TAG = InterestFragment.class.getName();
    public static final String TITLE = "Interests";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;


    public InterestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InterestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InterestFragment newInstance(String param1, String param2) {
        InterestFragment fragment = new InterestFragment();
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
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        //This will append items option menu by invoking fragment onCreateOptionMenu
        //So that you can have customer option menu for each fragment
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        mRecyclerView = view.findViewById(R.id.interest_list);
        GridAutoFitLayoutManager layoutManager = new GridAutoFitLayoutManager(getActivity(), 100);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mCommonUtil.showProgressDialog();
        RESTClient.get(APIUrl.GET_INTERESTS_URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                mCommonUtil.dismissProgressDialog();
                Type listType = new TypeToken<ArrayList<BasicInterestWrapper>>() {}.getType();
                List<BasicInterestWrapper> interestWrappers = new Gson().fromJson(response.toString(), listType);

                Collection<BasicInterest> userInterests = mUser.getInterests();
                for (BasicInterestWrapper interestWrapper: interestWrappers){
                    for (BasicInterest basicInterest: userInterests){
                        if (interestWrapper.getId() == basicInterest.getId()){
                            interestWrapper.setSelected(true);
                            break;
                        }
                    }
                }

                mAdapter = new InterestWrapperAdapter(interestWrappers, InterestFragment.this);
                mRecyclerView.setAdapter(mAdapter);

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.option_menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_item);
        item.setIcon(R.drawable.ic_tick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Logger.debug(TAG,"Selected Item is-"+item.getTitle().toString());
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item) {
            Logger.debug(TAG, "Save Clicked");
            //TODO on Save
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            //VERY IMP - Reason for storing as member variable instead as there are situations
            //when fragment is partially loaded and then immediately detached which in turn would
            //return null when you call mBaseFragment.getActivity .e.g in CommonUtil getSharedPreference function
            //So if we store as member variable, then we can reference al activity related resource even though
            //fragment is not visible
            //Scenario where it was failing - Wallet Fragment was loaded, then detached immediately as it was not visible
            //and in the same flow setAdapater was called which in turn try to get mUser which calls mBaseFragment.getActivity
            //it was throwing NPE
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
        void onInterestFragmentInteraction(String data);
    }
}
