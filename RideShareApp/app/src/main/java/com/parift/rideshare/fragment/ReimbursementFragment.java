package com.parift.rideshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.serviceprovider.domain.core.Offer;
import com.parift.rideshare.model.serviceprovider.dto.ReimbursementRequest;
import com.parift.rideshare.model.user.dto.BasicUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReimbursementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReimbursementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReimbursementFragment extends BaseFragment {

    public static final String TAG = ReimbursementFragment.class.getName();
    public static final String TITLE = "Reimbursement";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OFFER = "offer";

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private Offer mOffer;

    ImageView mFile1ImageView;
    ImageView mFile2ImageView;
    ImageView mFile3ImageView;

    TextView mFile1DeleteTextView;
    TextView mFile2DeleteTextView;
    TextView mFile3DeleteTextView;

    private LinkedList<FileDetails> mImages = new LinkedList<>();

    public ReimbursementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param offer Offer in Json format
     * @return A new instance of fragment Offer Info Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReimbursementFragment newInstance(String offer) {
        ReimbursementFragment fragment = new ReimbursementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OFFER, offer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String offer = getArguments().getString(ARG_OFFER);
            mOffer = new Gson().fromJson(offer, Offer.class);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reimbursement, container, false);
        View file1View = view.findViewById(R.id.file_1);
        View file2View = view.findViewById(R.id.file_2);
        View file3View = view.findViewById(R.id.file_3);

        initializeFileDetails(file1View, 0);
        initializeFileDetails(file2View, 1);
        initializeFileDetails(file3View, 2);

        Button submitButton = view.findViewById(R.id.reimbursement_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReimbursementRequest();
            }
        });

        return view;
    }

    private void initializeFileDetails(View fileView, int index){
        FileDetails fileDetails = new FileDetails();
        fileDetails.index = index;
        fileDetails.mFileImageView = fileView.findViewById(R.id.file_image);
        fileDetails.mDeleteTextView = fileView.findViewById(R.id.file_delete_text);
        fileDetails.mDeleteTextView.setVisibility(View.INVISIBLE);
        mImages.add(fileDetails);
        setFileUploadListener(fileView,index);
        setFileDeleteListener(index);

    }

    private void setFileUploadListener(View fileView, final int requestCode) {
        fileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Note - We don't want to have camera selection due to two reason
                //1. Group photo doesn't require mostly individual photo
                //2. Dialog is not looking good
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , requestCode);//one can be replaced with any action code
            }
        });
    }

    private void setFileDeleteListener(final int index){
        mImages.get(index).mDeleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImages.get(index).mFileImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                mImages.get(index).uploaded=false;
                mImages.get(index).image=null;
                mImages.get(index).mDeleteTextView.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    handleSelectedPhoto(imageReturnedIntent, requestCode);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    handleSelectedPhoto(imageReturnedIntent, requestCode);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    handleSelectedPhoto(imageReturnedIntent, requestCode);
                }
                break;
        }

    }

    private void handleSelectedPhoto(Intent imageReturnedIntent, int requestCode) {
        Uri selectedImage = imageReturnedIntent.getData();
        try {
            Bitmap bm = BitmapFactory.decodeStream(
                    getActivity().getContentResolver().openInputStream(selectedImage));
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
            mImages.get(requestCode).mFileImageView.setImageBitmap(bm);
            mImages.get(requestCode).mDeleteTextView.setVisibility(View.VISIBLE);
            //Note bytes is the compressed bytes
            mImages.get(requestCode).uploaded=true;
            mImages.get(requestCode).image = bytes.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void submitReimbursementRequest(){

        ReimbursementRequest request = new ReimbursementRequest();
        LinkedList<byte[]> uploadedPhotos = new LinkedList<>();
        for (FileDetails fileDetails: mImages){
            if (fileDetails.uploaded){
                uploadedPhotos.add(fileDetails.image);
            }
        }
        request.setImages(uploadedPhotos);
        request.setRewardTransactionDateTime(Calendar.getInstance().getTime());
        String URL = APIUrl.CREATE_REWARD_REIMBURSEMENT_TRANSACTIONS.replace(APIUrl.USER_ID_KEY, Long.toString(mUser.getId()))
                .replace(APIUrl.OFFER_ID_KEY,Integer.toString(mOffer.getId()));
        mCommonUtil.showProgressDialog();

        RESTClient.post(getActivity(),URL, request, new RSJsonHttpResponseHandler(mCommonUtil){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Toast.makeText(getActivity(), "Request successfully submitted", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        } );

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
        Logger.debug(TAG,"Inside OnResume");
        showBackStackDetails();
        ((HomePageActivity)getActivity()).showBackButton(true);
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
        void onReimbursementFragmentInteraction(String data);
    }

    private class FileDetails{
        int index;
        boolean uploaded;
        byte[] image;
        ImageView mFileImageView;
        TextView mDeleteTextView;
    }
}
