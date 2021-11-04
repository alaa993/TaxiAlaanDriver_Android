package com.taxialaan.drivers.Activity.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chaos.view.PinView;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.shuhart.stepview.StepView;
import com.taxialaan.drivers.Activity.MainActivity;
import com.taxialaan.drivers.Activity.RegisterActivity;
import com.taxialaan.drivers.Api.ApiClient;
import com.taxialaan.drivers.Api.ApiInterface;
import com.taxialaan.drivers.Api.request.LoginRequest;
import com.taxialaan.drivers.Api.response.LoginResponse;
import com.taxialaan.drivers.Api.response.OTPResponse;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.Helper.ConnectionHelper;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.Utils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;
import static com.taxialaan.drivers.G.getInstance;

import com.firebase.ui.auth.AuthUI;

public class BeginScreen extends Activity {

    private final int REQUESR_LOG = 1000;
    public Context context = BeginScreen.this;
    private Button login;
    ConnectionHelper helper;
    Boolean isInternet;
    Utils utils = new Utils();
    private ProgressDialog progressDialog;
    String device_token, device_UDID;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        bindView();
        FirebaseApp.initializeApp(this);
        helper = new ConnectionHelper(BeginScreen.this);
        isInternet = helper.isConnectingToInternet();
        //GetToken();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder().setIsSmartLockEnabled(false).setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build())).setTheme(R.style.AppTheme).build(), REQUESR_LOG);
            }
        });
    }

    public void bindView() {
        context = BeginScreen.this;
        login = (Button) findViewById(R.id.login);
    }
    private void verify(String phoneNumber ,LoginRequest request) {

        showLoading();
        //device_UDID = "";
        device_token = SharedHelper.getKeyDeviceToken(context, "device_token");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<OTPResponse> call = apiInterface.getOTP(phoneNumber);
        call.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, retrofit2.Response<OTPResponse> response) {
                hideLoading();

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        if (response.body().getStatus() == 0) {
                            moveToRegisterActivity(phoneNumber);
                            Log.d("alaa1",phoneNumber);
                        } else {
                            Log.d("alaa2",phoneNumber);
                            LoginRequest request = new LoginRequest();
                            request.setMobile(phoneNumber);
                            request.setDevice_id(device_UDID);
                            request.setDevice_token(device_token);
                          //  request.setMobile(SharedHelper.getKey(getInstance(), "phone_number_login"));
                            request.setDevice_type("android");
                            request.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);
                            Log.d("alaa91",phoneNumber);
                            goToLogin(request);

                        }
                    }

                } else {
                    displayMessage(getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {
                hideLoading();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });
    }
    private void goToLogin(LoginRequest request) {
        Log.d("alaa93","tttttt");
        showLoading();
        Log.d("alaa93",request.getMobile());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(getApplicationContext(), "access_token", response.body().getAccessToken());
                    SharedHelper.putKey(getApplicationContext(), "refresh_token", response.body().getRefreshToken());
                    SharedHelper.putKey(getApplicationContext(), "token_type", response.body().getTokenType());
                    Log.d("alaa3",response.message());
                    getProfile();
                    Log.d("alaa4",response.toString());
                } else {
                    hideLoading();
                    if (request != null) {
                        if (response.body() != null) {
                            displayMessage(response.body().getMessage());
                            Log.d("alaa5",response.toString());
                        }
                    }


                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                hideLoading();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });

    }
    private void moveToRegisterActivity(String phone) {

       // String phone = phoneNum.getText().toString().startsWith("0") ? phoneNum.getText().toString().substring(1) : phoneNum.getText().toString();
        SharedHelper.putKey(BeginScreen.this, "password", "");
        Intent mainIntent = new Intent(BeginScreen.this, RegisterActivity.class);
        mainIntent.putExtra("isFromMailActivity", true);
        mainIntent.putExtra("mobile", phone);
      //  mainIntent.putExtra("codeCountry", countryCodePicker.getSelectedCountryCode());
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }
    public void getProfile() {

        if (isInternet) {

            showLoading();

            Log.e("language", "language" + SharedHelper.getKey(context, "language"));
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URLHelper.USER_PROFILE_API + "?lang=" + "en", object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    hideLoading();
                    utils.print("GetProfile", response.toString());
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    if (response.optString("avatar").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("avatar"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
                    SharedHelper.putKey(context, "gender", "" + response.optString("gender"));
                    SharedHelper.putKey(context, "sos", response.optString("sos"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "approval_status", response.optString("status"));
                    SharedHelper.putKey(context, "balance", response.optString("balance"));
                    SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                    if (response.optJSONObject("service") != null) {
                        JSONObject service = response.optJSONObject("service");
                        JSONObject serviceType = null;
                        if (service != null) {
                            serviceType = service.optJSONObject("service_type");
                        }
                        if (serviceType != null) {
                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
                        }
                    }
                    GoToMainActivity();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideLoading();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                displayMessage(getString(R.string.something_went_wrong));
                            } else if (response.statusCode == 401) {

                                SharedHelper.putKey(context, "access_token", errorObj.optString("access_token"));
                            } else if (response.statusCode == 422) {
                                json = G.trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            G.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

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

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(findViewById(android.R.id.content), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

//    private void moveToRegisterActivity() {
//
//        String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
//        SharedHelper.putKey(BeginScreen.this, "password", "");
//        Intent mainIntent = new Intent(BeginScreen.this, RegisterActivity.class);
//        mainIntent.putExtra("isFromMailActivity", true);
//        mainIntent.putExtra("mobile", phone);
//        mainIntent.putExtra("codeCountry", );
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void showLoading() {
        hideLoading();
        progressDialog = new ProgressDialog(BeginScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Utils.getString(R.string.pls_wait));
        progressDialog.show();
    }

    public void hideLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESR_LOG) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    LoginRequest request = new LoginRequest();
                    request.setDevice_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    request.setDevice_token(FirebaseAuth.getInstance().getAccessToken(true).toString());
                    request.setMobile(SharedHelper.getKey(getInstance(), "phone_number_login"));
                    request.setDevice_type("android");
                    request.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);

                    verify(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),request);
                    //goToLogin(request);

                    return;
                } else {
                    if (response == null) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "NO internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Unkonw erorrs", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    moveToRegisterActivity();
                }
            }
        }
    }
}
