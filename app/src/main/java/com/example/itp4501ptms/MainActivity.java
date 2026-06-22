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

    private static final String PREF_NAME = "MathAppPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartPractice = findViewById(R.id.btnStartPractice);
        btnPracticeLogs = findViewById(R.id.btnPracticeLogs);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnFinish = findViewById(R.id.btnFinish);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        checkAndPromptUsername();

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        if (!username.isEmpty()) {
            tvWelcome.setText("Hello! " + username);
        }
    }

    private void checkAndPromptUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, "");

        if (username.isEmpty()) {
            final EditText inputEditText = new EditText(this);
            inputEditText.setHint("輸入你的暱稱");

            new AlertDialog.Builder(this)
                    .setTitle("歡迎第一次使用！")
                    .setMessage("請輸入你的名字：")
                    .setView(inputEditText)
                    .setCancelable(false)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int Lutheran) {
                            String nameInput = inputEditText.getText().toString().trim();
                            if (!nameInput.isEmpty()) {
                                sharedPreferences.edit().putString(KEY_USERNAME, nameInput).apply();
                                tvWelcome.setText("Hello! " + nameInput);
                                Toast.makeText(MainActivity.this, "歡迎 " + nameInput + " 🎉", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "名字不能留空喔！", Toast.LENGTH_SHORT).show();
                                checkAndPromptUsername();
                            }
                        }
                    })
                    .show();
        } else {
            tvWelcome.setText("Hello! " + username);
        }
    }

    private void setupClickListeners() {
        btnStartPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        btnPracticeLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogMenuActivity.class);
                startActivity(intent);
            }
        });

        btnChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsernameActivity.class);
                startActivity(intent);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}