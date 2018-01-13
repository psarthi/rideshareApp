package com.digitusrevolution.rideshare.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.GroupComp;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 1/13/18.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private static final String TAG = RideListAdapter.class.getName();
    private List<BasicUser> mUsers;
    private BaseFragment mBaseFragment;
    private CommonUtil mCommonUtil;

    public UserListAdapter(List<BasicUser> users, BaseFragment fragment) {
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
        UserComp userComp = new UserComp(mBaseFragment, user);
        userComp.setUserProfileSingleRow(holder.itemView);
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
