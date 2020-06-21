package com.todo.list.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.todo.list.R
import com.todo.list.ui.view.MainActivity
import java.util.*

class TodoNotificationService : IntentService("TodoNotificationService") {

    companion object {
        const val TODOTEXT = "com.todo.list.todonotificationservicetext"
        const val ANDROID_CHANNEL_ID = "com.todo.list.service.ANDROID"
        const val ANDROID_CHANNEL_NAME = " com.todo.list.service.ANDROID_CHANNEL"
    }

    private var mTodoText: String? = null
    private var mManager: NotificationManager? = null

    /**
     * @param intent get data
     * */
    override fun onHandleIntent(intent: Intent?) {
        Log.d("onHandleIntent", "onHandleIntent")
        mTodoText = intent!!.getStringExtra(TODOTEXT)
        mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val i = Intent(this, MainActivity::class.java)

        //Create Channel
        createChannels()
        val notification = Notification.Builder(applicationContext)
                .setContentTitle(mTodoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), i, PendingIntent.FLAG_UPDATE_CURRENT))
                .build()
        mManager!!.notify(Random().nextInt(), notification)
    }

    /**
     * Channel for Oreo and Above Versions
     * */
    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create android channel
            val androidChannel = NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true)
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true)
            // Sets the notification light color for notifications posted to this channel
            androidChannel.lightColor = Color.BLUE
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mManager!!.createNotificationChannel(androidChannel)
        }
    }

}