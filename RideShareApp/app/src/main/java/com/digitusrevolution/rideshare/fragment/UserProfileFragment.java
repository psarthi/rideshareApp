package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.ThumbnailCoTravellerAdapter;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
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
public class UserProfileFragment extends BaseFragment implements ThumbnailCoTravellerAdapter.ThumbnailCoTravellerAdapterListener {

    public static final String TAG = UserProfileFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUserData;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;
    private BasicUser mSignedInUser;
    private FullRide mCurrentRide;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user User in Json format
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String user, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserData = getArguments().getString(ARG_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mUser = new Gson().fromJson(mUserData, BasicUser.class);
        Log.d(TAG, "User Profile loaded for user Id:"+mUser.getId());
        mCurrentRide = getCurrentRide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ImageView userProfileImageView = (ImageView) view.findViewById(R.id.user_profile_large_image);
        Picasso.with(getActivity()).load(mUser.getPhoto().getImageLocation())
                .into(userProfileImageView);

        RecyclerView friendRecyclerView = view.findViewById(R.id.mutual_friends_list);
        RecyclerView groupsRecyclerView = view.findViewById(R.id.common_groups_list);

        friendRecyclerView.setHasFixedSize(true);
        groupsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager friendLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        friendLayoutManager.setAutoMeasureEnabled(true);

        RecyclerView.LayoutManager groupsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        groupsLayoutManager.setAutoMeasureEnabled(true);

        friendRecyclerView.setLayoutManager(friendLayoutManager);
        groupsRecyclerView.setLayoutManager(groupsLayoutManager);

        RecyclerView.Adapter adapter = new ThumbnailCoTravellerAdapter(getActivity(), UserProfileFragment.this,
                (List<BasicRideRequest>) mCurrentRide.getAcceptedRideRequests());

        friendRecyclerView.setAdapter(adapter);
        groupsRecyclerView.setAdapter(adapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUserProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onClickOfThumbnailCoTravellerAdapter(BasicUser user) {

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
        void onUserProfileFragmentInteraction(Uri uri);
    }
}
