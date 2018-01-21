package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.app.MemberRole;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.digitusrevolution.rideshare.model.user.dto.GroupMember;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder>
        implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = RideListAdapter.class.getName();
    private List<GroupMember> mGroupMembers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private GroupDetail mGroupDetail;
    private BasicUser mUser;
    private int currentSelectedPosition = -1;

    public GroupMemberListAdapter(GroupDetail groupDetail, List<GroupMember> groupMembers, BaseFragment fragment) {
        mGroupMembers = groupMembers;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
        mGroupDetail = groupDetail;
        mUser = mCommonUtil.getUser();
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GroupMember groupMember = getItem(position);
        View view = holder.itemView;
        //We can set groupMember as its also an extension of BasicUser
        UserComp userComp = new UserComp(mBaseFragment, groupMember);
        userComp.setUserProfileSingleRow(view);
        ImageView optionsMenu = view.findViewById(R.id.more_options_image);
        optionsMenu.setTag(position);
        TextView memberRole = view.findViewById(R.id.member_role_text);
        //This will validate the status of group member and not the logged in user status
        if (groupMember.isAdmin()) {
            memberRole.setVisibility(View.VISIBLE);
            memberRole.setText(MemberRole.Admin.toString());
        } else {
            memberRole.setVisibility(View.GONE);
        }
        //This will validate the logged in user status and not the group member status
        //This will ensure for self, this option doesn't exist
        if (mGroupDetail.getMembershipStatus().isAdmin() && mUser.getId()!=groupMember.getId()){
            optionsMenu.setVisibility(View.VISIBLE);
        }

        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"View Tag is: " + v.getTag());
                showPopup(v);
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(mBaseFragment.getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.group_member_option_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this);
        currentSelectedPosition = Integer.parseInt(v.getTag().toString());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.e(TAG, "Selected Position: " + currentSelectedPosition);
        switch (item.getItemId()) {
            case R.id.menu_add_admin:
                return true;
            case R.id.menu_remove_user:
                return true;
            default:
                return false;
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
