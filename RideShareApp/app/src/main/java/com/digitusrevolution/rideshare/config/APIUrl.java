package com.digitusrevolution.rideshare.config;

/**
 * Created by psarthi on 11/8/17.
 */

public class APIUrl {

    public static final String BASE_URL_RIDE_SYSTEM = "http://10.0.0.5:8080/RSRideSystem/api";
    public static final String BASE_URL_USER_SYSTEM = "http://10.0.0.5:8080/RSUserSystem/api";

    public static final String USER_EMAIL_KEY = "{userEmail}";
    public static final String ID_KEY = "{id}";

    public static final String GET_RIDE_URL = BASE_URL_RIDE_SYSTEM + "/domain/riderequests/{id}?fetchChild=true";
    public static final String SIGN_IN_URL = BASE_URL_USER_SYSTEM + "/users/signin";
    public static final String CHECK_USER_EXIST_URL = BASE_URL_USER_SYSTEM + "/users/checkuserexist/{userEmail}";

}
