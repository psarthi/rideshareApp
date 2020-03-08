package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parift.rideshare.R;
import com.parift.rideshare.adapter.GroupListAdapter;
import com.parift.rideshare.model.user.dto.GroupDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommonGroupListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommonGroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommonGroupListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_DETAIL_LIST = "groupDetailList";

    private OnFragmentInteractionListener mListener;
    private List<GroupDetail> mGroups = new ArrayList<>();


    public CommonGroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupDetailList group detail List in Json
     * @return A new instance of fragment CommonGroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommonGroupListFragment newInstance(String groupDetailList) {
        CommonGroupListFragment fragment = new CommonGroupListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_DETAIL_LIST, groupDetailList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String groupDetailList = getArguments().getString(ARG_GROUP_DETAIL_LIST);
            Type listType = new TypeToken<ArrayList<GroupDetail>>() {}.getType();
            mGroups = new Gson().fromJson(groupDetailList, listType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.group_list);
        TextView emptyTextView = view.findViewById(R.id.empty_result_text);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        RecyclerView.Adapter adapter = new GroupListAdapter(mGroups, this);
        recyclerView.setAdapter(adapter);
        if (mGroups.size()==0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            //VERY IMP - Reason for storing as member variable instead as there are situations
            //when fragment is partially loaded and then immediately detached which in turn would
            //return null when you call mBaseFragment.getActivity .e.g in CommonUtil getSharedPreference function
            //So if we store as member variable, then we can reference al activity related resource even though
            //fragment is not visible
            //Scenario where it was failing - Wallet Fragment was loaded, then detached immediately as it was not visible
            //and in the same flow setAdapater was called which in turn try to get mUser which calls mBaseFragment.getActivity
            //it was throwing NPE
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
        void onCommonGroupListFragmentInteraction(String data);
    }
}
