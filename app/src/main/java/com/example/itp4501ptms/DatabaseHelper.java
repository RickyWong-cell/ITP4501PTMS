package com.example.itp4501ptms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MathApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PracticeLog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date_time TEXT, " +
                "duration INTEGER, " +
                "wrong_count INTEGER)");

        db.execSQL("CREATE TABLE WrongAnswerLog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question TEXT, " +
                "wrong_answer TEXT, " +
                "correct_answer TEXT, " +
                "solution TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PracticeLog");
        db.execSQL("DROP TABLE IF EXISTS WrongAnswerLog");
        onCreate(db);
    }
}
