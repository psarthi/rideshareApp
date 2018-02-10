package com.parift.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.billing.domain.core.Account;
import com.parift.rideshare.model.common.ErrorMessage;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RedeemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RedeemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RedeemFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RedeemFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private Account mAccount;
    private TextView mWalletBalance;
    private TextView mRedeemAmount;
    private String mCurrencySymbol;


    private OnFragmentInteractionListener mListener;

    public RedeemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RedeemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RedeemFragment newInstance(String param1, String param2) {
        RedeemFragment fragment = new RedeemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreate Called");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mCurrencySymbol = mCommonUtil.getCurrencySymbol(mUser.getCountry());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreateView Called");
        //This will ensure we will get updated Amount on wallet top ups
        mAccount = mCommonUtil.getAccount();
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_redeem, container, false);
        mWalletBalance = view.findViewById(R.id.wallet_balance_amount);
        mRedeemAmount = view.findViewById(R.id.redeem_amount);
        setWalletBalance(mAccount.getBalance());

        setRedeemButtonListener(view);

        return view;
    }

    private void setRedeemButtonListener(final View view) {
        //TODO Implement redemption with Paytm

        view.findViewById(R.id.redeem_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String redeemAmount = ((TextView) view.findViewById(R.id.redeem_amount)).getText().toString();
                if (validateInput(redeemAmount)){
                    String REDEEM_MONEY = APIUrl.REDEEM_MONEY.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                            .replace(APIUrl.ACCOUNT_NUMBER_KEY, Long.toString(mAccount.getNumber()))
                            .replace(APIUrl.AMOUNT_KEY, redeemAmount);
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(REDEEM_MONEY, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            mAccount = new Gson().fromJson(response.toString(), Account.class);
                            mCommonUtil.updateAccount(mAccount);
                            //This will refresh the wallet balance
                            setWalletBalance(mAccount.getBalance());
                        }
                    });
                }
            }
        });
    }

    private boolean validateInput(String redeemAmount){
        if (redeemAmount.equals("")){
            Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (Integer.parseInt(redeemAmount) == 0) {
                Toast.makeText(getActivity(), "Please enter the valid amount.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void setWalletBalance(float balance) {
        String balanceString = mCurrencySymbol + mCommonUtil.getDecimalFormattedString(balance);
        mWalletBalance.setText(balanceString);
        String hint = getResources().getString(R.string.amount_hint)+" ("+mCurrencySymbol+")";
        mRedeemAmount.setHint(hint);
        mRedeemAmount.setText("");
    }


    @Override
    public void onResume() {
        super.onResume();
        Logger.debug(TAG,"Inside OnResume");
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
        void onRedeemFragmentRefresh();
    }
}
