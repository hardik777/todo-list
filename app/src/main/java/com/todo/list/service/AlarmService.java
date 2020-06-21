package com.todo.list.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.todo.list.R;
import com.todo.list.ui.view.MainActivity;

import java.util.Random;

import static android.app.Notification.VISIBILITY_PRIVATE;
import static com.todo.list.service.AlarmReceiver.ANDROID_CHANNEL_ID;
import static com.todo.list.service.AlarmReceiver.ANDROID_CHANNEL_NAME;

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
//        sendNotification(intent.getStringExtra(TODOTEXT));
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannels();

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this, ANDROID_CHANNEL_ID)
                .setContentTitle("Alarm")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(new Random().nextInt(), alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }

    /**
     * Channel for Oreo and Above Versions
     */
    public void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            androidChannel.enableLights(true);
            androidChannel.enableVibration(true);
            androidChannel.setLightColor(Color.BLUE);
            androidChannel.setLockscreenVisibility(VISIBILITY_PRIVATE);
            alarmNotificationManager.createNotificationChannel(androidChannel);
        }
    }
}