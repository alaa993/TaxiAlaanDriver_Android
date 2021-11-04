//package com.taxialaan.drivers.Activity;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.facebook.accountkit.Account;
//import com.facebook.accountkit.AccountKit;
//import com.facebook.accountkit.AccountKitCallback;
//import com.facebook.accountkit.AccountKitError;
//import com.facebook.accountkit.AccountKitLoginResult;
//import com.facebook.accountkit.PhoneNumber;
//import com.facebook.accountkit.ui.AccountKitActivity;
//import com.facebook.accountkit.ui.AccountKitConfiguration;
//import com.facebook.accountkit.ui.LoginType;
//import com.google.android.material.snackbar.Snackbar;
//import com.taxialaan.drivers.Api.ApiClient;
//import com.taxialaan.drivers.Api.ApiInterface;
//import com.taxialaan.drivers.Api.request.LoginRequest;
//import com.taxialaan.drivers.Api.response.LoginResponse;
//import com.taxialaan.drivers.Api.response.OTPResponse;
//import com.taxialaan.drivers.G;
//import com.taxialaan.drivers.Helper.ConnectionHelper;
//import com.taxialaan.drivers.Helper.CustomDialog;
//import com.taxialaan.drivers.Helper.SharedHelper;
//import com.taxialaan.drivers.Helper.URLHelper;
//import com.taxialaan.drivers.R;
//import com.taxialaan.drivers.Utilities.Utils;
//
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//
//import static com.taxialaan.drivers.G.getInstance;
//
//
//public class BeginScreen extends AppCompatActivity  {
//
//    ConnectionHelper helper;
//    Boolean isInternet;
//    CustomDialog customDialog;
//    public Context context = BeginScreen.this;
//    String TAG = "BEGINSCREEN";
//    String device_token, device_UDID,country_code, phoneNumberString;
//    Utils utils = new Utils();
//
//    TextView enter_ur_mailID;
//    public static int APP_REQUEST_CODE = 99;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_begin);
//        helper = new ConnectionHelper(BeginScreen.this);
//        isInternet = helper.isConnectingToInternet();
//        utils.print("token",SharedHelper.getKey(context, "access_token"));
//
//
//        enter_ur_mailID = findViewById(R.id.enter_ur_mailID);
//        enter_ur_mailID.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                phoneLogin();
//
//
//            }
//        });
//
//        GetToken();
//
//
//    }
//
//
//    public void phoneLogin() {
//
//        final Intent intent = new Intent(getApplicationContext(), AccountKitActivity.class);
//        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                new AccountKitConfiguration.AccountKitConfigurationBuilder(
//                        LoginType.PHONE,
//                        AccountKitActivity.ResponseType.TOKEN);
//
//        configurationBuilder.setDefaultCountryCode("IQ");
//
//        intent.putExtra(
//                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
//                configurationBuilder.build());
//        startActivityForResult(intent, APP_REQUEST_CODE);
//
//    }
//
//
//    @Override
//    protected void onActivityResult( final int requestCode, final int resultCode, final Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == APP_REQUEST_CODE) {
//            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//            String toastMessage;
//            if (loginResult.getError() != null) {
//                toastMessage = loginResult.getError().getErrorType().getMessage();
//                // showErrorActivity(loginResult.getError());
//                Toast.makeText(
//                        this,
//                        toastMessage,
//                        Toast.LENGTH_LONG)
//                        .show();
//            } else if (loginResult.wasCancelled()) {
//                toastMessage = "Login Cancelled";
//                Toast.makeText(this, toastMessage,Toast.LENGTH_LONG).show();
//            } else {
//                if (loginResult.getAccessToken() != null) {
//                    loginFaceBook();
//                } else {
//                    loginFaceBook();
//                }
//
//            }
//
//        }
//    }
//
//    private  void loginFaceBook(){
//
//        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//            @Override
//            public void onSuccess(Account account) {
//
//                PhoneNumber phoneNumber = account.getPhoneNumber();
//                phoneNumberString = phoneNumber.getPhoneNumber();
//                country_code = phoneNumber.getCountryCode();
//                SharedHelper.putKey(getInstance(),"phone_number_login","+"+country_code+phoneNumberString);
//
//                verify("+"+country_code+phoneNumberString);
//
//
//            }
//
//            @Override
//            public void onError(AccountKitError accountKitError) {
//
//                utils.print("onError",accountKitError.getUserFacingMessage());
//
//            }
//        });
//
//    }
//
//    private void verify(String phoneNumber) {
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
//
//                if (response.isSuccessful()){
//
//                    if (response.body() != null) {
//                        if (response.body().getStatus() == 0) {
//                            moveToRegisterActivity();
//                        } else {
//
//                            LoginRequest request = new LoginRequest();
//                            request.setDevice_id(device_UDID);
//                            request.setDevice_token(device_token);
//                            request.setMobile(SharedHelper.getKey(getInstance(), "phone_number_login"));
//                            request.setDevice_type("android");
//                            request.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);
//                            goToLogin(request);
//
//                        }
//                    }
//
//                }else{
//                    displayMessage(getResources().getString(R.string.something_went_wrong));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OTPResponse> call, Throwable t) {
//                dismissProgress();
//
//                displayMessage(getResources().getString(R.string.something_went_wrong));
//            }
//        });
//    }
//
//    private void goToLogin(LoginRequest request) {
//        showProgress();
//        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
//        Call<LoginResponse> call = apiInterface.Login(request);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
//                if (response.isSuccessful()){
//                    SharedHelper.putKey(getApplicationContext(), "access_token", response.body().getAccessToken());
//                    SharedHelper.putKey(getApplicationContext(), "refresh_token", response.body().getRefreshToken());
//                    SharedHelper.putKey(getApplicationContext(), "token_type", response.body().getTokenType());
//                    getProfile();
//                }else {
//                    dismissProgress();
//                    if (request != null) {
//                        if (response.body() != null) {
//                            displayMessage(response.body().getMessage());
//                        }
//                    }
//
//
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
//    public void getProfile() {
//
//        if (isInternet) {
//
//            customDialog = new CustomDialog(context);
//            customDialog.setCancelable(false);
//            customDialog.show();
//            Log.e("language","language"+SharedHelper.getKey(context,"language"));
//            JSONObject object = new JSONObject();
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                    URLHelper.USER_PROFILE_API +"?lang=" + "en", object, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    customDialog.dismiss();
//                    utils.print("GetProfile", response.toString());
//                    SharedHelper.putKey(context, "id", response.optString("id"));
//                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
//                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
//                    SharedHelper.putKey(context, "email", response.optString("email"));
//                    if (response.optString("avatar").startsWith("http"))
//                        SharedHelper.putKey(context, "picture", response.optString("avatar"));
//                    else
//                        SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
//                    SharedHelper.putKey(context, "gender", "" + response.optString("gender"));
//                    SharedHelper.putKey(context, "sos", response.optString("sos"));
//                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
//                    SharedHelper.putKey(context, "approval_status", response.optString("status"));
//                    SharedHelper.putKey(context, "balance", response.optString("balance"));
//                    SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
//                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
//                    if (response.optJSONObject("service") != null) {
//                        JSONObject service = response.optJSONObject("service");
//                        JSONObject serviceType = null;
//                        if (service != null) {
//                            serviceType = service.optJSONObject("service_type");
//                        }
//                        if (serviceType != null) {
//                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
//                        }
//
//                    }
//
//
//                    GoToMainActivity();
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    customDialog.dismiss();
//                    String json = null;
//                    String Message;
//                    NetworkResponse response = error.networkResponse;
//                    if (response != null && response.data != null) {
//                        try {
//                            JSONObject errorObj = new JSONObject(new String(response.data));
//
//                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                                displayMessage(getString(R.string.something_went_wrong));
//                            } else if (response.statusCode == 401) {
//
//                                SharedHelper.putKey(context, "access_token", errorObj.optString("access_token"));
//                            } else if (response.statusCode == 422) {
//                                json = G.trimMessage(new String(response.data));
//                                if (json != "" && json != null) {
//                                    displayMessage(json);
//                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
//                                }
//
//                            } else if (response.statusCode == 503) {
//                                displayMessage(getString(R.string.server_down));
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        } catch (Exception e) {
//                            displayMessage(getString(R.string.something_went_wrong));
//                        }
//
//                    }
//                }
//            }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
//                    return headers;
//                }
//            };
//
//            G.getInstance().addToRequestQueue(jsonObjectRequest);
//        } else {
//            displayMessage(getString(R.string.something_went_wrong_net));
//        }
//
//    }
//
//    public void GetToken() {
//
//
//        try {
//            if (!SharedHelper.getKeyDeviceToken(context, "device_token").equals("") && SharedHelper.getKeyDeviceToken(context, "device_token") != null) {
//                device_token = SharedHelper.getKeyDeviceToken(context, "device_token");
//                utils.print(TAG, "GCM Registration Token: " + device_token);
//            } else {
//                device_token = "COULD NOT GET FCM TOKEN";
//                utils.print(TAG, "Failed to complete token refresh: " + device_token);
//            }
//        } catch (Exception e) {
//            device_token = "COULD NOT GET FCM TOKEN";
//            utils.print(TAG, "Failed to complete token refresh");
//        }
//
//
//        try {
//            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//            SharedHelper.putKey(context, "device_UDID", device_UDID);
//            utils.print(TAG, "Device UDID:" + device_UDID);
//        } catch (Exception e) {
//            device_UDID = "COULD NOT GET UDID";
//            e.printStackTrace();
//            utils.print(TAG, "Failed to complete device UDID");
//        }
//    }
//
//
//    public void displayMessage(String toastString) {
//        Log.e("displayMessage", "" + toastString);
//        Snackbar.make(findViewById(android.R.id.content), toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
//    }
//
//    public void GoToMainActivity() {
//        Intent mainIntent = new Intent(context, MainActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(mainIntent);
//        finish();
//    }
//
//    private  void moveToRegisterActivity(){
//
//        SharedHelper.putKey(BeginScreen.this,"password", "");
//        Intent mainIntent = new Intent(BeginScreen.this, RegisterActivity.class);
//        mainIntent.putExtra("isFromMailActivity", true);
//        mainIntent.putExtra("mobile",phoneNumberString);
//        mainIntent.putExtra("codeCountry",country_code);
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }
//
//    private void showProgress() {
//        if (customDialog == null) {
//            customDialog = new CustomDialog(BeginScreen.this);
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
//    public  void sendError(String mobile, String request_id, String method_name, String status_code, String exception, String payload, String url){
//
//        String version = "";
//        try {
//            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            version = pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("mobile", mobile);
//            object.put("request_id", request_id);
//            object.put("method_name", method_name);
//            object.put("status_code", status_code);
//            object.put("exception", exception);
//            object.put("payload", payload);
//            object.put("url",url);
//            object.put("app_version",""+version);
//            object.put("user_agent","android-provider");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.sendERROR, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//            @Override
//            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                return headers;
//            }
//        };
//
//        G.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }
//
//
//}