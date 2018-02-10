package com.parift.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.component.GroupComp;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.SearchGroupFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.app.MembershipStatusType;
import com.parift.rideshare.model.user.dto.BasicMembershipRequest;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class UserMembershipRequestListAdapter extends RecyclerView.Adapter<UserMembershipRequestListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<BasicMembershipRequest> mRequests;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public UserMembershipRequestListAdapter(List<BasicMembershipRequest> requests, BaseFragment fragment) {
        mRequests = requests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_group_layout, parent, false);
        UserMembershipRequestListAdapter.ViewHolder vh = new UserMembershipRequestListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        BasicMembershipRequest request = mRequests.get(position);
        final GroupDetail groupDetail = request.getGroup();
        Logger.debug(TAG, "Group is:"+new Gson().toJson(groupDetail));
        GroupComp groupComp = new GroupComp(mBaseFragment, groupDetail);
        groupComp.setGroupBasicInfo(view);
        //This will set the name of the group which has been handled differently for different view
        //e.g. in case of group Info it has been set as title and for adapter, it has been set as text view
        TextView groupName = view.findViewById(R.id.group_name_text);
        groupName.setText(groupDetail.getName());
        TextView statusText = view.findViewById(R.id.status_text);
        statusText.setText(request.getStatus().toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadGroupInfoFragment(new Gson().toJson(groupDetail));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
