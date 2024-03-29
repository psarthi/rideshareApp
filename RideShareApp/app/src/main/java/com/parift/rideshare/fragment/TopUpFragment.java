package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.billing.domain.core.Account;
import com.parift.rideshare.model.billing.dto.TopUpResponse;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopUpFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REQD_BALANCE_VISIBILITY = "requiredBalanceVisiblity";
    private static final String ARG_REQD_BALANCE_AMOUNT = "requiredBalanceAmount";
    private static final String TAG = TopUpFragment.class.getName();

    private boolean mRequiredBalanceVisiblity;
    private float mRequiredBalanceAmount;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private Account mAccount;
    private TextView mWalletBalanceTextView;
    private TextView mTopUpAmountTextView;
    private String mCurrencySymbol;
    private int mMinTopUpAmount;
    private TextView mRequiredBalanceTextView;
    private String mTopUpAmount;

    public TopUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requiredBalanceVisiblity Do we need to show required balance amount text
     * @param requiredBalanceAmount Required balance amount
     * @return A new instance of fragment TopUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopUpFragment newInstance(boolean requiredBalanceVisiblity, float requiredBalanceAmount) {
        TopUpFragment fragment = new TopUpFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_REQD_BALANCE_VISIBILITY, requiredBalanceVisiblity);
        args.putFloat(ARG_REQD_BALANCE_AMOUNT, requiredBalanceAmount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG,"onCreate Called");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequiredBalanceVisiblity = getArguments().getBoolean(ARG_REQD_BALANCE_VISIBILITY);
            mRequiredBalanceAmount = getArguments().getFloat(ARG_REQD_BALANCE_AMOUNT);
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
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        mWalletBalanceTextView = view.findViewById(R.id.wallet_balance_amount);
        mTopUpAmountTextView = view.findViewById(R.id.topup_amount);
        mRequiredBalanceTextView = view.findViewById(R.id.required_wallet_balance);

        if (mRequiredBalanceVisiblity){
            String reqdAmount = getResources().getString(R.string.required_wallet_balance_label) +
                    mCurrencySymbol + mCommonUtil.getDecimalFormattedString(mRequiredBalanceAmount);
            mRequiredBalanceTextView.setText(reqdAmount);
            Logger.debug(TAG, "Required Min Balance:"+mRequiredBalanceAmount);
            //Reason for adding 1 is to just take care of decimals
            mMinTopUpAmount = (int) (mRequiredBalanceAmount - mAccount.getBalance() + 1);

            /* Commented this as have enabled back button on the toolbar
            view.findViewById(R.id.top_up_cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyBoard();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            */

        } else {
            mRequiredBalanceTextView.setVisibility(View.GONE);
            //view.findViewById(R.id.top_up_cancel_button).setVisibility(View.GONE);
        }

        setAddMoneyButtonListener(view);
        //This should be post mTopUp amount calculation in case of ride request top up flow, so that we can show proper amount
        setWalletBalance(mAccount.getBalance());

        return view;
    }

    private void setWalletBalance(float balance) {
        String balanceString = mCurrencySymbol + mCommonUtil.getDecimalFormattedString(balance);
        mWalletBalanceTextView.setText(balanceString);
        String hint = getResources().getString(R.string.amount_hint)+" ("+mCurrencySymbol+")";
        if (mRequiredBalanceVisiblity){
            hint = getResources().getString(R.string.min_amount_hint)+" ("+mCurrencySymbol+mMinTopUpAmount+")";
        }
        mTopUpAmountTextView.setHint(hint);
        mTopUpAmountTextView.setText("");
    }

    private void setAddMoneyButtonListener(final View view) {
        view.findViewById(R.id.add_money_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add Money via PayTm
                mTopUpAmount = ((TextView) view.findViewById(R.id.topup_amount)).getText().toString();
                Logger.debug(TAG, "Top Up Amount:"+mTopUpAmount);
                if (validateInput(mTopUpAmount)) {

                    String url = APIUrl.GET_ORDER_INFO.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                            .replace(APIUrl.AMOUNT_KEY, mTopUpAmount);

                    mCommonUtil.showProgressDialog();
                    RESTClient.get(url , null, new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            if (isAdded()) {
                                Type type = new TypeToken<Map<String, String>>(){}.getType();
                                Map<String, String> paramMap = new Gson().fromJson(response.toString(), type);
                                Logger.debug(TAG, "PayTM Param is:"+paramMap);
                                onStartTransaction(paramMap);
                            }
                        }
                    });
                }
            }
        });
    }

    private void resetRequiredBalanceViews(){
        //Below commented line will not come into effect as fragment would get reloaded
        // and it will again get visiblility so that's fine for now.
        //mRequiredBalanceTextView.setVisibility(View.GONE);
        String hint = getResources().getString(R.string.amount_hint)+" ("+mCurrencySymbol+")";
        mTopUpAmountTextView.setHint(hint);
        mTopUpAmountTextView.setText("");
    }

    private boolean validateInput(String topUpAmountString){
        if (topUpAmountString.equals("")){
            Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            int topUpAmount = Integer.parseInt(topUpAmountString);
            if (topUpAmount < mMinTopUpAmount) {
                Toast.makeText(getActivity(), "Min. Top up amount is " + mCurrencySymbol + mMinTopUpAmount, Toast.LENGTH_LONG).show();
                return false;
            }
            if (topUpAmount == 0) {
                Toast.makeText(getActivity(), "Please enter the valid amount.", Toast.LENGTH_LONG).show();
                return false;
            }
            if (topUpAmount > Constant.MAX_TOPUP_AMOUNT){
                Toast.makeText(getActivity(), "Max. Top up amount is "+mCurrencySymbol + Constant.MAX_TOPUP_AMOUNT, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void onStartTransaction(final Map<String, String> paramMap) {

        PaytmPGService Service = null;

        if (APIUrl.ENV.equals("DEVELOPMENT")) {
            Service = PaytmPGService.getStagingService();
        }
        if (APIUrl.ENV.equals("PRODUCTION")) {
            Service = PaytmPGService.getProductionService();
        }

        final PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(getActivity(), true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Logger.debug(TAG, "someUIErrorOccurred" + inErrorMessage);
                        Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Logger.debug(TAG, "Payment Transaction Response in Bundle: " + inResponse);
                        Set<String> keys = inResponse.keySet();
                        Map<String, String> paramMap = new HashMap<>();
                        for (String key: keys){
                            paramMap.put(key, inResponse.get(key).toString());
                        }
                        Logger.debug(TAG, "PayTM Response in HashMap:"+paramMap.toString());
                        validateAndProcessPayment(paramMap);
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                        Logger.debug(TAG, "networkNotAvailable");
                        Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Logger.debug(TAG, "clientAuthenticationFailed" + inErrorMessage);
                        Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Logger.debug(TAG, "onErrorLoadingWebPage:" + iniErrorCode +":"+inErrorMessage);
                        Toast.makeText(getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Logger.debug(TAG, "User Cancels the transaction");
                        String url = APIUrl.CANCEL_FINANCIAL_TRANSACTION.replace(APIUrl.ORDER_ID_KEY, Order.getRequestParamMap().get("ORDER_ID"))
                                .replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()));
                        mCommonUtil.showProgressDialog();
                        RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                mCommonUtil.dismissProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Logger.debug(TAG, "Payment Transaction Failed " + inErrorMessage);
                    }

                });
    }

    private void validateAndProcessPayment(Map<String, String> paramMap) {
        String url = APIUrl.VALIDATE_AND_PROCESS_PAYMENT.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.ACCOUNT_NUMBER_KEY, Long.toString(mAccount.getNumber()));
        mCommonUtil.showProgressDialog();
        RESTClient.post(getActivity(), url, paramMap, new RSJsonHttpResponseHandler(mCommonUtil) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    Logger.debug(TAG, "TopupResponse:"+response.toString());
                    mCommonUtil.dismissProgressDialog();
                    TopUpResponse topUpResponse = new Gson().fromJson(response.toString(), TopUpResponse.class);
                    Toast.makeText(getActivity(), topUpResponse.getMessage(), Toast.LENGTH_LONG).show();
                    mAccount = topUpResponse.getAccount();
                    mCommonUtil.updateAccount(mAccount);
                    //This will refresh the wallet balance
                    setWalletBalance(mAccount.getBalance());
                    // No need to go back to create ride automatically, let user press back to do that
                    //they can see their updated balance properly before moving back
                    if (mRequiredBalanceVisiblity) {
                        resetRequiredBalanceViews();
                        //This will go back to the create rides page
                        //Don't have this here as are refreshing the view and it will kill the fragment view above
                        //hideSoftKeyBoard();
                        //Toast.makeText(getActivity(), "New Wallet Balance:" + mCurrencySymbol + mAccount.getBalance(), Toast.LENGTH_LONG).show();
                        //TODO Fix this bug as its causing Crash
                        //getActivity().getSupportFragmentManager().popBackStack();
                    }
                    //VERY IMP - This has to be the last line else you will NPE on fragment
                    //as it would get killed post this line
                    //This will refresh the fragment and transaction list as well
                    mListener.onTopUpFragmentRefresh();
                }
            }
        });
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
        void onTopUpFragmentRefresh();
    }
}
