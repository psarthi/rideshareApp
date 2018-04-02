package com.parift.rideshare.component;

import android.app.Dialog;
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

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.config.Constant;
import com.parift.rideshare.dialog.CancelCoTravellerFragment;
import com.parift.rideshare.dialog.DropCoTravellerFragment;
import com.parift.rideshare.dialog.StandardAlertDialog;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.RideInfoFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.billing.domain.core.Account;
import com.parift.rideshare.model.billing.domain.core.Invoice;
import com.parift.rideshare.model.ride.domain.RidePoint;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.domain.core.PassengerStatus;
import com.parift.rideshare.model.ride.domain.core.RideMode;
import com.parift.rideshare.model.ride.domain.core.RideStatus;
import com.parift.rideshare.model.ride.dto.BasicRide;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRide;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.UserFeedbackInfo;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideComp implements DropCoTravellerFragment.DropCoTravellerFragmentListener,
        CancelCoTravellerFragment.CancelCoTravellerFragmentListener {

    public static final String TAG = RideComp.class.getName();
    private BaseFragment mBaseFragment;
    private FullRide mRide;
    private BasicRide mBasicRide;

    Button mCancelButton;
    Button mStartButton;
    Button mEndButton;
    Button mNavigationButton;
    LinearLayout mBasicRideButtonsLayout;
    private CommonUtil mCommonUtil;
    private RideCompListener mListener;
    private boolean mEndRideConfirmation;
    private BasicUser mUser;
    private TextView mRideStatusTextView;

    public RideComp(BaseFragment fragment, FullRide ride) {
        mBaseFragment = fragment;
        mRide = ride;
        //This will ensure that basic ride layout would work perfectly fine for Full Ride as well
        //No need to type case it will be taken care itself
        mBasicRide = ride;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
        //Its important to check instance of fragment as some fragment may not be required to implement Listener
        //And in those cases, it will throw ClassCasteException
        if (fragment instanceof RideCompListener) mListener = (RideCompListener) fragment;
    }

    //Reason behind this additional constructor so that we can send callback directly to adapter instead of BaseFragment class only
    public RideComp(RecyclerView.Adapter adapter, BaseFragment fragment, BasicRide ride) {
        mBaseFragment = fragment;
        mBasicRide = ride;
        mCommonUtil = new CommonUtil(fragment);
        //Its important to check instance of fragment as some fragment may not be required to implement Listener
        //And in those cases, it will throw ClassCasteException
        mListener = (RideCompListener) adapter;
    }

    //Note - Use Input as BasicRide and store REST response as FullRide so that we can support Ride Info as well as Ride List
    public void setBasicRideLayout(View view) {

        View basic_ride_layout = view.findViewById(R.id.basic_ride_layout);
        mCancelButton = basic_ride_layout.findViewById(R.id.ride_cancel_button);
        mStartButton = basic_ride_layout.findViewById(R.id.ride_start_button);
        mEndButton = basic_ride_layout.findViewById(R.id.ride_end_button);
        mNavigationButton = basic_ride_layout.findViewById(R.id.ride_navigation_button);
        mBasicRideButtonsLayout = basic_ride_layout.findViewById(R.id.ride_buttons_layout);


        TextView rideIdTextView = basic_ride_layout.findViewById(R.id.ride_id_text);
        if (mBaseFragment.isAdded()){
            String rideIdText = mBaseFragment.getResources().getString(R.string.ride_offer_id_text);
            rideIdTextView.setText(rideIdText);
        }
        mRideStatusTextView = basic_ride_layout.findViewById(R.id.ride_status_text);
        mRideStatusTextView.setText(mBasicRide.getStatus().toString());
        TextView rideStartTimeTextView = basic_ride_layout.findViewById(R.id.ride_start_time_text);
        rideStartTimeTextView.setText(mCommonUtil.getFormattedDateTimeString(mBasicRide.getStartTime()));

        if (mBasicRide.getInvoice()!=null){
            //This is required to ensure invisible items becomes visible on reload in recycler view
            basic_ride_layout.findViewById(R.id.total_money_earned_text).setVisibility(View.VISIBLE);
            basic_ride_layout.findViewById(R.id.invoice_status).setVisibility(View.VISIBLE);
            Invoice invoice = mBasicRide.getInvoice();
            float totalDeduction = invoice.getServiceCharge() + invoice.getCgst() + invoice.getSgst() + invoice.getIgst() + invoice.getTcs();
            float driverNetEarning = invoice.getTotalAmountEarned() - totalDeduction;
            String symbol = mCommonUtil.getCurrencySymbol(mBasicRide.getDriver().getCountry());
            String amount = mCommonUtil.getDecimalFormattedString(driverNetEarning);
            ((TextView) basic_ride_layout.findViewById(R.id.total_money_earned_text))
                    .setText(symbol + amount);
            ((TextView) basic_ride_layout.findViewById(R.id.invoice_status))
                    .setText(mBasicRide.getInvoice().getStatus().toString());

        } else {
            basic_ride_layout.findViewById(R.id.total_money_earned_text).setVisibility(View.GONE);
            basic_ride_layout.findViewById(R.id.invoice_status).setVisibility(View.GONE);
        }

        TextView rideStartPointTextView = basic_ride_layout.findViewById(R.id.ride_start_point_text);
        rideStartPointTextView.setText(mBasicRide.getStartPointAddress());
        TextView rideEndPointTextView = basic_ride_layout.findViewById(R.id.ride_end_point_text);
        rideEndPointTextView.setText(mBasicRide.getEndPointAddress());

        if (mRide!=null){
            //This will set the visibility of ride buttons initially
            updateBasicRideLayoutButtonsVisibility();
            //This will set listeners for ride buttons
            setBasicRideLayoutButtonsOnClickListener();
        } else {
            //This will ensure that no buttons are visible if fullride has not been set
            //In case of Ride List, this will ensure that Start/Continue navigation buttons
            //doesn't fail as all those action require fullride
            mBasicRideButtonsLayout.setVisibility(View.GONE);
            //This will update the status as Expired post end time of ride and if ride has not been started
            //This will take care of status in ride list view
            if (mBasicRide.getStatus().equals(RideStatus.Planned) && mBasicRide.getEndTime().before(Calendar.getInstance().getTime())) {
                mRideStatusTextView.setText(RideStatus.Expired.toString());
            }
        }

        RideInfoFragment fragment = (RideInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideInfoFragment.TAG);
        if (fragment != null && mBasicRide.getId() == fragment.getRideId()) {
            Logger.debug(TAG, "Ride Info is already loaded");
        } else {
            basic_ride_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String GET_RIDE_URL = APIUrl.GET_RIDE_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                            .replace(APIUrl.ID_KEY, Long.toString(mBasicRide.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(GET_RIDE_URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (mBaseFragment.isAdded()) {
                                super.onSuccess(statusCode, headers, response);
                                mCommonUtil.dismissProgressDialog();
                                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                                fragmentLoader.loadRideInfoFragment(response.toString());
                            }
                        }
                    });
                }
            });
        }
    }

    //Note - Use Input as BasicRide and store REST response as FullRide so that we can support Ride Info as well as Ride List
    private void setBasicRideLayoutButtonsOnClickListener() {

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mBaseFragment.getString(R.string.standard_cancellation_confirmation_message);
                DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardListAlertDialogListener() {
                    @Override
                    public void onPositiveStandardAlertDialog() {
                        //Imp - Ensure that input is always based on BasicRide as this has to work for both Ride List as well as Ride Info
                        String CANCEL_RIDE = APIUrl.CANCEL_RIDE.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                                .replace(APIUrl.ID_KEY, Long.toString(mBasicRide.getId()));
                        mCommonUtil.showProgressDialog();
                        RESTClient.get(CANCEL_RIDE, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (mBaseFragment.isAdded()) {
                                    super.onSuccess(statusCode, headers, response);
                                    mCommonUtil.dismissProgressDialog();
                                    Logger.debug(TAG, "Ride Cancelled");
                                    //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                                    mRide = new Gson().fromJson(response.toString(), FullRide.class);
                                    mListener.onRideRefresh(mRide);
                                    Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Cancelled", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNegativeStandardAlertDialog() {
                        Logger.debug(TAG, "Negative Button clicked on standard dialog");
                    }
                });

                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), StandardAlertDialog.TAG);
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Imp - Ensure that input is always based on BasicRide as this has to work for both Ride List as well as Ride Info
                String START_RIDE = APIUrl.START_RIDE.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.ID_KEY, Long.toString(mBasicRide.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(START_RIDE, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Logger.debug(TAG, "Ride Started");
                            //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                            mRide = new Gson().fromJson(response.toString(), FullRide.class);
                            mListener.onRideRefresh(mRide);
                            Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Started", Toast.LENGTH_LONG).show();
                            navigate(mRide);
                        }
                    }
                });
            }
        });

        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Imp - Ensure that input is always based on BasicRide as this has to work for both Ride List as well as Ride Info
                if (mBasicRide.getEndTime().after(Calendar.getInstance().getTime())) {
                    String message = mBaseFragment.getString(R.string.ride_end_confirmation_message);
                    DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardListAlertDialogListener() {
                        @Override
                        public void onPositiveStandardAlertDialog() {
                            endRide();
                        }

                        @Override
                        public void onNegativeStandardAlertDialog() {
                            Logger.debug(TAG, "Negative Button clicked on standard dialog");
                        }
                    });
                    dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), StandardAlertDialog.TAG);
                } else {
                    endRide();
                }

            }

            private void endRide() {
                String END_RIDE = APIUrl.END_RIDE.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.ID_KEY, Long.toString(mBasicRide.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(END_RIDE, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Logger.debug(TAG, "Ride Ended");
                            //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                            mRide = new Gson().fromJson(response.toString(), FullRide.class);
                            mListener.onRideRefresh(mRide);
                            Toast.makeText(mBaseFragment.getActivity(), "Ride Successfully Ended", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG, "Continue Navigation");
                navigate(mRide);
            }
        });
    }

    //Note - Use Input as BasicRide and store REST response as FullRide so that we can support Ride Info as well as Ride List
    private void updateBasicRideLayoutButtonsVisibility() {

        //Reason for setting it visible to handle view holder reuse where once item is invisible, it remains as it is
        //e.g. when one item make something invisble but the same view is used by another item, then it remains invisble
        //Initial value of Buttons Layout which would be applicable to all except when ride is cancelled or finished
        //Reason for setting this just to avoid any issue with visiblity when layout is invisible but child is visible
        //not sure how that would behave so cleaner option is to make layout visible and then childs visible/invisible as well
        mBasicRideButtonsLayout.setVisibility(View.VISIBLE);

        //Imp - Ensure that input is always based on BasicRide as this has to work for both Ride List as well as Ride Info
        if (mBasicRide.getStatus().equals(RideStatus.Planned)) {
            //Visible Buttons
            mCancelButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            //This will make end and navigation button invisible as ride is never started
            mEndButton.setVisibility(View.GONE);
            mNavigationButton.setVisibility(View.GONE);

            //Exception Rules Buttons visibility
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(mBasicRide.getStartTime());
            //This will subtract some buffer time from start time e.g. reduce 15 mins from current time
            //(using -) in drawable.add function for substraction
            startTime.add(Calendar.MINUTE, -Constant.START_TIME_BUFFER);
            //This will make Start button invisible if its early than start time by subtracting buffer
            //e.g. if ride start time is 3:00 PM and buffer is 15 mins, then it can only be started after 2:45 PM
            Logger.debug(TAG, "Ride Start Time - Buffer:" + startTime.getTime().toString());
            Logger.debug(TAG, "Current Time:" + Calendar.getInstance().getTime().toString());
            if (Calendar.getInstance().getTime().before(startTime.getTime())) {
                mStartButton.setVisibility(View.GONE);
            }
            //This will make Start and Cancel button invisible post end time of ride
            if (mBasicRide.getEndTime().before(Calendar.getInstance().getTime())) {
                mCancelButton.setVisibility(View.GONE);
                //IMP - This will ensure driver can always complete the flow in case he has missed to start on time
                //otherwise payment is stuck of the passenger
                //mStartButton.setVisibility(View.GONE);
                mRideStatusTextView.setText(RideStatus.Expired.toString());
            }
        }
        if (mBasicRide.getStatus().equals(RideStatus.Started)) {
            //Visible Buttons
            mNavigationButton.setVisibility(View.VISIBLE);
            mEndButton.setVisibility(View.VISIBLE);

            //Invisible buttons
            mCancelButton.setVisibility(View.GONE);
            mStartButton.setVisibility(View.GONE);
        }
        if (mBasicRide.getStatus().equals(RideStatus.Finished) || mBasicRide.getStatus().equals(RideStatus.Cancelled)) {
            //Invisible Buttons
            mBasicRideButtonsLayout.setVisibility(View.GONE);
        }
    }

    //Note - Use Input as FullRide as well as store REST response as FullRide as caller pass Fullride in constructor
    public void setCoTravellerButtonsOnClickListener(final View view, final FullRideRequest rideRequest) {
        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        final Button cancelButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);

        //Note - We always use Cancel word within the system but for user, we will show as Reject
        //So logically Reject is same as cancelling confirmed ride request, don't try to change it
        //else you will end up into mess as system is designed from the perspective of cancel and not reject
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Input is fine as Full Ride as caller set the fullride in constructor
                DialogFragment dialogFragment = CancelCoTravellerFragment.newInstance(RideComp.this, rideRequest);
                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), CancelCoTravellerFragment.TAG);
            }
        });

        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Input is fine as Full Ride as caller set the fullride in constructor
                String PICKUP_PASSENGER = APIUrl.PICKUP_PASSENGER.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.RIDE_ID_KEY, Long.toString(mRide.getId()))
                        .replace(APIUrl.RIDE_REQUEST_ID_KEY, Long.toString(rideRequest.getId()));
                mCommonUtil.showProgressDialog();
                RESTClient.get(PICKUP_PASSENGER, null, new RSJsonHttpResponseHandler(mCommonUtil) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            Logger.debug(TAG, "CoTraveller Picked");
                            //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                            mRide = new Gson().fromJson(response.toString(), FullRide.class);
                            mListener.onRideRefresh(mRide);
                            Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Picked", Toast.LENGTH_LONG).show();
                            navigate(mRide);
                        }
                    }
                });
            }
        });

        dropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = DropCoTravellerFragment.newInstance(RideComp.this, rideRequest, mUser);
                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), DropCoTravellerFragment.TAG);
            }
        });
    }

    //Note - Use Input as FullRide as well as store REST response as FullRide as caller pass Fullride in constructor
    public void updateCoTravellerButtonsVisibility(View view, final BasicRideRequest rideRequest) {

        //Don't get on layout as its not an external layout which is used as include, get on view.findviewbyId
        Button cancelButton = view.findViewById(R.id.co_traveller_cancel_button);
        Button pickupButton = view.findViewById(R.id.co_traveller_pickup_button);
        Button dropButton = view.findViewById(R.id.co_traveller_drop_button);
        RatingBar ratingBar = view.findViewById(R.id.co_traveller_rating_bar);
        View buttonsLayout = view.findViewById(R.id.co_traveller_buttons_layout);

        //Intial value of rating bar which will only be visible post Drop
        ratingBar.setVisibility(View.GONE);

        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)) {

            //Visible Buttons
            cancelButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            // You can not drop anyone until he/she has been picked up
            dropButton.setVisibility(View.GONE);

            //Exception Rules
            //This would make Pickup possible anytime post ride is started which would help to deal with
            //post settlement of ride in case of connectivity issue
            //Input is fine as Full Ride as caller set the fullride in constructor
            if (!mRide.getStatus().equals(RideStatus.Started)) {
                pickupButton.setVisibility(View.GONE);
            }

            if (mRide.getEndTime().before(Calendar.getInstance().getTime())) {
                //This will make cancel button invisible post the ride end time has lapsed
                cancelButton.setVisibility(View.GONE);
            }
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Picked)) {
            //Visible Buttons
            dropButton.setVisibility(View.VISIBLE);

            //Invisible Buttons
            //In this case only Drop is visible by default
            cancelButton.setVisibility(View.GONE);
            pickupButton.setVisibility(View.GONE);
        }
        if (rideRequest.getPassengerStatus().equals(PassengerStatus.Dropped)) {
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
        TextView paymentCode = dialog.findViewById(R.id.payment_code_text);
        Logger.debug(TAG, "CoTraveller Dropped with Ride Request Id:" + rideRequest.getId());
        Logger.debug(TAG, "Paid Ride Status:" + paidRideButton.isChecked());
        Logger.debug(TAG, "Free Ride Status:" + freeRideButton.isChecked());

        RideMode rideMode;
        String code;
        if (freeRideButton.isChecked()) {
            rideMode = RideMode.Free;
            code = "na";
        } else {
            rideMode = RideMode.Paid;
            code = paymentCode.getText().toString();
        }
        Logger.debug(TAG, "Ride Mode is:" + rideMode.toString());
        //Input is fine as Full Ride as caller set the fullride in constructor
        String DROP_PASSENGER = APIUrl.DROP_PASSENGER.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.RIDE_ID_KEY, Long.toString(mRide.getId()))
                .replace(APIUrl.RIDE_REQUEST_ID_KEY, Long.toString(rideRequest.getId()))
                .replace(APIUrl.RIDE_MODE_KEY, rideMode.toString())
                .replace(APIUrl.PAYMENT_CODE_KEY, code);
        mCommonUtil.showProgressDialog();
        RESTClient.get(DROP_PASSENGER, null, new RSJsonHttpResponseHandler(mCommonUtil) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (mBaseFragment.isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Logger.debug(TAG, "CoTraveller Dropped");
                    //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                    mRide = new Gson().fromJson(response.toString(), FullRide.class);
                    Account account = ((List<Account>) mRide.getDriver().getAccounts()).get(0);
                    //This will update the account balance post dropping any passenger
                    mCommonUtil.updateAccount(account);
                    mListener.onRideRefresh(mRide);
                    Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Dropped", Toast.LENGTH_LONG).show();
                    navigate(mRide);
                }
            }
        });
    }

    @Override
    public void onNegativeClickOfDropCoTravellerFragment(Dialog dialog, BasicRideRequest rideRequest) {
        Logger.debug(TAG, "CoTraveller Not Dropped");
    }

    @Override
    public void onPositiveClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest) {

        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Logger.debug(TAG, "Rating value:" + ratingBar.getRating());

        //Input is fine as Full Ride as caller set the fullride in constructor
        String CANCEL_PASSENGER = APIUrl.CANCEL_PASSENGER.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.RIDE_ID_KEY, Long.toString(mRide.getId()))
                .replace(APIUrl.RIDE_REQUEST_ID_KEY, Long.toString(rideRequest.getId()))
                .replace(APIUrl.RATING_KEY, Float.toString(ratingBar.getRating()));
        mCommonUtil.showProgressDialog();
        RESTClient.get(CANCEL_PASSENGER, null, new RSJsonHttpResponseHandler(mCommonUtil) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (mBaseFragment.isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Logger.debug(TAG, "CoTraveller Rejected");
                    //Imp - Ensure that output is always saved as FullRide as you actually get fullride
                    mRide = new Gson().fromJson(response.toString(), FullRide.class);
                    mListener.onRideRefresh(mRide);
                    Toast.makeText(mBaseFragment.getActivity(), "CoTraveller Rejected", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest) {
        Logger.debug(TAG, "CoTraveller Not Cancelled");
    }

    //Reason behind having seperate rating function for ride owner and cotraveller as we have to refresh different page on recieving response
    public void setCoTravellerRatingBar(final RatingBar userRatingBar, final FullRideRequest rideRequest) {
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String USER_FEEDBACK_URL;
                final UserFeedbackInfo feedbackInfo = new UserFeedbackInfo();
                Logger.debug(TAG, "Rating is:" + rating + "Given By Driver User Id:" + mRide.getDriver().getId());
                USER_FEEDBACK_URL = APIUrl.USER_FEEDBACK.replace(APIUrl.USER_ID_KEY, Long.toString(rideRequest.getPassenger().getId()))
                        .replace(APIUrl.RIDE_TYPE_KEY, RideType.OfferRide.toString());
                feedbackInfo.setGivenByUser(mRide.getDriver());
                feedbackInfo.setRating(rating);
                feedbackInfo.setRide(mRide);
                feedbackInfo.setRideRequest(rideRequest);
                mCommonUtil.showProgressDialog();
                RESTClient.post(mBaseFragment.getActivity(), USER_FEEDBACK_URL, feedbackInfo, new RSJsonHttpResponseHandler(mCommonUtil) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            userRatingBar.setEnabled(false);
                            mRide = new Gson().fromJson(response.toString(), FullRide.class);
                            mListener.onRideRefresh(mRide);
                        }
                    }
                });
            }
        });
    }

    public interface RideCompListener {
        //Input is Fullride as we get Fullride on any rides Action from the server
        public void onRideRefresh(FullRide ride);
    }

    public void navigate(FullRide ride) {

        List<RidePoint> ridePoints = new ArrayList<>();

        if (ride.getStatus().equals(RideStatus.Started)) {
            ridePoints.add(ride.getEndPoint());
        }
        for (FullRideRequest rideRequest : ride.getAcceptedRideRequests()) {
            if (rideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)) {
                ridePoints.add(rideRequest.getRidePickupPoint());
                ridePoints.add(rideRequest.getRideDropPoint());
            }
            if (rideRequest.getPassengerStatus().equals(PassengerStatus.Picked)) {
                ridePoints.add(rideRequest.getRideDropPoint());
            }
        }

        //This will sort the list in ascending order
        Collections.sort(ridePoints);

        CommonComp commonComp = new CommonComp(mBaseFragment);
        //This will take the next point in the sequence and navigate there from current location
        //So this will take care of pickup/drop etc. in the same sequence as its sorted by ride point sequence number
        commonComp.googleNavigation(ridePoints.get(0).getPoint());
    }
}























