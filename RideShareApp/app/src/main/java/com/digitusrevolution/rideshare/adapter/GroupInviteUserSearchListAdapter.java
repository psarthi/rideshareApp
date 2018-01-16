package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.user.domain.MemberRole;
import com.digitusrevolution.rideshare.model.user.dto.GroupInviteUserSearchResult;
import com.digitusrevolution.rideshare.model.user.dto.GroupMember;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupInviteUserSearchListAdapter extends RecyclerView.Adapter<GroupInviteUserSearchListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<GroupInviteUserSearchResult> mUserSearchResults;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public GroupInviteUserSearchListAdapter(List<GroupInviteUserSearchResult> userSearchResults, BaseFragment fragment) {
        mUserSearchResults = userSearchResults;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_user_item, parent, false);
        GroupInviteUserSearchListAdapter.ViewHolder vh = new GroupInviteUserSearchListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupInviteUserSearchResult userSearchResult = getItem(position);
        View view = holder.itemView;
        //We can set groupMember as its also an extension of BasicUser
        UserComp userComp = new UserComp(mBaseFragment, userSearchResult.getUser());
        userComp.setUserNamePhoto(view);
        TextView membershipStatus = view.findViewById(R.id.membership_status);
        CheckBox inviteCheckBox = view.findViewById(R.id.invite_checkbox);
        if (userSearchResult.isMember()){
            membershipStatus.setText(MemberRole.Member.toString());
            inviteCheckBox.setVisibility(View.GONE);
        } else {
            membershipStatus.setVisibility(View.GONE);
            inviteCheckBox.setVisibility(View.VISIBLE);
        }
    }

    public GroupInviteUserSearchResult getItem(int position){
        return mUserSearchResults.get(position);
    }

    @Override
    public int getItemCount() {
        return mUserSearchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
