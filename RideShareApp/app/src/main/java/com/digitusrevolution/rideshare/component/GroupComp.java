package com.digitusrevolution.rideshare.component;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.squareup.picasso.Picasso;

/**
 * Created by psarthi on 1/12/18.
 */

public class GroupComp {

    public static final String TAG = UserComp.class.getName();
    BaseFragment mBaseFragment;
    //Note - You will not get Full Group as its a very heavy object, so we will use only GroupDetail
    private GroupDetail mGroup;
    private CommonUtil mCommonUtil;

    public GroupComp(BaseFragment fragment, GroupDetail group){
        mBaseFragment = fragment;
        mGroup = group;
        mCommonUtil = new CommonUtil(fragment);
    }

    public void setGroupBasicInfo(View view){
        ImageView groupImageView = view.findViewById(R.id.group_photo_image_view);

        LinearLayout group_info_single_row_layout = view.findViewById(R.id.group_info_single_row_layout);
        TextView groupMemberCount = group_info_single_row_layout.findViewById(R.id.group_member_count);
        TextView groupUpVoteCount = group_info_single_row_layout.findViewById(R.id.group_up_vote_count);
        TextView groupDownVoteCount = group_info_single_row_layout.findViewById(R.id.group_down_vote_count);

        if (mGroup.getOwner().getPhoto()!=null){
            String imageUrl = mGroup.getOwner().getPhoto().getImageLocation();
            Picasso.with(mBaseFragment.getActivity()).load(imageUrl).into(groupImageView);
        }

        groupMemberCount.setText(Integer.toString(mGroup.getMemberCount()));
        groupUpVoteCount.setText(Integer.toString(mGroup.getGenuineVotes()));
        groupDownVoteCount.setText(Integer.toString(mGroup.getFakeVotes()));
    }

}
