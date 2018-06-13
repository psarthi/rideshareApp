package com.parift.rideshare.component;

import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.OfferInfoFragment;
import com.parift.rideshare.fragment.ReimbursementInfoFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.serviceprovider.domain.core.RedemptionType;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardReimbursementTransaction;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardTransaction;
import com.squareup.picasso.Picasso;

/**
 * Created by psarthi on 6/12/18.
 */

public class RewardReimbursementTransactionComp {

    public static final String TAG = OfferComp.class.getName();
    BaseFragment mBaseFragment;
    RewardReimbursementTransaction mRewardReimbursementTransaction;
    private CommonUtil mCommonUtil;

    public RewardReimbursementTransactionComp(BaseFragment baseFragment, RewardReimbursementTransaction rewardReimbursementTransaction){
        mBaseFragment = baseFragment;
        mRewardReimbursementTransaction = rewardReimbursementTransaction;
        mCommonUtil = new CommonUtil(baseFragment);
    }

    public void setBasicRewardTransactionLayout(View view){

        ImageView offerImageView = view.findViewById(R.id.offer_image);
        String imageUrl = mRewardReimbursementTransaction.getOffer().getPhoto().getImageLocation();
        Picasso.with(mBaseFragment.getActivity()).load(imageUrl).fit().into(offerImageView);
        TextView offerDescriptionTextView = view.findViewById(R.id.offer_description);
        offerDescriptionTextView.setText(mRewardReimbursementTransaction.getOffer().getDescription());
        TextView partnerNameTextView = view.findViewById(R.id.offer_partner_name);
        //Resetting the visibility to start fresh for each item in recycler view
        partnerNameTextView.setVisibility(View.VISIBLE);
        if (mRewardReimbursementTransaction.getOffer().isCompanyOffer()){
            partnerNameTextView.setVisibility(View.GONE);
        } else {
            partnerNameTextView.setText(mRewardReimbursementTransaction.getOffer().getPartner().getName());
        }
        TextView statusTextView = view.findViewById(R.id.reward_status);
        TextView redemptionDateTextView = view.findViewById(R.id.reward_redemption_date);
        redemptionDateTextView.setText(mCommonUtil.getFormattedDateTimeString(mRewardReimbursementTransaction.getTransactionDateTime()));
        statusTextView.setText(mRewardReimbursementTransaction.getStatus().toString());

        if (!(mBaseFragment instanceof ReimbursementInfoFragment)){
            offerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadReimbursementInfoFragment(new Gson().toJson(mRewardReimbursementTransaction));
                }
            });
        }
    }

    public void setReimbursementInfoLayout(View view) {
        setBasicRewardTransactionLayout(view);
        TextView approvedAmountTextView = view.findViewById(R.id.reimbursement_approved_amount);
        TextView paymentDateTextView = view.findViewById(R.id.reimbursement_payment_date);
        TextView remarksTextView = view.findViewById(R.id.reimbursement_remark);

        //TODO Set all text view's

        TextView termsAndConditionTextView = view.findViewById(R.id.offer_ride_termsAndCondition);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            termsAndConditionTextView.setText(Html.fromHtml(mRewardReimbursementTransaction.getOffer().getTermsAndCondition(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            termsAndConditionTextView.setText(Html.fromHtml(mRewardReimbursementTransaction.getOffer().getTermsAndCondition()));
        }
    }

    }
