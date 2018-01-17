package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.app.UserListType;

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
                .inflate(R.layout.user_layout, parent, false);
        UserListAdapter.ViewHolder vh = new UserListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BasicUser user = getItem(position);
        View view = holder.view;
        UserComp userComp = new UserComp(mBaseFragment, user);
        userComp.setUserProfileSingleRow(view);
        ImageView formImageView = view.findViewById(R.id.membership_form_image);
        if (mUserListType.equals(UserListType.Membership_Request)){
            formImageView.setVisibility(View.VISIBLE);
            formImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mBaseFragment.getActivity(), "Membership From", Toast.LENGTH_SHORT).show();
                }
            });
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
        //I have added this view variable instead of using itemView just from cleanniness of usage front
        //otherwise we can use itemView directly as well
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }
}
