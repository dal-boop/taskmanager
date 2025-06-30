package com.example.taskmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tasks.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "description TEXT," +
                "dueDate TEXT," +
                "list TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public long addTask(String title, String description, String dueDate, String list) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("dueDate", dueDate);
        cv.put("list", list);
        return db.insert("tasks", null, cv);
    }

    public List<Task> getAllTasks() {
        return getTasks(null);
    }
    public List<Task> getTasks(String listFilter) {
        List<Task> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (listFilter == null) {
            c = db.rawQuery("SELECT * FROM tasks ORDER BY id DESC", null);
        } else {
            c = db.rawQuery("SELECT * FROM tasks WHERE list = ? ORDER BY id DESC", new String[]{listFilter});
        }
        if (c.moveToFirst()) {
            do {
                list.add(new Task(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("dueDate")),
                        c.getString(c.getColumnIndexOrThrow("list"))
                ));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(id)});
    }
}
