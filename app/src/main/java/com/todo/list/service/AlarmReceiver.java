package com.todo.list.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.todo.list.R;
import com.todo.list.ui.view.MainActivity;

import java.util.Random;
import java.util.UUID;

import static android.app.Notification.VISIBILITY_PRIVATE;

//public class AlarmReceiver extends WakefulBroadcastReceiver {
public class AlarmReceiver extends BroadcastReceiver {

    public static final String TODOTEXT = "com.todo.list.todonotificationservicetext";
    public static final String ANDROID_CHANNEL_ID = "com.todo.list.service.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = " com.todo.list.service.ANDROID_CHANNEL";
    public static NotificationManager mManager;
    public static String mTodoText = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("JobService: ", "AlarmReceiver : onReceive: ");
        mTodoText = intent.getStringExtra(TODOTEXT);
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent i = new Intent(context, MainActivity.class);

        //Create Channel
        createChannels();

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT));

        mManager.notify(new Random().nextInt(), notification.build());
    }

    /**
     * Channel for Oreo and Above Versions
     */
    public void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            androidChannel.enableLights(true);
            androidChannel.enableVibration(true);
            androidChannel.setLightColor(Color.BLUE);
            androidChannel.setLockscreenVisibility(VISIBILITY_PRIVATE);
            mManager.createNotificationChannel(androidChannel);
        }
    }

}
