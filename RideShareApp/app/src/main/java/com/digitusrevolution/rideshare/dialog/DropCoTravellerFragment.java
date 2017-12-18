package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;

/**
 * Created by psarthi on 12/4/17.
 */

public class DropCoTravellerFragment extends DialogFragment{

    public static final String TAG = DropCoTravellerFragment.class.getName();

    private DropCoTravellerFragmentListener mListener;
    private BasicRideRequest mRideRequest;
    private RadioButton mFreeRideRadioButton;
    private TextView mPaymentTextView;

    public DropCoTravellerFragment(){}

    public static DropCoTravellerFragment newInstance(DropCoTravellerFragmentListener listener, BasicRideRequest rideRequest){
        DropCoTravellerFragment fragment = new DropCoTravellerFragment();
        fragment.mListener = listener;
        fragment.mRideRequest = rideRequest;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.drop_co_traveller, null);

        mFreeRideRadioButton = view.findViewById(R.id.free_ride_radio_button);
        mPaymentTextView = view.findViewById(R.id.payment_code_text);
        final TextView titleTextView = view.findViewById(R.id.title_text);
        titleTextView.setText(getString(R.string.drop_text_msg)+mRideRequest.getPassenger().getFirstName() +" "+mRideRequest.getPassenger().getLastName());

        //This will enable/disable payment code text according to the selection of radio button
        mFreeRideRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mPaymentTextView.setEnabled(false);
                } else {
                    mPaymentTextView.setEnabled(true);
                }
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No action required here as we are overwriting the onClick in onStart method
                        //Listener would be overwritten by onStart positiveButton onClick
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfDropCoTravellerFragment(getDialog(), mRideRequest);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog!=null){
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //This will take care of Paid Ride
                    if (!mFreeRideRadioButton.isChecked()){
                        Log.d(TAG, "Inside onStart Positive Button Click. Code is/Entered Code:"+mRideRequest.getConfirmationCode()+"/"+mPaymentTextView.getText());
                        if (mPaymentTextView.getText().toString().equals(mRideRequest.getConfirmationCode())){
                            mListener.onPositiveClickOfDropCoTravellerFragment(dialog, mRideRequest);
                            //dismiss should be the last line otherwise you will get NPE on the Listener callback as Dialog fragment would be lost on call of Dismiss
                            //Don't try to return the fragment itself in the interface instead return dialog as dismiss would kill this fragment itself
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Payment Code is not valid", Toast.LENGTH_LONG).show();
                        }
                    }
                    //This will take care of Free Ride
                    else {
                        mListener.onPositiveClickOfDropCoTravellerFragment(dialog, mRideRequest);
                        //dismiss should be the last line otherwise you will get NPE on the Listener callback as Dialog fragment would be lost on call of Dismiss
                        //Don't try to return the fragment itself in the interface instead return dialog as dismiss would kill this fragment itself
                        dismiss();
                    }
                }
            });
        }
    }

    public interface DropCoTravellerFragmentListener{
        public void onPositiveClickOfDropCoTravellerFragment(Dialog dialog, BasicRideRequest rideRequest);
        public void onNegativeClickOfDropCoTravellerFragment(Dialog dialog, BasicRideRequest rideRequest);
    }

    public BasicRideRequest getRideRequest() {
        return mRideRequest;
    }
}
