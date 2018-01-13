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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GET_USER_PROFILE = APIUrl.GET_USER_PROFILE.replace(APIUrl.USER_ID_KEY, Integer.toString(user.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.post(mBaseFragment.getActivity(), GET_USER_PROFILE, user, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        mCommonUtil.dismissProgressDialog();
                        FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                        fragmentLoader.loadUserProfileFragment(response.toString(), null);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        mCommonUtil.dismissProgressDialog();
                        if (errorResponse!=null) {
                            ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                            Log.d(TAG, errorMessage.getErrorMessage());
                            Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                            Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }
        });
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
