package com.digitusrevolution.rideshare.config;

/**
 * Created by psarthi on 11/8/17.
 */

public class APIUrl {

    public static final String BASE_URL_RIDE_SYSTEM = "http://10.0.0.4:8080/RSRideSystem/api";
    public static final String BASE_URL_USER_SYSTEM = "http://10.0.0.4:8080/RSUserSystem/api";

    public static final String USER_EMAIL_KEY = "{userEmail}";
    public static final String ID_KEY = "{id}";
    public static final String MOBILE_NUMBER_KEY = "{mobileNumber}";
    public static final String OTP_KEY = "{otp}";

    public static final String GET_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/domain/riderequests/{id}?fetchChild=true";
    public static final String SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/signin";
    public static final String GOOGLE_SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/googlesignin";
    public static final String SIGN_IN_WITH_TOKEN_URL = BASE_URL_USER_SYSTEM + "/users/signinwithtoken";
    public static final String CHECK_USER_EXIST_URL = BASE_URL_USER_SYSTEM + "/users/checkuserexist/{userEmail}";
    public static final String GET_COUNTRIES_URL = BASE_URL_USER_SYSTEM + "/usersystem/countries";
    public static final String GET_OTP_URL = BASE_URL_USER_SYSTEM + "/users/getotp/{mobileNumber}";
    public static final String VALIDATE_OTP_URL = BASE_URL_USER_SYSTEM + "/users/validateotp/{mobileNumber}/{otp}";
    public static final String USER_REGISTRATION_URL = BASE_URL_USER_SYSTEM + "/users";


}
