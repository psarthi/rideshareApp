package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.model.user.dto.BasicMembershipRequest;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupMembershipRequestListAdapter extends RecyclerView.Adapter<GroupMembershipRequestListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<BasicMembershipRequest> mRequests;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public GroupMembershipRequestListAdapter(List<BasicMembershipRequest> requests, BaseFragment fragment) {
        mRequests = requests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout, parent, false);
        GroupMembershipRequestListAdapter.ViewHolder vh = new GroupMembershipRequestListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BasicMembershipRequest request = getItem(position);
        View view = holder.view;
        UserComp userComp = new UserComp(mBaseFragment, request.getUser());
        userComp.setUserProfileSingleRow(view, true);
        ImageView formImageView = view.findViewById(R.id.membership_form_image);
        formImageView.setVisibility(View.VISIBLE);
        formImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadMembershipRequestFragment(new Gson().toJson(request), true, false);
            }
        });
    }

    public BasicMembershipRequest getItem(int position){
        return mRequests.get(position);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
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
