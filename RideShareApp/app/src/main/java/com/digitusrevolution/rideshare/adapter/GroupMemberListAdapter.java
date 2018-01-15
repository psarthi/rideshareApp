package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.user.domain.MemberRole;
import com.digitusrevolution.rideshare.model.user.dto.GroupMember;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<GroupMember> mGroupMembers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public GroupMemberListAdapter(List<GroupMember> groupMembers, BaseFragment fragment) {
        mGroupMembers = groupMembers;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_layout, parent, false);
        GroupMemberListAdapter.ViewHolder vh = new GroupMemberListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroupMember groupMember = getItem(position);
        View view = holder.itemView;
        //We can set groupMember as its also an extension of BasicUser
        UserComp userComp = new UserComp(mBaseFragment, groupMember);
        userComp.setUserProfileSingleRow(view);
        TextView memberRole = view.findViewById(R.id.member_role_text);
        if (groupMember.isAdmin()) {
            memberRole.setVisibility(View.VISIBLE);
            memberRole.setText(MemberRole.Admin.toString());
        } else {
            memberRole.setVisibility(View.GONE);
        }
    }

    public GroupMember getItem(int position){
        return mGroupMembers.get(position);
    }

    @Override
    public int getItemCount() {
        return mGroupMembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
