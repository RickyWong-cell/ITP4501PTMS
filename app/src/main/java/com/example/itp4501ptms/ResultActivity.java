package com.example.itp4501ptms; // ⚠️ 請確保與你的專案 Package 名稱一致

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvWelcomeResult, tvResultSummary;
    private Button btnResultBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 1. 初始化 UI 元件
        tvWelcomeResult = findViewById(R.id.tvWelcomeResult);
        tvResultSummary = findViewById(R.id.tvResultSummary);
        btnResultBackToMain = findViewById(R.id.btnResultBackToMain);

        // 2. 顯示頂部歡迎詞
        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeResult.setText("Hello! " + prefs.getString("username", "學生"));

        // 3. 接收從 QuizActivity 傳過來的測驗結果數據
        int duration = getIntent().getIntExtra("DURATION", 0);
        int wrongCount = getIntent().getIntExtra("WRONG_COUNT", 0);

        // 4. 將結果動態組合並顯示在畫面的白色卡片內
        String resultText = "總共花費：" + duration + " 秒\n\n答錯題數：" + wrongCount + " 題";
        tvResultSummary.setText(resultText);

        // 5. 點擊返回主選單按鈕
        btnResultBackToMain.setOnClickListener(v -> {
            finish(); // 關閉結果頁，自然回到主畫面
        });
    }
}