package com.example.ss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HeartRateDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "heart_rate.db";
    private static final int DATABASE_VERSION = 1;

    public HeartRateDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE heart_rate (id INTEGER PRIMARY KEY AUTOINCREMENT, rate REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS heart_rate");
        onCreate(db);
    }
}