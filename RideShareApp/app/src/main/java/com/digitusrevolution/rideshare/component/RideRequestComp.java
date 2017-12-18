package com.digitusrevolution.rideshare.component;

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
import com.digitusrevolution.rideshare.fragment.BaseFragment;
import com.digitusrevolution.rideshare.fragment.RideInfoFragment;
import com.digitusrevolution.rideshare.fragment.RideRequestInfoFragment;
import com.digitusrevolution.rideshare.helper.CommonUtil;
import com.digitusrevolution.rideshare.helper.RESTClient;
import com.digitusrevolution.rideshare.model.common.ErrorMessage;
import com.digitusrevolution.rideshare.model.ride.domain.core.PassengerStatus;
import com.digitusrevolution.rideshare.model.ride.domain.core.RideRequestStatus;
import com.digitusrevolution.rideshare.model.ride.dto.BasicRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRide;
import com.digitusrevolution.rideshare.model.ride.dto.FullRideRequest;
import com.digitusrevolution.rideshare.model.ride.dto.FullRidesInfo;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by psarthi on 12/6/17.
 */

public class RideRequestComp{

    public static final String TAG = RideRequestComp.class.getName();
    BaseFragment mBaseFragment;
    private FullRideRequest mRideRequest;
    private CommonUtil mCommonUtil;

    private Button mRideRequestCancelButton;
    private Button mDestinationNavigationButton;
    private LinearLayout mBasicRideRequestButtonsLayout;
    private Button mRideOwnerCancelButton;
    private Button mPickupPointNavigationButton;
    private LinearLayout mRideOwnerButtonsLayout;
    private RideRequestCompListener mListener;

    public RideRequestComp(BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
        if (fragment instanceof RideRequestCompListener) mListener = (RideRequestCompListener) fragment;
    }

    //Reason behind this additional constructor so that we can send callback directly to adapter instead of BaseFragment class only
    public RideRequestComp(RecyclerView.Adapter adapter, BaseFragment fragment, FullRideRequest rideRequest){
        mBaseFragment = fragment;
        mRideRequest = rideRequest;
        mCommonUtil = new CommonUtil(fragment);
        mListener = (RideRequestCompListener) adapter;
    }

    public void setRideRequestBasicLayout(View view){

        View layout = view.findViewById(R.id.basic_ride_request_layout);
        mRideRequestCancelButton = layout.findViewById(R.id.ride_request_cancel_button);
        mDestinationNavigationButton = layout.findViewById(R.id.ride_request_navigate_to_destination_button);
        mBasicRideRequestButtonsLayout = layout.findViewById(R.id.ride_request_buttons_layout);

        String rideRequestNumberText = mBaseFragment.getResources().getString(R.string.ride_request_id_text)+ mRideRequest.getId();
        ((TextView) layout.findViewById(R.id.ride_request_id_text)).setText(rideRequestNumberText);
        ((TextView) layout.findViewById(R.id.ride_request_status_text)).setText(mRideRequest.getStatus().toString());
        String pickupTime = mCommonUtil.getFormattedDateTimeString(mRideRequest.getPickupTime());
        ((TextView) layout.findViewById(R.id.ride_request_pickup_time_text)).setText(pickupTime);


        String paymentCode = mRideRequest.getConfirmationCode();
        if (paymentCode!=null){
            //Reason behind making it visible so that its visible on refresh
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.VISIBLE);
            String paymentLabel = mBaseFragment.getString(R.string.ride_request_payment_code_text);
            ((TextView) layout.findViewById(R.id.ride_request_confirmation_code_text)).setText(paymentLabel+paymentCode);
        } else {
            layout.findViewById(R.id.ride_request_confirmation_code_text).setVisibility(View.GONE);
        }

        ((TextView) layout.findViewById(R.id.ride_request_pickup_point_text)).setText(mRideRequest.getPickupPointAddress());
        ((TextView) layout.findViewById(R.id.ride_request_drop_point_text)).setText(mRideRequest.getDropPointAddress());


        //This will setup initial button visibility
        updateRideRequestBasicLayoutButtonsVisiblity();
        setRideRequestBasicLayoutButtonsOnClickListener();

