package com.digitusrevolution.rideshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.adapter.CoTravellerAdapter;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RideInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RideInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideInfoFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RIDE = "ride";

    public static final String TAG = RideInfoFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mRideData;

    private OnFragmentInteractionListener mListener;
    private FullRide mRide;
    private LinearLayout mCoTravellerLinearLayout;

    public RideInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ride data in json format
     * @return A new instance of fragment RideInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RideInfoFragment newInstance(String ride) {
        RideInfoFragment fragment = new RideInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RIDE, ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRideData = getArguments().getString(ARG_RIDE);
        }
        mRide = new Gson().fromJson(mRideData, FullRide.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_info, container, false);
        mCoTravellerLinearLayout = view.findViewById(R.id.ride_info_co_traveller_layout);
        //Note - We are using the same adapter which is also applicable for listview,
        //but this can be used for our own prupose and in this case, its the best suitable option
        ArrayAdapter<BasicRideRequest> coTravellerAdapter = new CoTravellerAdapter(getActivity(), (List<BasicRideRequest>) mRide.getAcceptedRideRequests());
        for (int i=0; i<coTravellerAdapter.getCount(); i++){
            View coTravellerView = null;
            coTravellerView = coTravellerAdapter.getView(i, coTravellerView, container);
            mCoTravellerLinearLayout.addView(coTravellerView);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"Inside OnResume");
        showBackStackDetails();
        showChildFragmentDetails();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRideInfoFragmentInteraction(uri);
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Inside Destroy View");
        showChildFragmentDetails();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ride_info_map);
        if (mapFragment != null) {
            // This needs to be called if you are facing duplicate map id on fragment reload
            // getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
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
        void onRideInfoFragmentInteraction(Uri uri);
    }
}
