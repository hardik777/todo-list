package com.todo.list.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ToDoData {

    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("time")
    private String time;
    @SerializedName("datetime")
    private Date datetime;
    @SerializedName("date")
    private String date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
