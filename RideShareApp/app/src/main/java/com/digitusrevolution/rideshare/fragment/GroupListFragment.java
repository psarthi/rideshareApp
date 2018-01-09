package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.app.GroupResultType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_RESULT_TYPE = "groupResultType";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private GroupResultType mGroupResultType;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupResultType Group Result Type (e.g. All, Member, Invite)
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance(GroupResultType groupResultType, String param2) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_RESULT_TYPE, groupResultType.toString());
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupResultType = GroupResultType.valueOf(getArguments().getString(ARG_GROUP_RESULT_TYPE));
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        Log.d(TAG, "Group Result Type is:"+mGroupResultType);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String data) {
        if (mListener != null) {
            mListener.onGroupListFragmentInteraction(data);
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
        void onGroupListFragmentInteraction(String data);
    }
}
