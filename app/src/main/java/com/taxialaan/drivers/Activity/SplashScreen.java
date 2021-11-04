package com.taxialaan.drivers.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.taxialaan.drivers.Activity.login.BeginScreen;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.GPS.LocationService;
import com.taxialaan.drivers.Helper.ConnectionHelper;
import com.taxialaan.drivers.Helper.LocaleUtils;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.taxialaan.drivers.G.getInstance;


public class SplashScreen extends AppCompatActivity {

    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;
    String selectedLanguage = "English";
    String[] arrayServiceTypes;
    int selectedIndex = 0;
    boolean statusCode401 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        handleCheckStatus = new Handler();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        arrayServiceTypes = new String[3];
        arrayServiceTypes[0] = "English";
        arrayServiceTypes[1] = "عربى";
        arrayServiceTypes[2] = "Kurdî";

        Log.i("language", SharedHelper.getKey(context, "language"));
        setLanguage(SharedHelper.getKey(context, "language"));

        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(R.string.True))){
            handleCheckStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.w("Handler", "Called");
                    if (helper.isConnectingToInternet()) {
                        getProfile();
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                        }
                    } else {
                        showDialog();
                        handleCheckStatus.postDelayed(this, 3000);
                    }
                }
            }, 3000);
        }else {
            showLanguageDialog();
        }

    }


    private void showLanguageDialog() {

        if (arrayServiceTypes.length > 0){
            MaterialDialog.Builder materialBuilder =  new MaterialDialog.Builder(this);
            materialBuilder.title(R.string.choose_language)
                    .items(arrayServiceTypes)
                    .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            selectedLanguage = arrayServiceTypes[which];
                            Log.e("selectedLanguage","selectedLanguage"+selectedLanguage);
                            if (selectedLanguage.equals("English")){
                                SharedHelper.putKey(context,"language","en");
                            }
                            if (selectedLanguage.equals("عربى")){
                                SharedHelper.putKey(context,"language","ar");
                            }
                            if (selectedLanguage.equals("Kurdî")){
                                SharedHelper.putKey(context,"language","ku");
                            }

                            selectedIndex = which;
                            handleCheckStatus.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w("Handler", "Called");
                                    if (helper.isConnectingToInternet()) {

                                        Log.e("language", "language"+SharedHelper.getKey(context, "language"));
                                        setLanguage(SharedHelper.getKey(context, "language"));

                                        //   setlanguage(SharedHelper.getKey(context, "language"));
                                        GoToBeginActivity();
                                        if (alert != null && alert.isShowing()) {
                                            alert.dismiss();
                                        }
                                    } else {
                                        showDialog();
                                        handleCheckStatus.postDelayed(this, 3000);
                                    }
                                }
                            }, 3000);
                            return true;
                        }
                    })
                    .positiveText(R.string.confirm)
                    .cancelable(false)
                    .show();
        }
    }


    private void setLanguage(String language) {
        LocaleUtils.setLocale(SplashScreen.this,language);
    }

    public void getProfile() {
        retryCount++;
        JSONObject object = new JSONObject();

        Log.e("url","url"+URLHelper.USER_PROFILE_API + "?lang=" + "eng");

        String languageToLoad =SharedHelper.getKey(context,"language");
        if(languageToLoad.equalsIgnoreCase("ku")){
            languageToLoad = "ar_IQ";
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.USER_PROFILE_API /*+ "?lang=" + languageToLoad*/, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Splash Screen", "onResponse: " + response );
                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                SharedHelper.putKey(context, "sos", response.optString("sos"));
                SharedHelper.putKey(context,"share_key",response.optString("share_key"));
                if (response.optString("avatar").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "balance", response.optString("balance"));
                SharedHelper.putKey(context, "wallet_id", response.optString("wallet_id"));
                SharedHelper.putKey(context, "approval_status", response.optString("status"));
                SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                // setLanguage(response.optString("language"));
                if (response.optJSONObject("service") != null) {
                    try {
                        JSONObject service = response.optJSONObject("service");
                        if (service.optJSONObject("service_type") != null) {
                            JSONObject serviceType = service.optJSONObject("service_type");
                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (response.optString("status").equalsIgnoreCase("new")) {
                    Intent intent = new Intent(activity, WaitingForApproval.class);
                    activity.startActivity(intent);
                } else {
                    GoToMainActivity();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (retryCount < 5) {
                    getProfile();
                } else {

                    // displayMessage(getString(R.string.something_went_wrong));
                    GoToBeginActivity();
                }
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.clearSharedPreferences(context);
                            Intent serviceIntent = new Intent(context, LocationService.class);
                            stopService(serviceIntent);
                            SharedHelper.putKey(context, "access_token", errorObj.optString("access_token"));
                        } else if (response.statusCode == 422) {

                            json = G.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                //  displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        }
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else {
                    //displayMessage(getString(R.string.please_try_again));
                    //GoToBeginActivity();
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

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    private void refreshAccessToken() {

        if (isInternet && statusCode401) {
            statusCode401 = false;
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.TOKEN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    statusCode401 = true;
                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                        GoToBeginActivity();
                    }
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            getInstance().addToRequestQueue(jsonObjectRequest);

        }else {
            //  displayMessage(getString(R.string.something_went_wrong_net));
        }

    }
}
