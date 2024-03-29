package com.parift.rideshare.config;

/**
 * Created by psarthi on 11/8/17.
 */

public class APIUrl {

    //Environment Specific Parameters
    //public static final String ENV = "DEVELOPMENT";
    public static final String ENV = "PRODUCTION";
    //public static final String HOST_NAME = "http://192.168.1.5:8080";
    //public static final String HOST_NAME = "http://rideshare-test.ap-south-1.elasticbeanstalk.com";
    public static final String HOST_NAME = "https://api.parift.in";
    public static final String BASE_URL_USER_SYSTEM = HOST_NAME + "/RSUserSystem/api";
    public static final String BASE_URL_RIDE_SYSTEM = HOST_NAME + "/RSRideSystem/api/users/{userId}";
    public static final String BASE_URL_BILLING_SYSTEM = HOST_NAME + "/RSBillingSystem/api/users/{userId}";
    public static final String BASE_URL_SERVICE_PROVIDER_SYSTEM = HOST_NAME + "/RSServiceProviderSystem/api";

    public static final String USER_EMAIL_KEY = "{userEmail}";
    public static final String USER_ID_KEY = "{userId}";
    public static final String SIGNEDIN_USER_ID_KEY = "{signedInUserId}";
    public static final String GROUP_ID_KEY = "{groupId}";
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
    public static final String ACCOUNT_NUMBER_KEY = "{accountNumber}";
    public static final String AMOUNT_KEY = "{amount}";
    public static final String RIDE_TYPE_KEY = "{rideType}";
    public static final String USER_LIST_TYPE_KEY = "{UserListType}";
    public static final String GROUP_LIST_TYPE_KEY = "{GroupListType}";
    public static final String SEARCH_NAME_KEY = "{name}";
    public static final String REQUESTER_USER_ID_KEY = "{requesterUserId}";
    public static final String MEMBER_USER_ID_KEY = "{memberUserId}";
    public static final String OTP_RETRY_STATUS = "{retryStatus}";
    public static final String TOKEN_KEY = "{token}";
    public static final String ORDER_ID_KEY = "{orderId}";
    public static final String OFFER_ID_KEY = "{offerId}";

