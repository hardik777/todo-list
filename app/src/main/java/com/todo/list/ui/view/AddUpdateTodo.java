package com.todo.list.ui.view;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.todo.list.R;
import com.todo.list.data.db.ToDoDatabase;
import com.todo.list.data.model.ToDoData;
import com.todo.list.service.AlarmReceiver;
import com.todo.list.utils.CustomTextView;

import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.todo.list.service.AlarmReceiver.TODOTEXT;
import static com.todo.list.utils.CollectionUtils.GetDate;
import static com.todo.list.utils.CollectionUtils.GetTime;
import static com.todo.list.utils.CollectionUtils.INTENT_DATA;
import static com.todo.list.utils.CollectionUtils.ToastMessage;
import static com.todo.list.utils.CollectionUtils.mUserReminderDate;

public class AddUpdateTodo extends AppCompatActivity {

    @BindView(R.id.txtTitle)
    EditText txtTitle;
    @BindView(R.id.txtDate)
    EditText txtDate;
    @BindView(R.id.txtTime)
    EditText txtTime;
    @BindView(R.id.txtDescription)
    EditText txtDescription;
    @BindView(R.id.txtAdd)
    CustomTextView txtAdd;
    @BindView(R.id.txtClear)
    CustomTextView txtClear;

    Context context;
    Boolean IsAdd = true;
    ToDoDatabase db;
    HashMap<String, Object> hashMap;
    ToDoData toDoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_todo);
        ButterKnife.bind(this);

        context = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new ToDoDatabase(context);

        InitView();
    }

    /**
     * init views
     */
    public void InitView() {
        hashMap = (HashMap<String, Object>) getIntent().getSerializableExtra(INTENT_DATA);

        IsAdd = (Boolean) hashMap.get("IsAdd");

        if (IsAdd) {
            getSupportActionBar().setTitle("New Task");
            txtAdd.setText("Add");
            txtClear.setText("Clear");
            toDoData = new ToDoData();
        } else {
            getSupportActionBar().setTitle("Update Task");
            txtAdd.setText("Update");
            txtClear.setText("Cancel");
            toDoData = new Gson().fromJson(hashMap.get("Task").toString(), ToDoData.class);
            IsUpdate();
        }
    }

    /**
     * Fill textviews
     */
    public void IsUpdate() {
        txtTitle.setText(toDoData.getTitle());
        txtDate.setText(toDoData.getDate());
        txtTime.setText(toDoData.getTime());
        txtDescription.setText(toDoData.getDescription());
    }

    /**
     * All Click Events
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick({R.id.txtDate, R.id.txtTime, R.id.txtAdd, R.id.txtClear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtDate:
                GetDate(context, txtDate);
                break;
            case R.id.txtTime:
                GetTime(context, txtTime);
                break;
            case R.id.txtAdd:
                if (TextUtils.isEmpty(txtTitle.getText().toString().trim())) {
                    txtTitle.setError("Please enter title");
                    return;
                }
                if (TextUtils.isEmpty(txtDate.getText().toString().trim())) {
                    txtDate.setError("Please select date");
                    return;
                }
                if (TextUtils.isEmpty(txtTime.getText().toString().trim())) {
                    txtTime.setError("Please select time");
                    return;
                }

                toDoData.setTitle(txtTitle.getText().toString().trim());
                toDoData.setDate(txtDate.getText().toString().trim());
                toDoData.setTime(txtTime.getText().toString().trim());
                toDoData.setDescription(txtDescription.getText().toString().trim());
                toDoData.setDatetime(mUserReminderDate);

                try {
                    if (IsAdd) {
                        if (db.insertToDo(toDoData)) {
                            ToastMessage(context, "Task inserted successfully");
                        } else {
                            ToastMessage(context, "Oops!! Please try after some time");
                        }
                    } else {
                        if (db.updateToDo(toDoData)) {
                            ToastMessage(context, "Task updated successfully");
                        } else {
                            ToastMessage(context, "Oops!! Please try after some time");
                        }
                    }

                    //Set Alarm
                    setAlarm();
                    Intent result = getIntent();
                    result.putExtra("IsRefresh", true);
                    setResult(Activity.RESULT_OK, result);
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.txtClear:
                if (IsAdd) {
                    //Clear All
                    txtTitle.setText("");
                    txtDate.setText("");
                    txtTime.setText("");
                    txtDescription.setText("");

                    txtTitle.setError(null);
                    txtDate.setError(null);
                    txtTime.setError(null);
                    txtDescription.setError(null);
                } else {
                    //Cancel
                    finish();
                }
                break;
        }
    }

    /**
     * Alarm Set
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarm() {
        if (toDoData.getDatetime() != null) {

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(TODOTEXT, toDoData.getTitle());
            PendingIntent sender = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, toDoData.getDatetime().getTime(), sender);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}
