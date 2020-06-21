/*
 * Copyright (C) 2013-2020 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.todo.list.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

import com.todo.list.data.db.ToDoDatabase;
import com.todo.list.data.model.ToDoData;

import java.util.ArrayList;
import java.util.UUID;

import static com.todo.list.service.AlarmReceiver.TODOTEXT;

/**
 * Verify version code and add wake lock in manifest is important to avoid crash
 */
public class AlarmRestoreOnRebootService extends JobIntentService {

    public static final int JOB_ID = 0x01;

    public static void enqueueWork(Context context, Intent work) {
        Log.e("JobService: ", "enqueueWork");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enqueueWork(context, AlarmRestoreOnRebootService.class, JOB_ID, work);
        } else {
            Intent jobIntent = new Intent(context, AlarmRestoreOnRebootService.class);
            context.startService(jobIntent);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("JobService: ", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("JobService: ", "onHandleWork");
//        LogDelegate.i("System rebooted: service refreshing reminders");
        Context mContext = getApplicationContext();

//        BaseActivity.notifyAppWidgets(mContext);

        ToDoDatabase db = new ToDoDatabase(mContext);
        ArrayList<ToDoData> listToDo = db.allToDoList();
        Log.e("JobService: ", listToDo.size() + " reminders");
        for (ToDoData toDoData : listToDo) {
            setAlarm(mContext, toDoData);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarm(Context context, ToDoData toDoData) {
        if (toDoData.getDatetime() != null) {

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(TODOTEXT, toDoData.getTitle());
            PendingIntent sender = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, toDoData.getDatetime().getTime(), sender);
        }
    }
}
