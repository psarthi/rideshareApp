package com.digitusrevolution.rideshare.config;

/**
 * Created by psarthi on 11/8/17.
 */

public class APIUrl {

    public static final String BASE_URL_RIDE_SYSTEM = "http://10.0.0.4:8080/RSRideSystem/api";
    public static final String BASE_URL_USER_SYSTEM = "http://10.0.0.4:8080/RSUserSystem/api";
    public static final String BASE_URL_BILLING_SYSTEM = "http://10.0.0.4:8080/RSBillingSystem/api";

    public static final String USER_EMAIL_KEY = "{userEmail}";
    public static final String USER_ID_KEY = "{userId}";
    public static final String ID_KEY = "{id}";
    public static final String RIDE_ID_KEY = "{rideId}";
    public static final String RIDE_REQUEST_ID_KEY = "{rideRequestId}";
    public static final String MOBILE_NUMBER_KEY = "{mobileNumber}";
    public static final String OTP_KEY = "{otp}";
    public static final String originLat_KEY = "{originLat}";
    public static final String originLng_KEY = "{originLng}";
    public static final String destinationLat_KEY = "{destinationLat}";
    public static final String destinationLng_KEY = "{destinationLng}";
    public static final String departureEpochSecond_KEY = "{departureEpochSecond}";
    public static final String GOOGLE_API_KEY = "{key}";
    public static final String FETCH_CHILD_VALUE_KEY = "{fetchChildValue}";
    public static final String PAGE_KEY = "{page}";
    public static final String RIDE_MODE_KEY = "{rideMode}";
    public static final String RATING_KEY = "{rating}";
    public static final String PAYMENT_CODE_KEY = "{paymentCode}";

    public static final String SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/signin";
    public static final String GOOGLE_SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/googlesignin";
    public static final String SIGN_IN_WITH_TOKEN_URL = BASE_URL_USER_SYSTEM + "/users/signinwithtoken";
    public static final String CHECK_USER_EXIST_URL = BASE_URL_USER_SYSTEM + "/users/checkuserexist/{userEmail}";
    public static final String GET_COUNTRIES_URL = BASE_URL_USER_SYSTEM + "/usersystem/countries";
    public static final String GET_VEHICLE_CATEGORIES_URL = BASE_URL_USER_SYSTEM + "/usersystem/vehiclecategores";
    public static final String GET_OTP_URL = BASE_URL_USER_SYSTEM + "/users/getotp/{mobileNumber}";
    public static final String VALIDATE_OTP_URL = BASE_URL_USER_SYSTEM + "/users/validateotp/{mobileNumber}/{otp}";
    public static final String USER_REGISTRATION_URL = BASE_URL_USER_SYSTEM + "/users";
    public static final String ADD_VEHICLE_URL = BASE_URL_USER_SYSTEM + "/users/{id}/vehicles";
    public static final String GET_USER_ALL_DATA_URL = BASE_URL_USER_SYSTEM + "/users/{id}?fetchChild={fetchChildValue}";
    public static final String GET_USER_RIDES_URL = BASE_URL_RIDE_SYSTEM + "/rides/user/{id}?page={page}";
    public static final String GET_USER_RIDE_REQUESTS_URL = BASE_URL_RIDE_SYSTEM + "/riderequests/user/{id}?page={page}";
    public static final String GET_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/rides/{id}";
    public static final String GET_RIDE_REQUEST_URL = BASE_URL_RIDE_SYSTEM + "/riderequests/{id}";
    public static final String GET_CURRENT_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/current/{userId}";
    public static final String GET_CURRENT_RIDE_REQUEST = BASE_URL_RIDE_SYSTEM + "/riderequests/current/{userId}";
    public static final String GET_USER_CURRENT_RIDES = BASE_URL_RIDE_SYSTEM + "/ridesystem/current/rides/{userId}";
    public static final String START_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/start/{id}";
    public static final String END_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/end/{id}";
    public static final String CANCEL_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/cancel/{id}";
    public static final String PICKUP_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/pickup/{rideRequestId}";
    public static final String DROP_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/drop/{rideRequestId}?ridemode={rideMode}&paymentcode={paymentCode}";
    public static final String CANCEL_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/cancelpassenger/{rideRequestId}?rating={rating}";
    public static final String CANCEL_DRIVER = BASE_URL_RIDE_SYSTEM + "/riderequests/{rideRequestId}/canceldriver/{rideId}?rating={rating}";
    public static final String CANCEL_RIDE_REQUEST = BASE_URL_RIDE_SYSTEM + "/riderequests/cancel/{id}";
    public static final String PAY_BILL = BASE_URL_BILLING_SYSTEM + "/billing/pay";
    public static final String USER_FEEDBACK = BASE_URL_USER_SYSTEM + "/users/{userId}/feedback";
    public static final String GET_USER_PROFILE = BASE_URL_USER_SYSTEM + "/users/{userId}/profile";


    public static final String GET_GOOGLE_DIRECTION_URL="https://maps.googleapis.com/maps/api/directions/json?origin={originLat},{originLng}" +
            "&destination={destinationLat},{destinationLng}&departure_time={departureEpochSecond}&key={key}";

    public static final String OFFER_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/rides";
    public static final String REQUEST_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/riderequests";

}
