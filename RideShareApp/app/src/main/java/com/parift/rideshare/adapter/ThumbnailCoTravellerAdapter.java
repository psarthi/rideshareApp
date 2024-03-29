package com.parift.rideshare.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/3/17.
 */

public class ThumbnailCoTravellerAdapter extends RecyclerView.Adapter<ThumbnailCoTravellerAdapter.ViewHolder> {

    public static final String TAG = ThumbnailCoTravellerAdapter.class.getName();
    private List<FullRideRequest> mRideRequests;
    private BaseFragment mBaseFragment;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.thumbnail_name_text);
            mImageView = v.findViewById(R.id.thumbnail_image);
        }
    }

    public ThumbnailCoTravellerAdapter(BaseFragment fragment, List<FullRideRequest> rideRequests) {
        mRideRequests = rideRequests;
        mBaseFragment = fragment;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();

    }

    @Override
    public ThumbnailCoTravellerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ThumbnailCoTravellerAdapter.ViewHolder holder, final int position) {
        final BasicRideRequest rideRequest = getItem(position);
        holder.mTextView.setText(rideRequest.getPassenger().getFirstName());
        Picasso.with(mBaseFragment.getActivity()).load(rideRequest.getPassenger().getPhoto().getImageLocation()).into(holder.mImageView);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String GET_USER_PROFILE = APIUrl.GET_USER_PROFILE.replace(APIUrl.SIGNEDIN_USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.USER_ID_KEY, Long.toString(rideRequest.getPassenger().getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(GET_USER_PROFILE, null, new RSJsonHttpResponseHandler(mCommonUtil){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadUserProfileFragment(response.toString(), true);
                        }
                    }
                });
            }
        });

    }

    private FullRideRequest getItem(int position){
        return mRideRequests.get(position);
    }

    @Override
    public int getItemCount() {
        return mRideRequests.size();
    }

}
