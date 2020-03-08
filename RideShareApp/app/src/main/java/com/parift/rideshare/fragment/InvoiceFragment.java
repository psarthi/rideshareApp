package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.billing.domain.core.Invoice;
import com.parift.rideshare.model.ride.dto.BasicRide;
import com.parift.rideshare.model.serviceprovider.domain.core.Company;
import com.parift.rideshare.model.user.dto.BasicUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoiceFragment extends BaseFragment {

    public static final String TAG = InvoiceFragment.class.getName();
    public static final String TITLE = "Invoice Details";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BASIC_RIDE = "basicRide";

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicRide mRide;
    private Invoice mInvoice;
    private BasicUser mDriver;

    public InvoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param basicRide Basic Ride in Json
     * @return A new instance of fragment InvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvoiceFragment newInstance(String basicRide) {
        InvoiceFragment fragment = new InvoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BASIC_RIDE, basicRide);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String basicRide = getArguments().getString(ARG_BASIC_RIDE);
            mRide = new Gson().fromJson(basicRide, BasicRide.class);
            mInvoice = mRide.getInvoice();
            mDriver = mRide.getDriver();
        }
        mCommonUtil = new CommonUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);

        String invoiceNumber = "Invoice No. "+mInvoice.getNumber();
        ((TextView) view.findViewById(R.id.invoice_number)).setText(invoiceNumber);

        String invoiceDate = mCommonUtil.getFormattedDateTimeString(mInvoice.getDate());
        ((TextView) view.findViewById(R.id.invoice_date)).setText(invoiceDate);

        String customerName = mDriver.getFirstName() + " "+ mDriver.getLastName();
        ((TextView) view.findViewById(R.id.customer_name)).setText(customerName);

        String customerContact = mDriver.getMobileNumber() +" / " + mDriver.getEmail();
        ((TextView) view.findViewById(R.id.customer_contact)).setText(customerContact);

        String rideStartPoint = mRide.getStartPointAddress();
        ((TextView) view.findViewById(R.id.ride_start_point_text)).setText(rideStartPoint);

        String rideEndPoint = mRide.getEndPointAddress();
        ((TextView) view.findViewById(R.id.ride_end_point_text)).setText(rideEndPoint);

        String totalEarning = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                + mCommonUtil.getDecimalFormattedString(mInvoice.getTotalAmountEarned());
        ((TextView) view.findViewById(R.id.total_ride_earning_amount)).setText(totalEarning);

        float platformFeesPercentage = mInvoice.getServiceCharge() / mInvoice.getTotalAmountEarned() * 100;
        String platformFeesLabel = "Platform Fees ("
                + mCommonUtil.getDecimalFormattedString(platformFeesPercentage) +"%)";
        ((TextView) view.findViewById(R.id.platform_fees_label)).setText(platformFeesLabel);

        String platformFeesAmount = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                + mCommonUtil.getDecimalFormattedString(mInvoice.getServiceCharge());
        ((TextView) view.findViewById(R.id.platform_fees_amount)).setText(platformFeesAmount);

        if (mInvoice.getCgst()!=0 && mInvoice.getSgst()!=0){
            float sgstPercentage = mInvoice.getSgst() / mInvoice.getServiceCharge() * 100;
            String sgstLabel = "SGST (" + mCommonUtil.getDecimalFormattedString(sgstPercentage) +"%)";
            ((TextView) view.findViewById(R.id.sgst_label)).setText(sgstLabel);

            String sgstAmount = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                    + mCommonUtil.getDecimalFormattedString(mInvoice.getSgst());
            ((TextView) view.findViewById(R.id.sgst_amount)).setText(sgstAmount);

            float cgstPercentage = mInvoice.getCgst() / mInvoice.getServiceCharge() * 100;
            String cgstLabel = "CGST (" + mCommonUtil.getDecimalFormattedString(cgstPercentage) +"%)";
            ((TextView) view.findViewById(R.id.cgst_label)).setText(cgstLabel);

            String cgstAmount = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                    + mCommonUtil.getDecimalFormattedString(mInvoice.getCgst());
            ((TextView) view.findViewById(R.id.cgst_amount)).setText(cgstAmount);

            view.findViewById(R.id.igst_layout).setVisibility(View.GONE);
        } else {
            float igstPercentage = mInvoice.getIgst() / mInvoice.getServiceCharge() * 100;
            String igstLabel = "IGST (" + mCommonUtil.getDecimalFormattedString(igstPercentage) +"%)";
            ((TextView) view.findViewById(R.id.igst_label)).setText(igstLabel);

            String igstAmount = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                    + mCommonUtil.getDecimalFormattedString(mInvoice.getIgst());
            ((TextView) view.findViewById(R.id.igst_amount)).setText(igstAmount);

            view.findViewById(R.id.cgst_layout).setVisibility(View.GONE);
            view.findViewById(R.id.sgst_layout).setVisibility(View.GONE);
        }

        if (mInvoice.getTcs()!=0){
            float tcsPercentage = mInvoice.getTcs() / mInvoice.getTotalAmountEarned() * 100;
            String tcsLabel = "TCS (" + mCommonUtil.getDecimalFormattedString(tcsPercentage) +"%)";
            ((TextView) view.findViewById(R.id.tcs_label)).setText(tcsLabel);

            String tcsAmount = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                    + mCommonUtil.getDecimalFormattedString(mInvoice.getTcs());
            ((TextView) view.findViewById(R.id.tcs_amount)).setText(tcsAmount);
        } else {
            view.findViewById(R.id.tcs_layout).setVisibility(View.GONE);
        }

        float totalDeduction = mInvoice.getServiceCharge() + mInvoice.getCgst() + mInvoice.getSgst() + mInvoice.getIgst()
                + mInvoice.getTcs();
        String totalDeductionString = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                + mCommonUtil.getDecimalFormattedString(totalDeduction);
        ((TextView) view.findViewById(R.id.total_deduction_amount)).setText(totalDeductionString);

        float netEarning = mInvoice.getTotalAmountEarned() - totalDeduction;
        String netEarningString = mCommonUtil.getCurrencySymbol(mDriver.getCountry())
                + mCommonUtil.getDecimalFormattedString(netEarning);
        ((TextView) view.findViewById(R.id.net_ride_earning_amount)).setText(netEarningString);

        Company company = mCommonUtil.getCompany();
        ((TextView) view.findViewById(R.id.company_name)).setText(company.getName());
        ((TextView) view.findViewById(R.id.company_address)).setText(company.getAddress());
        ((TextView) view.findViewById(R.id.company_gstin)).setText("GSTIN #"+company.getGstNumber());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
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
        void onInvoiceFragmentInteraction(String data);
    }
}
