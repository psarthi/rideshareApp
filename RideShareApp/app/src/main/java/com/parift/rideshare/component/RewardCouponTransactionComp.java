package com.parift.rideshare.component;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardCouponTransaction;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardReimbursementTransaction;
import com.squareup.picasso.Picasso;

/**
 * Created by psarthi on 6/12/18.
 */

public class RewardCouponTransactionComp {

    public static final String TAG = OfferComp.class.getName();
    BaseFragment mBaseFragment;
    RewardCouponTransaction mRewardCouponTransaction;
    private CommonUtil mCommonUtil;

    public RewardCouponTransactionComp(BaseFragment baseFragment, RewardCouponTransaction rewardCouponTransaction){
        mBaseFragment = baseFragment;
        mRewardCouponTransaction = rewardCouponTransaction;
        mCommonUtil = new CommonUtil(baseFragment);
    }

    public void setBasicRewardTransactionLayout(View view){

        ImageView offerImageView = view.findViewById(R.id.offer_image);
        String imageUrl = mRewardCouponTransaction.getOffer().getPhoto().getImageLocation();
        Picasso.with(mBaseFragment.getActivity()).load(imageUrl).fit().into(offerImageView);
        TextView offerDescriptionTextView = view.findViewById(R.id.offer_description);
        offerDescriptionTextView.setText(mRewardCouponTransaction.getOffer().getDescription());
        TextView partnerNameTextView = view.findViewById(R.id.offer_partner_name);
        //Resetting the visibility to start fresh for each item in recycler view
        partnerNameTextView.setVisibility(View.VISIBLE);
        if (mRewardCouponTransaction.getOffer().isCompanyOffer()){
            partnerNameTextView.setVisibility(View.GONE);
        } else {
            partnerNameTextView.setText(mRewardCouponTransaction.getOffer().getPartner().getName());
        }
        TextView statusTextView = view.findViewById(R.id.reward_status);
        TextView redemptionDateTextView = view.findViewById(R.id.reward_redemption_date);
        redemptionDateTextView.setText(mCommonUtil.getFormattedDateTimeString(mRewardCouponTransaction.getRedemptionDateTime()));
        statusTextView.setText(mRewardCouponTransaction.getStatus().toString());

    }


}
