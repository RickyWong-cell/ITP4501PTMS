package com.example.itp4501ptms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WrongLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 這邊共用當初設計的展示模板 layout
        setContentView(R.layout.activity_practice_log);

        TextView tvWelcome = findViewById(R.id.tvWelcomePracticeLog);
        TextView tvLogTitle = findViewById(R.id.tvLogTitle);
        Button btnBack = findViewById(R.id.btnLogBack);

        // 改寫標題為錯題本
        tvLogTitle.setText("❌ 我的錯題小筆記");
        btnBack.setText("⬅️ 返回紀錄選單");

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcome.setText("Hello! " + prefs.getString("username", "學生"));

        btnBack.setOnClickListener(v -> finish());
    }
}