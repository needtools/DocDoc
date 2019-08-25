package com.example.docdoc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // http://www.sqlitetutorial.net/
    public DBHelper(Context context) {
        super(context, "DocDocDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table data (" +
                "id integer primary key autoincrement," +
                "time text," +
                "temperature text," +
                "systolic text," +
                "diastolic text," +
                "pulse text," +
                "sugar text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
