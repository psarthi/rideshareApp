package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parift.rideshare.R;
import com.parift.rideshare.component.FragmentLoader;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.dialog.StandardAlertDialog;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.user.domain.ApprovalStatus;
import com.parift.rideshare.model.user.dto.BasicMembershipRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
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

    public static final String TAG = AboutGroupFragment.class.getName();
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
    private Button mLeaveButton;
    private Button mJoinButton;
    private Button mMemberShipFormButton;
    private Button mMembershipRequestStatusButton;



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
        Logger.debug(TAG, "newInstance Called");
        AboutGroupFragment fragment = new AboutGroupFragment();
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
        mFragmentLoader = new FragmentLoader(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug(TAG, "onCreateView Called of instance:"+this.hashCode());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_group, container, false);
        ((TextView) view.findViewById(R.id.group_description)).setText(mGroup.getInformation());
        mLeaveButton = view.findViewById(R.id.leave_group_button);
        mJoinButton = view.findViewById(R.id.group_join_button);
        mMemberShipFormButton = view.findViewById(R.id.membership_form_button);
        mMembershipRequestStatusButton = view.findViewById(R.id.group_membership_request_status_button);

        //If user is a member
        if (mGroup.getMembershipStatus().isMember()){
            //Member don't see Join/Request Status button
            mJoinButton.setVisibility(View.GONE);
            mMembershipRequestStatusButton.setVisibility(View.GONE);

            if (!mGroup.getMembershipStatus().isAdmin()){
                //Only admin can see the membership form
                mMemberShipFormButton.setVisibility(View.GONE);
                view.findViewById(R.id.membership_form_button_seperator).setVisibility(View.GONE);
            }
            if (mGroup.getOwner().getId()==mUser.getId()){
                //Owners's can't leave the group
                mLeaveButton.setVisibility(View.GONE);
                view.findViewById(R.id.leave_group_button_seperator).setVisibility(View.GONE);
            }
        }
        //If user is not member
        else {
            //Non member don't see Leave/Membership Form button
            mLeaveButton.setVisibility(View.GONE);
            mMemberShipFormButton.setVisibility(View.GONE);
            view.findViewById(R.id.leave_group_button_seperator).setVisibility(View.GONE);
            view.findViewById(R.id.membership_form_button_seperator).setVisibility(View.GONE);

            //This is the case, when request has been submitted
            if (mGroup.getMembershipStatus().isRequestSubmitted()){
                mJoinButton.setVisibility(View.GONE);
                String status = mGroup.getMembershipStatus().getApprovalStatus().toString();
                mMembershipRequestStatusButton.setText(status);
                if (mGroup.getMembershipStatus().getApprovalStatus().equals(ApprovalStatus.Rejected)){
                    //This will overwrite the status
                    status = status + " (View Details)";
                    mMembershipRequestStatusButton.setText(status);
                }
            }
            //When request has not been submitted
            else {
                mMembershipRequestStatusButton.setVisibility(View.GONE);
            }
        }

        setActionListeners();

        return view;
    }

    private void setActionListeners(){

        mJoinButton.setOnClickListener(new View.OnClickListener() {
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


        mMembershipRequestStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.GET_SPECIFIC_MEMBERSHIP_REQUEST.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                        .replace(APIUrl.GROUP_ID_KEY, Long.toString(mGroup.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            BasicMembershipRequest request = new Gson().fromJson(response.toString(), BasicMembershipRequest.class);
                            mFragmentLoader.loadMembershipRequestFragment(new Gson().toJson(request), false, false);
                        }
                    }
                });
            }
        });

        mLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = getActivity().getString(R.string.standard_cancellation_confirmation_message);
                DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardListAlertDialogListener() {
                    @Override
                    public void onPositiveStandardAlertDialog() {
                        String url = APIUrl.LEAVE_GROUP.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                                .replace(APIUrl.GROUP_ID_KEY, Long.toString(mGroup.getId()));
                        RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (isAdded()) {
                                    super.onSuccess(statusCode, headers, response);
                                    mGroup = new Gson().fromJson(response.toString(), GroupDetail.class);
                                    //Toast.makeText(getActivity(), "Successfully left the group", Toast.LENGTH_SHORT).show();
                                    mListener.onAboutGroupFragmentRefresh(mGroup);
                                }
                            }
                        });
                    }

                    @Override
                    public void onNegativeStandardAlertDialog() {
                        Logger.debug(TAG, "Negative Button clicked on standard dialog");
                    }
                });
                dialogFragment.show(getActivity().getSupportFragmentManager(), StandardAlertDialog.TAG);
            }
        });

        mMemberShipFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentLoader.loadMembershipFormFragment(new Gson().toJson(mGroup), null);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.debug(TAG,"Inside OnResume of instance:"+this.hashCode());
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
        void onAboutGroupFragmentRefresh(GroupDetail groupDetail);
    }
}
