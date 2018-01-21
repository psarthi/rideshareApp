package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.component.FragmentLoader;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.helper.RSJsonHttpResponseHandler;
import com.digitusrevolution.rideshare.model.user.domain.MembershipForm;
import com.digitusrevolution.rideshare.model.user.dto.BasicGroup;
import com.digitusrevolution.rideshare.model.user.dto.BasicGroupInfo;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateMembershipFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateMembershipFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMembershipFormFragment extends BaseFragment {

    public static final String TAG = CreateMembershipFormFragment.class.getName();
    public static final String CREATE_TITLE = "Create Membership Form";
    public static final String UPDATE_TITLE = "Update Membership Form";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP = "group";
    private static final String ARG_RAW_IMAGE = "rawImage";

    // TODO: Rename and change types of parameters
    private String mGroupData;
    private byte[] mRawImage;

    private OnFragmentInteractionListener mListener;
    private BasicGroup mGroup;
    private LinearLayout mQuestionsLayout;
    private MembershipForm mMembershipForm;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private FloatingActionButton mAddQuestionButton;
    private Button mCreateGroupButton;
    private Button mUpdateButton;


    public CreateMembershipFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param group basicGroup in Json format
     * @return A new instance of fragment CreateMembershipFormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateMembershipFormFragment newInstance(String group, byte[] rawImage) {
        CreateMembershipFormFragment fragment = new CreateMembershipFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, group);
        args.putByteArray(ARG_RAW_IMAGE, rawImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupData = getArguments().getString(ARG_GROUP);
            mRawImage = getArguments().getByteArray(ARG_RAW_IMAGE);
        }
        mGroup = new Gson().fromJson(mGroupData, BasicGroup.class);
        mMembershipForm = new MembershipForm();
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_membership_form, container, false);
        mAddQuestionButton = view.findViewById(R.id.add_question_button);
        mQuestionsLayout = view.findViewById(R.id.membership_form_question_layout);
        mCreateGroupButton = view.findViewById(R.id.create_group_submit_button);
        mUpdateButton = view.findViewById(R.id.group_form_update_button);

        if (mGroup.getId()!=0){
            Log.d(TAG, "Its a existing group membership form");
            mCreateGroupButton.setVisibility(View.GONE);

            for (String question:mGroup.getMembershipForm().getQuestions()){
                addQuestion(question);
            }

        } else {
            Log.d(TAG, "Its a new group membeship form");
            mUpdateButton.setVisibility(View.GONE);
        }

        setActionListeners();

        return view;
    }

    private void addQuestion(String question) {
        final View questionView = getLayoutInflater().inflate(R.layout.membership_form_question, null, false);
        EditText questionEditText = questionView.findViewById(R.id.question);
        //This will take care of setting question for existing form
        questionEditText.setText(question);

        //This will add question to the layout
        mQuestionsLayout.addView(questionView);

        ImageView questionRemoveImageView = questionView.findViewById(R.id.question_delete_button);
        questionRemoveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This will remove question to the layout
                mQuestionsLayout.removeView(questionView);
            }
        });
    }

    private void setActionListeners(){

        mAddQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add Question with blank question
                addQuestion("");
            }
        });

        mCreateGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()){
                    setupGroup();
                    //Its important to use Gson to convert BasicGroup to BasicGroupInfo as its not compatible as standard java casting would not work
                    BasicGroupInfo basicGroupInfo = new Gson().fromJson(mGroupData, BasicGroupInfo.class);
                    basicGroupInfo.setRawImage(mRawImage);
                    String CREATE_GROUP = APIUrl.CREATE_GROUP.replace(APIUrl.USER_ID_KEY,Integer.toString(mUser.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.post(getActivity(), CREATE_GROUP, basicGroupInfo, new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Log.d(TAG, "Group Created:"+response.toString());
                            GroupDetail groupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                            hideSoftKeyBoard();
                            mListener.onMembershipFormFragmentLoadGroup(groupDetail);
                        }
                    });
                }
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()){
                    setupGroup();
                    String URL = APIUrl.GROUP_UPDATE_MEMBERSHIP_FORM.replace(APIUrl.USER_ID_KEY, Integer.toString(mUser.getId()))
                            .replace(APIUrl.GROUP_ID_KEY,Integer.toString(mGroup.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.post(getActivity(), URL, mGroup.getMembershipForm(), new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Log.d(TAG, "Form updated:"+response.toString());
                            GroupDetail groupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                            mListener.onMembershipFormFragmentLoadGroup(groupDetail);
                        }
                    });
                }
            }
        });


    }

    private boolean validateInput(){
        if (mQuestionsLayout.getChildCount() == 0){
            Toast.makeText(getActivity(), "Min one question is required", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (int i = 0; i < mQuestionsLayout.getChildCount(); i++){
                String question = ((EditText)mQuestionsLayout.getChildAt(i).findViewById(R.id.question)).getText().toString();
                question = question.trim();
                if (question.equals("")){
                    Toast.makeText(getActivity(), "Question can't be blank", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private void setupGroup(){
        for (int i = 0; i < mQuestionsLayout.getChildCount(); i++){
            String question = ((EditText)mQuestionsLayout.getChildAt(i).findViewById(R.id.question)).getText().toString();
            Log.d(TAG, "Question No "+i+":"+question);
            mMembershipForm.getQuestions().add(question);
        }
        mGroup.setMembershipForm(mMembershipForm);
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(true);
        if (mGroup.getId()==0){
            getActivity().setTitle(CREATE_TITLE);
        } else {
            getActivity().setTitle(UPDATE_TITLE);
        }
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
        void onMembershipFormFragmentLoadGroup(GroupDetail groupDetail);
    }
}
