package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.Logger;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.billing.domain.core.Account;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

                String topUpAmountString = ((TextView) view.findViewById(R.id.topup_amount)).getText().toString();
                Logger.debug(TAG, "Top Up Amount:"+topUpAmountString);
                if (validateInput(topUpAmountString)){
                    String ADD_MONEY = APIUrl.ADD_MONEY.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                            .replace(APIUrl.ACCOUNT_NUMBER_KEY, Long.toString(mAccount.getNumber()))
                            .replace(APIUrl.AMOUNT_KEY, topUpAmountString);
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(ADD_MONEY, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            mAccount = new Gson().fromJson(response.toString(), Account.class);
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
        }
        return true;
    }

    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getStagingService();


        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , "Digitu20940997232495");
        paramMap.put( "ORDER_ID" , "ORDER0000000004");
        paramMap.put( "CUST_ID" , "CUST00001");
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , "1");
        paramMap.put( "WEBSITE" , "WEB_STAGING");
        paramMap.put( "CALLBACK_URL" , "https://pguat.paytm.com/oltp-web/processTransaction?ORDER_ID=ORDER0000000004");
        paramMap.put( "EMAIL" , "partha.sarthi@gmail.com");
        paramMap.put( "MOBILE_NO" , "7777777777");
        paramMap.put( "CHECKSUMHASH" , "/C+EVL7C7t9ntKFpkZV0KGFelBjpKPvO2VdGHt2sNoJI+lTi0CzOqG0h96fpRs73Kh5y2JU1Z75ac0fzDSSQqIX0oBDtDrpgOgB8/0i9BWI=");

        PaytmOrder Order = new PaytmOrder(paramMap);

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
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Logger.debug(TAG, "Payment Transaction : " + inResponse);
                        Toast.makeText(getActivity(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                        Logger.debug(TAG, "networkNotAvailable");
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
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Logger.debug(TAG, "onErrorLoadingWebPage:" + iniErrorCode +":"+inErrorMessage);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Logger.debug(TAG, "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getActivity(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
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
