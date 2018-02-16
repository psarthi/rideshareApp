package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.app.MemberRole;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.parift.rideshare.model.user.dto.GroupMember;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

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
    private int mCurrentSelectedPosition = -1;

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
        userComp.setUserProfileSingleRow(view, false);
        ImageView optionsMenu = view.findViewById(R.id.more_options_image);
        optionsMenu.setTag(position);
        TextView memberRole = view.findViewById(R.id.member_role_text);
        if (groupMember.isAdmin()) {
            memberRole.setVisibility(View.VISIBLE);
            optionsMenu.setVisibility(View.GONE);
            if (groupMember.getId() == mGroupDetail.getOwner().getId()){
                memberRole.setText(MemberRole.Owner.toString());
            } else {
                memberRole.setText(MemberRole.Admin.toString());
            }
        } else {
            memberRole.setVisibility(View.GONE);
            optionsMenu.setVisibility(View.VISIBLE);
        }

        //Exception Rules goes here

        //Applicable for owner, where if he/she is logged in user, then he will see options for all irrespective of admin status
        if (mUser.getId() == mGroupDetail.getOwner().getId()){
            optionsMenu.setVisibility(View.VISIBLE);
        }

        //Applicable for logged in user, option menu is not visible for self
        if (groupMember.getId() == mUser.getId()){
            optionsMenu.setVisibility(View.GONE);
        }

        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG,"View Tag is: " + v.getTag());
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
        mCurrentSelectedPosition = Integer.parseInt(v.getTag().toString());
        if (mGroupMembers.get(mCurrentSelectedPosition).isAdmin()){
            popup.getMenu().getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Logger.debug(TAG, "Selected Position: " + mCurrentSelectedPosition);
        switch (item.getItemId()) {
            case R.id.menu_add_admin:
                addAdmin();
                return true;
            case R.id.menu_remove_user:
                removeMember();
                return true;
            default:
                return false;
        }
    }


    public GroupMember getItem(int position){
        return mGroupMembers.get(position);
    }

    private void addAdmin(){
        BasicUser selectedMember = mGroupMembers.get(mCurrentSelectedPosition);
        String url = APIUrl.ADD_ADMIN_TO_GROUP.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                .replace(APIUrl.GROUP_ID_KEY, Long.toString(mGroupDetail.getId()))
                .replace(APIUrl.MEMBER_USER_ID_KEY, Long.toString(selectedMember.getId()));
        RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                GroupMember updatedMember = new Gson().fromJson(response.toString(), GroupMember.class);
                mGroupMembers.get(mCurrentSelectedPosition).setAdmin(updatedMember.isAdmin());
                notifyItemChanged(mCurrentSelectedPosition);
            }
        });
    }

    private  void removeMember(){
        BasicUser selectedMember = mGroupMembers.get(mCurrentSelectedPosition);
        String url = APIUrl.REMOVE_MEMBER_FROM_GROUP.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                .replace(APIUrl.GROUP_ID_KEY, Long.toString(mGroupDetail.getId()))
                .replace(APIUrl.MEMBER_USER_ID_KEY, Long.toString(selectedMember.getId()));
        RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mGroupMembers.remove(mCurrentSelectedPosition);
                notifyItemRemoved(mCurrentSelectedPosition);
            }
        });
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
