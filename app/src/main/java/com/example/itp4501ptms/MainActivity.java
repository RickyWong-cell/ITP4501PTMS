package com.example.itp4501ptms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itp4501ptms.R;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnStartPractice, btnPracticeLogs, btnChangeUsername, btnFinish;
    private SharedPreferences sharedPreferences;

    // 定義儲存 SharedPreferences 的 Key
    private static final String PREF_NAME = "MathAppPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 初始化 UI 元件
        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartPractice = findViewById(R.id.btnStartPractice);
        btnPracticeLogs = findViewById(R.id.btnPracticeLogs);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnFinish = findViewById(R.id.btnFinish);

        // 2. 初始化 SharedPreferences (用於本地儲存用戶名)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // 3. 檢查是否已有使用者名稱
        checkAndPromptUsername();

        // 4. 設定按鈕點擊監聽器
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 當從其他頁面（如修改名字頁）返回主畫面時，重新整理歡迎詞
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        if (!username.isEmpty()) {
            tvWelcome.setText("Hello! " + username);
        }
    }

    /**
     * 檢查 SharedPreferences 是否有名字，若無則跳出輸入視窗
     */
    private void checkAndPromptUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, "");

        if (username.isEmpty()) {
            // 建立一個讓用戶輸入名字的輸入框
            final EditText inputEditText = new EditText(this);
            inputEditText.setHint("輸入你的暱稱");

            // 建立彈出視窗 (AlertDialog)
            new AlertDialog.Builder(this)
                    .setTitle("歡迎第一次使用！")
                    .setMessage("請輸入你的名字：")
                    .setView(inputEditText)
                    .setCancelable(false) // 規格書硬性要求：一定要輸入，所以不允許點擊外面關閉
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int Lutheran) {
                            String nameInput = inputEditText.getText().toString().trim();
                            if (!nameInput.isEmpty()) {
                                // 儲存名字到 SharedPreferences
                                sharedPreferences.edit().putString(KEY_USERNAME, nameInput).apply();
                                // 更新介面文字
                                tvWelcome.setText("Hello! " + nameInput);
                                Toast.makeText(MainActivity.this, "歡迎 " + nameInput + " 🎉", Toast.LENGTH_SHORT).show();
                            } else {
                                // 如果學生什麼都沒打就按確定，重新強制彈窗
                                Toast.makeText(MainActivity.this, "名字不能留空喔！", Toast.LENGTH_SHORT).show();
                                checkAndPromptUsername();
                            }
                        }
                    })
                    .show();
        } else {
            // 若已有名字，直接顯示
            tvWelcome.setText("Hello! " + username);
        }
    }

    /**
     * 設定按鈕的點擊跳轉邏輯
     */
    private void setupClickListeners() {
        // 按鈕 1：開始練習
        btnStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 進入測驗頁面 (QuizActivity)
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        // 按鈕 2：檢視練習記錄
        btnPracticeLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 進入記錄選單頁面 (LogMenuActivity)
                Intent intent = new Intent(MainActivity.this, LogMenuActivity.class);
                startActivity(intent);
            }
        });

        // 按鈕 3：修改我的名字
        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 進入修改名字頁面 (UsernameActivity)
                Intent intent = new Intent(MainActivity.this, UsernameActivity.class);
                startActivity(intent);
            }
        });

        // 按鈕 4：離開練習系統 (Finish)
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 關閉目前 Activity，退出 App
                finish();
            }
        });
    }
}