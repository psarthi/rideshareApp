package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.billing.domain.core.Bill;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.google.gson.Gson;

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
    private static final String ARG_RIDE_REQUEST = "rideRequest";

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private Bill mBill;
    private FullRideRequest mRideRequest;
    private TextView mBillStatusTextView;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideRequest Ride Request in Json format
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String rideRequest) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_REQUEST, rideRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String rideRequest = getArguments().getString(ARG_RIDE_REQUEST);
            mRideRequest = new Gson().fromJson(rideRequest, FullRideRequest.class);
            mBill = mRideRequest.getBill();
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        mBillStatusTextView = view.findViewById(R.id.reciept_status);

        setBillView(view);
        return view;
    }

    private void setBillView(View view) {
        mBillStatusTextView.setText(mBill.getStatus().toString());
        //Reason for casting to float to get decimal value as distance is int, its not giving decimal value
        float distance = (float) mRideRequest.getTravelDistance() / 1000;

        ((TextView) view.findViewById(R.id.rate_per_km_text)).setText(mCommonUtil.getDecimalFormattedString(mBill.getRate()));
        ((TextView) view.findViewById(R.id.distance_text)).setText(mCommonUtil.getDecimalFormattedString(distance));
        ((TextView) view.findViewById(R.id.discount_text)).setText(mCommonUtil.getDecimalFormattedString(mBill.getDiscountPercentage()));
        ((TextView) view.findViewById(R.id.co_traveller_count_text)).setText(Integer.toString(mRideRequest.getSeatRequired()));

        String currencySymbol = mCommonUtil.getCurrencySymbol(mUser.getCountry());
        String amountText = currencySymbol + mCommonUtil.getDecimalFormattedString(mBill.getAmount());
        ((TextView) view.findViewById(R.id.total_amount_text)).setText(amountText);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
        Logger.debug(TAG,"Inside OnResume");
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
        void onBillFragmentInteraction(String data);
    }
}
