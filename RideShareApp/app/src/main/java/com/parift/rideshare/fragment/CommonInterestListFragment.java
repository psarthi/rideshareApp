package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parift.rideshare.R;
import com.parift.rideshare.adapter.InterestAdapter;
import com.parift.rideshare.adapter.InterestWrapperAdapter;
import com.parift.rideshare.helper.GridAutoFitLayoutManager;
import com.parift.rideshare.model.user.dto.BasicInterest;
import com.parift.rideshare.test.Interest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommonInterestListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommonInterestListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommonInterestListFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_INTEREST_LIST = "interestList";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;
    private List<BasicInterest> mInterests = new ArrayList<>();

    public CommonInterestListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param interestList Interest List in Json
     * @return A new instance of fragment CommonInterestListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommonInterestListFragment newInstance(String interestList) {
        CommonInterestListFragment fragment = new CommonInterestListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INTEREST_LIST, interestList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String interestList = getArguments().getString(ARG_INTEREST_LIST);
            Type listType = new TypeToken<ArrayList<BasicInterest>>() {}.getType();
            mInterests = new Gson().fromJson(interestList, listType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_common_interest_list, container, false);
        TextView emptyTextView = view.findViewById(R.id.empty_result_text);
        if (mInterests.size()==0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = view.findViewById(R.id.interest_list);
        //IMP - Don't try to use Gridlayout manager as its having very weired behavior
        //sometime image size is smaller / sometimes its bigger / sometimes it doesn't refresh
        //and its all may be due to span count, so either stick with LinearLayoutManager or use custom GridAutoFitLayoutManager
        //which takes care of adjusting the span count as per the width available
        GridAutoFitLayoutManager layoutManager = new GridAutoFitLayoutManager(getActivity(), 100);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        RecyclerView.Adapter adapter = new InterestAdapter(mInterests, this);
        recyclerView.setAdapter(adapter);
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
        void onCommonInterestListFragmentInteraction(String data);
    }
}
