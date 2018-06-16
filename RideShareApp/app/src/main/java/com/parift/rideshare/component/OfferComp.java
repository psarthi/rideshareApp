package com.parift.rideshare.component;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.OfferInfoFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.serviceprovider.domain.core.Offer;
import com.parift.rideshare.model.serviceprovider.domain.core.RedemptionType;
import com.parift.rideshare.model.serviceprovider.domain.core.RewardCouponTransaction;
import com.parift.rideshare.model.serviceprovider.dto.UserOffer;
import com.parift.rideshare.model.user.domain.core.User;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 6/12/18.
 */

public class OfferComp {

    public static final String TAG = OfferComp.class.getName();
    BaseFragment mBaseFragment;
    UserOffer mUserOffer;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;

    public OfferComp(BaseFragment baseFragment, UserOffer userOffer){
        mBaseFragment = baseFragment;
        mUserOffer = userOffer;
        mCommonUtil = new CommonUtil(baseFragment);
        mUser = mCommonUtil.getUser();
    }

    public void setBasicOfferLayout(View view){

        ImageView offerImageView = view.findViewById(R.id.offer_image);
        String imageUrl = mUserOffer.getPhoto().getImageLocation();
        Picasso.with(mBaseFragment.getActivity()).load(imageUrl).fit().into(offerImageView);
        TextView offerDescriptionTextView = view.findViewById(R.id.offer_description);
        offerDescriptionTextView.setText(mUserOffer.getDescription());
        TextView offerRideRequirementTextView = view.findViewById(R.id.offer_rides_requirement);
        String rideRequirement = mUserOffer.getRidesRequired() + " Rides in a "+mUserOffer.getRidesDuration();
        if (mUserOffer.getBalanceRideRequirement()>0){
            String balanceRequirementString = " ("+mUserOffer.getBalanceRideRequirement()+" more to go)";
            rideRequirement = rideRequirement + balanceRequirementString;
        }
        offerRideRequirementTextView.setText(rideRequirement);
        TextView partnerNameTextView = view.findViewById(R.id.offer_partner_name);
        //Resetting the visibility to start fresh for each item in recycler view
        partnerNameTextView.setVisibility(View.VISIBLE);
        if (mUserOffer.isCompanyOffer()){
            partnerNameTextView.setVisibility(View.GONE);
        } else {
            partnerNameTextView.setText(mUserOffer.getPartner().getName());
        }

        ImageView eligibilityImageView = view.findViewById(R.id.offer_eligibility_icon);
            if (mUserOffer.isUserEligible()){
                eligibilityImageView.setImageResource(R.drawable.ic_unlocked);
            } else {
                eligibilityImageView.setImageResource(R.drawable.ic_locked);
            }

        if (!(mBaseFragment instanceof OfferInfoFragment)){
            offerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadOfferInfoFragment(new Gson().toJson(mUserOffer));
                }
            });
        }
    }

    public void setOfferInfoLayout(View view){
        setBasicOfferLayout(view);
        setOfferDetails(view);

        Button reimburseButton = view.findViewById(R.id.reimburse_button);
        Button redeemButton = view.findViewById(R.id.redeem_button);

        if (mUserOffer.isUserEligible()){
            if (mUserOffer.getRedemptionType().equals(RedemptionType.Reimburse)){
                redeemButton.setVisibility(View.GONE);
            } else {
                reimburseButton.setVisibility(View.GONE);
            }
        } else {
            redeemButton.setVisibility(View.GONE);
            reimburseButton.setVisibility(View.GONE);
        }

        reimburseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadReimbursementFragment(new Gson().toJson(mUserOffer));
            }
        });

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.GENERATE_COUPON.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.OFFER_ID_KEY, Integer.toString(mUserOffer.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if(mBaseFragment.isAdded()){
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            RewardCouponTransaction couponTransaction = new Gson().fromJson(response.toString(), RewardCouponTransaction.class);
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadCouponInfoFragment(new Gson().toJson(couponTransaction));
                        }
                    }
                });


            }
        });
    }

    public void setOfferDetails(View view) {
        TextView termsAndConditionTextView = view.findViewById(R.id.offer_ride_termsAndCondition);
        TextView redemptionProcessTextView = view.findViewById(R.id.offer_ride_redemptionProcess);
        TextView redemptionProcessLabelTextView = view.findViewById(R.id.offer_ride_redemptionProcess_label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            termsAndConditionTextView.setText(Html.fromHtml(mUserOffer.getTermsAndCondition(),Html.FROM_HTML_MODE_COMPACT));
            if (mUserOffer.getRedemptionType().equals(RedemptionType.Reimburse)){
                redemptionProcessTextView.setVisibility(View.GONE);
                redemptionProcessLabelTextView.setVisibility(View.GONE);
            } else {
                //IMP - If you mark the visiblity as gone and then setText it would throw NPE, so we need to ensure we don't set until its visibility is visible
                redemptionProcessTextView.setText(Html.fromHtml(mUserOffer.getRedemptionProcess(),Html.FROM_HTML_MODE_COMPACT));
            }
        } else {
            termsAndConditionTextView.setText(Html.fromHtml(mUserOffer.getTermsAndCondition()));
            if (mUserOffer.getRedemptionType().equals(RedemptionType.Reimburse)){
                redemptionProcessTextView.setVisibility(View.GONE);
                redemptionProcessLabelTextView.setVisibility(View.GONE);
            } else {
                redemptionProcessTextView.setText(Html.fromHtml(mUserOffer.getRedemptionProcess()));
            }
        }
    }


}