        RideRequestInfoFragment fragment = (RideRequestInfoFragment) mBaseFragment.getActivity().getSupportFragmentManager()
                .findFragmentByTag(RideRequestInfoFragment.TAG);
        if (fragment!=null && mRideRequest.getId() == fragment.getRideRequestId()){
            Log.d(TAG, "Ride Request Info is already loaded");
        } else {

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String GET_RIDE_REQUEST_URL = APIUrl.GET_RIDE_REQUEST_URL.replace(APIUrl.ID_KEY, Integer.toString(mRideRequest.getId()));

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

    private void setRideRequestBasicLayoutButtonsOnClickListener(){
        mRideRequestCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CANCEL_RIDE_REQUEST = APIUrl.CANCEL_RIDE_REQUEST.replace(APIUrl.ID_KEY, Integer.toString(mRideRequest.getId()));
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
        });
        mDestinationNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Destination for Id:"+mRideRequest.getId());
            }
        });
    }

    private void updateRideRequestBasicLayoutButtonsVisiblity(){

        //Reason for setting it visible to handle view holder reuse where once item is invisible, it remains as it is
        //e.g. when one item make something invisble but the same view is used by another item, then it remains invisble
        //Initial value of Buttons Layout which would be applicable to all except when ride is cancelled or finished
        //Reason for setting this just to avoid any issue with visiblity when layout is invisible but child is visible
        //not sure how that would behave so cleaner option is to make layout visible and then childs visible/invisible as well
        mBasicRideRequestButtonsLayout.setVisibility(View.VISIBLE);

        if (mRideRequest.getStatus().equals(RideRequestStatus.Unfulfilled)
                || mRideRequest.getStatus().equals(RideRequestStatus.Fulfilled)) {

            //Rules based on passenger status
            if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)
                    || mRideRequest.getPassengerStatus().equals(PassengerStatus.Unconfirmed)){

                //Visible Buttons
                mRideRequestCancelButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mDestinationNavigationButton.setVisibility(View.GONE);

            } else {
                //Visible Buttons for Picked / Dropped Status of passenger
                mDestinationNavigationButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                mRideRequestCancelButton.setVisibility(View.GONE);
            }

            Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mRideRequest);
            Log.d(TAG, "Drop Time with Variation:"+maxEndTime.getTime().toString());
            Log.d(TAG, "Current Time:"+Calendar.getInstance().getTime().toString());

            //Exception Rules common for both cases
            //This will make cancel invisible if current time is after pickup time + pickup variation + travel time
            if (maxEndTime.before(Calendar.getInstance())){
                Log.d(TAG, "Pickup time lapsed, so no cancel and Destination button for id:"+mRideRequest.getId());
                mRideRequestCancelButton.setVisibility(View.GONE);
                mDestinationNavigationButton.setVisibility(View.GONE);
            }
        }

        if (mRideRequest.getStatus().equals(RideRequestStatus.Cancelled)){
            mBasicRideRequestButtonsLayout.setVisibility(View.GONE);
        }
    }

    public void setRideOwnerLayout(View view){

        UserComp userComp = new UserComp(mBaseFragment, null);
        userComp.setUserProfileSingleRow(view, mRideRequest.getAcceptedRide().getDriver());

        setPickupTimeAndBillLayout(view, (BasicRideRequest) mRideRequest);

        ((TextView) view.findViewById(R.id.ride_pickup_point_text)).setText(mRideRequest.getRidePickupPointAddress());
        ((TextView) view.findViewById(R.id.ride_drop_point_text)).setText(mRideRequest.getRideDropPointAddress());

        String pickupDistance = Integer.toString((int)mRideRequest.getRidePickupPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        String dropDistance = Integer.toString((int)mRideRequest.getRideDropPointDistance()) + mBaseFragment.getResources().getString(R.string.distance_metrics);
        ((TextView) view.findViewById(R.id.ride_pickup_point_variation_text)).setText(pickupDistance);
        ((TextView) view.findViewById(R.id.ride_drop_point_variation_text)).setText(dropDistance);

        mRideOwnerButtonsLayout = view.findViewById(R.id.ride_owner_buttons_layout);
        mRideOwnerCancelButton = view.findViewById(R.id.ride_owner_cancel_button);
        mPickupPointNavigationButton = view.findViewById(R.id.navigate_to_ride_pickup_point_button);

        updateRideOwnerLayoutButtonsVisiblity(view);
        setRideOwnerLayoutButtonsOnClickListener(view);

    }

    private void setRideOwnerLayoutButtonsOnClickListener(final View view){

        mRideOwnerCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String CANCEL_DRIVER = APIUrl.CANCEL_DRIVER.replace(APIUrl.RIDE_REQUEST_ID_KEY, Integer.toString(mRideRequest.getId()))
                        .replace(APIUrl.RIDE_ID_KEY, Integer.toString(mRideRequest.getAcceptedRide().getId()));
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
        });

        mPickupPointNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Navigate to Pickup Point for Id:"+mRideRequest.getId());
            }
        });
    }

    private void updateRideOwnerLayoutButtonsVisiblity(View view){

        RatingBar ratingBar = view.findViewById(R.id.ride_owner_rating_bar);

        //This means that only when passenger is in confirmed state i.e. he/she has not been picked up, he can cancel / navigate
        //As far as rating is concerned, he can rate post pickup up or cancel. In case of cancel, we will ask for rating in dialog bar itself
        if (mRideRequest.getPassengerStatus().equals(PassengerStatus.Confirmed)){

            Calendar maxEndTime = mCommonUtil.getRideRequestMaxEndTime(mRideRequest);
            if (maxEndTime.before(Calendar.getInstance().getTime())){
                //Visible Buttons
                ratingBar.setVisibility(View.VISIBLE);

                //This will make Ride Owner buttons layout invisible post ride request drop point time has lapsed
                //i.e. No cancel and Pickup Point Navigation button would be visible
                mRideOwnerButtonsLayout.setVisibility(View.GONE);
            } else {
                //Visible Buttons
                mRideOwnerCancelButton.setVisibility(View.VISIBLE);
                mPickupPointNavigationButton.setVisibility(View.VISIBLE);

                //Invisible Buttons
                ratingBar.setVisibility(View.GONE);
            }
        } else {
            //Visible Buttons
            ratingBar.setVisibility(View.VISIBLE);

            //Invisible Buttons
            mRideOwnerButtonsLayout.setVisibility(View.GONE);
        }

    }

    public void setPickupTimeAndBillLayout(View view, BasicRideRequest rideRequest){
        View layout = view.findViewById(R.id.pickup_time_bill_layout);
        TextView pickupTimeTextView = layout.findViewById(R.id.pickup_time_text);
        Date pickupTime = rideRequest.getRidePickupPoint().getRidePointProperties().get(0).getDateTime();
        String pickupTimeString = mCommonUtil.getFormattedDateTimeString(pickupTime);
        pickupTimeTextView.setText(pickupTimeString);

        TextView fareTextView = layout.findViewById(R.id.fare_text);
        String amount = Float.toString(rideRequest.getBill().getAmount());
        String symbol = Currency.getInstance(rideRequest.getPassenger().getCountry().getCurrency().getName()).getSymbol();
        fareTextView.setText(symbol+amount);

        TextView billStatusTextView = layout.findViewById(R.id.bill_status);
        billStatusTextView.setText(rideRequest.getBill().getStatus().toString());
    }

    public void setRidePickupDropPointsLayout(View view, BasicRideRequest rideRequest){
        View layout = view.findViewById(R.id.ride_pickup_drop_point_layout);
        TextView pickupPointTextView = layout.findViewById(R.id.ride_pickup_point_text);
        pickupPointTextView.setText(rideRequest.getRidePickupPointAddress());

        TextView dropPointTextView = layout.findViewById(R.id.ride_drop_point_text);
        dropPointTextView.setText(rideRequest.getRideDropPointAddress());

    }

    public interface RideRequestCompListener{
        public void onRideRequestRefresh(FullRideRequest rideRequest);
    }

}
































