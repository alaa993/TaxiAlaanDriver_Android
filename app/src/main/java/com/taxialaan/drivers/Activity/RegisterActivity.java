package com.taxialaan.drivers.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.taxialaan.drivers.Activity.login.BeginScreen;
import com.taxialaan.drivers.Api.ApiClient;
import com.taxialaan.drivers.Api.ApiInterface;
import com.taxialaan.drivers.Api.request.LoginRequest;
import com.taxialaan.drivers.Api.request.RegisterRequest;
import com.taxialaan.drivers.Api.response.LoginResponse;
import com.taxialaan.drivers.Api.response.RegisterResponse;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.Helper.ConnectionHelper;
import com.taxialaan.drivers.Helper.CustomDialog;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

import static com.taxialaan.drivers.G.getInstance;


public class RegisterActivity extends AppCompatActivity {

    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    String device_token, device_UDID;
    ImageView backArrow, countryImage;
    FloatingActionButton nextICON;
    EditText email, first_name, last_name, phoneNumber, edtCode;
    Spinner spinnercity;

    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utils utils = new Utils();
    Boolean fromActivity = false;
    TextView countryNumber;
    String country_code = "+964";
    String phoneNumberIntent = "";

    public static int APP_REQUEST_CODE = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        try {
            Intent intent = getIntent();
            if (intent != null) {
                if (getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = true;
                    phoneNumberIntent = getIntent().getExtras().getString("mobile");
                    country_code = getIntent().getExtras().getString("codeCountry");
                } else if (!getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = false;
                } else {
                    fromActivity = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fromActivity = false;
        }

        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());
                String strPhoneNumber = phoneNumber.getText().toString().trim();

                if (TextUtils.isEmpty(strPhoneNumber)) {
                    displayMessage(getResources().getString(R.string.enter_your_mobile_number));
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase(getString(R.string.last_name))) {
                    displayMessage(getString(R.string.last_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(getString(R.string.first_name_no_number));
                } else if (lastName.matches()) {
                    displayMessage(getString(R.string.last_name_no_number));
                } else {
                    if (isInternet) {
                        saveRegisterFields();

                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        GetToken();
    }

    private void showProgress() {
        if (customDialog == null) {
            customDialog = new CustomDialog(RegisterActivity.this);
            customDialog.setCancelable(false);
            customDialog.show();
        }
    }

    private void dismissProgress() {
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
    }

    private void saveRegisterFields() {

        SharedHelper.putKey(context, "first_name", first_name.getText().toString());
        SharedHelper.putKey(context, "last_name", last_name.getText().toString());
        SharedHelper.putKey(context, "email", email.getText().toString());
        SharedHelper.putKey(RegisterActivity.this, "phone_number_register", phoneNumberIntent);
        SharedHelper.putKey(context,"city",spinnercity.getSelectedItem().toString());
       // SharedHelper.putKey(RegisterActivity.this, "countrycode_register", "+" + country_code);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setMobile(phoneNumberIntent);
      //  registerRequest.setCountry_code(SharedHelper.getKey(getInstance(), "countrycode_register"));
        registerRequest.setFirst_name(SharedHelper.getKey(getInstance(), "first_name"));
        registerRequest.setLast_name(SharedHelper.getKey(getInstance(), "last_name"));
        registerRequest.setEmail(SharedHelper.getKey(getInstance(), "email"));
        registerRequest.setDevice_id(device_UDID);
        registerRequest.setDevice_token(device_token);
        registerRequest.setDevice_type("android");
        registerRequest.setService_model("");
        registerRequest.setService_number("");
        registerRequest.setService_model("");
        registerRequest.setShare_key(edtCode.getText().toString());
        registerRequest.setApp_version("" + versionNumber);
        registerRequest.setUser_agent("" + Build.VERSION.SDK_INT + " " + Build.MODEL);
        registerRequest.setCity(SharedHelper.getKey(getInstance(),"city"));
        goToRegister(registerRequest);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
    }

    public void findViewById() {
        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phoneNumber);
        nextICON = findViewById(R.id.nextIcon);
        backArrow = findViewById(R.id.backArrow);
        countryImage = findViewById(R.id.countryImage);
        helper = new ConnectionHelper(context);
        countryNumber = findViewById(R.id.countryNumber);
        edtCode = findViewById(R.id.edtCode);
        spinnercity = findViewById(R.id.spinnerCity);

        String[] items = new String[]{"Erbil", "Sulaymaniyah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        spinnercity.setAdapter(adapter);

        isInternet = helper.isConnectingToInternet();
        if (!fromActivity) {
            email.setText(SharedHelper.getKey(context, "email"));
        }

        phoneNumber.setText(phoneNumberIntent);
       // countryNumber.setText(country_code);
        phoneNumber.setEnabled(false);

    }

    private void goToRegister(RegisterRequest request) {
        showProgress();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterResponse> call = apiInterface.Register(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    LoginRequest request = new LoginRequest();
                    request.setDevice_id(device_UDID);
                    request.setDevice_token(device_token);
                    request.setMobile(SharedHelper.getKey(getInstance(), "countrycode_register") + SharedHelper.getKey(getInstance(), "phone_number_register"));
                    request.setDevice_type("android");
                    goToLogin(request);
                } else {
                    dismissProgress();
                    displayMessage(getResources().getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                dismissProgress();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private void goToLogin(LoginRequest request) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Login(request);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "access_token", response.body().getAccessToken());
                    SharedHelper.putKey(context, "refresh_token", response.body().getRefreshToken());
                    SharedHelper.putKey(context, "token_type", response.body().getTokenType());
                    getProfile();
                } else {
                    dismissProgress();
                    displayMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dismissProgress();
                displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });

    }

    public void getProfile() {

        JSONObject object = new JSONObject();
        Log.e("url", "url" + URLHelper.USER_PROFILE_API + "?lang=" + "en");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.USER_PROFILE_API /*+"?lang=" + "en"*/, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                utils.print("GetProfile", response.toString());
                Log.e(TAG, "onResponse: " + response);
                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                SharedHelper.putKey(context, "gender", "" + response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "approval_status", response.optString("status"));
                SharedHelper.putKey(context, "balance", response.optString("balance"));
                SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
                SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                SharedHelper.putKey(context,"share_key",response.optString("share_key"));
                if (response.optString("avatar").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));

                SharedHelper.getKey(context, "picture");

                if (response.optJSONObject("service") != null) {
                    try {
                        JSONObject service = response.optJSONObject("service");
                        if (service.optJSONObject("service_type") != null) {
                            JSONObject serviceType = service.optJSONObject("service_type");
                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SharedHelper.putKey(context, "sos", response.optString("sos"));

                GoToMainActivity();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            displayMessage(getString(R.string.something_went_wrong));
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = G.trimMessage(new String(response.data));
                            if (json.trim() != "" && json != null) {
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

    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }

    public void displayMessage(String toastString) {

        Snackbar.make(findViewById(R.id.parentLayout), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {

        if (fromActivity) {
            Intent mainIntent = new Intent(RegisterActivity.this, BeginScreen.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKeyDeviceToken(context, "device_token").equals("") && SharedHelper.getKeyDeviceToken(context, "device_token") != null) {
                device_token = SharedHelper.getKeyDeviceToken(context, "device_token");
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(context, device_UDID, "device_UDID");
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }
}
