package com.todo.list.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.todo.list.data.model.ToDoData;

import java.util.ArrayList;

public class ToDoDatabase extends SQLiteOpenHelper {

    // Database Version
    private static int DATABASE_VERSION = 1;

    // Database Name
    private static String DATABASE_NAME = "todo_db";
    private static String TABLE_NAME = "todolist";
    private static String COLUMN_ID = "id";
    private static String COLUMN_TITLE = "title";
    private static String COLUMN_DESCRIPTION = "description";
    private static String COLUMN_TIME = "time";
    private static String COLUMN_DATE = "date";

    // Create table SQL query
    private static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT"
            + ")";

    public ToDoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    /**
     * @param toDoData insert data
     */
    public Boolean insertToDo(ToDoData toDoData) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add thems
        values.put(COLUMN_TITLE, toDoData.getTitle());
        values.put(COLUMN_DESCRIPTION, toDoData.getDescription());
        values.put(COLUMN_TIME, toDoData.getTime());
        values.put(COLUMN_DATE, toDoData.getDate());

        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        if (id != (-1))
            return true;
        else
            return false;
    }

    /**
     * All Task
     */
    // Select All Query
    public ArrayList<ToDoData> allToDoList() {
        ArrayList<ToDoData> toDoTables = new ArrayList<ToDoData>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ToDoData toDoData = new ToDoData();
                toDoData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                toDoData.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                toDoData.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                toDoData.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                toDoData.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                toDoTables.add(toDoData);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return toDoTables;
    }

    /**
     * @param toDoData update Task
     */
    public Boolean updateToDo(ToDoData toDoData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, toDoData.getTitle());
            values.put(COLUMN_DESCRIPTION, toDoData.getDescription());
            values.put(COLUMN_TIME, toDoData.getTime());
            values.put(COLUMN_DATE, toDoData.getDate());

            // updating row
            int id = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(toDoData.getId())});

            if (id != (-1))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param toDoData delete Task
     */
    public Boolean deleteToDo(ToDoData toDoData) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(toDoData.getId())});
            db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
