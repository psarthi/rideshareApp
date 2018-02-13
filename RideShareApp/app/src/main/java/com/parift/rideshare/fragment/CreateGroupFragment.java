package com.parift.rideshare.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.parift.rideshare.model.common.ResponseMessage;
import com.parift.rideshare.model.user.dto.BasicGroup;
import com.parift.rideshare.model.user.dto.BasicGroupInfo;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.GroupDetail;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFragment extends BaseFragment{

    public static final String TAG = CreateGroupFragment.class.getName();
    public static final String CREATE_TITLE = "Create Group";
    public static final String UPDATE_TITLE = "Update Group";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_DETAIL = "groupDetail";
    private static final String ARG_NEW_GROUP = "newGroup";

    // TODO: Rename and change types of parameters
    private String mGroupDetailData;
    private boolean mNewGroup;

    private OnFragmentInteractionListener mListener;
    private BasicGroup mGroup;
    CircleImageView mGroupPhotoImageView;
    private byte[] mRawImage;
    private Button mNextButton;
    private Button mUpdateButton;
    private EditText mGroupNameEditText;
    private EditText mGroupDesriptionEditText;
    private BasicUser mUser;
    private CommonUtil mCommonUtil;
    private boolean mGroupNameExist;
    private TextView mGroupNameExistMsgTextView;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @param newGroup is it a new group or existing
     * @param groupDetail Group Detail in Json format
     * @return A new instance of fragment CreateGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateGroupFragment newInstance(boolean newGroup, String groupDetail) {
        CreateGroupFragment fragment = new CreateGroupFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_NEW_GROUP, newGroup);
        args.putString(ARG_GROUP_DETAIL, groupDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewGroup = getArguments().getBoolean(ARG_NEW_GROUP);
            mGroupDetailData = getArguments().getString(ARG_GROUP_DETAIL);
        }
        if (mNewGroup){
            mGroup = new BasicGroup();
        } else {
            mGroup = new Gson().fromJson(mGroupDetailData, GroupDetail.class);
        }

        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        mNextButton = view.findViewById(R.id.create_group_next_button);
        mUpdateButton = view.findViewById(R.id.group_update_button);
        mGroupPhotoImageView = view.findViewById(R.id.group_photo_image_view);
        mGroupNameEditText = view.findViewById(R.id.group_name_text);
        mGroupDesriptionEditText = view.findViewById(R.id.group_description);
        mGroupNameExistMsgTextView = view.findViewById(R.id.group_exist_message);

        //Make it invisible at the start
        mGroupNameExistMsgTextView.setVisibility(View.INVISIBLE);

        if (mNewGroup){
            mUpdateButton.setVisibility(View.GONE);
        } else {
            mNextButton.setVisibility(View.GONE);
            mGroupNameEditText.setText(mGroup.getName());
            //Group name can't be changed else it will unnessarily complicate the system
            //as we can't have duplicate names
            mGroupNameEditText.setEnabled(false);
            mGroupDesriptionEditText.setText(mGroup.getInformation());
            if (mGroup.getPhoto()!=null){
                String imageUrl = mGroup.getPhoto().getImageLocation();
                Picasso.with(getActivity()).load(imageUrl).into(mGroupPhotoImageView);
            }
        }

        setupActionListeners();

        return view;
    }

    private void setupActionListeners() {
        mGroupPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Note - We don't want to have camera selection due to two reason
                //1. Group photo doesn't require mostly individual photo
                //2. Dialog is not looking good
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()){
                    setGroup();
                    FragmentLoader fragmentLoader = new FragmentLoader(CreateGroupFragment.this);
                    fragmentLoader.loadMembershipFormFragment(new Gson().toJson(mGroup), mRawImage);
                }
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()){
                    setGroup();
                    String basicGroupJson = new Gson().toJson(mGroup);
                    BasicGroupInfo basicGroupInfo = new Gson().fromJson(basicGroupJson, BasicGroupInfo.class);
                    basicGroupInfo.setRawImage(mRawImage);

                    String url = APIUrl.UPDATE_GROUP.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.post(getActivity(), url, basicGroupInfo, new RSJsonHttpResponseHandler(mCommonUtil){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            //This will update the status of user group member in shared preference
                            //which will be used by create ride use case
                            mCommonUtil.updateIsUserGroupMember(true);
                            GroupDetail groupDetail = new Gson().fromJson(response.toString(), GroupDetail.class);
                            FragmentLoader fragmentLoader = new FragmentLoader(CreateGroupFragment.this);
                            fragmentLoader.loadGroupInfoByRemovingBackStacks(groupDetail);
                        }
                    });

                }
            }
        });

        mGroupNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String groupName = mGroupNameEditText.getText().toString();
                if (!hasFocus){
                    if (!groupName.trim().equals("")){
                        String url = APIUrl.CHECK_GROUP_NAME_EXIST.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                                .replace(APIUrl.SEARCH_NAME_KEY, groupName);

                        RESTClient.get(url, null, new RSJsonHttpResponseHandler(mCommonUtil){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                ResponseMessage responseMessage = new Gson().fromJson(response.toString(), ResponseMessage.class);
                                boolean status = Boolean.valueOf(responseMessage.getResult());
                                if (status){
                                    mGroupNameExist = true;
                                    mGroupNameExistMsgTextView.setVisibility(View.VISIBLE);
                                } else {
                                    mGroupNameExist = false;
                                    mGroupNameExistMsgTextView.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        mGroupNameExist = false;
                        mGroupNameExistMsgTextView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }

    private void showPhotoMenuDialog()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item);
        final ArrayList<String> list = new ArrayList<>();
        list.add("Choose Photo");
        list.add("Take Photo");
        arrayAdapter.addAll(list);

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                if(strName.equals(list.get(0))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 0);//one can be replaced with any action code
                }
                if(strName.equals(list.get(1))) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 1);//zero can be replaced with any action code
                }
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bm = BitmapFactory.decodeStream(
                                getActivity().getContentResolver().openInputStream(selectedImage));
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
                        mGroupPhotoImageView.setImageBitmap(bm);
                        //Note bytes is the compressed bytes
                        mRawImage = bytes.toByteArray();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            //Note - This case is not in used until we give an option of taking photo intent
            case 1:
                if(resultCode == RESULT_OK){
                    Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
                    mGroupPhotoImageView.setImageBitmap(selectedImage);
                    //Note bytes is the compressed bytes
                    mRawImage = bytes.toByteArray();
                }
                break;
        }
    }

    private boolean validateInput(){
        String name = mGroupNameEditText.getText().toString();
        String desciption = mGroupDesriptionEditText.getText().toString();
        name = name.trim();
        desciption = desciption.trim();
        if (name.equals("")){
            Toast.makeText(getActivity(), "Group Name can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (desciption.equals("")){
            Toast.makeText(getActivity(), "Description can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mGroupNameExist){
            Toast.makeText(getActivity(), "Group name already exist", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setGroup(){
        String groupName = ((EditText) getView().findViewById(R.id.group_name_text)).getText().toString();
        String groupDesription = ((EditText) getView().findViewById(R.id.group_description)).getText().toString();
        String trimmedGroupDescription = groupDesription.trim().replaceAll(" +"," ")
                .replaceAll("\n"," ");
        Logger.debug(TAG, "Original Information -"+groupDesription);
        Logger.debug(TAG, "Post Trimming Information -"+trimmedGroupDescription);

        CommonUtil commonUtil = new CommonUtil(this);
        BasicUser user = commonUtil.getUser();
        mGroup.setName(groupName);
        mGroup.setInformation(trimmedGroupDescription);
        mGroup.setOwner(user);
    }

    @Override
    public void onResume() {
        Logger.debug(TAG,"onResume");
        super.onResume();
        ((HomePageActivity)getActivity()).showBackButton(true);
        if (mNewGroup){
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
        void onCreateGroupFragmentInteraction(String data);
    }

}
