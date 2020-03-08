package com.parift.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.parift.rideshare.helper.Logger;

/**
 * Created by psarthi on 12/19/17.
 */

public class StandardAlertDialog extends DialogFragment {

    public static final String TAG = StandardAlertDialog.class.getName();
    private StandardListAlertDialogListener mListener;
    private String mMessage;

    public StandardAlertDialog() {}

    public static StandardAlertDialog newInstance(String message, StandardListAlertDialogListener listener){
        StandardAlertDialog standardAlertDialog = new StandardAlertDialog();
        standardAlertDialog.mListener = listener;
        standardAlertDialog.mMessage = message;
        return standardAlertDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.debug(TAG, "Submit Clicked");
                        mListener.onPositiveStandardAlertDialog();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Logger.debug(TAG, "Cancel Clicked");
                        mListener.onNegativeStandardAlertDialog();
                    }
                });

        return builder.create();
    }

    public interface StandardListAlertDialogListener {
        public void onPositiveStandardAlertDialog();
        public void onNegativeStandardAlertDialog();
    }
}
