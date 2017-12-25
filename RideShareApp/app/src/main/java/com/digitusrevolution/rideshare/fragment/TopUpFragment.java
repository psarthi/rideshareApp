package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.billing.domain.core.Account;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequiredBalanceVisiblity = getArguments().getBoolean(ARG_REQD_BALANCE_VISIBILITY);
            mRequiredBalanceAmount = getArguments().getFloat(ARG_REQD_BALANCE_AMOUNT);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        List<Account> accounts = (List<Account>) mUser.getAccounts();
        //This is just the basic account with no transaction
        mAccount = accounts.get(0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_up, container, false);
        String currencySymbol = mCommonUtil.getCurrencySymbol(mUser.getCountry());
        String balance = currencySymbol + mAccount.getBalance();
        ((TextView) view.findViewById(R.id.wallet_balance_amount)).setText(balance);
        String hint = getResources().getString(R.string.amount_hint)+" ("+currencySymbol+")";
        ((TextView)view.findViewById(R.id.topup_amount)).setHint(hint);

        if (mRequiredBalanceVisiblity){
            String reqdAmount = getResources().getString(R.string.required_wallet_balance) +
                    currencySymbol + Float.toString(mRequiredBalanceAmount);
            ((TextView) view.findViewById(R.id.required_wallet_balance)).setText(reqdAmount);
            //Adding 1 to cover decimals as when you typecast to int decimal would not be taken care e.g. 3.2 would become 3
            Log.d(TAG, "Required Min Balance:"+mRequiredBalanceAmount);

        } else {
            view.findViewById(R.id.required_wallet_balance).setVisibility(View.GONE);
        }


        view.findViewById(R.id.add_money_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartTransaction();
            }
        });

        return view;
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
                        Log.d(TAG, "someUIErrorOccurred" + inErrorMessage);
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d(TAG, "Payment Transaction : " + inResponse);
                        Toast.makeText(getActivity(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        // If network is not
                        // available, then this
                        // method gets called.
                        Log.d(TAG, "networkNotAvailable");
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
                        Log.d(TAG, "clientAuthenticationFailed" + inErrorMessage);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                        Log.d(TAG, "onErrorLoadingWebPage:" + iniErrorCode +":"+inErrorMessage);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d(TAG, "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getActivity(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Inside OnResume");
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
        void onTopUpFragmentInteraction(String data);
    }
}
