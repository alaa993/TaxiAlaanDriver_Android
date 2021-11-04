package com.taxialaan.drivers.Helper;

/**
 * Created by jayakumar on 26/12/16.
 */


public class URLHelper {

    public static final int client_id = 2;
    public static final String client_secret = "1seil0BqIM6TdcyZ0hIlEuNctAaxdatqK44AhwdW";

    public static String base = "https://mobile.taxialaan.com/";
//   public static String base = "http://taxialaan.com/";
    //  public static String base = "https://testing.taxialaan.com/";

    public static final String Code = "https://taxialaan.com/redirect/store";
    public static String HELP_URL = "https://taxialaan.com";
    public static String CALL_PHONE = "1";
    public static final String APP_URL = "https://taxialaan.com";
    public static String login = base + "api/provider/oauth/token";
    public static String register = base + "api/provider/register";
    public static String USER_PROFILE_API = base + "api/provider/profile";
    public static String UPDATE_AVAILABILITY_API = base + "api/provider/profile/available";
    public static String GET_HISTORY_API = base + "api/provider/requests/history";
    public static String GET_HISTORY_DETAILS_API = base + "api/provider/requests/history/details";
    public static String CHANGE_PASSWORD_API = base + "api/provider/profile/password";
    public static final String UPCOMING_TRIP_DETAILS = base + "api/provider/requests/upcoming/details";
    public static final String UPCOMING_TRIPS = base + "api/provider/requests/upcoming";
    public static final String CANCEL_REQUEST_API = base + "api/provider/cancel";
    public static final String TARGET_API = base + "api/provider/target";
    public static final String RESET_PASSWORD = base + "api/provider/reset/password";
    public static final String FORGET_PASSWORD = base + "api/provider/forgot/password";
    public static final String FACEBOOK_LOGIN = base + "api/provider/auth/facebook";
    public static final String GOOGLE_LOGIN = base + "api/provider/auth/google";
    public static final String LOGOUT = base + "api/provider/logout";
    public static final String SUMMARY = base + "api/provider/summary";
    public static final String HELP = base + "api/provider/help";
    public static final String UpdateLocation = base + "api/provider/update_location";
    public static final String PAY_NOW_API = base+"api/provider/payment";
    public static final String PAY_CHECK = base+"api/provider/payment/check";
    public static final String TOKEN = base+"api/oauth/token";
    public static final String sendERROR = base+"api/provider/logs/capture";
    public static final String TEXT = base+"/api/provider/trips/has_active";
    public static final String configMqtt = base + "/init/config";


}
