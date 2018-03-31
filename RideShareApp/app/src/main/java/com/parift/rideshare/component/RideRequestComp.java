package com.parift.rideshare.component;

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

import com.parift.rideshare.R;
import com.parift.rideshare.config.APIUrl;
import com.parift.rideshare.dialog.CancelCoTravellerFragment;
import com.parift.rideshare.dialog.StandardAlertDialog;
import com.parift.rideshare.fragment.BaseFragment;
import com.parift.rideshare.fragment.RideRequestInfoFragment;
import com.parift.rideshare.helper.CommonUtil;
import com.parift.rideshare.helper.Logger;
import com.parift.rideshare.helper.RESTClient;
import com.parift.rideshare.helper.RSJsonHttpResponseHandler;
import com.parift.rideshare.model.billing.domain.core.BillStatus;
import com.parift.rideshare.model.ride.domain.RideType;
import com.parift.rideshare.model.ride.domain.core.PassengerStatus;
import com.parift.rideshare.model.ride.domain.core.RideRequestStatus;
import com.parift.rideshare.model.ride.dto.BasicRide;
import com.parift.rideshare.model.ride.dto.BasicRideRequest;
import com.parift.rideshare.model.ride.dto.FullRideRequest;
import com.parift.rideshare.model.ride.dto.SuggestedMatchedRideInfo;
import com.parift.rideshare.model.user.domain.UserFeedback;
import com.parift.rideshare.model.user.dto.BasicUser;
import com.parift.rideshare.model.user.dto.UserFeedbackInfo;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private BasicUser mUser;
    private TextView mRideRequestStatusTextView;

    public RideRequestComp(BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        //This will ensure that basic ride request layout would work perfectly fine for Full Ride Request as well
        mBasicRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
        mUser = mCommonUtil.getUser();
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
        mRideRequestStatusTextView = layout.findViewById(R.id.ride_request_status_text);
        if (mBaseFragment.isAdded()){
            String rideRequestNumberText = mBaseFragment.getResources().getString(R.string.ride_request_id_text);
            ((TextView) layout.findViewById(R.id.ride_request_id_text)).setText(rideRequestNumberText);
        }
        mRideRequestStatusTextView.setText(mBasicRideRequest.getStatus().toString());
        String pickupTime = mCommonUtil.getFormattedDateTimeString(mBasicRideRequest.getPickupTime());
        ((TextView) layout.findViewById(R.id.ride_request_pickup_time_text)).setText(pickupTime);

        ((TextView) layout.findViewById(R.id.ride_request_pickup_point_text)).setText(mBasicRideRequest.getPickupPointAddress());
        ((TextView) layout.findViewById(R.id.ride_request_drop_point_text)).setText(mBasicRideRequest.getDropPointAddress());

        //This will setup those views which is available only in Full Ride Request
        if (mListener instanceof BaseFragment){
            setExtraOfBasicLayout(layout);
        } else {
            Logger.debug(TAG, "Basic Ride Request Layout found. Called from Rides List, so hiding payment code information for Id:"+mBasicRideRequest.getId());
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.GONE);
        }

        if (mRideRequest!=null){
            //This will setup initial button visibility
            updateRideRequestBasicLayoutButtonsVisiblity();
            setRideRequestBasicLayoutButtonsOnClickListener();
        } else {
            //This will ensure that no buttons are visible if fullride request has not been set
            //In case of Ride Request List, this will ensure that Destination Point navigation buttons
            //doesn't fail as all those action require fullride rrequest
            mBasicRideRequestButtonsLayout.setVisibility(View.GONE);
            //This is the case of showing Expired ride requests, this will take care of the status in ride request list view
            Calendar maxPickupTime = mCommonUtil.getRideRequestMaxPickupTime(mBasicRideRequest);
            if (mBasicRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled) && maxPickupTime.before(Calendar.getInstance())){
                Logger.debug(TAG, "Max Pickup time has passed, so no cancellation button for id:"+mBasicRideRequest.getId());
                mRideRequestStatusTextView.setText(RideRequestStatus.Expired.toString());
            }

        }

        RideRequestInfoFragment fragment = (RideRequestInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideRequestInfoFragment.TAG);
        if (fragment!=null && mBasicRideRequest.getId() == fragment.getRideRequestId()){
            Logger.debug(TAG, "Ride Request Info is already loaded");
        } else {

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String GET_RIDE_REQUEST_URL = APIUrl.GET_RIDE_REQUEST_URL.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                            .replace(APIUrl.ID_KEY, Long.toString(mBasicRideRequest.getId()));
                    mCommonUtil.showProgressDialog();
                    RESTClient.get(GET_RIDE_REQUEST_URL, null, new RSJsonHttpResponseHandler(mCommonUtil) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            if (mBaseFragment.isAdded()) {
                                super.onSuccess(statusCode, headers, response);
                                mCommonUtil.dismissProgressDialog();
                                FragmentLoader fragmentLoader = new FragmentLoader(mBaseFragment);
                                fragmentLoader.loadRideRequestInfoFragment(response.toString());
                            }
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
        if (mRideRequest.getAcceptedRide()!=null && mRideRequest.getBill().getStatus().equals(BillStatus.Pending)){
            Logger.debug(TAG, "Accepted Ride Request for Id:"+mRideRequest.getId()+" is :"+mRideRequest.getAcceptedRide().getId());
            //Reason behind making it visible so that its visible on refresh
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.VISIBLE);
            String paymentLabel = mBaseFragment.getString(R.string.ride_request_payment_code_text);
            ((TextView) layout.findViewById(R.id.ride_request_confirmation_code_text)).setText(paymentLabel+paymentCode);
        }else {
            Logger.debug(TAG, "No Accepted Ride for Id or Its a Free Ride:"+mBasicRideRequest.getId());
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
                DialogFragment dialogFragment = new StandardAlertDialog().newInstance(message, new StandardAlertDialog.StandardListAlertDialogListener() {
                    @Override
                    public void onPositiveStandardAlertDialog() {
                        String CANCEL_RIDE_REQUEST = APIUrl.CANCEL_RIDE_REQUEST.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                                .replace(APIUrl.ID_KEY, Long.toString(mBasicRideRequest.getId()));
                        mCommonUtil.showProgressDialog();
                        RESTClient.get(CANCEL_RIDE_REQUEST, null, new RSJsonHttpResponseHandler(mCommonUtil){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if (mBaseFragment.isAdded()) {
                                    super.onSuccess(statusCode, headers, response);
                                    mCommonUtil.dismissProgressDialog();
                                    Logger.debug(TAG, "Ride Request Cancelled");
                                    mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                                    mListener.onRideRequestRefresh(mRideRequest);
                                    Toast.makeText(mBaseFragment.getActivity(), "Ride Request Cancelled", Toast.LENGTH_LONG).show();
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
        mDestinationNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.debug(TAG, "Navigate to Destination for Id:"+mBasicRideRequest.getId());
                CommonComp commonComp = new CommonComp(mBaseFragment);
                commonComp.googleNavigation(mRideRequest.getDropPoint().getPoint());
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
                    || mBasicRideRequest.getPassengerStatus().equals(PassengerStatus.Unconfirmed)
                    || mBasicRideRequest.getPassengerStatus().equals(PassengerStatus.Picked)){

                //Visible Buttons
                mRideRequestCancelButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mDestinationNavigationButton.setVisibility(View.GONE);

            } else {
                //Visible Buttons for Dropped Status of passenger
                mDestinationNavigationButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mRideRequestCancelButton.setVisibility(View.GONE);

                Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mBasicRideRequest);
                //Logger.debug(TAG, "Drop Time with Variation:"+maxEndTime.getTime().toString());
                //Logger.debug(TAG, "Current Time:"+Calendar.getInstance().getTime().toString());

                //Exception Rules
                //This will make destination button invisible if current time is after drop time + drop variation which includes drop time buffer as well
                if (maxEndTime.before(Calendar.getInstance())){
                    Logger.debug(TAG, "Drop time has passed, so no Destination button for id:"+mBasicRideRequest.getId());
                    //IMP - Disabling this so that user can cancel ride request at any point of time so that his money can be released
                    //mRideRequestCancelButton.setVisibility(View.GONE);
                    mDestinationNavigationButton.setVisibility(View.GONE);
                }
            }
        }
        if (mBasicRideRequest.getStatus().equals(RideRequestStatus.Cancelled)){
            mBasicRideRequestButtonsLayout.setVisibility(View.GONE);
        }
        //This is the case of showing Expired ride requests
        Calendar maxPickupTime = mCommonUtil.getRideRequestMaxPickupTime(mBasicRideRequest);
        if (mBasicRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled) && maxPickupTime.before(Calendar.getInstance())){
            Logger.debug(TAG, "Max Pickup time has passed, so no cancellation button for id:"+mBasicRideRequest.getId());
            mRideRequestCancelButton.setVisibility(View.GONE);
            mRideRequestStatusTextView.setText(RideRequestStatus.Expired.toString());
        }
    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    public void setRideOwnerLayout(View view){

        UserComp userComp = new UserComp(mBaseFragment, mRideRequest.getAcceptedRide().getDriver());
        userComp.setUserProfileSingleRow(view, true);

        String vehicle = mRideRequest.getAcceptedRide().getVehicle().getModel() + " " +
                mRideRequest.getAcceptedRide().getVehicle().getRegistrationNumber();
        ((TextView) view.findViewById(R.id.ride_vehicle_name)).setText(vehicle);

        setPickupTimeAndBillLayout(view, RideType.RequestRide);

        ((TextView) view.findViewById(R.id.ride_pickup_point_text)).setText(mRideRequest.getRidePickupPointAddress());
        ((TextView) view.findViewById(R.id.ride_drop_point_text)).setText(mRideRequest.getRideDropPointAddress());

        if (mBaseFragment.isAdded()){
            String pickupDistance = Integer.toString((int)mRideRequest.getRidePickupPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
            String dropDistance = Integer.toString((int)mRideRequest.getRideDropPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
            ((TextView) view.findViewById(R.id.ride_pickup_point_variation_text)).setText(pickupDistance);
            ((TextView) view.findViewById(R.id.ride_drop_point_variation_text)).setText(dropDistance);
        }

        mRideOwnerButtonsLayout = view.findViewById(R.id.ride_owner_buttons_layout);
        mRideOwnerCancelButton = view.findViewById(R.id.ride_owner_cancel_button);
        mPickupPointNavigationButton = view.findViewById(R.id.navigate_to_ride_pickup_point_button);
        mRatingBar = view.findViewById(R.id.ride_owner_rating_bar);

        //IMP - This will ensure Accept button is not visible irrespective of any condition
        //as this is only applicable for rides suggestion for manual accept
        view.findViewById(R.id.ride_owner_accept_button).setVisibility(View.GONE);

        updateRideOwnerLayoutButtonsVisiblity(view);
        setRideOwnerLayoutButtonsOnClickListener(view);

    }

    public void setSuggestedRideOwnerLayout(View view, final SuggestedMatchedRideInfo rideInfo){

        //IMP - Ensure all not applicable ones are marked as invisible
        view.findViewById(R.id.ride_vehicle_name).setVisibility(View.GONE);
        view.findViewById(R.id.ride_owner_cancel_button).setVisibility(View.GONE);
        view.findViewById(R.id.navigate_to_ride_pickup_point_button).setVisibility(View.GONE);
        view.findViewById(R.id.ride_owner_rating_bar).setVisibility(View.GONE);

        View pickupTimeAndBilllayout = view.findViewById(R.id.pickup_time_bill_layout);
        pickupTimeAndBilllayout.findViewById(R.id.bill_status).setVisibility(View.GONE);

        //All Required one's are below
        UserComp userComp = new UserComp(mBaseFragment, rideInfo.getRide().getDriver());
        userComp.setUserProfileSingleRow(view, true);

        TextView pickupTimeTextView = pickupTimeAndBilllayout.findViewById(R.id.pickup_time_text);
        Date pickupTime = rideInfo.getRidePickupPoint().getRidePointProperties().get(0).getDateTime();
        String pickupTimeString = mCommonUtil.getFormattedDateTimeString(pickupTime);
        pickupTimeTextView.setText(pickupTimeString);

        TextView fareTextView = pickupTimeAndBilllayout.findViewById(R.id.fare_text);
        String amount = mCommonUtil.getDecimalFormattedString(rideInfo.getPrice());
        String symbol = mCommonUtil.getCurrencySymbol(mRideRequest.getPassenger().getCountry());
        fareTextView.setText(symbol+amount);

        ((TextView) view.findViewById(R.id.ride_pickup_point_text)).setText(rideInfo.getRidePickupPointAddress());
        ((TextView) view.findViewById(R.id.ride_drop_point_text)).setText(rideInfo.getRideDropPointAddress());

        if (mBaseFragment.isAdded()){
            String pickupDistance = Integer.toString((int)rideInfo.getPickupPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
            String dropDistance = Integer.toString((int)rideInfo.getDropPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
            ((TextView) view.findViewById(R.id.ride_pickup_point_variation_text)).setText(pickupDistance);
            ((TextView) view.findViewById(R.id.ride_drop_point_variation_text)).setText(dropDistance);
        }

        View acceptButton = view.findViewById(R.id.ride_owner_accept_button);
        acceptButton.setVisibility(View.VISIBLE);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = APIUrl.MANUAL_ACCEPT_RIDE.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                        .replace(APIUrl.RIDE_REQUEST_ID_KEY, Long.toString(rideInfo.getRideRequestId()))
                        .replace(APIUrl.RIDE_ID_KEY, Long.toString(rideInfo.getRideId()));
                mCommonUtil.showProgressDialog();
                RESTClient.post(mBaseFragment.getActivity(), url, rideInfo, new RSJsonHttpResponseHandler(mCommonUtil){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                            mListener.onRideRequestRefresh(mRideRequest);
                        }
                    }
                });
            }
        });

    }


    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    private void setRideOwnerLayoutButtonsOnClickListener(final View view){

        //Note - We always use Cancel word within the system but for user, we will show as Reject
        //So logically Reject is same as cancelling confirmed ride request, don't try to change it
        //else you will end up into mess as system is designed from the perspective of cancel and not reject
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
                Logger.debug(TAG, "Navigate to Pickup Point for Id:"+mRideRequest.getId());
                CommonComp commonComp = new CommonComp(mBaseFragment);
                commonComp.googleNavigation(mRideRequest.getRidePickupPoint().getPoint());
            }
        });

        boolean feedbackAvailable = false;
        Logger.debug(TAG,"RatingBar Instance:"+mRatingBar.hashCode());
        //This will show the user given rating
        for (UserFeedback feedback: mRideRequest.getFeedbacks()) {
            if (feedback.getForUser().getId() == mRideRequest.getAcceptedRide().getDriver().getId()) {
                Logger.debug(TAG,"Setting Rating:"+feedback.getRating());
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
                Logger.debug(TAG, "Rating is:"+rating+" Given By Passenger User Id:"+mRideRequest.getPassenger().getId());
                USER_FEEDBACK_URL = APIUrl.USER_FEEDBACK.replace(APIUrl.USER_ID_KEY, Long.toString(mRideRequest.getAcceptedRide().getDriver().getId()))
                        .replace(APIUrl.RIDE_TYPE_KEY, RideType.RequestRide.toString());
                feedbackInfo.setGivenByUser(mRideRequest.getPassenger());
                feedbackInfo.setRating(rating);
                feedbackInfo.setRide(mRideRequest.getAcceptedRide());
                feedbackInfo.setRideRequest(mRideRequest);
                mCommonUtil.showProgressDialog();
                RESTClient.post(mBaseFragment.getActivity(), USER_FEEDBACK_URL, feedbackInfo, new RSJsonHttpResponseHandler(mCommonUtil){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (mBaseFragment.isAdded()) {
                            super.onSuccess(statusCode, headers, response);
                            mCommonUtil.dismissProgressDialog();
                            userRatingBar.setEnabled(false);
                            mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                            mListener.onRideRequestRefresh(mRideRequest);
                        }
                    }
                });

            }
        });
    }

    //Use Full Ride Request as this would be called from Base Fragment where we have access to Full Ride Request
    private void updateRideOwnerLayoutButtonsVisiblity(View view){

        //This means that only when passenger is in confirmed state i.e. he/she has not been picked up, he can cancel / navigate
        //As far as rating is concerned, he can rate post pickup up or cancel. In case of cancel, we will ask for rating in dialog bar itself
        if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)
                || mRideRequest.getPassengerStatus().equals(PassengerStatus.Picked)){

            Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mRideRequest);
            Logger.debug(TAG, "Current Time is:"+Calendar.getInstance().getTime().toString());
            Logger.debug(TAG, "Max End Time is:"+maxEndTime.getTime().toString());
            if (maxEndTime.before(Calendar.getInstance())){
                //Visible Buttons
                mRideOwnerCancelButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                //i.e. No Pickup Point Navigation button would be invisible post ride request drop point time has lapsed
                mPickupPointNavigationButton.setVisibility(View.GONE);

                //No Rating Bar as he has not been picked, and while cancelling user get an option to give feedback
                mRatingBar.setVisibility(View.GONE);
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
        Logger.debug(TAG, "Rating value:"+ratingBar.getRating());

        String CANCEL_DRIVER = APIUrl.CANCEL_DRIVER.replace(APIUrl.USER_ID_KEY,Long.toString(mUser.getId()))
                .replace(APIUrl.RIDE_REQUEST_ID_KEY, Long.toString(rideRequest.getId()))
                .replace(APIUrl.RIDE_ID_KEY, Long.toString(rideRequest.getAcceptedRide().getId()))
                .replace(APIUrl.RATING_KEY, Float.toString(ratingBar.getRating()));
        mCommonUtil.showProgressDialog();
        RESTClient.get(CANCEL_DRIVER, null, new RSJsonHttpResponseHandler(mCommonUtil){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (mBaseFragment.isAdded()) {
                    super.onSuccess(statusCode, headers, response);
                    mCommonUtil.dismissProgressDialog();
                    Logger.debug(TAG, "Ride Owner Cancelled");
                    mRideRequest = new Gson().fromJson(response.toString(), FullRideRequest.class);
                    mListener.onRideRequestRefresh(mRideRequest);
                    Toast.makeText(mBaseFragment.getActivity(), "Ride Owner Rejected", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onNegativeClickOfCancelCoTravellerFragment(Dialog dialog, FullRideRequest rideRequest) {
        Logger.debug(TAG, "Ride Owner Not Cancelled");

    }

    public interface RideRequestCompListener{
        public void onRideRequestRefresh(FullRideRequest rideRequest);
    }
}
































