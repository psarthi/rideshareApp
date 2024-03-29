package com.parift.rideshare.component;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class UserComp {

    public static final String TAG = UserComp.class.getName();
    BaseFragment mBaseFragment;
    //Note - You will not get FullUser as its a very heavy object, so we will use only BasicUser
    private BasicUser mUser;
    private BasicUser mSignedInUser;
    private CommonUtil mCommonUtil;

    public UserComp(BaseFragment fragment, BasicUser user){
        mBaseFragment = fragment;
        mUser = user;
        mCommonUtil = new CommonUtil(fragment);
        //IMP - Its important to differentiate between user and signedInUser as we need to use both of them while calling backend service
        mSignedInUser = mCommonUtil.getUser();
    }

    public void setUserNamePhoto(View view){
        View layout = view.findViewById(R.id.user_photo_name_layout);
        ImageView userProfileImageView = layout.findViewById(R.id.user_image);
        Picasso.with(mBaseFragment.getActivity()).load(mUser.getPhoto().getImageLocation()).into(userProfileImageView);
        TextView userNameTextView = layout.findViewById(R.id.user_name_text);
        String userName = mUser.getFirstName() + " " + mUser.getLastName();
        userNameTextView.setText(userName);
    }

    public void setUserProfileSingleRow(View view, boolean showPersonalInfo){

        View user_profile_layout = view.findViewById(R.id.user_profile_single_row_layout);

        //Marking all extra elements invisible and let this be set by calling functions as per their requirement
        user_profile_layout.findViewById(R.id.more_options_image).setVisibility(View.GONE);
        user_profile_layout.findViewById(R.id.membership_form_image).setVisibility(View.GONE);

        //Below are the view's on which we will actually operate
        setUserNamePhoto(view);
        TextView userRatingTextView = user_profile_layout.findViewById(R.id.user_rating_text);
        String profileRating = mCommonUtil.getDecimalFormattedString(mUser.getProfileRating());
        userRatingTextView.setText(profileRating);

        ImageView mobileImageView = user_profile_layout.findViewById(R.id.user_mobile_image);
        mobileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG, "Calling User Mobile - "+mUser.getCountry().getCode() + mUser.getMobileNumber());
                mBaseFragment.call(mUser.getMobileNumber());
            }
        });

        if (!showPersonalInfo){
            mobileImageView.setVisibility(View.GONE);
            userRatingTextView.setVisibility(View.GONE);
        }

        setUserProfileLayoutOnClickListener(user_profile_layout, showPersonalInfo);

        /* Disabled this as its causing issue in getting the right state of UserProfile,
        //as this can be loaded from multiple places e.g. group memebers, request etc.
        //So let this be standard and from anywhere user can open the user profile
        //and this would support for self profile as well
        UserProfileFragment fragment = (UserProfileFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(UserProfileFragment.TAG);
        if (fragment!=null && mUser.getId() == fragment.getUserId()){
            Logger.debug(TAG, "User Profile is already loaded");
        } else {
            setUserProfileLayoutOnClickListener(user_profile_layout);
        }*/
    }

    private void setUserProfileLayoutOnClickListener(View user_profile_layout, final boolean showPersonalInfo) {
        user_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GET_USER_PROFILE = APIUrl.GET_USER_PROFILE.replace(APIUrl.SIGNEDIN_USER_ID_KEY, Long.toString(mSignedInUser.getId()))
                        .replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(GET_USER_PROFILE, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadUserProfileFragment(response.toString(), showPersonalInfo);
                        }
                    }
                });
            }
        });
    }

    public interface OnUserCompListener{
        public void call(String number);
    }
}
