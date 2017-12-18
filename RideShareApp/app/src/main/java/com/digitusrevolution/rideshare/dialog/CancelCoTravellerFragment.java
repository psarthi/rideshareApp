package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;

/**
 * Created by psarthi on 12/4/17.
 */

public class CancelCoTravellerFragment extends DialogFragment{

    public static final String TAG = CancelCoTravellerFragment.class.getName();

    private CancelCoTravellerFragmentListener mListener;
    private BasicRideRequest mRideRequest;
    private BasicRide mRide;

    public CancelCoTravellerFragment(){}

    public static CancelCoTravellerFragment newInstance(CancelCoTravellerFragmentListener listener, BasicRide ride, BasicRideRequest rideRequest){
        CancelCoTravellerFragment fragment = new CancelCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mRideRequest = rideRequest;
        fragment.mRide = ride;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cancel_co_traveller, null);

        final TextView titleTextView = view.findViewById(R.id.title_text);

        String coTravellerName;
        if (mListener instanceof RideComp){
            Log.d(TAG, "Ride Comp. Instance");
            coTravellerName = mRide.getDriver().getFirstName() +" "+mRide.getDriver().getLastName();
        } else {
            Log.d(TAG, "Ride Request Comp. Instance");
            coTravellerName = mRideRequest.getPassenger().getFirstName() +" "+mRideRequest.getPassenger().getLastName();
        }
        titleTextView.setText(getString(R.string.cancel_cotraveller_text)+coTravellerName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfCancelCoTravellerFragment(getDialog(), mRide, mRideRequest);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfCancelCoTravellerFragment(getDialog(), mRide, mRideRequest);
                    }
                });

        return builder.create();
    }

    public interface CancelCoTravellerFragmentListener {
        public void onPositiveClickOfCancelCoTravellerFragment(Dialog dialog, BasicRide ride, BasicRideRequest rideRequest);
        public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, BasicRide ride, BasicRideRequest rideRequest);
    }
}
