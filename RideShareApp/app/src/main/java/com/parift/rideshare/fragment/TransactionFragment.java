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
import com.parift.rideshare.adapter.TransactionAdapter;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.billing.domain.core.Account;
import com.parift.rideshare.model.billing.domain.core.Transaction;
import com.parift.rideshare.model.user.dto.BasicUser;
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
 * {@link TransactionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = TransactionFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private TextView mEmptyTextView;
    private CommonUtil mCommonUtil;
    private List<Transaction> mTransactions = new ArrayList<>();
    private Account mAccount;
    private BasicUser mUser;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener mScrollListener;
    private String GET_USER_WALLET_TRANSACTION_URL;

    public TransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1, String param2) {
        Logger.debug(TAG, "newInstance Called");
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.debug(TAG, "onCreate Called of instance:"+this.hashCode());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mAccount = mCommonUtil.getAccount();
        GET_USER_WALLET_TRANSACTION_URL = APIUrl.GET_USER_WALLET_TRANSACTION.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.ACCOUNT_NUMBER_KEY, Long.toString(mAccount.getNumber()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreateView Called of instance:"+this.hashCode());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        mRecyclerView = view.findViewById(R.id.transaction_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEmptyTextView = view.findViewById(R.id.empty_result_text);

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

    @Override
    public void onResume() {
        super.onResume();
        Logger.debug(TAG,"Inside OnResume of instance:"+this.hashCode());
    }

    private void loadInitialData() {
        Logger.debug(TAG, "loadInitialData Called of instance:"+this.hashCode());
        //Initial Data loading

        String URL = GET_USER_WALLET_TRANSACTION_URL.replace(APIUrl.PAGE_KEY, Integer.toString(0));
        //showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    //dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<Transaction>>() {
                    }.getType();
                    mTransactions = new Gson().fromJson(response.toString(), listType);
                    setAdapter();
                    //This will load adapter only when data is loaded
                }
            }
        });
    }

    private void setAdapter() {
        Logger.debug(TAG, "Setting Adapter of Transaction instance:"+this.hashCode());
        mAdapter = new TransactionAdapter(mTransactions, this);
        mRecyclerView.setAdapter(mAdapter);
        if (mTransactions.size()==0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
        }

    }
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        Logger.debug(TAG, "loadNextDataFromApi Called of instance:"+this.hashCode());
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        String URL = GET_USER_WALLET_TRANSACTION_URL.replace(APIUrl.PAGE_KEY, Integer.toString(offset));
        //showProgressDialog();
        RESTClient.get(URL, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    //dismissProgressDialog();
                    Type listType = new TypeToken<ArrayList<Transaction>>() {
                    }.getType();
                    List<Transaction> transactions = new Gson().fromJson(response.toString(), listType);
                    mTransactions.addAll(transactions);
                    Logger.debug(TAG, "Transaction Size changed. Current Size is:" + mTransactions.size());
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), mTransactions.size() - 1);
                }
            }
        });
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
        Logger.debug(TAG, "onDetach Called of instance:"+this.hashCode());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.debug(TAG, "Inside Destroy View of instance:"+this.hashCode());
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
        void onTransactionFragmentInteraction(String data);
    }
}
