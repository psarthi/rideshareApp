package com.digitusrevolution.rideshare.component;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.FullUser;
import com.digitusrevolution.rideshare.model.user.dto.UserListType;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
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
    private CommonUtil mCommonUtil;

    public UserComp(BaseFragment fragment, BasicUser user){
        mBaseFragment = fragment;
        mUser = user;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setUserNamePhoto(View view){
        View layout = view.findViewById(R.id.user_photo_name_layout);
        ImageView userProfileImageView = layout.findViewById(R.id.user_image);
        Picasso.with(mBaseFragment.getActivity()).load(mUser.getPhoto().getImageLocation()).into(userProfileImageView);
        TextView userNameTextView = layout.findViewById(R.id.user_name_text);
        String userName = mUser.getFirstName() + " " + mUser.getLastName();
        userNameTextView.setText(userName);
    }

    public void setUserProfileSingleRow(View view){

        View user_profile_layout = view.findViewById(R.id.user_profile_single_row_layout);

        //Marking all extra elements invisible and let this be set by calling functions as per their requirement
        user_profile_layout.findViewById(R.id.add_friend_image).setVisibility(View.GONE);
        user_profile_layout.findViewById(R.id.friend_image).setVisibility(View.GONE);
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
                //TODO Needs to be replaced with proper logic of calling user
                Log.d(TAG, "Calling User Mobile - "+mUser.getCountry().getCode() + mUser.getMobileNumber());
            }
        });

        UserProfileFragment fragment = (UserProfileFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(UserProfileFragment.TAG);
        if (fragment!=null && mUser.getId() == fragment.getUserId()){
            Log.d(TAG, "User Profile is already loaded");
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String GET_USER_PROFILE = APIUrl.GET_USER_PROFILE.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(GET_USER_PROFILE, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadUserProfileFragment(response.toString(), null);
                        }
                    });
                }
            });
        }
    }
}
