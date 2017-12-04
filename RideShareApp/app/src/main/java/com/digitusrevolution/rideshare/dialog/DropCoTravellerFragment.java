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

public class DropCoTravellerFragment extends DialogFragment{

    public static final String TAG = DropCoTravellerFragment.class.getName();

    private DropCoTravellerFragmentListener mListener;
    private String mCoTravellerName;
    private String mPaymentCode;

    public DropCoTravellerFragment(){}

    public static DropCoTravellerFragment newInstance(DropCoTravellerFragmentListener listener,
                                                      String coTravellerName, String paymentCode){
        DropCoTravellerFragment fragment = new DropCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mCoTravellerName = coTravellerName;
        fragment.mPaymentCode = paymentCode;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.drop_co_traveller, null);

        RadioButton freeRideRadioButton= view.findViewById(R.id.free_ride_radio_button);
        final TextView paymentTextView = view.findViewById(R.id.payment_code_text);
        final TextView titleTextView = view.findViewById(R.id.title_text);
        titleTextView.setText("Drop "+mCoTravellerName);

        //This will enable/disable payment code text according to the selection of radio button
        freeRideRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    paymentTextView.setEnabled(false);
                } else {
                    paymentTextView.setEnabled(true);
                }
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfDropCoTravellerFragment(DropCoTravellerFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfDropCoTravellerFragment(DropCoTravellerFragment.this);
                    }
                });

        return builder.create();
    }

    public interface DropCoTravellerFragmentListener{
        public void onPositiveClickOfDropCoTravellerFragment(DialogFragment dialogFragment);
        public void onNegativeClickOfDropCoTravellerFragment(DialogFragment dialogFragment);
    }
}
