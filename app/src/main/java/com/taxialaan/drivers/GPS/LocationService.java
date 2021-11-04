package com.taxialaan.drivers.GPS;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.taxialaan.drivers.Activity.MainActivity;
import com.taxialaan.drivers.Activity.SplashScreen;
import com.taxialaan.drivers.Api.request.UpdateLocation;
import com.taxialaan.drivers.Api.response.ModelMqtt;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static com.taxialaan.drivers.G.CHANNEL_ID;

/**
 * Created by kavos khajavi on 9/29/16.
 */

public class LocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public MqttHelper mqttHelper;
    MediaPlayer     mPlayer;
    GoogleApiClient mGoogleApiClient;
    Long            timer;
    String          mobile = "";
    String token = "";
    private BroadCastStartService mReceiver = new BroadCastStartService();
    UpdateLocation updateLocation = new UpdateLocation();
    Gson g = new Gson();


    @Override
    public void onCreate() {
        super.onCreate();

        mobile = SharedHelper.getKey(this, "mobile");
        token = SharedHelper.getKey(this, "access_token");

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver,
                        new IntentFilter("com.start.service"));

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                                                                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Active TaxiAlaan")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();


        startForeground(1, notification);
        timer = System.currentTimeMillis() / 1000;
        startMqtt();
        buildGoogleApiClient();
        return START_STICKY;

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Log.i("onTaskRemoved", "onTaskRemoved");
        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000, restartServicePI);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (SharedHelper.getKey(this, "statusService").equals("1")) {
            stopForeground(true);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            SharedHelper.putKey(this, "statusService", "0");
        } else {
            Intent customBroadcastIntent = new Intent("com.start.service");
            LocalBroadcastManager.getInstance(this).sendBroadcast(customBroadcastIntent);

        }

    }

    @SuppressLint("InvalidWakeLockTag")
    private void intent() {

        try {


            PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                powerManager.newWakeLock(268435466, "taxi").acquire(10000);
            }
            boolean                               isInBackground;
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            if (isInBackground) {

                Intent notifyIntent = new Intent(this, SplashScreen.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                notifyIntent.putExtra("background", true);
                startActivity(notifyIntent);

            }
            mPlayer = MediaPlayer.create(this, R.raw.alert);
            mPlayer.start();


        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    public void startMqtt() {

        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                //   publishOnline(context);
               // Log.i("Debug", "Connected" + s);
               // mqttHelper.publishMessage(mobile + " " + s + " " + getData(), "PROD/Provider/Error");
                mqttHelper.publishOnline();

            }

            @SuppressLint("InvalidWakeLockTag")
            @Override
            public void connectionLost(Throwable throwable) {

                PowerManager          powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock     = null;
                if (powerManager != null) {
                    wakeLock = powerManager.newWakeLock(PARTIAL_WAKE_LOCK,
                                                        "kavos");
                    wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
                }

//                mqttHelper.publishMessage(mobile + " " + throwable.getMessage() + " " + getData(), "PROD/Provider/Error");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                Gson mqqt = new Gson();
                ModelMqtt modelMqtt = mqqt.fromJson(new String(mqttMessage.getPayload()), ModelMqtt.class);

                if (modelMqtt.getStatus() == 1) {
                    intent();
                } else if (modelMqtt.getStatus() == 2 && modelMqtt.getToken().equals(token)) {
                    SharedHelper.clearSharedPreferences(getApplicationContext());
                    SharedHelper.putKey(getApplicationContext(), "statusService", "1");
                    onDestroy();
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                Log.i("Debug", "deliveryComplete");

            }
        });
    }


    private String getData() {

        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(c.getTime());
    }

    @Override
    public void onLocationChanged(Location location) {

        try {

            if (location != null && location.getAccuracy() < 40 && location.hasAccuracy() && (timer + 5 < (System.currentTimeMillis() / 1000))) {
                timer = System.currentTimeMillis() / 1000;

                updateLocation.setDeviceId(SharedHelper.getKey(this, "device_UDID"));
                updateLocation.setId(SharedHelper.getKey(this, "id"));
                updateLocation.setLatitude(location.getLatitude());
                updateLocation.setLongitude(location.getLongitude());
                String str = g.toJson(updateLocation);
                mqttHelper.publishMessage(str, "PROD/Provider/Location");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {

            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                buildGoogleApiClient();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {

        try {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        } catch (Exception e) {

            e.printStackTrace();
        }


    }
}


