package com.todo.list.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.todo.list.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CollectionUtils {

    public static final String INTENT_DATA = "INTENT_DATA";
    public static final int ACTIVITY_RESULT = 500;

    public static DatePickerDialog datePickerDialog;
    public static TimePickerDialog mTimePicker;

    public static Calendar mcurrentTime;
    public static SimpleDateFormat dateFormatter;

    public static int hour = 0;
    public static int minute = 0;

    public static String AMPM = "AM";

    public static Date mUserReminderDate;

    /**
     * Show DatePickerDialog
     */
    public static void GetDate(Context context, EditText txtDate) {
        if (datePickerDialog != null) {
            if (datePickerDialog.isShowing()) {
                return;
            }
        }

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtDate.setText(dateFormatter.format(newDate.getTime()));

                //----------------------Add New-----------------------

                Calendar calendar = Calendar.getInstance();
                int hour;
                int minute;

                if (mUserReminderDate != null) {
                    calendar.setTime(mUserReminderDate);
                }

                if (DateFormat.is24HourFormat(context)) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                } else {
                    hour = calendar.get(Calendar.HOUR);
                }

                minute = calendar.get(Calendar.MINUTE);

                calendar.set(year, monthOfYear, dayOfMonth, hour, minute);
                mUserReminderDate = calendar.getTime();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        Calendar calendar = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    /**
     * Show TimePicker
     */
    public static void GetTime(Context context, final EditText txtTime) {
        if (mTimePicker != null) {
            if (mTimePicker.isShowing()) {
                return;
            }
        }

        mcurrentTime = Calendar.getInstance();
        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int hourCalendar = selectedHour;

                if (selectedHour > 12) {
                    selectedHour -= 12;
                    AMPM = "PM";
                } else {
                    AMPM = "AM";
                }

                txtTime.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute) + ":00 " + AMPM);

                //------------Add New----------------------
                try {
                    Calendar calendar = Calendar.getInstance();
                    if (mUserReminderDate != null) {
                        calendar.setTime(mUserReminderDate);
                    }

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    calendar.set(year, month, day, hourCalendar, selectedMinute, 0);
                    mUserReminderDate = calendar.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, hour, minute, DateFormat.is24HourFormat(context));//Yes 24 hour time

        //mTimePicker.setTitle("Select Time");
        TextView text = new TextView(context);
        text.setText("Select Time");
        text.setGravity(Gravity.CENTER);
        text.setPadding(0, 40, 0, 40);
        text.setTextSize(20);
        text.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        text.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        mTimePicker.setCustomTitle(text);
        mTimePicker.show();
    }

    /**
     * To display toast in application
     *
     * @param msg display msg for toast.
     */
    public static void ToastMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * To display toast in application
     *
     * @param context     Application object.
     * @param ourClass    Next activity class.
     * @param hashMap     Pass data between one activity to second activity.
     * @param isAnimation Perform animation.
     * @param isFinish    After navigate activity to finish.
     */
    public static void NewIntentWithDataActivityResult(Context context, Class ourClass, HashMap<String, Object> hashMap, Boolean isAnimation, Boolean isFinish) {
        Intent intent = new Intent(context, ourClass);
        intent.putExtra(INTENT_DATA, hashMap);
        ((Activity) context).startActivityForResult(intent, ACTIVITY_RESULT);
        ((Activity) context).overridePendingTransition(
                (isAnimation) ? R.anim.fade_in : R.anim.animation_one,
                (isAnimation) ? R.anim.fade_out : R.anim.animation_two
        );
        if (isFinish) {
            ((Activity) context).finish();
        }
    }


}
