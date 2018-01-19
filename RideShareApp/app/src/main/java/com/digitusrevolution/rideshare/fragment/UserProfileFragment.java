package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.adapter.GroupListAdapter;
import com.digitusrevolution.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.digitusrevolution.rideshare.component.UserComp;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserProfile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends BaseFragment {

    public static final String TAG = UserProfileFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "userProfile";
    private static final String ARG_PARAM2 = "param2";
    public static final String TITLE = "User Profile";

    // TODO: Rename and change types of parameters
    private String mUserProfileData;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private UserProfile mUserProfile;
    private FullRide mCurrentRide;
    private CommonUtil mCommonUtil;
    private FragmentLoader mFragmentLoader;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userProfile UserProfile in Json format
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String userProfile, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, userProfile);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserProfileData = getArguments().getString(ARG_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mUserProfile = new Gson().fromJson(mUserProfileData, UserProfile.class);
        Log.d(TAG, "User Profile loaded for user Id:"+mUserProfile.getUser().getId());
        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mCurrentRide = mCommonUtil.getCurrentRide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ImageView userProfileImageView = (ImageView) view.findViewById(R.id.user_profile_large_image);
        Picasso.with(getActivity()).load(mUserProfile.getUser().getPhoto().getImageLocation())
                .into(userProfileImageView);

        UserComp userComp = new UserComp(this, mUserProfile.getUser());
        userComp.setUserProfileSingleRow(view);

        View user_profile_layout = view.findViewById(R.id.user_profile_single_row_layout);
        //This will make small user image invisible
        user_profile_layout.findViewById(R.id.user_image).setVisibility(View.GONE);

        String offeredRideText = getResources().getString(R.string.rides_offered_text) + mUserProfile.getOfferedRides();
        String ridesTakenText = getResources().getString(R.string.rides_taken_text) + mUserProfile.getRidesTaken();

        ((TextView) view.findViewById(R.id.rides_offered_text)).setText(offeredRideText);
        ((TextView) view.findViewById(R.id.ride_taken_text)).setText(ridesTakenText);

        int commonGroupsSize = 0;
        if (mUserProfile.getCommonGroups()!=null){
            commonGroupsSize = mUserProfile.getCommonGroups().size();
        }

        String commonGroupsText = getResources().getString(R.string.common_groups_text) + commonGroupsSize;

        ((TextView) view.findViewById(R.id.common_groups_count_text)).setText(commonGroupsText);

        RecyclerView mRecyclerView = view.findViewById(R.id.common_groups_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter mAdapter = new GroupListAdapter(mUserProfile.getCommonGroups(), this);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(true);
        getActivity().setTitle(TITLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mActivity = (FragmentActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onUserProfileFragmentInteraction(String data);
    }

    public int getUserId(){
        return mUserProfile.getUser().getId();
    }
}
