package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;

/**
 * Created by psarthi on 12/4/17.
 */

public class CancelCoTravellerFragment extends DialogFragment{

    public static final String TAG = CancelCoTravellerFragment.class.getName();

    private CancelCoTravellerFragmentListener mListener;
    private String mCoTravellerName;

    public CancelCoTravellerFragment(){}

    public static CancelCoTravellerFragment newInstance(CancelCoTravellerFragmentListener listener, String coTravellerName){
        CancelCoTravellerFragment fragment = new CancelCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mCoTravellerName = coTravellerName;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cancel_co_traveller, null);

        final TextView titleTextView = view.findViewById(R.id.title_text);
        titleTextView.setText("Reject "+mCoTravellerName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfCancelCoTravellerFragment(CancelCoTravellerFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfCancelCoTravellerFragment(CancelCoTravellerFragment.this);
                    }
                });

        return builder.create();
    }

    public interface CancelCoTravellerFragmentListener {
        public void onPositiveClickOfCancelCoTravellerFragment(DialogFragment dialogFragment);
        public void onNegativeClickOfCancelCoTravellerFragment(DialogFragment dialogFragment);
    }
}