    //User System URL's
    public static final String GOOGLE_SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/googlesignin";
    public static final String CHECK_USER_EXIST_URL = BASE_URL_USER_SYSTEM + "/users/checkuserexist/{userEmail}";
    public static final String GET_COUNTRIES_URL = BASE_URL_USER_SYSTEM + "/usersystem/countries";
    public static final String GET_VEHICLE_CATEGORIES_URL = BASE_URL_USER_SYSTEM + "/usersystem/vehiclecategories";
    public static final String GET_OTP_URL = BASE_URL_USER_SYSTEM + "/users/getotp/{mobileNumber}?retry={retryStatus}";
    public static final String VALIDATE_OTP_URL = BASE_URL_USER_SYSTEM + "/users/validateotp/{mobileNumber}/{otp}";
    public static final String USER_REGISTRATION_URL = BASE_URL_USER_SYSTEM + "/users";
    public static final String ADD_VEHICLE_URL = BASE_URL_USER_SYSTEM + "/users/{id}/vehicles";
    public static final String GET_USER_ALL_DATA_URL = BASE_URL_USER_SYSTEM + "/users/{id}?fetchChild={fetchChildValue}";
    public static final String USER_FEEDBACK = BASE_URL_USER_SYSTEM + "/users/{userId}/feedback?rideType={rideType}";
    public static final String GET_USER_PROFILE = BASE_URL_USER_SYSTEM + "/users/{signedInUserId}/profile/{userId}";
    public static final String CREATE_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups";
    public static final String UPDATE_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/update";
    public static final String GET_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}";
    public static final String GET_GROUP_MEMBERS = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/members?page={page}";
    public static final String GET_USER_GROUPS = BASE_URL_USER_SYSTEM + "/users/{userId}/groups?listType={GroupListType}&page={page}";
    public static final String SEARCH_USER_FOR_GROUP_INVITE = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/searchuser?name={name}&page={page}";
    public static final String INVITE_USER = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/invite";
    public static final String SEARCH_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/search?name={name}&page={page}";
    public static final String GET_USER_MEMBERSHIP_REQUESTS = BASE_URL_USER_SYSTEM + "/users/{userId}/membershiprequests?page={page}";
    public static final String GET_GROUP_MEMBERSHIP_REQUESTS = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/membershiprequests?page={page}";
    public static final String GET_SPECIFIC_MEMBERSHIP_REQUEST = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/request";
    public static final String SUBMIT_MEMBERSHIP_REQUEST = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/request";
    public static final String APPROVE_MEMBERSHIP_REQUEST = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/approverequest/{requesterUserId}";
    public static final String REJECT_MEMBERSHIP_REQUEST = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/rejectrequest/{requesterUserId}";
    public static final String GROUP_FEEDBACK = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/feedback";
    public static final String LEAVE_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/leave";
    public static final String GROUP_UPDATE_MEMBERSHIP_FORM = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/updatemembershipform";
    public static final String ADD_ADMIN_TO_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/addadmin/{memberUserId}";
    public static final String REMOVE_MEMBER_FROM_GROUP = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/{groupId}/removemember/{memberUserId}";
    public static final String UPDATE_USER_PREFERENCE = BASE_URL_USER_SYSTEM + "/users/{userId}/preference";
    public static final String CHECK_GROUP_NAME_EXIST = BASE_URL_USER_SYSTEM + "/users/{userId}/groups/checkgroupexist/{name}";
    public static final String UPDATE_PUSH_NOTIFICATION_TOKEN = BASE_URL_USER_SYSTEM + "/users/{userId}/updatepushnotificationtoken/{token}";
    public static final String GET_INTERESTS_URL = BASE_URL_USER_SYSTEM + "/usersystem/interests";
    public static final String SAVE_USER_INTERESTS = BASE_URL_USER_SYSTEM + "/users/{userId}/interests";


    //Ride System URL's
    public static final String OFFER_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/rides";
    public static final String REQUEST_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/riderequests";
    public static final String GET_USER_RIDES_URL = BASE_URL_RIDE_SYSTEM + "/rides?page={page}";
    public static final String GET_USER_RIDE_REQUESTS_URL = BASE_URL_RIDE_SYSTEM + "/riderequests?page={page}";
    public static final String GET_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/rides/{id}";
    public static final String GET_RIDE_REQUEST_URL = BASE_URL_RIDE_SYSTEM + "/riderequests/{id}";
    public static final String GET_CURRENT_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/current";
    public static final String GET_CURRENT_RIDE_REQUEST = BASE_URL_RIDE_SYSTEM + "/riderequests/current";
    public static final String GET_USER_CURRENT_RIDES = BASE_URL_RIDE_SYSTEM + "/ridesystem/currentrides";
    public static final String START_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/start/{id}";
    public static final String END_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/end/{id}";
    public static final String CANCEL_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/cancel/{id}";
    public static final String CANCEL_ALL_UPCOMING_RIDE = BASE_URL_RIDE_SYSTEM + "/rides/cancelallupcoming/{id}";
    public static final String PICKUP_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/pickup/{rideRequestId}";
    public static final String DROP_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/drop/{rideRequestId}?ridemode={rideMode}&paymentcode={paymentCode}";
    public static final String CANCEL_PASSENGER = BASE_URL_RIDE_SYSTEM + "/rides/{rideId}/cancelpassenger/{rideRequestId}?rating={rating}";
    public static final String CANCEL_DRIVER = BASE_URL_RIDE_SYSTEM + "/riderequests/{rideRequestId}/canceldriver/{rideId}?rating={rating}";
    public static final String CANCEL_RIDE_REQUEST = BASE_URL_RIDE_SYSTEM + "/riderequests/cancel/{id}";
    public static final String GET_PRE_BOOKING_RIDE_REQUEST_INFO = BASE_URL_RIDE_SYSTEM + "/riderequests/prebookinginfo";
    public static final String MANUAL_ACCEPT_RIDE = BASE_URL_RIDE_SYSTEM + "/riderequests/{rideRequestId}/accept/{rideId}";



    //Billing System URL's
    public static final String PAY_BILL = BASE_URL_BILLING_SYSTEM + "/billing/pay";
    public static final String REDEEM_MONEY = BASE_URL_BILLING_SYSTEM + "/accounts/{accountNumber}/redeem/{amount}";
    public static final String GET_USER_WALLET_TRANSACTION = BASE_URL_BILLING_SYSTEM + "/accounts/{accountNumber}/transactions?page={page}";
    public static final String GET_PENDING_BILLS = BASE_URL_BILLING_SYSTEM + "/billing/pending";
    public static final String GET_ORDER_INFO = BASE_URL_BILLING_SYSTEM + "/financialtransaction/{amount}/getorderinfo";
    public static final String VALIDATE_AND_PROCESS_PAYMENT = BASE_URL_BILLING_SYSTEM + "/financialtransaction/{accountNumber}/validateandprocesspayment";
    public static final String CANCEL_FINANCIAL_TRANSACTION = BASE_URL_BILLING_SYSTEM + "/financialtransaction/cancel/{orderId}";
    public static final String GET_WALLET_INFO = BASE_URL_BILLING_SYSTEM + "/accounts/walletinfo";

    //Service Provider URL's
    public static final String GET_HELP_QUESTION_ANSWER_LIST = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/serviceprovider/help";
    public static final String GET_APP_INFO = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/serviceprovider/appinfo";
    public static final String GET_OFFERS = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/users/{userId}/offers?page={page}";
    public static final String GET_REWARD_COUPON_TRANSACTIONS = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/users/{userId}/rewardtransactions/coupon?page={page}";
    public static final String GET_REWARD_REIMBURSEMENT_TRANSACTIONS = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/users/{userId}/rewardtransactions/reimbursement?page={page}";
    public static final String CREATE_REWARD_REIMBURSEMENT_TRANSACTIONS = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/users/{userId}/rewardtransactions/offer/{offerId}/create";
    public static final String GENERATE_COUPON = BASE_URL_SERVICE_PROVIDER_SYSTEM + "/users/{userId}/rewardtransactions/coupon/generate/offer/{offerId}";


    //Google Service
    public static final String GET_GOOGLE_DIRECTION_URL="https://maps.googleapis.com/maps/api/directions/json?origin={originLat},{originLng}" +
            "&destination={destinationLat},{destinationLng}&departure_time={departureEpochSecond}&key={key}";
    //Note - Departure time is not required here as this is used only for fare calculation and in the backend we will include departure time for duration caluclation
    public static final String GET_GOOGLE_DISTANCE_URL="https://maps.googleapis.com/maps/api/distancematrix/json?origins={originLat},{originLng}" +
            "&destinations={destinationLat},{destinationLng}&departure_time={departureEpochSecond}&key={key}";
    public static final String GET_GOOGLE_REVERSE_GEOCODE_URL="https://maps.googleapis.com/maps/api/geocode/json?latlng={originLat},{originLng}" +
            "&result_type=street_address%7Cpoint_of_interest%7Croute%7Csublocality&key={key}";

    //Static Html Content
    public static final String TERMS_OF_SERVICE_URL ="https://www.parift.com/terms-of-service";
    public static final String PRIVACY_POLICY_URL="https://www.parift.com/privacy-policy";
    public static final String WEBSITE_URL="https://www.parift.com";
}
