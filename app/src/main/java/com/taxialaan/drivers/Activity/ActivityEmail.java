//package com.taxialaan.drivers.Activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.StrictMode;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.snackbar.Snackbar;
//import androidx.appcompat.app.AppCompatActivity;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
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
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.taxialaan.drivers.Api.ApiClient;
//import com.taxialaan.drivers.Api.ApiInterface;
//import com.taxialaan.drivers.Api.request.LoginRequest;
//import com.taxialaan.drivers.Api.response.LoginResponse;
//import com.taxialaan.drivers.Api.response.OTPResponse;
//import com.taxialaan.drivers.CountryPicker.Country;
//import com.taxialaan.drivers.CountryPicker.CountryPicker;
//import com.taxialaan.drivers.CountryPicker.CountryPickerListener;
//import com.taxialaan.drivers.Helper.CustomDialog;
//import com.taxialaan.drivers.Helper.SharedHelper;
//import com.taxialaan.drivers.Helper.URLHelper;
//import com.taxialaan.drivers.R;
//import com.taxialaan.drivers.Utilities.MyTextView;
//import com.taxialaan.drivers.Utilities.Utils;
//import com.taxialaan.drivers.G;
//
//import org.json.JSONObject;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static com.taxialaan.drivers.G.getInstance;
//
///**
// * Created by jayakumar on 31/01/17.
// */
//
//public class ActivityEmail extends AppCompatActivity {
//
//    ImageView backArrow,countryImage;
//    FloatingActionButton nextICON;
//    EditText phoneNumber;
//    MyTextView register, forgetPassword;
//    CountryPicker mCountryPicker;
//    TextView countryNumber;
//
//    String country_code = "+964";
//    CustomDialog customDialog;
//    Utils utils = new Utils();
//    private AdView mAdView;
//    public static int APP_REQUEST_CODE = 99;
//    String TAG = "ActivityPassword";
//    String device_token, device_UDID;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_email);
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//
//             initView();
//
//
//        mCountryPicker = CountryPicker.newInstance("Select Country");
//        // You can limit the displayed countries
//        List<Country> countryList = Country.getAllCountries();
//        Collections.sort(countryList, new Comparator<Country>() {
//            @Override
//            public int compare(Country s1, Country s2) {
//                return s1.getName().compareToIgnoreCase(s2.getName());
//            }
//        });
//        mCountryPicker.setCountriesList(countryList);
//        setListener();
//
//        MobileAds.initialize(this,
//                "ca-app-pub-6606021354718512~9312146330");
//
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//
//
//          phoneLogin();
//          GetToken();
//
//
//
//    }
//
//
//    private void initView(){
//
//        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
//        nextICON = (FloatingActionButton) findViewById(R.id.right_arrow);
//        backArrow = (ImageView) findViewById(R.id.backArrow);
//        countryImage = (ImageView) findViewById(R.id.countryImage);
//        register = (MyTextView) findViewById(R.id.register);
//        forgetPassword = (MyTextView) findViewById(R.id.forgetPassword);
//        countryNumber = (TextView) findViewById(R.id.countryNumber);
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
//        intent.putExtra(
//                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
//                configurationBuilder.build());
//        startActivityForResult(intent, APP_REQUEST_CODE);
//    }
//
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
//               // showErrorActivity(loginResult.getError());
//            } else if (loginResult.wasCancelled()) {
//                toastMessage = "Login Cancelled";
//            } else {
//                if (loginResult.getAccessToken() != null) {
//                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
//
//                } else {
//                    toastMessage = String.format(
//                            "Success:%s...",
//                            loginResult.getAuthorizationCode().substring(0,10));
//                }
//
//            }
//
//
//            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                @Override
//                public void onSuccess(Account account) {
//
//                    PhoneNumber phoneNumber = account.getPhoneNumber();
//                    String phoneNumberString = phoneNumber.toString();
//                    SharedHelper.putKey(getInstance(),"phone_number_login",phoneNumberString);
//
//                }
//
//                @Override
//                public void onError(AccountKitError accountKitError) {
//
//                    utils.print("onError",accountKitError.getUserFacingMessage());
//
//                }
//            });
//
//            Toast.makeText(
//                    this,
//                    toastMessage,
//                    Toast.LENGTH_LONG)
//                    .show();
//        }
//    }
//
//
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
//            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
//                dismissProgress();
//
//                if (response.isSuccessful()){
//
//                    if (response.body().getStatus() == 0) {
//                        moveToRegisterActivity();
//                    }else {
//
//                        LoginRequest request = new LoginRequest();
//                        request.setDevice_id(device_UDID);
//                        request.setDevice_token(SharedHelper.getKeyDeviceToken(getInstance(),"device_token"));
//                        request.setMobile(SharedHelper.getKey(getInstance(),"phone_number_login"));
//                       // request.setOtp(otp);
//                        request.setDevice_type("android");
//                       // request.setRequest_id(requestID);
//
//                        goToLogin(request);
//
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
//
//    private void goToLogin(LoginRequest request) {
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
//    public void getProfile() {
//
//
//        customDialog = new CustomDialog(getApplicationContext());
//        customDialog.setCancelable(false);
//        customDialog.show();
//        JSONObject object = new JSONObject();
//        Log.e("url","url"+ URLHelper.USER_PROFILE_API +"?lang=" + "en");
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                URLHelper.USER_PROFILE_API /*+"?lang=" + "en"*/, object, new com.android.volley.Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                customDialog.dismiss();
//                utils.print("GetProfile", response.toString());
//               // Log.e(TAG, "onResponse: "+response );
//                SharedHelper.putKey(getApplicationContext(), "id", response.optString("id"));
//                SharedHelper.putKey(getApplicationContext(), "first_name", response.optString("first_name"));
//                SharedHelper.putKey(getApplicationContext(), "last_name", response.optString("last_name"));
//                SharedHelper.putKey(getApplicationContext(), "email", response.optString("email"));
//                SharedHelper.putKey(getApplicationContext(), "gender", ""+response.optString("gender"));
//                SharedHelper.putKey(getApplicationContext(), "mobile", response.optString("mobile"));
//                SharedHelper.putKey(getApplicationContext(), "approval_status", response.optString("status"));
//                SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.True));
//                SharedHelper.putKey(getApplicationContext(), "balance", response.optString("balance"));
//                SharedHelper.putKey(getApplicationContext(), "wallet_id", response.optString("wallet_id"));
//                if (response.optString("avatar").startsWith("http"))
//                    SharedHelper.putKey(getApplicationContext(), "picture", response.optString("avatar"));
//                else
//                    SharedHelper.putKey(getApplicationContext(), "picture", URLHelper.base + "storage/" + response.optString("avatar"));
//
//                SharedHelper.getKey(getApplicationContext(),"picture");
//
//                if (response.optJSONObject("service") != null) {
//                    try {
//                        JSONObject service = response.optJSONObject("service");
//                        if (service.optJSONObject("service_type") != null) {
//                            JSONObject serviceType = service.optJSONObject("service_type");
//                            SharedHelper.putKey(getApplicationContext(), "service", serviceType.optString("name"));
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                SharedHelper.putKey(getApplicationContext(), "sos", response.optString("sos"));
//
//              //  GoToMainActivity();
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
//               // Log.e(TAG, "onErrorResponse: "+error.getMessage() );
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
//                            SharedHelper.putKey(getApplicationContext(),"loggedIn",getString(R.string.False));
//                           // GoToBeginActivity();
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
//                headers.put("Authorization", "Bearer "+SharedHelper.getKey(getApplicationContext(), "access_token"));
//
//                return headers;
//            }
//        };
//
//        G.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }
//
//
//    private void showProgress() {
//        if (customDialog == null) {
//            customDialog = new CustomDialog(ActivityEmail.this);
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
//    private void moveToOTP(String requestID) {
//        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
//        mainIntent.putExtra("request_id",requestID);
//        mainIntent.putExtra("action","login");
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//    }
//
//
//    private  void moveToRegisterActivity(){
//
//        SharedHelper.putKey(ActivityEmail.this,"password", "");
//        Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
//        mainIntent.putExtra("isFromMailActivity", true);
//        mainIntent.putExtra("mobile",phoneNumber.getText().toString().trim());
//        mainIntent.putExtra("codeCountry",country_code);
//        startActivity(mainIntent);
//        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//    }
//
//    private void setListener() {
//        mCountryPicker.setListener(new CountryPickerListener() {
//            @Override
//            public void onSelectCountry(String name, String code, String dialCode,
//                                        int flagDrawableResID) {
//                countryNumber.setText(dialCode);
//                country_code = dialCode;
//                countryImage.setImageResource(flagDrawableResID);
//                mCountryPicker.dismiss();
//            }
//        });
//
//        countryImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
//            }
//        });
//
//        countryNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
//            }
//        });
//
//        nextICON.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String strPhoneNumber = phoneNumber.getText().toString().trim();
//                if (TextUtils.isEmpty(strPhoneNumber)){
//                    displayMessage(getString(R.string.mobile_number_validation));
//                }else if(strPhoneNumber.length()<10){
//                    displayMessage(getString(R.string.valid_mobile_number));
//                }else{
//                    SharedHelper.putKey(ActivityEmail.this,"phone_number_login",country_code+strPhoneNumber);
//                    sendOTP(country_code+strPhoneNumber);
//                }
//
//
//
//            }
//        });
//
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               /* SharedHelper.putKey(ActivityEmail.this,"email", "");
//                Intent mainIntent = new Intent(ActivityEmail.this, BeginScreen.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(mainIntent);
//
//                ActivityEmail.this.finish();*/
//                onBackPressed();
//            }
//        });
//
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedHelper.putKey(ActivityEmail.this,"password", "");
//                Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
//                mainIntent.putExtra("isFromMailActivity", true);
//
//                startActivity(mainIntent);
//                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//            }
//        });
//
//        forgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedHelper.putKey(ActivityEmail.this,"password", "");
//                Intent mainIntent = new Intent(ActivityEmail.this, ForgetPassword.class);
//                mainIntent.putExtra("isFromMailActivity", true);
//                startActivity(mainIntent);
//            }
//        });
//        phoneNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
//
//                if (phoneNumber.getText().length() == 1){
//
//                    if (phoneNumber.getText().toString().equals("0")) {
//                        phoneNumber.setText("");
//                    }
//                }
//            }
//
//            @Override public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//
//        getUserCountryInfo();
//    }
//
//    private void getUserCountryInfo() {
//        Locale current = getResources().getConfiguration().locale;
//        Country country = Country.getCountryFromSIM(ActivityEmail.this);
//        if (country != null) {
//            countryImage.setImageResource(country.getFlag());
//            countryNumber.setText(country.getDialCode());
//            country_code = country.getDialCode();
//        } else {
//            Country india = new Country("IQ", "Iraq", "+964", R.drawable.flag_iq);
//            countryImage.setImageResource(india.getFlag());
//            countryNumber.setText(india.getDialCode());
//            country_code = india.getDialCode();
//        }
//    }
//
//    public void displayMessage(String toastString){
//        try{
//            Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
//        }catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private boolean isValidEmail(String email) {
//        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.matches();
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
//    }
//
//    public void GetToken(){
//        try {
//            if(!SharedHelper.getKeyDeviceToken(getInstance(),"device_token").equals("") && SharedHelper.getKeyDeviceToken(getInstance(),"device_token") != null) {
//                device_token = SharedHelper.getKeyDeviceToken(getInstance(), "device_token");
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