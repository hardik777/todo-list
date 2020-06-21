package com.todo.list.ui.interfaces;

import com.todo.list.data.model.ToDoData;

import java.util.ArrayList;

public interface ToDoActivityListener {
    void onSuccess(ArrayList<ToDoData> data);

    void onDeleteSuccess(String message);

    void onDelete(ToDoData data);

    void onFailure(String message);
}
