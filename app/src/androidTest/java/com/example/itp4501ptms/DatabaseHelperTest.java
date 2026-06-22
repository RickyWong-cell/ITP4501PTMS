package com.example.itp4501ptms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        // Use an in-memory database or a test database name to avoid polluting the actual app data
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        db.close();
        dbHelper.close();
        // Delete database file after test
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("MathApp.db");
    }

    @Test
    public void testOnCreate_TablesCreated() {
        // Check if PracticeLog table exists
        Cursor cursor1 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='PracticeLog'", null);
        assertTrue("PracticeLog table should exist", cursor1.moveToFirst());
        cursor1.close();

        // Check if WrongAnswerLog table exists
        Cursor cursor2 = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='WrongAnswerLog'", null);
        assertTrue("WrongAnswerLog table should exist", cursor2.moveToFirst());
        cursor2.close();
    }

    @Test
    public void testPracticeLog_Columns() {
        Cursor cursor = db.rawQuery("PRAGMA table_info(PracticeLog)", null);
        
        boolean hasId = false;
        boolean hasDateTime = false;
        boolean hasDuration = false;
        boolean hasWrongCount = false;

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            switch (name) {
                case "id": hasId = true; break;
                case "date_time": hasDateTime = true; break;
                case "duration": hasDuration = true; break;
                case "wrong_count": hasWrongCount = true; break;
            }
        }
        cursor.close();

        assertTrue("Should have id column", hasId);
        assertTrue("Should have date_time column", hasDateTime);
        assertTrue("Should have duration column", hasDuration);
        assertTrue("Should have wrong_count column", hasWrongCount);
    }

    @Test
    public void testWrongAnswerLog_Columns() {
        Cursor cursor = db.rawQuery("PRAGMA table_info(WrongAnswerLog)", null);
        
        boolean hasId = false;
        boolean hasQuestion = false;
        boolean hasWrongAnswer = false;
        boolean hasCorrectAnswer = false;
        boolean hasSolution = false;

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            switch (name) {
                case "id": hasId = true; break;
                case "question": hasQuestion = true; break;
                case "wrong_answer": hasWrongAnswer = true; break;
                case "correct_answer": hasCorrectAnswer = true; break;
                case "solution": hasSolution = true; break;
            }
        }
        cursor.close();

        assertTrue("Should have id column", hasId);
        assertTrue("Should have question column", hasQuestion);
        assertTrue("Should have wrong_answer column", hasWrongAnswer);
        assertTrue("Should have correct_answer column", hasCorrectAnswer);
        assertTrue("Should have solution column", hasSolution);
    }
}
