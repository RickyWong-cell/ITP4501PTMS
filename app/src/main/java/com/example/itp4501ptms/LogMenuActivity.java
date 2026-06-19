package com.example.itp4501ptms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itp4501ptms.R;

public class LogMenuActivity extends AppCompatActivity {

    private TextView tvWelcomeLogMenu;
    private Button btnGoPracticeLog, btnGoWrongLog, btnLogMenuBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_menu);

        tvWelcomeLogMenu = findViewById(R.id.tvWelcomeLogMenu);
        btnGoPracticeLog = findViewById(R.id.btnGoPracticeLog);
        btnGoWrongLog = findViewById(R.id.btnGoWrongLog);
        btnLogMenuBack = findViewById(R.id.btnLogMenuBack);

        // 同步歡迎詞
        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeLogMenu.setText("Hello! " + prefs.getString("username", "學生"));

        // 跳轉到：歷次練習總覽
        btnGoPracticeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogMenuActivity.this, PracticeLogActivity.class);
                startActivity(intent);
            }
        });

        // 跳轉到：錯題小筆記
        btnGoWrongLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ⚠️ 我們在 XML 中將 PracticeLog 和 WrongLog 的結構做得很像
                // 這裡我們直接導向 WrongLogActivity
                Intent intent = new Intent(LogMenuActivity.this, WrongLogActivity.class);
                startActivity(intent);
            }
        });

        // 返回上一頁 (主畫面)
        btnLogMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 關閉目前頁面，自然就會回到上一個頁面
            }
        });
    }
}