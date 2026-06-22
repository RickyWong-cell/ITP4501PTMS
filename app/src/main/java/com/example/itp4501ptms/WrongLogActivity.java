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

public class WrongLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_log);

        TextView tvWelcome = findViewById(R.id.tvWelcomePracticeLog);
        TextView tvLogTitle = findViewById(R.id.tvLogTitle);
        ListView lvLogList = findViewById(R.id.lvLogList);
        Button btnBack = findViewById(R.id.btnLogBack);

        tvLogTitle.setText("❌ 我的錯題小筆記");
        btnBack.setText("⬅️ 返回紀錄選單");

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcome.setText("Hello! " + prefs.getString("username", "學生"));

        btnBack.setOnClickListener(v -> finish());

        ArrayList<String> wrongLogs = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM WrongAnswerLog ORDER BY id DESC", null);
        
        if (cursor.moveToFirst()) {
            do {
                String q = cursor.getString(cursor.getColumnIndexOrThrow("question"));
                String wAns = cursor.getString(cursor.getColumnIndexOrThrow("wrong_answer"));
                String cAns = cursor.getString(cursor.getColumnIndexOrThrow("correct_answer"));
                String sol = cursor.getString(cursor.getColumnIndexOrThrow("solution"));
                wrongLogs.add("❓ 題目: " + q + "\n👎 你的回答: " + wAns + " | 👍 正確答案: " + cAns + "\n💡 詳解步驟: " + sol);
            } while (cursor.moveToNext());
        }
        cursor.close();

        lvLogList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wrongLogs));
    }
}
