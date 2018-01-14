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
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserListType;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<BasicUser> mUsers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;
    private UserListType mUserListType;

    public UserListAdapter(UserListType userListType, List<BasicUser> users, BaseFragment fragment) {
        mUserListType = userListType;
        mUsers = users;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_user_layout, parent, false);
        UserListAdapter.ViewHolder vh = new UserListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BasicUser user = getItem(position);
        View view = holder.itemView;
        UserComp userComp = new UserComp(mBaseFragment, user);
        userComp.setUserProfileSingleRow(view);
        TextView memberRole = view.findViewById(R.id.member_role_text);
        TextView membershipForm = view.findViewById(R.id.membership_form_text);
        if (mUserListType.equals(UserListType.Member)){
            //TODO set the role of the user for that group
            memberRole.setVisibility(View.VISIBLE);
            membershipForm.setVisibility(View.GONE);
        }
        if (mUserListType.equals(UserListType.Membership_Request)){
            //TODO link the click of form with the user membership form
            memberRole.setVisibility(View.GONE);
            membershipForm.setVisibility(View.VISIBLE);
        }
    }

    public BasicUser getItem(int position){
        return mUsers.get(position);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
