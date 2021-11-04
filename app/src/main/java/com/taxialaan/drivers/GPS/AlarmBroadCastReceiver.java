package com.taxialaan.drivers.GPS;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.taxialaan.drivers.Helper.SharedHelper;

public class AlarmBroadCastReceiver extends BroadcastReceiver {


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        if (!isMyServiceRunning(LocationService.class,context) &&
                SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase("true") &&
                SharedHelper.getKey(context, "statusTrack").equals("ONLINE")){

            Intent serviceIntent = new Intent(context, LocationService.class);
            serviceIntent.putExtra("inputExtra", "Active TaxiAlaan");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                serviceIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            }
            ContextCompat.startForegroundService(context, serviceIntent);
        }

    }


    public boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
