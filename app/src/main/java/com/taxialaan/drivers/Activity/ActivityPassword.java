//package com.taxialaan.drivers.Activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.StrictMode;
//import androidx.annotation.NonNull;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//import androidx.appcompat.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.chaos.view.PinView;
//
//import com.taxialaan.drivers.Api.ApiClient;
//import com.taxialaan.drivers.Api.ApiInterface;
//import com.taxialaan.drivers.Api.request.LoginRequest;
//import com.taxialaan.drivers.Api.request.RegisterRequest;
//import com.taxialaan.drivers.Api.response.LoginResponse;
//import com.taxialaan.drivers.Api.response.OTPResponse;
//import com.taxialaan.drivers.Api.response.RegisterResponse;
//import com.taxialaan.drivers.Api.response.VerifyOTP;
//import com.taxialaan.drivers.Helper.ConnectionHelper;
//import com.taxialaan.drivers.Helper.CustomDialog;
//import com.taxialaan.drivers.Helper.SharedHelper;
//import com.taxialaan.drivers.Helper.URLHelper;
//import com.taxialaan.drivers.R;
//import com.taxialaan.drivers.Utilities.MyTextView;
//import com.taxialaan.drivers.Utilities.Utils;
//import com.taxialaan.drivers.G;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//import java.util.StringTokenizer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//
//import static com.taxialaan.drivers.G.getInstance;
//import static com.taxialaan.drivers.G.trimMessage;
//
//
///**
// * Created by jayakumar on 31/01/17.
// */
//
//public class ActivityPassword extends AppCompatActivity {
//
//
//    public Context context = ActivityPassword.this;
//    public Activity activity = ActivityPassword.this;
//    ConnectionHelper helper;
//    Boolean isInternet;
//    ImageView backArrow;
//    FloatingActionButton nextICON;
//    //  EditText password;
//    MyTextView register, forgetPassword,resendOTP;
//    CustomDialog customDialog;
//    String TAG = "ActivityPassword";
//    String device_token, device_UDID;
//    Utils utils =new Utils();
//    PinView pvOTP;
//
//    String requestID ="",action="";
//    private SmsVerifyCatcher smsVerifyCatcher;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_password);
//        findViewByIdandInit();
//        GetToken();
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle!=null && bundle.containsKey("request_id")){
//            requestID = bundle.getString("request_id");
//            action = bundle.getString("action");
//        }
//            utils.print("token",SharedHelper.getKey(context, "access_token"));
//
//        nextICON.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String mOTP = pvOTP.getText().toString().trim();
//
//                if (TextUtils.isEmpty(mOTP) || mOTP.length()< 4){
//                    displayMessage(getString(R.string.error_enter_otp));
//                    return;
//                }
//
//                verifyOTP(mOTP);
//            }
//        });
//
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               /* SharedHelper.putKey(context,"password", "");
//                Intent mainIntent = new Intent(activity, ActivityEmail.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mainIntent);
//
//                activity.finish();*/
//                onBackPressed();
//            }
//        });
//
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedHelper.putKey(context,"password", "");
//                Intent mainIntent = new Intent(activity, RegisterActivity.class);
//                startActivity(mainIntent);
//                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//            }
//        });
//
//        forgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedHelper.putKey(context,"password", "");
//                Intent mainIntent = new Intent(activity, ForgetPassword.class);
//                startActivity(mainIntent);
//            }
//        });
//
//        resendOTP.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (action.equalsIgnoreCase("login"))
//                    sendOTP(SharedHelper.getKey(getInstance(), "phone_number_login"));
//                else
//                    sendOTP(SharedHelper.getKey(getInstance(), "countrycode_register") + SharedHelper.getKey(getInstance(), "phone_number_register"));
//            }
//        });
//
//        //init SmsVerifyCatcher
//        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
//            @Override
//            public void onSmsCatch(String message) {
//                String code = parseCode(message);//Parse verification code
//                pvOTP.setText(code);//set code in edit text
//                //then you can send verification code to server
//            }
//        });
//
//    }
//
//    /**
//     * Parse verification code
//     *
//     * @param message sms message
//     * @return only four numbers from massage string
//     */
//    private String parseCode(String message) {
//        Pattern p = Pattern.compile("\\b\\d{4}\\b");
//        Matcher m = p.matcher(message);
//        String code = "";
//        while (m.find()) {
//            code = m.group(0);
//        }
//        return code;
//    }
//
//    private void sendOTP(String phoneNumber) {
//
//        showProgress();
//
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//
//        Call<OTPResponse> call = apiInterface.getOTP(phoneNumber);
//        call.enqueue(new Callback<OTPResponse>() {
//            @Override
//            public void onResponse(Call<OTPResponse> call, retrofit2.Response<OTPResponse> response) {
//                dismissProgress();
//                if (response.isSuccessful()){
//                    requestID = response.body().getRequestId();
//                    displayMessage(getString(R.string.otp_sent_ur_number));
//                }else{
//                    displayMessage(getResources().getString(R.string.something_went_wrong));
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OTPResponse> call, Throwable t) {
//                dismissProgress();
//                displayMessage(getResources().getString(R.string.something_went_wrong));
//            }
//        });
//    }
//
//
//    private void verifyOTP(final String otp) {
//        showProgress();
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<VerifyOTP> call = apiInterface.verifyOTP(otp,requestID);
//        call.enqueue(new Callback<VerifyOTP>() {
//            @Override
//            public void onResponse(Call<VerifyOTP> call, retrofit2.Response<VerifyOTP> response) {
//                if (response.isSuccessful()){
//
//                    if (response.body().getStatus() == 1){
//
//                        if (action.equalsIgnoreCase("login")){
//                            LoginRequest request = new LoginRequest();
//                            request.setDevice_id(device_UDID);
//                            request.setDevice_token(SharedHelper.getKeyDeviceToken(getInstance(),"device_token"));
//                            request.setMobile(SharedHelper.getKey(getInstance(),"phone_number_login"));
//                            request.setOtp(otp);
//                            request.setDevice_type("android");
//                            request.setRequest_id(requestID);
//
//                            goToLogin(request);
//                        }else{
//                            RegisterRequest registerRequest = new RegisterRequest();
//                            registerRequest.setOtp(otp);
//                            registerRequest.setRequest_id(requestID);
//                            registerRequest.setMobile(SharedHelper.getKey(getInstance(),"phone_number_register"));
//                            registerRequest.setCountry_code(SharedHelper.getKey(getInstance(),"countrycode_register"));
//                            registerRequest.setFirst_name(SharedHelper.getKey(getInstance(),"first_name"));
//                            registerRequest.setLast_name(SharedHelper.getKey(getInstance(),"last_name"));
//                            registerRequest.setEmail(SharedHelper.getKey(getInstance(),"email"));
//                            registerRequest.setDevice_id(device_UDID);
//                            registerRequest.setDevice_token(SharedHelper.getKeyDeviceToken(getInstance(),"device_token"));
//                            registerRequest.setDevice_type("android");
//                            registerRequest.setService_model("");
//                            registerRequest.setService_number("");
//                            registerRequest.setService_model("");
//                            goToRegister(registerRequest);
//                        }
//
//
//                    }else{
//                        dismissProgress();
//                        displayMessage(getString(R.string.error_please_enter_correct_otp));
//                    }
//
//                }else{
//                    dismissProgress();
//                    displayMessage(getResources().getString(R.string.something_went_wrong));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VerifyOTP> call, Throwable t) {
//                dismissProgress();
//                displayMessage(getResources().getString(R.string.something_went_wrong));
//            }
//        });
//
//    }
//
//    private void goToRegister(RegisterRequest request) {
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<RegisterResponse> call = apiInterface.Register(request);
//        call.enqueue(new Callback<RegisterResponse>() {
//            @Override
//            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
//                if (response.isSuccessful()){
//                    LoginRequest request = new LoginRequest();
//                    request.setDevice_id("");
//                    request.setDevice_token(SharedHelper.getKeyDeviceToken(getInstance(),"device_token"));
//                    request.setMobile(SharedHelper.getKey(getInstance(),"countrycode_register")+SharedHelper.getKey(getInstance(),"phone_number_register"));
//                    request.setOtp(pvOTP.getText().toString().trim());
//                    request.setDevice_type("android");
//                    request.setRequest_id(requestID);
//                    goToLogin(request);
//                }else {
//                    dismissProgress();
//                    displayMessage(getResources().getString(R.string.something_went_wrong));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                dismissProgress();
//                displayMessage(getResources().getString(R.string.something_went_wrong));
//            }
//        });
//    }
//
//    private void goToLogin(LoginRequest request) {
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<LoginResponse> call = apiInterface.Login(request);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
//                if (response.isSuccessful()){
//                    SharedHelper.putKey(context, "access_token", response.body().getAccessToken());
//                    SharedHelper.putKey(context, "refresh_token", response.body().getRefreshToken());
//                    SharedHelper.putKey(context, "token_type", response.body().getTokenType());
//                    getProfile();
//                }else {
//                    dismissProgress();
//                    displayMessage(response.body().getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                dismissProgress();
//                displayMessage(getResources().getString(R.string.something_went_wrong));
//            }
//        });
//
//    }
//
//    private void showProgress() {
//        if (customDialog == null) {
//            customDialog = new CustomDialog(ActivityPassword.this);
//            customDialog.setCancelable(false);
//            customDialog.show();
//        }
//    }
//
//    private void dismissProgress() {
//        if (customDialog != null) {
//            customDialog.dismiss();
//            customDialog = null;
//        }
//    }
//
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        dismissProgress();
//    }
//
//    private void signIn() {
//        if (isInternet) {
//            customDialog = new CustomDialog(activity);
//            customDialog.setCancelable(false);
//            if(customDialog != null)
//                customDialog.show();
//            JSONObject object = new JSONObject();
//            try {
//
//                object.put("grant_type", "password");
//               // object.put("client_id", URLHelper.client_id);
//                //object.put("client_secret", URLHelper.client_secret);
//                object.put("username", SharedHelper.getKey(context, "phone_number_login"));
//                object.put("password", SharedHelper.getKey(context, "password"));
//                object.put("scope", "");
//                object.put("device_type", "android");
//                object.put("device_id", device_UDID);
//                object.put("device_token", SharedHelper.getKeyDeviceToken(getInstance(),"device_token"));
//                utils.print("InputToLoginAPI", "" + object);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    if ((customDialog != null) && customDialog.isShowing())
//                        customDialog.dismiss();
//                    utils.print("SignUpResponse", response.toString());
//                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
//                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
//                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                    getProfile();
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    if ((customDialog != null) && customDialog.isShowing())
//                        customDialog.dismiss();
//                    String json = null;
//                    String Message;
//                    NetworkResponse response = error.networkResponse;
//                    utils.print("MyTest", "" + error);
//                    utils.print("MyTestError", "" + error.networkResponse);
//
//                    if (response != null && response.data != null) {
//                        try {
//                            JSONObject errorObj = new JSONObject(new String(response.data));
//
//                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
//                                try {
//                                    displayMessage(errorObj.optString("message"));
//                                } catch (Exception e) {
//                                    displayMessage(getString(R.string.something_went_wrong));
//                                }
//                            }else if (response.statusCode == 422) {
//                                json = trimMessage(new String(response.data));
//                                if (json != "" && json != null) {
//                                    displayMessage(json);
//                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
//                                }
//
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
//                        }
//
//
//                    }
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    return headers;
//                }
//            };
//
//            getInstance().addToRequestQueue(jsonObjectRequest);
//
//        }else {
//            displayMessage(getString(R.string.something_went_wrong_net));
//        }
//
//    }
//
//    public void getProfile() {
//
//
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        customDialog.show();
//        JSONObject object = new JSONObject();
//        Log.e("url","url"+URLHelper.USER_PROFILE_API +"?lang=" + "en");
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                URLHelper.USER_PROFILE_API /*+"?lang=" + "en"*/, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                customDialog.dismiss();
//                utils.print("GetProfile", response.toString());
//                Log.e(TAG, "onResponse: "+response );
//                SharedHelper.putKey(context, "id", response.optString("id"));
//                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
//                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
//                SharedHelper.putKey(context, "email", response.optString("email"));
//                SharedHelper.putKey(context, "gender", ""+response.optString("gender"));
//                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
//                SharedHelper.putKey(context, "approval_status", response.optString("status"));
//                SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
//                SharedHelper.putKey(context, "balance", response.optString("balance"));
//                SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
//                if (response.optString("avatar").startsWith("http"))
//                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
//                else
//                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
//
//                SharedHelper.getKey(context,"picture");
//
//                if (response.optJSONObject("service") != null) {
//                    try {
//                        JSONObject service = response.optJSONObject("service");
//                        if (service.optJSONObject("service_type") != null) {
//                            JSONObject serviceType = service.optJSONObject("service_type");
//                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                SharedHelper.putKey(context, "sos", response.optString("sos"));
//
//                GoToMainActivity();
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
//                Log.e(TAG, "onErrorResponse: "+error.getMessage() );
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//                    try {
//                        JSONObject errorObj = new JSONObject(new String(response.data));
//
//                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            displayMessage(getString(R.string.something_went_wrong));
//                        } else if (response.statusCode == 401) {
//                            SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
//                            GoToBeginActivity();
//                        } else if (response.statusCode == 422){
//                            json = G.trimMessage(new String(response.data));
//                            if (json.trim() != "" && json != null) {
//                                displayMessage(json);
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        }else if(response.statusCode == 503){
//                            displayMessage(getString(R.string.server_down));
//                        } else {
//                            displayMessage(getString(R.string.please_try_again));
//                        }
//
//                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
//                    }
//
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "Bearer "+SharedHelper.getKey(context, "access_token"));
//
//                return headers;
//            }
//        };
//
//        G.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }
//
//    private void setLanguage(String language) {
//        if (language != null && !language.equalsIgnoreCase("")){
//            if (language.equals("ar_IQ")){
//                StringTokenizer tokens = new StringTokenizer(language, "_");
//                String lang = tokens.nextToken();// this will contain "Fruit"
//                String country = tokens.nextToken();// this will contain " they taste good"
//                Locale locale = new Locale(lang, country);
//                Locale.setDefault(locale);
//
//                Configuration config = new Configuration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//            }else {
//                Locale locale = new Locale(language);
//                Locale.setDefault(locale);
//                Configuration config = new Configuration();
//                config.locale = locale;
//                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//
//            }
//        }else {
//            Locale locale = new Locale("en");
//            Locale.setDefault(locale);
//            Configuration config = new Configuration();
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config,
//                    getBaseContext().getResources().getDisplayMetrics());
//        }
//
//    }
//
//
//    private void refreshAccessToken() {
//        if (isInternet) {
//            customDialog = new CustomDialog(activity);
//            customDialog.setCancelable(false);
//            if(customDialog != null)
//                customDialog.show();
//            JSONObject object = new JSONObject();
//            try {
//
//                object.put("grant_type", "refresh_token");
//                //object.put("client_id", URLHelper.client_id);
//                //object.put("client_secret", URLHelper.client_secret);
//                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
//                object.put("scope", "");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    if ((customDialog != null) && customDialog.isShowing())
//                        customDialog.dismiss();
//                    utils.print("SignUpResponse", response.toString());
//                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
//                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
//                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
//                    getProfile();
//
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    if ((customDialog != null) && customDialog.isShowing())
//                        customDialog.dismiss();
//                    String json = null;
//                    String Message;
//                    NetworkResponse response = error.networkResponse;
//                    utils.print("MyTest", "" + error);
//                    utils.print("MyTestError", "" + error.networkResponse);
//                    utils.print("MyTestError1", "" + response.statusCode);
//
//                    if (response != null && response.data != null) {
//                        //    SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
//                        GoToBeginActivity();
//                    }
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    return headers;
//                }
//            };
//
//            getInstance().addToRequestQueue(jsonObjectRequest);
//
//        }else {
//            displayMessage(getString(R.string.something_went_wrong_net));
//        }
//
//    }
//
//    public void findViewByIdandInit(){
//        register = (MyTextView) findViewById(R.id.register);
//        forgetPassword = (MyTextView) findViewById(R.id.forgetPassword);
//        resendOTP = (MyTextView) findViewById(R.id.resendOTP);
//        // password = (EditText)findViewById(R.id.password);
//        nextICON = (FloatingActionButton) findViewById(R.id.nextIcon);
//        backArrow = (ImageView) findViewById(R.id.backArrow);
//        pvOTP = (PinView) findViewById(R.id.pvOTP);
//        helper = new ConnectionHelper(context);
//        isInternet = helper.isConnectingToInternet();
//    }
//
//    public void GoToBeginActivity(){
//        Intent mainIntent = new Intent(activity, BeginScreen.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
//        activity.finish();
//    }
//
//    public void displayMessage(String toastString){
//        utils.print("displayMessage",""+toastString);
//        Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
//    }
//
//    public void GoToMainActivity(){
//        Intent mainIntent = new Intent(activity, MainActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//        activity.finish();
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        smsVerifyCatcher.onStart();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        smsVerifyCatcher.onStop();
//    }
//
//
//
//    /**
//     * need for Android 6 real time permissions
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//   /* @Override
//    public void onBackPressed() {
//        SharedHelper.putKey(context,"password", "");
//        Intent mainIntent = new Intent(activity, ActivityEmail.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainIntent);
//        activity.finish();
//    }*/
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
//    }
//
//    public void GetToken(){
//        try {
//            if(!SharedHelper.getKeyDeviceToken(context,"device_token").equals("") && SharedHelper.getKeyDeviceToken(context,"device_token") != null) {
//                device_token = SharedHelper.getKeyDeviceToken(context, "device_token");
//                utils.print(TAG, "GCM Registration Token: " + device_token);
//            }else{
//                device_token = "COULD NOT GET FCM TOKEN";
//                utils.print(TAG, "Failed to complete token refresh: " + device_token);
//            }
//        }catch (Exception e) {
//            device_token = "COULD NOT GET FCM TOKEN";
//            utils.print(TAG, "Failed to complete token refresh");
//        }
//
//        try {
//            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//            utils.print(TAG, "Device UDID:" + device_UDID);
//        }catch (Exception e) {
//            device_UDID = "COULD NOT GET UDID";
//            e.printStackTrace();
//            utils.print(TAG, "Failed to complete device UDID");
//        }
//    }
//}
