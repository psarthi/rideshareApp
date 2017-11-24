package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.ride.domain.core.Ride;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferenceFragment extends BaseFragment {

    public static final String TAG = PreferenceFragment.class.getName();
    public static final String TITLE = "Preference";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_PARAM2 = "data";

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private String mData;

    private OnFragmentInteractionListener mListener;
    private UserSignInResult mUserSignInResult;

    public PreferenceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data  Data in Json format
     * @return A new instance of fragment PreferenceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreferenceFragment newInstance(RideType rideType, String data) {
        PreferenceFragment fragment = new PreferenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE_TYPE, rideType.toString());
        args.putString(ARG_PARAM2, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideType = RideType.valueOf(getArguments().getString(ARG_RIDE_TYPE));
            mData = getArguments().getString(ARG_PARAM2);
        }
        mUserSignInResult = new Gson().fromJson(mData,UserSignInResult.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preference, container, false);
        //This will always be invisible as it would be used in other screens and not here
        view.findViewById(R.id.trust_category_root_layout).setVisibility(View.GONE);
        if (!mRideType.equals(RideType.OfferRide)){
            view.findViewById(R.id.preference_ride_layout).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.preference_ride_request_layout).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(TITLE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPreferenceFragmentInteraction(uri);
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
        void onPreferenceFragmentInteraction(Uri uri);
    }
}
