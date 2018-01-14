package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.GroupComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.user.dto.BasicGroup;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by psarthi on 1/13/18.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<GroupDetail> mGroups;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public GroupListAdapter(List<GroupDetail> groups, BaseFragment fragment) {
        mGroups = groups;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_group_layout, parent, false);
        GroupListAdapter.ViewHolder vh = new GroupListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;
        GroupDetail groupDetail = mGroups.get(position);
        GroupComp groupComp = new GroupComp(mBaseFragment, groupDetail);
        groupComp.setGroupBasicInfo(view);
        //This will set the name of the group which has been handled differently for different view
        //e.g. in case of group Info it has been set as title and for adapter, it has been set as text view
        TextView groupName = view.findViewById(R.id.group_name_text);
        groupName.setText(groupDetail.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadGroupInfoFragment(new Gson().toJson(mGroups.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
