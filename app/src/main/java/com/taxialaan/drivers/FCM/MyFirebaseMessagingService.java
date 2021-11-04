package com.taxialaan.drivers.FCM;

/**
 * Created by jayakumar on 16/02/17.
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taxialaan.drivers.Activity.MainActivity;
import com.taxialaan.drivers.Helper.SharedHelper;
import com.taxialaan.drivers.R;
import com.taxialaan.drivers.Utilities.Utils;

import static com.taxialaan.drivers.G.CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    int notificationId = 0;
    private static final String TAG = "MyFirebaseMsgService";
    String channelId = "fcm_default_channel";
    Utils utils = new Utils();
    MediaPlayer mPlayer;
    boolean isInBackground;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {

            String data = remoteMessage.getData().get("data");
            // if (data.equals("1")) {

            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            // if (isInBackground) {

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notifyIntent.putExtra("background", true);
            startActivity(notifyIntent);

            // }

            sendNotification(remoteMessage.getData().get("message"));

            //  } else if (data.equals("2")) {

            //  } else {

            //  sendNotification(remoteMessage.getData().get("message"));
            // }

        } else {

            utils.print(TAG, "FCM Notification failed");
            sendNotification(remoteMessage.getData().get("message"));
        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        int num = ++notificationId;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this,String.valueOf(notificationId))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("TaxiAlaan")
                        .setContentText("")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        //startForeground(1, notification);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(notificationId), "MyNotification", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(num, notification.build());
        mPlayer = MediaPlayer.create(this, R.raw.alert);
        mPlayer.start();

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            return R.drawable.notification_white;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedHelper.putKeyDeviceToken(getApplicationContext(), "device_token", s);

    }


}