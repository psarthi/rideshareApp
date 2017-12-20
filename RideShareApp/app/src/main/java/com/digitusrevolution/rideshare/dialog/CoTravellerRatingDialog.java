package com.digitusrevolution.rideshare.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.component.RideComp;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.dto.BasicUser;

/**
 * Created by psarthi on 12/4/17.
 */

public class CoTravellerRatingDialog extends DialogFragment{

    public static final String TAG = CoTravellerRatingDialog.class.getName();

    private CoTravellerRatingDialogListener mListener;
    private BasicUser mUser;
    private float mRating;

    public CoTravellerRatingDialog(){}

    public static CoTravellerRatingDialog newInstance(BasicUser user, float rating, CoTravellerRatingDialogListener listener){
        CoTravellerRatingDialog fragment = new CoTravellerRatingDialog();
        fragment.mListener = listener;
        fragment.mUser = user;
        fragment.mRating = rating;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.co_traveller_rating, null);

        final TextView titleTextView = view.findViewById(R.id.title_text);

        String coTravellerName;
        coTravellerName = mUser.getFirstName() +" "+mUser.getLastName();
        titleTextView.setText(getString(R.string.rate_cotraveller_text)+coTravellerName);
        ((RatingBar) view.findViewById(R.id.rating_bar)).setRating(mRating);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                //Pass the value of title from calling method in Bundle
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPositiveClickOfCoTravellerRatingDialog(getDialog());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClickOfCoTravellerRatingDialog(getDialog());
                    }
                });

        return builder.create();
    }

    public interface CoTravellerRatingDialogListener {
        public void onPositiveClickOfCoTravellerRatingDialog(Dialog dialog);
        public void onNegativeClickOfCoTravellerRatingDialog(Dialog dialog);
    }
}
