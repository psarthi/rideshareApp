package com.parift.rideshare.component;

import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.OfferInfoFragment;
import com.parift.rideshare.model.serviceprovider.domain.core.Offer;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by psarthi on 6/12/18.
 */

public class OfferComp {

    public static final String TAG = OfferComp.class.getName();
    BaseFragment mBaseFragment;
    Offer mOffer;

    public OfferComp(BaseFragment baseFragment, Offer offer){
        mBaseFragment = baseFragment;
        mOffer = offer;
    }

    public void setBasicOfferLayout(View view){

        ImageView offerImageView = view.findViewById(R.id.offer_image);
        String imageUrl = mOffer.getPhoto().getImageLocation();
        Picasso.with(mBaseFragment.getActivity()).load(imageUrl).fit().into(offerImageView);
        TextView offerDescriptionTextView = view.findViewById(R.id.offer_description);
        offerDescriptionTextView.setText(mOffer.getDescription());
        TextView offerRideRequirementTextView = view.findViewById(R.id.offer_rides_requirement);
        String rideRequirement = mOffer.getRidesRequired() + " Rides Per "+mOffer.getRidesDuration();
        offerRideRequirementTextView.setText(rideRequirement);
        TextView partnerNameTextView = view.findViewById(R.id.offer_partner_name);
        //Resetting the visibility to start fresh for each item in recycler view
        partnerNameTextView.setVisibility(View.VISIBLE);
        if (mOffer.isCompanyOffer()){
            partnerNameTextView.setVisibility(View.GONE);
        } else {
            partnerNameTextView.setText(mOffer.getPartner().getName());
        }

        if (!(mBaseFragment instanceof OfferInfoFragment)){
            offerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadOfferInfoFragment(new Gson().toJson(mOffer));
                }
            });
        }
    }

    public void setOfferInfoLayout(View view){
        setBasicOfferLayout(view);
        TextView termsAndConditionTextView = view.findViewById(R.id.offer_ride_termsAndCondition);
        TextView redemptionProcessTextView = view.findViewById(R.id.offer_ride_redemptionProcess);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            termsAndConditionTextView.setText(Html.fromHtml(mOffer.getTermsAndCondition(),Html.FROM_HTML_MODE_COMPACT));
            redemptionProcessTextView.setText(Html.fromHtml(mOffer.getRedemptionProcess(),Html.FROM_HTML_MODE_COMPACT));

        } else {
            termsAndConditionTextView.setText(Html.fromHtml(mOffer.getTermsAndCondition()));
            redemptionProcessTextView.setText(Html.fromHtml(mOffer.getRedemptionProcess()));
        }

        Button reimburseButton = view.findViewById(R.id.reimburse_button);
        Button redeemButton = view.findViewById(R.id.redeem_button);

        reimburseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadReimbursementFragment(new Gson().toJson(mOffer));
            }
        });
        //TODO Implement listener based on eligibility logic
    }


}
