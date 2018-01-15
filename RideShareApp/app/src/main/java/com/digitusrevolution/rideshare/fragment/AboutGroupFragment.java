package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP = "group";

    // TODO: Rename and change types of parameters
    private String mGroupData;

    private OnFragmentInteractionListener mListener;
    private GroupDetail mGroup;
    private Button mLeaveButton;
    private Button mMemberShipFormButton;
    private Button mJoinButton;


    public AboutGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param group Group Detail in Json format
     * @return A new instance of fragment AboutGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutGroupFragment newInstance(String group) {
        AboutGroupFragment fragment = new AboutGroupFragment();
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
        View view = inflater.inflate(R.layout.fragment_about_group, container, false);
        ((TextView) view.findViewById(R.id.group_description)).setText(mGroup.getInformation());
        mLeaveButton = view.findViewById(R.id.leave_group_button);
        mJoinButton = view.findViewById(R.id.group_join_button);
        mMemberShipFormButton = view.findViewById(R.id.membership_form_button);

        if (mGroup.getMembershipStatus().isMember()){
            mJoinButton.setVisibility(View.GONE);
            if (!mGroup.getMembershipStatus().isAdmin()){
                mMemberShipFormButton.setVisibility(View.GONE);
            }
        } else {
            mLeaveButton.setVisibility(View.GONE);
            mMemberShipFormButton.setVisibility(View.GONE);
        }

        return view;
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
        void onAboutGroupFragmentInteraction(String data);
    }
}
