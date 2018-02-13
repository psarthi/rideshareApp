package com.parift.rideshare.fragment;

import android.content.Context;
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

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.GroupListAdapter;
import com.parift.rideshare.component.UserComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.UserProfile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
    private BasicUser mUser;

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
        Logger.debug(TAG, "User Profile loaded for user Id:"+mUserProfile.getUser().getId());
        mCommonUtil = new CommonUtil(this);
        mFragmentLoader = new FragmentLoader(this);
        mCurrentRide = mCommonUtil.getCurrentRide();
        mUser = mCommonUtil.getUser();
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
        String ridesTakenText = getResources().getString(R.string.rides_requested_text) + mUserProfile.getRequestedRides();

        ((TextView) view.findViewById(R.id.rides_offered_text)).setText(offeredRideText);
        ((TextView) view.findViewById(R.id.rides_requested_text)).setText(ridesTakenText);

        int commonGroupsSize = 0;
        if (mUserProfile.getCommonGroups()!=null){
            commonGroupsSize = mUserProfile.getCommonGroups().size();
        }
        String commonGroupLabel = getResources().getString(R.string.common_groups_text);
        String commonGroupsText;
        //This is the case where user profile is of logged in user
        if (mUser.getId() == mUserProfile.getUser().getId()){
            commonGroupLabel = getResources().getString(R.string.my_groups_text);
            commonGroupsText = commonGroupLabel + commonGroupsSize;
        }
        //This is the case where user profile is of someone else
        else {
            //This is the scenario where there is no common groups
            if (commonGroupsSize==0){
                commonGroupsText = getResources().getString(R.string.no_common_groups_text);
            }
            //This is the scenario where you have some common groups
            else {
                commonGroupsText = commonGroupLabel + commonGroupsSize;
            }
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //This will take care of dismissing progress dialog so that we don't get NPE (not attached to window manager)
        //This happens when you make http call which is async and when response comes, activity is no longer there
        //and then when dismissProgressDialog is called it will throw error
        mCommonUtil.dismissProgressDialog();
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

    public long getUserId(){
        return mUserProfile.getUser().getId();
    }
}
