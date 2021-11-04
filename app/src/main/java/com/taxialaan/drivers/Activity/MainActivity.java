package com.taxialaan.drivers.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.taxialaan.drivers.Activity.login.BeginScreen;
import com.taxialaan.drivers.Activity.wallet.ActivityWalletManager;
import com.taxialaan.drivers.Api.ApiClient;
import com.taxialaan.drivers.Api.ApiInterface;
import com.taxialaan.drivers.Api.Repository;
import com.taxialaan.drivers.Api.response.ModelConfigMqtt;
import com.taxialaan.drivers.Api.response.UpdateResponse;
import com.taxialaan.drivers.Fragment.EarningsFragment;
import com.taxialaan.drivers.Fragment.Help;
import com.taxialaan.drivers.Fragment.Map;
import com.taxialaan.drivers.Fragment.SummaryFragment;
import com.taxialaan.drivers.G;
import com.taxialaan.drivers.GPS.LocationService;
import com.taxialaan.drivers.Helper.LocaleUtils;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.Helper.URLHelper;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.NotificationCenter;
import com.taxialaan.drivers.Utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.taxialaan.drivers.G.getInstance;
import static com.taxialaan.drivers.G.trimMessage;

public class MainActivity extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate {
    // tags used to attach the fragments
    private final String TAG_HOME = "home";
    private final String TAG_SUMMARY = "summary";
    private final String TAG_HELP = "help";
    private final String TAG_EARNINGS = "earnings";
    public static FragmentManager fragmentManager;

    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    Fragment fragment;
    Activity activity;
    Context context;
    Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, approvaltxt, amountTxt;
    private ImageView status;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    Utils utils = new Utils();
    boolean push = false;
    boolean background = false;
    String  token = "";

    public static final String MESSENGER_INTENT_KEY = "msg-intent-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }


        setContentView(R.layout.activity_main);
        findViewById();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            push = extras.getBoolean("push");
            background = extras.getBoolean("background");
        }

        map();
        configMqtt();

        loadNavHeader();
        setUpNavigationView();

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(activity, EditProfile.class));
            }
        });


        NotificationCenter.getInstance().addObserver(this, NotificationCenter.runService);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateWalletAmount);

        checkVersion();
        reqPermission();


    }


    private void reqPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!isMyServiceRunning(LocationService.class) && SharedHelper.getKey(getInstance(), "statusTrack").equals("ONLINE")) {

                            if (SharedHelper.getKey(context, "ip") != null) {

                                startService();

                            }


                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).check();

    }


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void findViewById() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.usernameTxt);
        amountTxt = navHeader.findViewById(R.id.amountTxt);
        approvaltxt = navHeader.findViewById(R.id.status_txt);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        status = navHeader.findViewById(R.id.status);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:

                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragment = new Map();

                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.content, fragment);

                        Bundle bundle = new Bundle();
                        bundle.putBoolean("push", push);
                        bundle.putBoolean("background", background);
                        fragment.setArguments(bundle);


                        transaction.addToBackStack(null);
                        transaction.commit();

                        break;
                    case R.id.nav_wallet_manager:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, ActivityWalletManager.class));
                        break;
                    case R.id.nav_yourtrips:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        break;
                    case R.id.nav_wallet:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new SummaryFragment();
                        drawer.closeDrawers();
                        FragmentManager manager2 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction1 = manager2.beginTransaction();
                        transaction1.replace(R.id.content, fragment);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        //GoToFragment();
                        break;
                    case R.id.nav_settings:
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, ActivitySettings.class));
                        break;
                    case R.id.nav_help:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_HELP;
                        fragment = new Help();
                        drawer.closeDrawers();
                        FragmentManager manager4 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction2 = manager4.beginTransaction();
                        transaction2.replace(R.id.content, fragment);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                        //GoToFragment();
                        break;
                    case R.id.nav_earnings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_EARNINGS;
                        fragment = new EarningsFragment();
                        drawer.closeDrawers();
                        FragmentManager manager1 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction3 = manager1.beginTransaction();
                        transaction3.replace(R.id.content, fragment);
                        transaction3.addToBackStack(null);
                        transaction3.commit();
