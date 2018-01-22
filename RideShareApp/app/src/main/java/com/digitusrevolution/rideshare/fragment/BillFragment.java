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
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;
import com.digitusrevolution.rideshare.model.billing.domain.core.Bill;
import com.digitusrevolution.rideshare.model.billing.domain.core.BillStatus;
import com.digitusrevolution.rideshare.model.billing.dto.BillInfo;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends BaseFragment {

    public static final String TAG = BillFragment.class.getName();
    public static final String TITLE = "Bill Details";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BILL = "bill";
    private static final String ARG_RIDE_TYPE = "rideType";

    // TODO: Rename and change types of parameters
    private String mBillData;
    private RideType mRideType;

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private Bill mBill;
    private TextView mBillStatusTextView;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bill Bill in Json format
     * @param rideType Type of ride
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String bill, RideType rideType) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BILL, bill);
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBillData = getArguments().getString(ARG_BILL);
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mBill = new Gson().fromJson(mBillData, Bill.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        mBillStatusTextView = view.findViewById(R.id.bill_status);

        setBillView(view);

        setButtonsOnClickListener(view);

        return view;
    }

    private void setBillView(View view) {
        String billNumberText = getResources().getString(R.string.bill_no_text)+mBill.getNumber();
        ((TextView) view.findViewById(R.id.bill_number)).setText(billNumberText);
        String coTravellerName = mBill.getRideRequest().getPassenger().getFirstName() + " "+
                mBill.getRideRequest().getPassenger().getLastName();
        ((TextView) view.findViewById(R.id.co_traveller_name)).setText(coTravellerName);
        mBillStatusTextView.setText(mBill.getStatus().toString());
        //Reason for casting to float to get decimal value as distance is int, its not giving decimal value
        float distance = (float) mBill.getRideRequest().getTravelDistance() / 1000;

        ((TextView) view.findViewById(R.id.rate_per_km_text)).setText(mCommonUtil.getDecimalFormattedString(mBill.getRate()));
        ((TextView) view.findViewById(R.id.distance_text)).setText(mCommonUtil.getDecimalFormattedString(distance));
        ((TextView) view.findViewById(R.id.discount_text)).setText(mCommonUtil.getDecimalFormattedString(mBill.getDiscountPercentage()));
        ((TextView) view.findViewById(R.id.co_traveller_count_text)).setText(Integer.toString(mBill.getRideRequest().getSeatRequired()));

        String currencySymbol = mCommonUtil.getCurrencySymbol(mUser.getCountry());
        String amountText = currencySymbol + mCommonUtil.getDecimalFormattedString(mBill.getAmount());
        ((TextView) view.findViewById(R.id.total_amount_text)).setText(amountText);

        float serviceCharge = mBill.getServiceChargePercentage() * mBill.getAmount() / 100;
        float rideOwnerAmount = mBill.getAmount() - serviceCharge;

        String serviceChargeText = currencySymbol + mCommonUtil.getDecimalFormattedString(serviceCharge);
        String rideOwnerAmountText = currencySymbol + mCommonUtil.getDecimalFormattedString(rideOwnerAmount);

        ((TextView) view.findViewById(R.id.service_charge_text)).setText(serviceChargeText);
        ((TextView) view.findViewById(R.id.payable_to_ride_owner_amount_text)).setText(rideOwnerAmountText);

        //This will ensure label is shown properly based on ride status
        if (!mBill.getStatus().equals(BillStatus.Paid)){
            ((TextView) view.findViewById(R.id.ride_owner_amount_label)).setText(R.string.pending_bills_ride_owner_amount_label);
        } else {
            ((TextView) view.findViewById(R.id.ride_owner_amount_label)).setText(R.string.paid_bills_ride_owner_amount_label);
        }

        //This will check is the person vewing the bill is the passenger or the driver
        //if its passenger, then check if the status is approved. In that case only, show Pay button
        if (mUser.getId() == mBill.getRideRequest().getPassenger().getId()
                && mBill.getStatus().equals(BillStatus.Approved)){
            view.findViewById(R.id.bill_pay_button).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.bill_pay_button).setVisibility(View.GONE);
        }
    }

    private void setButtonsOnClickListener(View view) {

        view.findViewById(R.id.bill_pay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BillInfo billInfo = new BillInfo();
                billInfo.setBillNumber(mBill.getNumber());
                mCommonUtil.showProgressDialog();
                RESTClient.post(getActivity(), APIUrl.PAY_BILL,billInfo, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
                        mBill = new Gson().fromJson(response.toString(), Bill.class);
                        setBillView(getView());
                        Toast.makeText(getActivity(),"Bill Paid Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
        ((HomePageActivity)getActivity()).showBackButton(true);
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
        // TODO: Update argument type and name
        void onBillFragmentInteraction(RideType rideType, String data);
    }
}
