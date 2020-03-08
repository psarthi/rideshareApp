package com.parift.rideshare.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.parift.rideshare.R;
import com.parift.rideshare.activity.HomePageActivity;
import com.parift.rideshare.component.OfferComp;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.model.serviceprovider.dto.UserOffer;
import com.parift.rideshare.model.user.dto.BasicUser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferInfoFragment extends BaseFragment {

    public static final String TAG = OfferInfoFragment.class.getName();
    public static final String TITLE = "Offer";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OFFER = "offer";

    private OnFragmentInteractionListener mListener;
    private CommonUtil mCommonUtil;
    private BasicUser mUser;
    private UserOffer mOffer;

    public OfferInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userOffer UserOffer in Json format
     * @return A new instance of fragment Offer Info Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferInfoFragment newInstance(String userOffer) {
        OfferInfoFragment fragment = new OfferInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OFFER, userOffer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String offer = getArguments().getString(ARG_OFFER);
            mOffer = new Gson().fromJson(offer, UserOffer.class);
        }
        mCommonUtil = new CommonUtil(this);
        mUser = mCommonUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_info, container, false);
        OfferComp offerComp = new OfferComp(this, mOffer);
        offerComp.setOfferInfoLayout(view);
        return view;
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
        void onOfferInfoFragmentInteraction(String data);
    }
}
