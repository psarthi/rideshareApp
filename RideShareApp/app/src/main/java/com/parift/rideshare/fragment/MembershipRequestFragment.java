package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.user.dto.BasicMembershipRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MembershipRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MembershipRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MembershipRequestFragment extends BaseFragment {
    public static final String TAG = MembershipRequestFragment.class.getName();
    public static final String TITLE = "Membership Form";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MEMBERSHIP_REQUEST = "membershipRequest";
    private static final String ARG_ADMIN_ROLE = "adminRole";
    private static final String ARG_NEW_REQUEST = "newRequest";

    // TODO: Rename and change types of parameters
    private String mMembershipRequestData;
    private boolean mAdminRole;
    private boolean mNewRequest;

    private OnFragmentInteractionListener mListener;
    private BasicMembershipRequest mRequest;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    Button mSubmitButton;
    Button mApproveButton;
    Button mRejectButton;
    private LinearLayout mQuestionAnswerLayout;
    private EditText mAdminRemarkText;
    private EditText mUserRemarkText;
    private FragmentLoader mFragmentLoader;


    public MembershipRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param membershipRequest membership request in Json Format
     * @param adminRole Is user Admin or not
     * @param newRequest Is this a new request or existing one
     * @return A new instance of fragment MembershipRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MembershipRequestFragment newInstance(String membershipRequest, boolean adminRole, boolean newRequest) {
        MembershipRequestFragment fragment = new MembershipRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBERSHIP_REQUEST, membershipRequest);
        args.putBoolean(ARG_ADMIN_ROLE, adminRole);
        args.putBoolean(ARG_NEW_REQUEST, newRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMembershipRequestData = getArguments().getString(ARG_MEMBERSHIP_REQUEST);
            mAdminRole = getArguments().getBoolean(ARG_ADMIN_ROLE);
            mNewRequest = getArguments().getBoolean(ARG_NEW_REQUEST);
        }
        mRequest = new Gson().fromJson(mMembershipRequestData, BasicMembershipRequest.class);
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
        mFragmentLoader = new FragmentLoader(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_membership_request, container, false);
        mQuestionAnswerLayout = view.findViewById(R.id.membership_form_question_answer_layout);
        mSubmitButton = view.findViewById(R.id.submit_button);
        mApproveButton = view.findViewById(R.id.approve_button);
        mRejectButton = view.findViewById(R.id.reject_button);
        mAdminRemarkText = view.findViewById(R.id.admin_remark);
        mUserRemarkText = view.findViewById(R.id.user_remark);

        Map<String, String> questionAnswers = mRequest.getQuestionAnswers();
        Iterator<String> iterator = questionAnswers.keySet().iterator();

        for (Map.Entry<String, String> entry: questionAnswers.entrySet()){
            String question = entry.getKey();
            String answer = entry.getValue();
            View questionAnswerView = inflater.inflate(R.layout.question_answer_layout, container, false);
            TextView questionTextView = questionAnswerView.findViewById(R.id.question);
            EditText answerEditText = questionAnswerView.findViewById(R.id.answer);

            questionTextView.setText(question);
            answerEditText.setText(answer);
            if (mQuestionAnswerLayout.getChildCount()==0){
                answerEditText.requestFocus();
            }

            if (mAdminRole){
                answerEditText.setEnabled(false);
            }
            mQuestionAnswerLayout.addView(questionAnswerView);
        }

        mUserRemarkText.setText(mRequest.getUserRemark());
        mAdminRemarkText.setText(mRequest.getAdminRemark());

        if (mAdminRole){
            mSubmitButton.setVisibility(View.GONE);
            mUserRemarkText.setEnabled(false);
        } else {
            mApproveButton.setVisibility(View.GONE);
            mRejectButton.setVisibility(View.GONE);
            mAdminRemarkText.setEnabled(false);
            if (mNewRequest){
                mAdminRemarkText.setVisibility(View.GONE);
            }
        }

        setupButtonsListener();

        return view;
    }

    private void setupButtonsListener(){
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.SUBMIT_MEMBERSHIP_REQUEST.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.GROUP_ID_KEY, Long.toString(mRequest.getGroup().getId()));
                if (validateInput()){
                    setupMembershipRequest();
                    mCommonUtil.showProgressDialog();
                    RESTClient.post(getActivity(), url, mRequest, new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (isAdded()) {
                                super.onSuccess(statusCode, headers, response);
                                mCommonUtil.dismissProgressDialog();
                                GroupDetail updatedGroupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                                //Toast.makeText(getActivity(), "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                                mListener.onMembershipRequestFragmentRefreshGroupInfo(updatedGroupDetail);
                            }
                        }
                    });
                }
            }
        });

        mApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.APPROVE_MEMBERSHIP_REQUEST.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.GROUP_ID_KEY, Long.toString(mRequest.getGroup().getId()))
                        .replace(APIUrl.REQUESTER_USER_ID_KEY, Long.toString(mRequest.getUser().getId()));
                if (validateInput()) {
                    setupMembershipRequest();
                    mCommonUtil.showProgressDialog();
                    RESTClient.post(getActivity(), url, mRequest.getAdminRemark(), new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (isAdded()) {
                                super.onSuccess(statusCode, headers, response);
                                mCommonUtil.dismissProgressDialog();
                                GroupDetail updatedGroupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                                Toast.makeText(getActivity(), "Request Approved", Toast.LENGTH_SHORT).show();
                                mListener.onMembershipRequestFragmentRefreshBasicGroupInfo(updatedGroupDetail);
                            }
                        }
                    });
                }
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.REJECT_MEMBERSHIP_REQUEST.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.GROUP_ID_KEY, Long.toString(mRequest.getGroup().getId()))
                        .replace(APIUrl.REQUESTER_USER_ID_KEY, Long.toString(mRequest.getUser().getId()));
                String adminRemark = mAdminRemarkText.getText().toString();
                if (validateInput()) {
                    if (adminRemark.trim().equals("")){
                        Toast.makeText(getActivity(), "Please enter remark for rejection", Toast.LENGTH_SHORT).show();
                    } else {
                        setupMembershipRequest();
                        mCommonUtil.showProgressDialog();
                        RESTClient.post(getActivity(), url, mRequest.getAdminRemark(), new RSJsonHttpResponseHandler(mCommonUtil){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (isAdded()) {
                                    super.onSuccess(statusCode, headers, response);
                                    mCommonUtil.dismissProgressDialog();
                                    GroupDetail updatedGroupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                                    Toast.makeText(getActivity(), "Request Rejected", Toast.LENGTH_SHORT).show();
                                    mListener.onMembershipRequestFragmentRefreshBasicGroupInfo(updatedGroupDetail);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean validateInput(){
        for (int i =0; i<mQuestionAnswerLayout.getChildCount(); i++){
            String question = ((TextView)mQuestionAnswerLayout.getChildAt(i).findViewById(R.id.question)).getText().toString();
            String answer = ((EditText)mQuestionAnswerLayout.getChildAt(i).findViewById(R.id.answer)).getText().toString();
            if (question.trim().equals("") || answer.trim().equals("")){
                Toast.makeText(getActivity(), "Answer can't be blank", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void setupMembershipRequest(){
        //Only for newRequest, we are updating the user and for other case, user would already be there
        if (mNewRequest){
            mRequest.setUser(mUser);
        }
        //This will update the answers of all questions
        for (int i =0; i<mQuestionAnswerLayout.getChildCount(); i++){
            Map<String, String> questionAnswers = mRequest.getQuestionAnswers();
            String question = ((TextView)mQuestionAnswerLayout.getChildAt(i).findViewById(R.id.question)).getText().toString();
            String answer = ((EditText)mQuestionAnswerLayout.getChildAt(i).findViewById(R.id.answer)).getText().toString();
            questionAnswers.put(question, answer);
        }
        mRequest.setAdminRemark(mAdminRemarkText.getText().toString());
        mRequest.setUserRemark(mUserRemarkText.getText().toString());
    }

    @Override
    public void onResume() {
        Logger.debug(TAG,"onResume");
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
        void onMembershipRequestFragmentRefreshGroupInfo(GroupDetail groupDetail);
        void onMembershipRequestFragmentRefreshBasicGroupInfo(GroupDetail groupDetail);
    }
}
