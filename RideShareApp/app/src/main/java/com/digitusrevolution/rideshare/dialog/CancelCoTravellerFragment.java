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
import com.digitusrevolution.rideshare.helper.Logger;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;

/**
 * Created by psarthi on 12/4/17.
 */

public class CancelCoTravellerFragment extends DialogFragment{

    public static final String TAG = CancelCoTravellerFragment.class.getName();

    private CancelCoTravellerFragmentListener mListener;
    private FullRideRequest mRideRequest;

    public CancelCoTravellerFragment(){}

    public static CancelCoTravellerFragment newInstance(CancelCoTravellerFragmentListener listener, FullRideRequest rideRequest){
        CancelCoTravellerFragment fragment = new CancelCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mRideRequest = rideRequest;
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
            Logger.debug(TAG, "Ride Comp. Instance");
            coTravellerName = mRideRequest.getPassenger().getFirstName() +" "+mRideRequest.getPassenger().getLastName();
        } else {
            Logger.debug(TAG, "Ride Request Comp. Instance");
            coTravellerName = mRideRequest.getAcceptedRide().getDriver().getFirstName() +" "+mRideRequest.getAcceptedRide().getDriver().getLastName();
        }
        titleTextView.setText(getString(R.string.cancel_cotraveller_text)+coTravellerName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfCancelCoTravellerFragment(getDialog(), mRideRequest);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfCancelCoTravellerFragment(getDialog(), mRideRequest);
                    }
                });

        return builder.create();
    }

    public interface CancelCoTravellerFragmentListener {
        public void onPositiveClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest);
        public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest);
    }
}
