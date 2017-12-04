package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;

/**
 * Created by psarthi on 12/4/17.
 */

public class RejectCoTravellerFragment extends DialogFragment{

    public static final String TAG = RejectCoTravellerFragment.class.getName();

    private RejectCoTravellerFragmentListener mListener;
    private String mCoTravellerName;

    public RejectCoTravellerFragment(){}

    public static RejectCoTravellerFragment newInstance(RejectCoTravellerFragmentListener listener, String coTravellerName){
        RejectCoTravellerFragment fragment = new RejectCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mCoTravellerName = coTravellerName;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reject_co_traveller, null);

        final TextView titleTextView = view.findViewById(R.id.title_text);
        titleTextView.setText("Reject "+mCoTravellerName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfRejectCoTravellerFragment(RejectCoTravellerFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfRejectCoTravellerFragment(RejectCoTravellerFragment.this);
                    }
                });

        return builder.create();
    }

    public interface RejectCoTravellerFragmentListener{
        public void onPositiveClickOfRejectCoTravellerFragment(DialogFragment dialogFragment);
        public void onNegativeClickOfRejectCoTravellerFragment(DialogFragment dialogFragment);
    }
}
