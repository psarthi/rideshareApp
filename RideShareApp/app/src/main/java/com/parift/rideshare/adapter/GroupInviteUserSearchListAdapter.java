package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.app.GroupInviteUserSearchResultWrapper;
import com.parift.rideshare.model.app.MembershipStatusType;
import com.parift.rideshare.model.user.dto.GroupInviteUserSearchResult;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupInviteUserSearchListAdapter extends RecyclerView.Adapter<GroupInviteUserSearchListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<GroupInviteUserSearchResultWrapper> mUserSearchResults;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public GroupInviteUserSearchListAdapter(List<GroupInviteUserSearchResultWrapper> userSearchResults, BaseFragment fragment) {
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        GroupInviteUserSearchResult userSearchResult = getItem(position);
        //We can set groupMember as its also an extension of BasicUser
        UserComp userComp = new UserComp(mBaseFragment, userSearchResult.getUser());
        userComp.setUserNamePhoto(view);
        TextView membershipStatus = view.findViewById(R.id.membership_status);
        CheckBox inviteCheckBox = view.findViewById(R.id.invite_checkbox);
        if (userSearchResult.isMember()){
            inviteCheckBox.setVisibility(View.GONE);
            membershipStatus.setVisibility(View.VISIBLE);
            membershipStatus.setText(MembershipStatusType.Member.toString());
        } else {
            if (userSearchResult.isInvited()){
                inviteCheckBox.setVisibility(View.GONE);
                membershipStatus.setVisibility(View.VISIBLE);
                membershipStatus.setText(MembershipStatusType.Invited.toString());
            } else {
                if (userSearchResult.isRequestSubmitted()){
                    inviteCheckBox.setVisibility(View.GONE);
                    membershipStatus.setVisibility(View.VISIBLE);
                    membershipStatus.setText(MembershipStatusType.Submitted.toString());
                } else {
                    membershipStatus.setVisibility(View.GONE);
                    inviteCheckBox.setVisibility(View.VISIBLE);
                    inviteCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getItem(position).setSelected(!getItem(position).isSelected());
                        }
                    });
                }
            }
        }
    }

    public GroupInviteUserSearchResultWrapper getItem(int position){
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
