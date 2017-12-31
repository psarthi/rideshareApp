package com.digitusrevolution.rideshare.component;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitusrevolution.rideshare.R;
import com.digitusrevolution.rideshare.config.APIUrl;
import com.digitusrevolution.rideshare.dialog.CancelCoTravellerFragment;
import com.digitusrevolution.rideshare.dialog.StandardAlertDialog;
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.ride.domain.RideType;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequestStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.user.domain.UserFeedback;
import com.digitusrevolution.rideshare.model.user.dto.UserFeedbackInfo;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideRequestComp implements CancelCoTravellerFragment.CancelCoTravellerFragmentListener{

    public static final String TAG = RideRequestComp.class.getName();
    private BaseFragment mBaseFragment;
    private FullRideRequest mRideRequest;
    private BasicRideRequest mBasicRideRequest;
    private CommonUtil mCommonUtil;

    private Button mRideRequestCancelButton;
    private Button mDestinationNavigationButton;
    private LinearLayout mBasicRideRequestButtonsLayout;
    private Button mRideOwnerCancelButton;
    private Button mPickupPointNavigationButton;
    private LinearLayout mRideOwnerButtonsLayout;
    private RideRequestCompListener mListener;
    private RatingBar mRatingBar;

    public RideRequestComp(BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        //This will ensure that basic ride request layout would work perfectly fine for Full Ride Request as well
        mBasicRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
        if (fragment instanceof RideRequestCompListener) mListener = (RideRequestCompListener) fragment;
    }

    //Reason behind this additional constructor so that we can send callback directly to adapter instead of BaseFragment class only
    public RideRequestComp(RecyclerView.Adapter adapter, BaseFragment fragment, BasicRideRequest rideRequest){
        mBaseFragment = fragment;
        mBasicRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
        mListener = (RideRequestCompListener) adapter;
    }

    //Use all input as Basic Ride Request
    public void setRideRequestBasicLayout(View view){

        View layout = view.findViewById(R.id.basic_ride_request_layout);
        mRideRequestCancelButton = layout.findViewById(R.id.ride_request_cancel_button);
        mDestinationNavigationButton = layout.findViewById(R.id.ride_request_navigate_to_destination_button);
        mBasicRideRequestButtonsLayout = layout.findViewById(R.id.ride_request_buttons_layout);

        String rideRequestNumberText = mBaseFragment.getResources().getString(R.string.ride_request_id_text)+ mBasicRideRequest.getId();
        ((TextView) layout.findViewById(R.id.ride_request_id_text)).setText(rideRequestNumberText);
        ((TextView) layout.findViewById(R.id.ride_request_status_text)).setText(mBasicRideRequest.getStatus().toString());
        String pickupTime = mCommonUtil.getFormattedDateTimeString(mBasicRideRequest.getPickupTime());
        ((TextView) layout.findViewById(R.id.ride_request_pickup_time_text)).setText(pickupTime);

        ((TextView) layout.findViewById(R.id.ride_request_pickup_point_text)).setText(mBasicRideRequest.getPickupPointAddress());
        ((TextView) layout.findViewById(R.id.ride_request_drop_point_text)).setText(mBasicRideRequest.getDropPointAddress());

        //This will setup those views which is available only in Full Ride Request
        if (mListener instanceof BaseFragment){
            setExtraOfBasicLayout(layout);
        } else {
            Log.d(TAG, "Basic Ride Request Layout within Rides List, So Payment Code information for Id:"+mBasicRideRequest.getId());
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.GONE);
        }

        //This will setup initial button visibility
        updateRideRequestBasicLayoutButtonsVisiblity();
        setRideRequestBasicLayoutButtonsOnClickListener();

        RideRequestInfoFragment fragment = (RideRequestInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideRequestInfoFragment.TAG);
        if (fragment!=null && mBasicRideRequest.getId() == fragment.getRideRequestId()){
            Log.d(TAG, "Ride Request Info is already loaded");
        } else {

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String GET_RIDE_REQUEST_URL = APIUrl.GET_RIDE_REQUEST_URL.replace(APIUrl.ID_KEY, Integer.toString(mBasicRideRequest.getId()));

                    RESTClient.get(GET_RIDE_REQUEST_URL, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                            fragmentLoader.loadRideRequestInfoFragment(response.toString());
                        }
                    });
                }
            });
        }
    }

    //Use all input as Full Ride Request with an assumption that fragments would have access to Full ride request
    private void setExtraOfBasicLayout(View layout) {
        String paymentCode = mRideRequest.getConfirmationCode();
        //IMP - This will work fine when getting called from Ride Request Info with Full Ride Request
        if (mRideRequest.getAcceptedRide()!=null){
            Log.d(TAG, "Accepted Ride Request for Id:"+mRideRequest.getId()+" is :"+mRideRequest.getAcceptedRide().getId());
            //Reason behind making it visible so that its visible on refresh
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.VISIBLE);
            String paymentLabel = mBaseFragment.getString(R.string.ride_request_payment_code_text);
            ((TextView) layout.findViewById(R.id.ride_request_confirmation_code_text)).setText(paymentLabel+paymentCode);
        }else {
            Log.d(TAG, "No Accepted Ride for Id:"+mBasicRideRequest.getId());
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.GONE);
        }
    }

    //Use all input as Basic Ride Request and save response as Full Ride Request as we get that only from server
    //and we can support Ride Request List as well as Ride Request Info fragment as well
    private void setRideRequestBasicLayoutButtonsOnClickListener(){
        mRideRequestCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = mBaseFragment.getString(R.string.standard_cancellation_confirmation_message);
                DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardAlertDialogListener() {
                    @Override
                    public void onPositiveStandardAlertDialog() {
                        String CANCEL_RIDE_REQUEST = APIUrl.CANCEL_RIDE_REQUEST.replace(APIUrl.ID_KEY, Integer.toString(mBasicRideRequest.getId()));
                        RESTClient.get(CANCEL_RIDE_REQUEST, null, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.d(TAG, "Ride Request Cancelled");
                                mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                                mListener.onRideRequestRefresh(mRideRequest);
                                Toast.makeText(mBaseFragment.getActivity(), "Ride Request Cancelled", Toast.LENGTH_LONG).show();
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
                                }                    }
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
        mDestinationNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Destination for Id:"+mBasicRideRequest.getId());
            }
        });
    }

    //Use all input as Basic Ride Request and save response as Full Ride Request as we get that only from server
    //and we can support Ride Request List as well as Ride Request Info fragment as well
    private void updateRideRequestBasicLayoutButtonsVisiblity(){

        //Reason for setting it visible to handle view holder reuse where once item is invisible, it remains as it is
        //e.g. when one item make something invisble but the same view is used by another item, then it remains invisble
        //Initial value of Buttons Layout which would be applicable to all except when ride is cancelled or finished
        //Reason for setting this just to avoid any issue with visiblity when layout is invisible but child is visible
        //not sure how that would behave so cleaner option is to make layout visible and then childs visible/invisible as well
        mBasicRideRequestButtonsLayout.setVisibility(View.VISIBLE);

        if (mBasicRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled)
                || mBasicRideRequest.getStatus().equals(RideRequestStatus.Fulfilled)) {

            //Rules based on passenger status
            if (mBasicRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)
                    || mBasicRideRequest.getPassengerStatus().equals(PassengerStatus.Unconfirmed)){

                //Visible Buttons
                mRideRequestCancelButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mDestinationNavigationButton.setVisibility(View.GONE);

            } else {
                //Visible Buttons for Picked / Dropped Status of passenger
                mDestinationNavigationButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mRideRequestCancelButton.setVisibility(View.GONE);

                Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mBasicRideRequest);
                //Log.d(TAG, "Drop Time with Variation:"+maxEndTime.getTime().toString());
                //Log.d(TAG, "Current Time:"+Calendar.getInstance().getTime().toString());

                //Exception Rules
                //This will make destination button invisible if current time is after drop time + drop variation which includes drop time buffer as well
                if (maxEndTime.before(Calendar.getInstance())){
                    Log.d(TAG, "Drop time has passed, so no Destination button for id:"+mBasicRideRequest.getId());
                    //IMP - Disabling this so that user can cancel ride request at any point of time so that his money can be released
                    //mRideRequestCancelButton.setVisibility(View.GONE);
                    mDestinationNavigationButton.setVisibility(View.GONE);
                }
            }
        }
        if (mBasicRideRequest.getStatus().equals(RideRequestStatus.Cancelled)){
            mBasicRideRequestButtonsLayout.setVisibility(View.GONE);
        }
    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    public void setRideOwnerLayout(View view){

        UserComp userComp = new UserComp(mBaseFragment, null);
        userComp.setUserProfileSingleRow(view, mRideRequest.getAcceptedRide().getDriver());

        String vehicle = mRideRequest.getAcceptedRide().getVehicle().getModel() + " " +
                mRideRequest.getAcceptedRide().getVehicle().getRegistrationNumber();
        ((TextView) view.findViewById(R.id.ride_vehicle_name)).setText(vehicle);

        setPickupTimeAndBillLayout(view, RideType.RequestRide);

        ((TextView) view.findViewById(R.id.ride_pickup_point_text)).setText(mRideRequest.getRidePickupPointAddress());
        ((TextView) view.findViewById(R.id.ride_drop_point_text)).setText(mRideRequest.getRideDropPointAddress());

        String pickupDistance = Integer.toString((int)mRideRequest.getRidePickupPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        String dropDistance = Integer.toString((int)mRideRequest.getRideDropPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        ((TextView) view.findViewById(R.id.ride_pickup_point_variation_text)).setText(pickupDistance);
        ((TextView) view.findViewById(R.id.ride_drop_point_variation_text)).setText(dropDistance);

        mRideOwnerButtonsLayout = view.findViewById(R.id.ride_owner_buttons_layout);
        mRideOwnerCancelButton = view.findViewById(R.id.ride_owner_cancel_button);
        mPickupPointNavigationButton = view.findViewById(R.id.navigate_to_ride_pickup_point_button);
        mRatingBar = view.findViewById(R.id.ride_owner_rating_bar);

        updateRideOwnerLayoutButtonsVisiblity(view);
        setRideOwnerLayoutButtonsOnClickListener(view);

    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    private void setRideOwnerLayoutButtonsOnClickListener(final View view){

        mRideOwnerCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = CancelCoTravellerFragment.newInstance(RideRequestComp.this, mRideRequest);
                dialogFragment.show(mBaseFragment.getActivity().getSupportFragmentManager(), CancelCoTravellerFragment.TAG);
            }
        });

        mPickupPointNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Pickup Point for Id:"+mRideRequest.getId());
            }
        });

        boolean feedbackAvailable = false;
        Log.d(TAG,"RatingBar Instance:"+mRatingBar.hashCode());
        //This will show the user given rating
        for (UserFeedback feedback: mRideRequest.getFeedbacks()) {
            if (feedback.getForUser().getId() == mRideRequest.getAcceptedRide().getDriver().getId()) {
                Log.d(TAG,"Setting Rating:"+feedback.getRating());
                mRatingBar.setRating(feedback.getRating());
                mRatingBar.setEnabled(false);
                feedbackAvailable = true;
            }
        }
        if (!feedbackAvailable){
            setRideOwnerRatingBar(mRatingBar);
        }

    }

    //Reason behind having seperate rating function for ride owner and cotraveller as we have to refresh different page on recieving response
    public void setRideOwnerRatingBar(final RatingBar userRatingBar) {
        userRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String USER_FEEDBACK_URL;
                final UserFeedbackInfo feedbackInfo = new UserFeedbackInfo();
                Log.d(TAG, "Rating is:"+rating+" Given By Passenger User Id:"+mRideRequest.getPassenger().getId());
                USER_FEEDBACK_URL = APIUrl.USER_FEEDBACK.replace(APIUrl.USER_ID_KEY, Integer.toString(mRideRequest.getAcceptedRide().getDriver().getId()))
                .replace(APIUrl.RIDE_TYPE_KEY, RideType.RequestRide.toString());
                feedbackInfo.setGivenByUser(mRideRequest.getPassenger());
                feedbackInfo.setRating(rating);
                feedbackInfo.setRide(mRideRequest.getAcceptedRide());
                feedbackInfo.setRideRequest(mRideRequest);
                RESTClient.post(mBaseFragment.getActivity(), USER_FEEDBACK_URL, feedbackInfo, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        userRatingBar.setEnabled(false);
                        mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                        mListener.onRideRequestRefresh(mRideRequest);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

            }
        });
    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    private void updateRideOwnerLayoutButtonsVisiblity(View view){

        //This means that only when passenger is in confirmed state i.e. he/she has not been picked up, he can cancel / navigate
        //As far as rating is concerned, he can rate post pickup up or cancel. In case of cancel, we will ask for rating in dialog bar itself
        if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){

            Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mRideRequest);
            Log.d(TAG, "Current Time is:"+Calendar.getInstance().getTime().toString());
            Log.d(TAG, "Max End Time is:"+maxEndTime.getTime().toString());
            if (maxEndTime.before(Calendar.getInstance())){
                //Visible Buttons
                mRatingBar.setVisibility(View.VISIBLE);

                //This will make Ride Owner buttons layout invisible post ride request drop point time has lapsed
                //i.e. No cancel and Pickup Point Navigation button would be visible
                mRideOwnerButtonsLayout.setVisibility(View.GONE);
            } else {
                //Visible Buttons
                mRideOwnerCancelButton.setVisibility(View.VISIBLE);
                mPickupPointNavigationButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mRatingBar.setVisibility(View.GONE);
            }
        } else {
            //Visible Buttons
            mRatingBar.setVisibility(View.VISIBLE);

            //Invisible Buttons
            mRideOwnerButtonsLayout.setVisibility(View.GONE);
        }

    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    public void setPickupTimeAndBillLayout(View view, final RideType rideType){
        View layout = view.findViewById(R.id.pickup_time_bill_layout);
        TextView pickupTimeTextView = layout.findViewById(R.id.pickup_time_text);
        Date pickupTime = mRideRequest.getRidePickupPoint().getRidePointProperties().get(0).getDateTime();
        String pickupTimeString = mCommonUtil.getFormattedDateTimeString(pickupTime);
        pickupTimeTextView.setText(pickupTimeString);

        TextView fareTextView = layout.findViewById(R.id.fare_text);
        String amount = mCommonUtil.getDecimalFormattedString(mRideRequest.getBill().getAmount());
        String symbol = mCommonUtil.getCurrencySymbol(mRideRequest.getPassenger().getCountry());
        fareTextView.setText(symbol+amount);

        TextView billStatusTextView = layout.findViewById(R.id.bill_status);
        billStatusTextView.setText(mRideRequest.getBill().getStatus().toString());

        billStatusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                fragmentLoader.loadBillFragment(new Gson().toJson(mRideRequest.getBill()), rideType);
            }
        });
    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    public void setRidePickupDropPointsLayout(View view){
        View layout = view.findViewById(R.id.ride_pickup_drop_point_layout);
        TextView pickupPointTextView = layout.findViewById(R.id.ride_pickup_point_text);
        pickupPointTextView.setText(mRideRequest.getRidePickupPointAddress());

        TextView dropPointTextView = layout.findViewById(R.id.ride_drop_point_text);
        dropPointTextView.setText(mRideRequest.getRideDropPointAddress());

    }

    @Override
    public void onPositiveClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest) {
        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Log.d(TAG, "Rating value:"+ratingBar.getRating());

        String CANCEL_DRIVER = APIUrl.CANCEL_DRIVER.replace(APIUrl.RIDE_REQUEST_ID_KEY, Integer.toString(rideRequest.getId()))
                .replace(APIUrl.RIDE_ID_KEY, Integer.toString(rideRequest.getAcceptedRide().getId()))
                .replace(APIUrl.RATING_KEY, Float.toString(ratingBar.getRating()));

        RESTClient.get(CANCEL_DRIVER, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "Ride Owner Cancelled");
                mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                mListener.onRideRequestRefresh(mRideRequest);
                Toast.makeText(mBaseFragment.getActivity(), "Ride Owner Cancelled", Toast.LENGTH_LONG).show();
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
    public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest) {
        Log.d(TAG, "Ride Owner Not Cancelled");

    }

    public interface RideRequestCompListener{
        public void onRideRequestRefresh(FullRideRequest rideRequest);
    }

}
































