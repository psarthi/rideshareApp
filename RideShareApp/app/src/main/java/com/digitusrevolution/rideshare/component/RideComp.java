package com.digitusrevolution.rideshare.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.activity.LandingPageActivity;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.config.Constant;
import com.digitusrevolution.rideshare.dialog.CancelCoTravellerFragment;
import com.digitusrevolution.rideshare.dialog.DropCoTravellerFragment;
import com.digitusrevolution.rideshare.dialog.StandardAlertDialog;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.UserProfileFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideMode;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequest;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRide;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRidesInfo;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideComp implements DropCoTravellerFragment.DropCoTravellerFragmentListener,
        CancelCoTravellerFragment.CancelCoTravellerFragmentListener{

    public static final String TAG = RideComp.class.getName();
    private BaseFragment mBaseFragment;
    private FullRide mRide;

    Button mCancelButton;
    Button mStartButton;
    Button mEndButton;
    Button mNavigationButton;
    LinearLayout mBasicRideButtonsLayout;
    private CommonUtil mCommonUtil;
    private RideCompListener mListener;
    private boolean mEndRideConfirmation;

    public RideComp(BaseFragment fragment, FullRide ride){
        mBaseFragment = fragment;
        mRide = ride;
        mCommonUtil = new CommonUtil(fragment);
        //Its important to check instance of fragment as some fragment may not be required to implement Listener
        //And in those cases, it will throw ClassCasteException
        if (fragment instanceof RideCompListener) mListener = (RideCompListener) fragment;
    }

    //Reason behind this additional constructor so that we can send callback directly to adapter instead of BaseFragment class only
    public RideComp(RecyclerView.Adapter adapter, BaseFragment fragment, FullRide ride){
        mBaseFragment = fragment;
        mRide = ride;
        mCommonUtil = new CommonUtil(fragment);
        //Its important to check instance of fragment as some fragment may not be required to implement Listener
        //And in those cases, it will throw ClassCasteException
        mListener = (RideCompListener) adapter;
    }


    public void setBasicRideLayout(View view){

        View basic_ride_layout = view.findViewById(R.id.basic_ride_layout);
        mCancelButton = basic_ride_layout.findViewById(R.id.ride_cancel_button);
        mStartButton = basic_ride_layout.findViewById(R.id.ride_start_button);
        mEndButton = basic_ride_layout.findViewById(R.id.ride_end_button);
        mNavigationButton = basic_ride_layout.findViewById(R.id.ride_navigation_button);
        mBasicRideButtonsLayout = basic_ride_layout.findViewById(R.id.ride_buttons_layout);

        TextView rideIdTextView = basic_ride_layout.findViewById(R.id.ride_id_text);
        String rideIdText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text) +mRide.getId();
        rideIdTextView.setText(rideIdText);
        TextView rideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        rideStatusTextView.setText(mRide.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(mCommonUtil.getFormattedDateTimeString(mRide.getStartTime()));
        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(mRide.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(mRide.getEndPointAddress());

        //This will set the visibility of ride buttons initially
        updateBasicRideLayoutButtonsVisibility();
        //This will set listeners for ride buttons
        setBasicRideLayoutButtonsOnClickListener();

        RideInfoFragment fragment = (RideInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideInfoFragment.TAG);
        if (fragment!=null && mRide.getId() == fragment.getRideId()){
            Log.d(TAG, "Ride Info is already loaded");
        } else {
            basic_ride_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String GET_RIDE_URL = APIUrl.GET_RIDE_URL.replace(APIUrl.ID_KEY,Integer.toString(mRide.getId()));
                    RESTClient.get(GET_RIDE_URL, null, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadRideInfoFragment(response.toString());
                        }

                    });
                }
            });
        }
    }

    private void setBasicRideLayoutButtonsOnClickListener() {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mBaseFragment.getString(R.string.standard_cancellation_confirmation_message);
                DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardAlertDialogListener() {
                    @Override
                    public void onPositiveStandardAlertDialog() {
                        String CANCEL_RIDE = APIUrl.CANCEL_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                        RESTClient.get(CANCEL_RIDE, null, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.d(TAG, "Ride Cancelled");
                                mRide = new Gson().fromJson(response.toString(), FullRide.class);
                                mListener.onRideRefresh(mRide);
                                Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Cancelled", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                if (errorResponse!=null) {
                                    ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                                    Log.d(TAG, errorMessage.getErrorMessage());
                                    Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                                    Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNegativeStandardAlertDialog() {
                        Log.d(TAG, "Negative Button clicked on standard dialog");
                    }
                });

                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), StandardAlertDialog.TAG);
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String START_RIDE = APIUrl.START_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                RESTClient.get(START_RIDE, null, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "Ride Started");
                        mRide = new Gson().fromJson(response.toString(), FullRide.class);
                        mListener.onRideRefresh(mRide);
                        Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Started", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        if (errorResponse!=null) {
                            ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                            Log.d(TAG, errorMessage.getErrorMessage());
                            Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                            Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int notOnBoardedPassenger = 0;
                for (BasicRideRequest rideRequest: mRide.getAcceptedRideRequests()){
                    if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){
                        notOnBoardedPassenger++;
                    }
                }

                if (notOnBoardedPassenger > 0){
                    String message = mBaseFragment.getString(R.string.standard_cancellation_confirmation_message);
                    DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardAlertDialogListener() {
                        @Override
                        public void onPositiveStandardAlertDialog() {
                            mEndRideConfirmation = true;
                        }

                        @Override
                        public void onNegativeStandardAlertDialog() {
                            Log.d(TAG, "Negative Button clicked on standard dialog");
                        }
                    });
                    dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), StandardAlertDialog.TAG);
                }

                if (mEndRideConfirmation || notOnBoardedPassenger == 0){
                    String END_RIDE = APIUrl.END_RIDE.replace(APIUrl.ID_KEY, Integer.toString(mRide.getId()));
                    RESTClient.get(END_RIDE, null, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d(TAG, "Ride Ended");
                            mRide = new Gson().fromJson(response.toString(), FullRide.class);
                            mListener.onRideRefresh(mRide);
                            Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Ended", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (errorResponse!=null) {
                                ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                                Log.d(TAG, errorMessage.getErrorMessage());
                                Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                            }
                            else {
                                Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                                Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        mNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Continue Navigation");
            }
        });
    }

    private void updateBasicRideLayoutButtonsVisibility(){

        //Reason for setting it visible to handle view holder reuse where once item is invisible, it remains as it is
        //e.g. when one item make something invisble but the same view is used by another item, then it remains invisble
        //Initial value of Buttons Layout which would be applicable to all except when ride is cancelled or finished
        //Reason for setting this just to avoid any issue with visiblity when layout is invisible but child is visible
        //not sure how that would behave so cleaner option is to make layout visible and then childs visible/invisible as well
        mBasicRideButtonsLayout.setVisibility(View.VISIBLE);

        if (mRide.getStatus().equals(RideStatus.Planned) || mRide.getStatus().equals(RideStatus.Fulfilled)){
            //Visible Buttons
            mCancelButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            //This will make end and navigation button invisible as ride is never started
            mEndButton.setVisibility(View.GONE);
            mNavigationButton.setVisibility(View.GONE);

            //Exception Rules Buttons visibility
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(mRide.getStartTime());
            //This will subtract some buffer time from start time e.g. reduce 15 mins from current time
            //(using -) in add function for substraction
            startTime.add(Calendar.MINUTE, -Constant.START_TIME_BUFFER);
            //This will make Start button invisible if its early than start time by subtracting buffer
            //e.g. if ride start time is 3:00 PM and buffer is 15 mins, then it can only be started after 2:45 PM
            Log.d(TAG, "Ride Start Time - Buffer:"+startTime.getTime().toString());
            Log.d(TAG, "Current Time:"+Calendar.getInstance().getTime().toString());
            if (Calendar.getInstance().getTime().before(startTime.getTime())){
                mStartButton.setVisibility(View.GONE);
            }
            //This will make Start and Cancel button invisible post end time of ride
            if (mRide.getEndTime().before(Calendar.getInstance().getTime())){
                mCancelButton.setVisibility(View.GONE);
                mStartButton.setVisibility(View.GONE);
            }
        }
        if (mRide.getStatus().equals(RideStatus.Started)){
            //Visible Buttons
            mNavigationButton.setVisibility(View.VISIBLE);
            mEndButton.setVisibility(View.VISIBLE);

            //Invisible buttons
            mCancelButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.GONE);
        }
        if (mRide.getStatus().equals(RideStatus.Finished) || mRide.getStatus().equals(RideStatus.Cancelled)){
            //Invisible Buttons
            mBasicRideButtonsLayout.setVisibility(View.GONE);
        }
    }

    public void setCoTravellerButtonsOnClickListener(final View view, final BasicRideRequest rideRequest){
        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        final Button cancelButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = CancelCoTravellerFragment.newInstance(RideComp.this, mRide, rideRequest);
                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), CancelCoTravellerFragment.TAG);
            }
        });

        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PICKUP_PASSENGER = APIUrl.PICKUP_PASSENGER.replace(APIUrl.RIDE_ID_KEY, Integer.toString(mRide.getId()))
                        .replace(APIUrl.RIDE_REQUEST_ID_KEY, Integer.toString(rideRequest.getId()));
                RESTClient.get(PICKUP_PASSENGER, null, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d(TAG, "CoTraveller Picked");
                        mRide = new Gson().fromJson(response.toString(), FullRide.class);
                        mListener.onRideRefresh(mRide);
                        Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Picked", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        if (errorResponse!=null) {
                            ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                            Log.d(TAG, errorMessage.getErrorMessage());
                            Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                            Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = DropCoTravellerFragment.newInstance(RideComp.this, rideRequest);
                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), DropCoTravellerFragment.TAG);
            }
        });
    }

    public void updateCoTravellerButtonsVisibility(View view, final BasicRideRequest rideRequest){

        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button cancelButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);
        RatingBar ratingBar = view.findViewById(R.id.co_traveller_rating_bar);
        View buttonsLayout = view.findViewById(R.id.co_traveller_buttons_layout);

        //Intial value of rating bar which will only be visible post Drop
        ratingBar.setVisibility(View.GONE);

        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){

            //Visible Buttons
            cancelButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            // You can not drop anyone until he/she has been picked up
            dropButton.setVisibility(View.GONE);

            //Exception Rules
            //This would make Pickup possible anytime post ride is started which would help to deal with
            //post settlement of ride in case of connectivity issue
            if (!mRide.getStatus().equals(RideStatus.Started)){
                pickupButton.setVisibility(View.GONE);
            }

            if (mRide.getEndTime().before(Calendar.getInstance().getTime())){
                //This will make cancel button invisible post the ride end time has lapsed
                cancelButton.setVisibility(View.GONE);
            }
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Picked)){
            //Visible Buttons
            dropButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            //In this case only Drop is visible by default
            cancelButton.setVisibility(View.GONE);
            pickupButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Dropped)){
            //Visible Buttons
            //Note - Rating Bar is only visible post you drop a passenger
            ratingBar.setVisibility(View.VISIBLE);

            //Invisible Buttons
            buttonsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPositiveClickOfDropCoTravellerFragment(Dialog dialog, BasicRideRequest rideRequest) {

        //IMP - Don't use getView else it will throw nullpointer. Right option is to use getDialog
        RadioButton paidRideButton = (RadioButton) dialog.findViewById(R.id.paid_ride_radio_button);
        RadioButton freeRideButton = (RadioButton) dialog.findViewById(R.id.free_ride_radio_button);
        Log.d(TAG, "CoTraveller Dropped with Ride Request Id:"+rideRequest.getId());
        Log.d(TAG, "Paid Ride Status:"+paidRideButton.isChecked());
        Log.d(TAG, "Free Ride Status:"+freeRideButton.isChecked());

        RideMode rideMode;
        if (freeRideButton.isChecked()){
            rideMode = RideMode.Free;
        } else {
            rideMode = RideMode.Paid;
        }
        Log.d(TAG, "Ride Mode is:"+rideMode.toString());

        String DROP_PASSENGER = APIUrl.DROP_PASSENGER.replace(APIUrl.RIDE_ID_KEY, Integer.toString(mRide.getId()))
                .replace(APIUrl.RIDE_REQUEST_ID_KEY, Integer.toString(rideRequest.getId()))
                .replace(APIUrl.RIDE_MODE_KEY, rideMode.toString());

        RESTClient.get(DROP_PASSENGER, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "CoTraveller Dropped");
                mRide = new Gson().fromJson(response.toString(), FullRide.class);
                mListener.onRideRefresh(mRide);
                Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Dropped", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse!=null) {
                    ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                    Log.d(TAG, errorMessage.getErrorMessage());
                    Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                    Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onNegativeClickOfDropCoTravellerFragment(Dialog dialog, BasicRideRequest rideRequest) {
        Log.d(TAG, "CoTraveller Not Dropped");
    }

    @Override
    public void onPositiveClickOfCancelCoTravellerFragment(Dialog dialog, BasicRide ride, BasicRideRequest rideRequest) {

        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Log.d(TAG, "Rating value:"+ratingBar.getRating());

        String CANCEL_PASSENGER = APIUrl.CANCEL_PASSENGER.replace(APIUrl.RIDE_ID_KEY, Integer.toString(mRide.getId()))
                .replace(APIUrl.RIDE_REQUEST_ID_KEY, Integer.toString(rideRequest.getId()))
                .replace(APIUrl.RATING_KEY, Float.toString(ratingBar.getRating()));

        RESTClient.get(CANCEL_PASSENGER, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "CoTraveller Cancelled");
                mRide = new Gson().fromJson(response.toString(), FullRide.class);
                mListener.onRideRefresh(mRide);
                Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse!=null) {
                    ErrorMessage errorMessage = new Gson().fromJson(errorResponse.toString(), ErrorMessage.class);
                    Log.d(TAG, errorMessage.getErrorMessage());
                    Toast.makeText(mBaseFragment.getActivity(), errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(TAG, "Request Failed with error:"+ throwable.getMessage());
                    Toast.makeText(mBaseFragment.getActivity(), R.string.system_exception_msg, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, BasicRide ride, BasicRideRequest rideRequest) {
        Log.d(TAG, "CoTraveller Not Cancelled");
    }

    public interface RideCompListener{
        public void onRideRefresh(FullRide ride);
    }
}























