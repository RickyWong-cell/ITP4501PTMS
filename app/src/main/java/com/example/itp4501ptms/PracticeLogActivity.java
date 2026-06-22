package com.example.itp4501ptms;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PracticeLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_log);

        TextView tvWelcome = findViewById(R.id.tvWelcomePracticeLog);
        ListView lvLogList = findViewById(R.id.lvLogList);
        Button btnBack = findViewById(R.id.btnLogBack);

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcome.setText("Hello! " + prefs.getString("username", "學生"));

        btnBack.setOnClickListener(v -> finish());

        // 從 SQLite 資料庫撈取 PracticeLog 歷史資料
        ArrayList<String> logs = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM PracticeLog ORDER BY id DESC", null);
        
        if (cursor.moveToFirst()) {
            do {
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow("date_time"));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
                int wrongs = cursor.getInt(cursor.getColumnIndexOrThrow("wrong_count"));
                logs.add("📅 " + dateTime + "\n⏱️ 歷時: " + duration + " 秒 | ❌ 錯題: " + wrongs + " 題");
            } while (cursor.moveToNext());
        }
        cursor.close();

        lvLogList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logs));
    }
}
