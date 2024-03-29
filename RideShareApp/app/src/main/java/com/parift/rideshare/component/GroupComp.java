package com.parift.rideshare.component;

import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.user.domain.Vote;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.parift.rideshare.model.user.dto.GroupFeedbackInfo;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 1/12/18.
 */

public class GroupComp {

    public static final String TAG = UserComp.class.getName();
    BaseFragment mBaseFragment;
    //Note - You will not get Full Group as its a very heavy object, so we will use only GroupDetail
    private GroupDetail mGroup;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private String FEEDBACK_URL;
    private AppCompatTextView mGroupMemberCount;
    private AppCompatTextView mGroupUpVoteCount;
    private AppCompatTextView mGroupDownVoteCount;
    private ImageView mGroupInvite;
    private LinearLayout mGroupUpVoteLayout;
    private LinearLayout mGroupDownVoteLayout;
    private LinearLayout mGroupInviteLayout;


    public GroupComp(BaseFragment fragment, GroupDetail group){
        mBaseFragment = fragment;
        mGroup = group;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
    }

    //This will setup basic group information in both layout's (basic_group_layout and group_info_layout)
    //Any thing specific for group_info_layout has to be set seperately in groupInfoFragment itself
    //basic group info includes - group photo, member count, vote count
    //Invite button is not part of basic group info as well as about information is also not part of basic group info
    public void setGroupBasicInfo(View view){
        ImageView groupImageView = view.findViewById(R.id.group_photo_image_view);

        LinearLayout group_info_single_row_layout = view.findViewById(R.id.group_info_single_row_layout);
        mGroupMemberCount = group_info_single_row_layout.findViewById(R.id.group_member_count);
        mGroupUpVoteCount = group_info_single_row_layout.findViewById(R.id.group_up_vote_count);
        mGroupDownVoteCount = group_info_single_row_layout.findViewById(R.id.group_down_vote_count);
        mGroupUpVoteLayout = group_info_single_row_layout.findViewById(R.id.group_up_vote_layout);
        mGroupDownVoteLayout = group_info_single_row_layout.findViewById(R.id.group_down_vote_layout);

        //This will ensure we start on clean slate of drawables else previous tint of another group may still be there
        //Due to viewholder reusing views
        mCommonUtil.removeDrawableTint(mGroupUpVoteCount.getCompoundDrawables()[0]);
        mCommonUtil.removeDrawableTint(mGroupDownVoteCount.getCompoundDrawables()[0]);
        //This will ensure previous image gets cleaned up for new view
        groupImageView.setImageResource(R.drawable.ic_profile);

        //IMP - Don't set voting tint here as somehow its behaving wrongly
        //and its messsed up within different view's of view holder

        if (mGroup.getPhoto()!=null){
            String imageUrl = mGroup.getPhoto().getImageLocation();
            Picasso.with(mBaseFragment.getActivity()).load(imageUrl).into(groupImageView);
        }

        mGroupMemberCount.setText(Integer.toString(mGroup.getMemberCount()));
        mGroupUpVoteCount.setText(Integer.toString(mGroup.getGenuineVotes()));
        mGroupDownVoteCount.setText(Integer.toString(mGroup.getFakeVotes()));
    }

    public void setFullGroupInfo(View view){
        setGroupBasicInfo(view);
        LinearLayout group_info_single_row_layout = view.findViewById(R.id.group_info_single_row_layout);
        mGroupInvite = group_info_single_row_layout.findViewById(R.id.group_invite_user);
        mGroupInviteLayout = group_info_single_row_layout.findViewById(R.id.group_invite_layout);
        ImageView editGroup = view.findViewById(R.id.edit_group_image_view);

        if (mGroup.getMembershipStatus().isMember()){
            if (mGroup.getMembershipStatus().getVote()!=null){
                if (mBaseFragment.isAdded()){
                    int color = mBaseFragment.getResources().getColor(R.color.colorAccent);
                    if (mGroup.getMembershipStatus().getVote().equals(Vote.Genuine)){
                        Drawable drawable = mGroupUpVoteCount.getCompoundDrawables()[0];
                        mCommonUtil.setDrawableTint(drawable, color);
                    }
                    if (mGroup.getMembershipStatus().getVote().equals(Vote.Fake)){
                        Drawable drawable = mGroupDownVoteCount.getCompoundDrawables()[0];
                        mCommonUtil.setDrawableTint(drawable, color);
                    }
                }
            }
            //Voting functionality not required in basic view, so setting lisneters here only
            setupListeners();
        } else {
            mGroupInviteLayout.setVisibility(View.GONE);
        }

        if (mGroup.getMembershipStatus().isAdmin()){
            editGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                    fragmentLoader.loadCreateGroupFragment(false, new Gson().toJson(mGroup));
                }
            });
        } else {
            editGroup.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        FEEDBACK_URL = APIUrl.GROUP_FEEDBACK.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                .replace(APIUrl.GROUP_ID_KEY, Long.toString(mGroup.getId()));

        mGroupUpVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFeedback(Vote.Genuine);
            }
        });

        mGroupDownVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFeedback(Vote.Fake);
            }
        });

        mGroupInviteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadSearchUserForGroupFragment(new Gson().toJson(mGroup));
            }
        });

    }

    private void postFeedback(final Vote vote) {
        mCommonUtil.showProgressDialog();
        RESTClient.post(mBaseFragment.getActivity(), FEEDBACK_URL, getFeedbackInfo(vote), new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (mBaseFragment.isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    mGroup = new Gson().fromJson(response.toString(), GroupDetail.class);
                    int color = mBaseFragment.getResources().getColor(R.color.colorAccent);
                    if (vote.equals(Vote.Genuine)) {
                        Logger.debug(TAG, "Voted as Genuine");
                        //This will set genuine vote tint as well as update vote count
                        mGroupUpVoteCount.setText(Integer.toString(mGroup.getGenuineVotes()));
                        mCommonUtil.setDrawableTint(mGroupUpVoteCount.getCompoundDrawables()[0], color);

                        //This will set remove fake vote tint as well as update vote count
                        mGroupDownVoteCount.setText(Integer.toString(mGroup.getFakeVotes()));
                        mCommonUtil.removeDrawableTint(mGroupDownVoteCount.getCompoundDrawables()[0]);
                    } else {
                        Logger.debug(TAG, "Voted as Fake");
                        //This will set fake vote tint as well as update vote count
                        mGroupDownVoteCount.setText(Integer.toString(mGroup.getFakeVotes()));
                        mCommonUtil.setDrawableTint(mGroupDownVoteCount.getCompoundDrawables()[0], color);

                        //This will set remove genuine vote tint as well as update vote count
                        mGroupUpVoteCount.setText(Integer.toString(mGroup.getGenuineVotes()));
                        mCommonUtil.removeDrawableTint(mGroupUpVoteCount.getCompoundDrawables()[0]);
                    }
                }
            }
        });
    }

    private GroupFeedbackInfo getFeedbackInfo(Vote vote) {
        GroupFeedbackInfo feedbackInfo = new GroupFeedbackInfo();
        feedbackInfo.setVote(vote);
        feedbackInfo.setGivenByUser(mUser);
        return feedbackInfo;
    }
}
