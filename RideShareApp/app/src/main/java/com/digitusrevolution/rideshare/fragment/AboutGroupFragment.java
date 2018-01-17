package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.user.domain.ApprovalStatus;
import com.digitusrevolution.rideshare.model.user.dto.BasicMembershipRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutGroupFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP = "group";

    // TODO: Rename and change types of parameters
    private String mGroupData;

    private OnFragmentInteractionListener mListener;
    private GroupDetail mGroup;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private FragmentLoader mFragmentLoader;


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
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mFragmentLoader = new FragmentLoader(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_group, container, false);
        ((TextView) view.findViewById(R.id.group_description)).setText(mGroup.getInformation());
        Button leaveButton = view.findViewById(R.id.leave_group_button);
        Button joinButton = view.findViewById(R.id.group_join_button);
        Button memberShipFormButton = view.findViewById(R.id.membership_form_button);
        Button membershipRequestStatusButton = view.findViewById(R.id.group_membership_request_status_button);

        if (mGroup.getMembershipStatus().isMember()){
            joinButton.setVisibility(View.GONE);
            membershipRequestStatusButton.setVisibility(View.GONE);
            if (!mGroup.getMembershipStatus().isAdmin()){
                memberShipFormButton.setVisibility(View.GONE);
            }
        } else {
            //This is appliacable for both cases
            leaveButton.setVisibility(View.GONE);
            memberShipFormButton.setVisibility(View.GONE);
            if (mGroup.getMembershipStatus().isRequestSubmitted()){
                joinButton.setVisibility(View.GONE);
                String status = mGroup.getMembershipStatus().getApprovalStatus().toString();
                membershipRequestStatusButton.setText(status);
                if (mGroup.getMembershipStatus().getApprovalStatus().equals(ApprovalStatus.Rejected)){
                    //This will overwrite the status
                    status = status + " (View Details)";
                    membershipRequestStatusButton.setText(status);
                    membershipRequestStatusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = APIUrl.GET_SPECIFIC_MEMBERSHIP_REQUEST.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()))
                                    .replace(APIUrl.GROUP_ID_KEY, Integer.toString(mGroup.getId()));
                            mCommonUtil.showProgressDialog();
                            RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    mCommonUtil.dismissProgressDialog();
                                    BasicMembershipRequest request = new Gson().fromJson(response.toString(), BasicMembershipRequest.class);
                                    mFragmentLoader.loadMembershipRequestFragment(new Gson().toJson(request),false, false);
                                }
                            });
                        }
                    });
                }
            } else {
                membershipRequestStatusButton.setVisibility(View.GONE);
            }
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicMembershipRequest request = new BasicMembershipRequest();
                request.setGroup(mGroup);
                Map<String, String> questionAnswers = new HashMap<>();

                Collection<String> questions = mGroup.getMembershipForm().getQuestions();
                for (String question: questions){
                    questionAnswers.put(question, "");
                }
                request.setQuestionAnswers(questionAnswers);
                mFragmentLoader.loadMembershipRequestFragment(new Gson().toJson(request),false, true);
            }
        });

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
