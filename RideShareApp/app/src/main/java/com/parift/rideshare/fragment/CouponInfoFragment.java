package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.component.RewardCouponTransactionComp;
import com.parift.rideshare.component.RewardReimbursementTransactionComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardCouponTransaction;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardReimbursementTransaction;
import com.parift.rideshare.model.user.dto.BasicUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CouponInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CouponInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CouponInfoFragment extends BaseFragment {

    public static final String TAG = CouponInfoFragment.class.getName();
    public static final String TITLE = "Coupon";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REWARD_COUPON_TRANSACTION = "couponTransaction";

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private RewardCouponTransaction mRewardCouponTransaction;

    public CouponInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param couponTransaction couponTransaction in Json format
     * @return A new instance of fragment Offer Info Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CouponInfoFragment newInstance(String couponTransaction) {
        CouponInfoFragment fragment = new CouponInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REWARD_COUPON_TRANSACTION, couponTransaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String transaction = getArguments().getString(ARG_REWARD_COUPON_TRANSACTION);
            mRewardCouponTransaction = new Gson().fromJson(transaction, RewardCouponTransaction.class);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coupon_info, container, false);
        RewardCouponTransactionComp rewardCouponTransactionComp = new RewardCouponTransactionComp(this, mRewardCouponTransaction);
        rewardCouponTransactionComp.setCouponInfoLayout(view);
        return view;
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
        void onCouponInfoFragmentInteraction(String data);
    }
}
