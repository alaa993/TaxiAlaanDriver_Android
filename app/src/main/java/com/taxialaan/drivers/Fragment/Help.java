package com.taxialaan.drivers.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.taxialaan.drivers.Activity.login.BeginScreen;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.Helper.CustomDialog;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;

import org.json.JSONObject;

import java.util.HashMap;

import static com.taxialaan.drivers.G.getInstance;
import static com.taxialaan.drivers.G.trimMessage;

public class Help extends Fragment implements View.OnClickListener {

    ImageView backImg;
    ImageView phoneImg;
    ImageView webImg;
    ImageView mailImg;
    ImageView callPhone;
    ImageView callWhatsApp;
    ImageView callViber;
    TextView title_txt;
    String phone, email;
    LinearLayout lnCall;

    public Help() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        findViewByIdAndInitialize(view);
        setClickListeners();
        getHelp();
        return view;
    }

    private void setClickListeners() {
        backImg.setOnClickListener(this);
        mailImg.setOnClickListener(this);
        webImg.setOnClickListener(this);
        phoneImg.setOnClickListener(this);
        callWhatsApp.setOnClickListener(this);
        callViber.setOnClickListener(this);
        callPhone.setOnClickListener(this);
    }

    public void findViewByIdAndInitialize(View view) {
        backImg = view.findViewById(R.id.backArrow);
        phoneImg = view.findViewById(R.id.img_phone);
        webImg = view.findViewById(R.id.img_web);
        mailImg = view.findViewById(R.id.img_mail);
        title_txt = view.findViewById(R.id.title_txt);
        title_txt.setText(getString(R.string.app_name) +" "+ getString(R.string.help));
        callPhone = view.findViewById(R.id.img_call_phone);
        callViber = view.findViewById(R.id.img_viber);
        callWhatsApp = view.findViewById(R.id.img_whatsapp);
        lnCall = view.findViewById(R.id.lnCall);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.img_mail:
                String to = email;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+"-"+getString(R.string.help));
                intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
                startActivity(Intent.createChooser(intent, "Send Email"));
                break;
            case R.id.img_phone:

                if (!isAppInstalled(getInstance(),"com.whatsapp")){

                   callWhatsApp.setVisibility(View.INVISIBLE);
                }

                if (!isAppInstalled(getInstance(),"com.viber.voip")){

                   callViber.setVisibility(View.INVISIBLE);
                }
                lnCall.setVisibility(View.VISIBLE);

                break;
            case R.id.img_web:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_URL));
                startActivity(browserIntent);
                break;

            case R.id.img_call_phone:
                 Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
                 startActivity(intentPhone);
                 lnCall.setVisibility(View.INVISIBLE);
             break;

            case R.id.img_viber:
                callViber(phone);
                lnCall.setVisibility(View.INVISIBLE);
             break;

            case R.id.img_whatsapp:
                openWhatsappContact(phone);
                lnCall.setVisibility(View.INVISIBLE);
             break;
        }


    }

    public void callViber(String phone) {

       Uri uri = Uri.parse("tel:" + Uri.encode(phone));
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity");
        intent.setData(uri);
        getContext().startActivity(intent);

    }

   private void openWhatsappContact(String number) {

       Intent sendIntent = new Intent("android.intent.action.MAIN");
       sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
       String waNumber = number.replace("+", "");
       sendIntent.putExtra("jid", waNumber + "@s.whatsapp.net");
       startActivity(sendIntent);


    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.HELP, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                phone = response.optString("contact_number");
                email = response.optString("contact_email");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
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
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
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
                        e.printStackTrace();
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(getContext(), "access_token"));
                return headers;
            }
        };
        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(getContext(), BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        getActivity().finish();
    }

}
