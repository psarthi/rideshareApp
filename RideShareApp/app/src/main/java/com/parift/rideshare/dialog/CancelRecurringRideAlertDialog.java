package com.parift.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.parift.rideshare.helper.Logger;

/**
 * Created by psarthi on 12/19/17.
 */

public class CancelRecurringRideAlertDialog extends DialogFragment {

    public static final String TAG = CancelRecurringRideAlertDialog.class.getName();
    private CancelRecurringRideAlertDialogListener mListener;
    private String mMessage;

    public CancelRecurringRideAlertDialog() {}

    public static CancelRecurringRideAlertDialog newInstance(String message, CancelRecurringRideAlertDialogListener listener){
        CancelRecurringRideAlertDialog alertDialog = new CancelRecurringRideAlertDialog();
        alertDialog.mListener = listener;
        alertDialog.mMessage = message;
        return alertDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton("Current", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.debug(TAG, "Cancel Current Clicked");
                        mListener.onPositiveCancelRecurringRideAlertDialog();
                    }
                })
                .setNegativeButton("All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.debug(TAG, "Cancel All Clicked");
                        mListener.onNegativeCancelRecurringRideAlertDialog();
                    }
                });

        return builder.create();
    }

    public interface CancelRecurringRideAlertDialogListener {
        public void onPositiveCancelRecurringRideAlertDialog();
        public void onNegativeCancelRecurringRideAlertDialog();
    }
}
