package com.todo.list.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todo.list.R;
import com.todo.list.data.adapter.ToDoAdapter;
import com.todo.list.data.db.ToDoDatabase;
import com.todo.list.data.model.ToDoData;
import com.todo.list.ui.interfaces.ToDoActivityListener;
import com.todo.list.utils.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.todo.list.utils.CollectionUtils.ACTIVITY_RESULT;
import static com.todo.list.utils.CollectionUtils.NewIntentWithDataActivityResult;
import static com.todo.list.utils.CollectionUtils.ToastMessage;

//import com.todo.list.service.AlarmReceiver;

public class MainActivity extends AppCompatActivity implements ToDoActivityListener {

    public Context context;
    public ArrayList<ToDoData> listToDo;
    public ToDoDatabase db;
    public ToDoAdapter toDoAdapter;
    public AlertDialog alertDialog;
    public AlertDialog.Builder builder;

    @BindView(R.id.rvToDo)
    RecyclerView rvToDo;
    @BindView(R.id.txtNoData)
    CustomTextView txtNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;
        initView();
    }

    /**
     * init views
     */
    public void initView() {
        db = new ToDoDatabase(context);
        listToDo = db.allToDoList();
        txtNoData.setVisibility((listToDo.size() > 0) ? View.GONE : View.VISIBLE);

        rvToDo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        toDoAdapter = new ToDoAdapter(context, this, listToDo);
        rvToDo.setAdapter(toDoAdapter);
    }

    @OnClick(R.id.fabAdd)
    public void onViewClicked() {
        HashMap<String, Object> data = new HashMap();
        data.put("IsAdd", true);
        NewIntentWithDataActivityResult(context, AddUpdateTodo.class, data, false, false);
    }

    /**
     * @param data add tasks
     */
    @Override
    public void onSuccess(ArrayList<ToDoData> data) {
        listToDo.clear();
        listToDo = data;

        txtNoData.setVisibility((listToDo.size() > 0) ? View.GONE : View.VISIBLE);

        toDoAdapter = new ToDoAdapter(context, this, listToDo);
        rvToDo.setAdapter(toDoAdapter);
    }

    @Override
    public void onDeleteSuccess(String message) {
        onSuccess(db.allToDoList());
        ToastMessage(context, message);
    }

    /**
     * @param data delete tasks
     */
    @Override
    public void onDelete(ToDoData data) {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                return;
            }
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        builder = new AlertDialog.Builder(context).setView(dialogView);

        CustomTextView txtTitle = dialogView.findViewById(R.id.txtTitle);
        CardView cvDelete = dialogView.findViewById(R.id.cvDelete);
        CardView cvCancel = dialogView.findViewById(R.id.cvCancel);

        txtTitle.setText(data.getTitle());

        //performing positive action
        cvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.deleteToDo(data)) {
                    onDeleteSuccess("ToDo Delete Successfully");
                } else {
                    onFailure("Oops...Please try again");
                }

                alertDialog.dismiss();
                alertDialog = null;
            }
        });

        //performing cancel action
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        });

        // Create the AlertDialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Set other dialog properties
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onFailure(String message) {
        ToastMessage(context, message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);
        switch (requestCode) {
            case ACTIVITY_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    if (intentData != null) {
                        try {
                            boolean isRefresh = intentData.getBooleanExtra("IsRefresh", false);
                            if (isRefresh) {
                                onSuccess(db.allToDoList());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
}