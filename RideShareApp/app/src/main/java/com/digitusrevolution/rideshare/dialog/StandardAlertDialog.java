package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;

/**
 * Created by psarthi on 12/19/17.
 */

public class StandardAlertDialog extends DialogFragment {

    public static final String TAG = StandardAlertDialog.class.getName();
    private StandardAlertDialogListener mListener;
    private String mMessage;

    public StandardAlertDialog() {}

    public StandardAlertDialog newInstance(String message, StandardAlertDialogListener listener){
        StandardAlertDialog standardAlertDialog = new StandardAlertDialog();
        standardAlertDialog.mListener = listener;
        standardAlertDialog.mMessage = message;
        return standardAlertDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Submit Clicked");
                        mListener.onPositiveStandardAlertDialog();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel Clicked");
                        mListener.onNegativeStandardAlertDialog();
                    }
                });

        return builder.create();
    }

    public interface StandardAlertDialogListener {
        public void onPositiveStandardAlertDialog();
        public void onNegativeStandardAlertDialog();
    }
}
