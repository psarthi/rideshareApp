package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.adapter.GroupInfoViewPagerAdapter;
import com.parift.rideshare.component.GroupComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
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
    private GroupInfoViewPagerAdapter mGroupInfoViewPagerAdapter;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;

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
        Logger.debug(TAG, "newInstance Called");
        GroupInfoFragment fragment = new GroupInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreate Called of instance:"+this.hashCode());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupData = getArguments().getString(ARG_GROUP);
        }
        mGroup = new Gson().fromJson(mGroupData, GroupDetail.class);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreateView Called of instance:"+this.hashCode());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        TabLayout tabLayout = view.findViewById(R.id.group_tab);
        CommonUtil commonUtil = new CommonUtil(this);

        GroupComp groupComp = new GroupComp(this, mGroup);
        groupComp.setFullGroupInfo(view);

        final ViewPager viewPager = view.findViewById(R.id.group_viewPager);
        int pageCount;
        //This will take care of setting the visibility of page and icons based on role and membership status
        if (mGroup.getMembershipStatus().isAdmin()){
             pageCount = 3;
        } else {
            if (mGroup.getMembershipStatus().isMember()){
                pageCount = 2;
            } else {
                pageCount = 1;
            }
        }

        //This is very important else you will have issue in syncing tab selection with view pager content
        //i.e. view pager may show request ride but tab selection would show offer ride
        tabLayout.setupWithViewPager(viewPager);
        mGroupInfoViewPagerAdapter = new GroupInfoViewPagerAdapter(getChildFragmentManager(), pageCount, mGroup);
        viewPager.setAdapter(mGroupInfoViewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Selected Tab with position:"+tab.getText()+"("+tab.getPosition()+")");
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Tab Unselected:"+tab.getText());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Logger.debug(TAG, "Tab Reselected:"+tab.getText());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Logger.debug(TAG,"Inside OnResume of instance:"+this.hashCode());
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(false);
        //This will set the title as group name
        getActivity().setTitle(mGroup.getName());
        showBackStackDetails();
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
        void onGroupInfoFragmentInteraction(String data);
    }

    // Don't use this function till we sort the issue of refreshing AboutFragment
    /*
    public void refresh(GroupDetail groupDetail){
        Logger.debug(TAG,"refresh called with updated group:"+new Gson().toJson(groupDetail));
        //This will update the latest groupDetail
        //This will not work as internally we need to ensure AboutFragment gets updated Group which is not possible from here
        //mGroup = groupDetail;
        //Needs to validate if this works or not as what i have observed, that this will only work when view pager
        // is already loaded with all fragments else you will get exception that fragment doesn't exist
        //This requires that your fragment should be alive and when you load fragment via ViewPager and move to another fragment
        //fragment may get killed as we don't put it into backstack. That's just my theory, need to validate
        //mGroupInfoViewPagerAdapter.notifyDataSetChanged();
    }*/

    /*
     * This will take care of refreshing basic information of group which is in heading section
     * e.g. nmember count, vote up, down
     */
    public void refreshBasicInfo(GroupDetail groupDetail){
        Logger.debug(TAG,"Basic refresh called with updated group:"+new Gson().toJson(groupDetail));
        mGroup = groupDetail;
    }
}
