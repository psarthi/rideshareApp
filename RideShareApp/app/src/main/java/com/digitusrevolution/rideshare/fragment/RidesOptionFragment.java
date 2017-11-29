package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.HomePageActivity;
import com.digitusrevolution.rideshare.model.app.RideType;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;
import com.digitusrevolution.rideshare.model.user.dto.UserSignInResult;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RidesOptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RidesOptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidesOptionFragment extends BaseFragment {

    public static final String TAG = RidesOptionFragment.class.getName();
    public static final String OFFER_RIDE_OPTION_TITLE = "Offer Ride Option";
    public static final String REQUEST_RIDE_OPTION_TITLE = "Request Ride Option";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE_TYPE = "rideType";
    private static final String ARG_PARAM2 = "data";

    // TODO: Rename and change types of parameters
    private RideType mRideType;
    private String mData;

    private OnFragmentInteractionListener mListener;
    private BasicUser mUser;

    public RidesOptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param rideType Type of ride e.g. Offer Ride or Request Ride
     * @param data  Data in Json format
     * @return A new instance of fragment RidesOptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RidesOptionFragment newInstance(RideType rideType, String data) {
        RidesOptionFragment fragment = new RidesOptionFragment();
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
        mUser = getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        // Inflate the layout for this fragment
        if (mRideType.equals(RideType.OfferRide)){
            view = inflater.inflate(R.layout.fragment_offer_ride_option, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_ride_request_option, container, false);
        }
        Log.d(TAG,"RideType:"+mRideType);
        view.findViewById(R.id.trust_category_layout).setVisibility(View.GONE);

        View buttonView = view.findViewById(R.id.button_layout);
        buttonView.findViewById(R.id.rides_option_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //((HomePageActivity)getActivity()).showBackButton(true);
        if (mRideType.equals(RideType.OfferRide)){
            getActivity().setTitle(OFFER_RIDE_OPTION_TITLE);
        } else {
            getActivity().setTitle(REQUEST_RIDE_OPTION_TITLE);
        }
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
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
