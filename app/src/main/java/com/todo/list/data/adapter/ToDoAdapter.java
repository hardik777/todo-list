package com.todo.list.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.todo.list.R;
import com.todo.list.data.model.ToDoData;
import com.todo.list.ui.interfaces.ToDoActivityListener;
import com.todo.list.ui.view.AddUpdateTodo;
import com.todo.list.utils.CollectionUtils;
import com.todo.list.utils.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    Context context;
    ToDoActivityListener listener;
    ArrayList<ToDoData> AdapterList;

    public ToDoAdapter(
            Context context,
            ToDoActivityListener listener,
            ArrayList<ToDoData> AdapterList) {
        this.context = context;
        this.listener = listener;
        this.AdapterList = AdapterList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblItemName;
        private TextView lblDescription;
        private TextView lblDate;
        private TextView lblTime;

        public ViewHolder(final View itemView) {
            super(itemView);
            lblItemName = (TextView) itemView.findViewById(R.id.lblItemName);
            lblDescription = (TextView) itemView.findViewById(R.id.lblDescription);
            lblDate = (TextView) itemView.findViewById(R.id.lblDate);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    @NonNull
    @Override
    public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.ViewHolder holder, int position) {
        holder.lblItemName.setText(AdapterList.get(position).getTitle());
        holder.lblDescription.setText(AdapterList.get(position).getDescription());
        holder.lblDate.setText(AdapterList.get(position).getDate());
        holder.lblTime.setText(AdapterList.get(position).getTime());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ConfirmDialog(AdapterList.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return AdapterList.size();
    }

    public static AlertDialog alertDialog;
    public static AlertDialog.Builder builder;

    public void ConfirmDialog(ToDoData toDoData) {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                return;
            }
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        builder = new AlertDialog.Builder(context).setView(dialogView);

        CustomTextView txtUTitle = dialogView.findViewById(R.id.txtUTitle);
        CardView cvUpdate = dialogView.findViewById(R.id.cvUpdate);
        CardView cvUDelete = dialogView.findViewById(R.id.cvUDelete);
        CardView cvUCancel = dialogView.findViewById(R.id.cvUCancel);

        txtUTitle.setText(toDoData.getTitle());

        //performing update action
        cvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> data = new HashMap<String, Object>();
                data.put("Task", new Gson().toJson(toDoData));
                data.put("IsAdd", false);
                CollectionUtils.NewIntentWithDataActivityResult(context,
                        AddUpdateTodo.class,
                        data,
                        false,
                        false
                );
                alertDialog.dismiss();
                alertDialog = null;
            }
        });

        //performing cancel action
        cvUCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        });

        //performing delete action
        cvUDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog = null;
                listener.onDelete(toDoData);
            }
        });

        // Create the AlertDialog
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Set other dialog properties
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}
