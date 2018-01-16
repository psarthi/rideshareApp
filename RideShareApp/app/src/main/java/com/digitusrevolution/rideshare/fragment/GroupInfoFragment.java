package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.adapter.GroupInfoViewPager;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.component.GroupComp;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.model.app.SearchType;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends BaseFragment {

    public static final String TAG = GroupInfoFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP = "group";

    // TODO: Rename and change types of parameters
    private String mGroupData;

    private OnFragmentInteractionListener mListener;
    private GroupDetail mGroup;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param group Group Detail in Json format
     * @return A new instance of fragment GroupInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance(String group) {
        GroupInfoFragment fragment = new GroupInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupData = getArguments().getString(ARG_GROUP);
        }
        mGroup = new Gson().fromJson(mGroupData, GroupDetail.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        TabLayout tabLayout = view.findViewById(R.id.group_tab);
        ImageView groupImageView = view.findViewById(R.id.group_photo_image_view);
        ImageView inviteUserImageView = view.findViewById(R.id.group_invite_user);
        CommonUtil commonUtil = new CommonUtil(this);

        GroupComp groupComp = new GroupComp(this, mGroup);
        groupComp.setGroupBasicInfo(view);
        inviteUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(GroupInfoFragment.this);
                fragmentLoader.loadGroupInviteUserSearchFragment(new Gson().toJson(mGroup));
            }
        });

        final ViewPager viewPager = view.findViewById(R.id.group_viewPager);

        int pageCount = 3;

        //This is very important else you will have issue in syncing tab selection with view pager content
        //i.e. view pager may show request ride but tab selection would show offer ride
        tabLayout.setupWithViewPager(viewPager);
        GroupInfoViewPager groupInfoViewPager = new GroupInfoViewPager(getChildFragmentManager(), pageCount, mGroup);
        viewPager.setAdapter(groupInfoViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Unselected:"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "Tab Reselected:"+tab.getText());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(false);
        //This will set the title as group name
        getActivity().setTitle(mGroup.getName());
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
        void onGroupInfoFragmentInteraction(String data);
    }
}