//                        GoToFragment();
                        break;
                    case R.id.nav_share:
                        drawer.closeDrawers();
                        navigateToShareScreen(URLHelper.Code);
                        return true;
                    case R.id.nav_logout:
                        showLogoutDialog();
                        return true;
                    default:
                        navItemIndex = 0;
                }


                return true;
            }
        });

        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                Repository.getInstance().getProfile(null);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    loadNavHeader();
                }
                updateWallet();
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateWalletAmount);
        utils.print("onResume","onDestroy");
    }

    private void updateWallet() {
        if (txtName != null) {
            txtName.setText(SharedHelper.getKey(context, "first_name"));
            txtName.append(" " + SharedHelper.getKey(context, "last_name"));
            amountTxt.setText(Utils.getString(R.string.wallet_id));
            amountTxt.append(" : ");
            amountTxt.append(SharedHelper.getKey(context, "wallet_id"));
            amountTxt.append("\n");
            amountTxt.append(Utils.getString(R.string.balance) + " : ");
            amountTxt.append(SharedHelper.getKey(context, "currency"));
            amountTxt.append(SharedHelper.getKey(context, "balance"));
            amountTxt.append("\n");
            amountTxt.append(getString(R.string.Introducer_code));
            amountTxt.append(" : ");
            amountTxt.append(SharedHelper.getKey(context, "share_key"));
            navHeader.invalidate();
        }
    }

    private void loadNavHeader() {
        // name, website
        if (SharedHelper.getKey(context, "approval_status").equals("new") || SharedHelper.getKey(context, "approval_status").equals("onboarding")) {
            approvaltxt.setTextColor(Color.YELLOW);
            approvaltxt.setText(getText(R.string.waiting_for_approval));
            // status.setImageResource(R.drawable.newuser);
        } else if (SharedHelper.getKey(context, "approval_status").equals("banned")) {
            approvaltxt.setTextColor(Color.RED);
            approvaltxt.setText(getText(R.string.banned));
            // status.setImageResource(R.drawable.banned);
        } else {
            approvaltxt.setTextColor(Color.GREEN);
            approvaltxt.setText(getText(R.string.approved));
            // status.setImageResource(R.drawable.approved);
        }

        txtName.setText(SharedHelper.getKey(context, "first_name"));
        txtName.append(" " + SharedHelper.getKey(context, "last_name"));
        amountTxt.setText(Utils.getString(R.string.wallet_id));
        amountTxt.append(" : ");
        amountTxt.append(SharedHelper.getKey(context, "wallet_id"));
        amountTxt.append("\n");
        amountTxt.append(Utils.getString(R.string.balance) + " : ");
        amountTxt.append(SharedHelper.getKey(context, "currency"));
        amountTxt.append(SharedHelper.getKey(context, "balance"));
        amountTxt.append("\n");
        amountTxt.append(getString(R.string.Introducer_code));
        amountTxt.append(" : ");
        amountTxt.append(SharedHelper.getKey(context, "share_key"));

        utils.print("Profile_PIC", "" + SharedHelper.getKey(context, "picture"));
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)
                && !SharedHelper.getKey(context, "picture").equalsIgnoreCase("null")) {
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .resize(50, 50)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            Picasso.with(context)
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .resize(50, 50)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        }

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                fragment = new Map();
                GoToFragment();
                return;
            } else {
                System.exit(0);
            }
        }

        super.onBackPressed();
    }


    private void map() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment = new Map();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content, fragment);

                Bundle bundle = new Bundle();
                bundle.putBoolean("push", push);
                bundle.putBoolean("background", background);
                fragment.setArguments(bundle);


                transaction.commit();
                fragmentManager = getSupportFragmentManager();
            }
        });
    }

    public void GoToFragment() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawers();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
    }

    public void navigateToShareScreen(String shareUrl) {

        Bitmap bitmap;
        OutputStream output;

        // Retrieve the image from the res folder
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.qq);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder AndroidBegin in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/Share Image Tutorial/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "sample_wallpaper.png");

        try {

            // Share Intent
            Intent share = new Intent(Intent.ACTION_SEND);
            // Type of file to share
            share.setType("image/jpeg");
            output = new FileOutputStream(file);
            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
            // Locate the image to Share
            Uri uri = Uri.fromFile(file);
            // Pass the image into an Intnet
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.putExtra(Intent.EXTRA_TEXT, shareUrl + " \n " + getString(R.string.Introducer_code) + " : " + SharedHelper.getKey(context, "share_key"));
            // Show the social share chooser list
            startActivity(Intent.createChooser(share, "Share Image Tutorial"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("image/jpeg");
//        Uri uri = Uri.parse("android.resource://com.taxialaan.drivers/"+R.drawable.qq);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, This is test Sharing");
//        startActivity(Intent.createChooser(shareIntent, "Send your image"));
       /* String text = "Look at my awesome picture";
        Uri pictureUri = Uri.parse("https://homepages.cae.wisc.edu/~ece533/images/airplane.png");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
      //  shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
        shareIntent.setType("image/png");
       // shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share images..."));
       /* Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " \n " + getString(R.string.Introducer_code)+" : "+SharedHelper.getKey(context,"share_key") );
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);*/
    }

    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                drawer.closeDrawers();

                NotificationCenter.getInstance().postNotificationName(NotificationCenter.runService, "offline");
                SharedHelper.clearSharedPreferences(context);

                Intent goToLogin = new Intent(activity, BeginScreen.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToLogin);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            // refreshAccessToken();
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
                    }

                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @SuppressLint("NewApi")
    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.logout));
            builder.setMessage(getString(R.string.exit_confirm));

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateWalletAmount) {
            updateWallet();
        } else {
            String s = (String) args[0];
            if (s.equals("offline")) {

                SharedHelper.putKey(this, "statusService", "1");
                Intent serviceIntent = new Intent(this, LocationService.class);
                stopService(serviceIntent);

            } else {

                Intent serviceIntent = new Intent(getApplicationContext(), LocationService.class);
                serviceIntent.putExtra("inputExtra", "TaxiAlaan");
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

            }
        }
    }

    public void checkVersion() {

        PackageInfo pinfo = null;
        int versionNumber = 0;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionNumber = pinfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<UpdateResponse> call = apiInterface.updateVersionApp("provider");
        final int finalVersionNumber = versionNumber;
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, retrofit2.Response<UpdateResponse> response) {

                if (response.isSuccessful()) {

                    if (Integer.valueOf(response.body().getVersion()) > finalVersionNumber) {

                        showDialogCheckVersion(response.body().getDec(), response.body().getUrl());
                    }

                }

            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                // dismissProgress();
                //  displayMessage(getResources().getString(R.string.something_went_wrong));
            }
        });

    }

    private void showDialogCheckVersion(String message, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            }
                        });

        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        utils.print("kavos", "kavos");
    }

    private void configMqtt() {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URLHelper.configMqtt,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.length() > 0) {

                            Gson mqqt = new Gson();
                            ModelConfigMqtt modelMqtt = mqqt.fromJson(response.toString(), ModelConfigMqtt.class);
                            if (modelMqtt.getMqtt().getServer() != null) {

                                utils.print("isMyServiceRunning", modelMqtt.getMqtt().getServer() + ":" + modelMqtt.getMqtt().getPort());
                                utils.print("isMyServiceRunning", SharedHelper.getKey(context, "ip"));
                                utils.print("isMyServiceRunning", "" + isMyServiceRunning(LocationService.class));


                                if (!(modelMqtt.getMqtt().getServer() + ":" + modelMqtt.getMqtt().getPort()).equals(SharedHelper.getKey(context, "ip"))) {

                                    SharedHelper.putKey(context, "ip", modelMqtt.getMqtt().getServer() + ":" + modelMqtt.getMqtt().getPort());

                                    if (isMyServiceRunning(LocationService.class)) {

                                        SharedHelper.putKey(getApplicationContext(), "statusService", "1");
                                        Intent serviceIntent = new Intent(context, LocationService.class);
                                        stopService(serviceIntent);

                                        startService();

                                    } else {

                                        startService();
                                    }

                                }

                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };

        G.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void startService() {

        if (SharedHelper.getKey(getInstance(), "statusTrack").equals("ONLINE")) {

            Intent serviceIntent = new Intent(MainActivity.this, LocationService.class);
            serviceIntent.putExtra("inputExtra", "Active Taxi Alaan");
            serviceIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

        }

    }
}
